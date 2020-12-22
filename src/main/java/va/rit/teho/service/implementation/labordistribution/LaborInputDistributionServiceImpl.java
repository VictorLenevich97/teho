package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.labordistribution.*;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.repository.labordistribution.EquipmentRFUDistributionRepository;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;
import va.rit.teho.repository.labordistribution.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class LaborInputDistributionServiceImpl implements LaborInputDistributionService {

    private final CalculationService calculationService;
    private final EquipmentPerFormationService equipmentPerFormationService;
    private final RepairTypeService repairTypeService;
    private final FormationService formationService;

    private final EquipmentRFUDistributionRepository equipmentRFUDistributionRepository;
    private final LaborDistributionRepository laborDistributionRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public LaborInputDistributionServiceImpl(
            WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
            CalculationService calculationService,
            EquipmentPerFormationService equipmentPerFormationService,
            RepairTypeService repairTypeService,
            FormationService formationService,
            EquipmentRFUDistributionRepository equipmentRFUDistributionRepository,
            LaborDistributionRepository laborDistributionRepository) {
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.calculationService = calculationService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.repairTypeService = repairTypeService;
        this.formationService = formationService;
        this.equipmentRFUDistributionRepository = equipmentRFUDistributionRepository;
        this.laborDistributionRepository = laborDistributionRepository;
    }

    private EquipmentLaborInputDistribution buildLaborInputDistribution(List<LaborDistributionData> laborDistributionDataList) {
        LaborDistributionData eir = laborDistributionDataList.get(0);
        Map<Long, CountAndLaborInput> laborInputMap = new HashMap<>();
        for (LaborDistributionData eird : laborDistributionDataList) {
            if (laborInputMap.put(eird.getIntervalId(),
                                  new CountAndLaborInput(eird.getCount(), eird.getAvgLaborInput())) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        return EquipmentLaborInputDistribution
                .builder()
                .formationName(eir.getFormationName())
                .equipmentName(eir.getEquipmentName())
                .avgDailyFailure(eir.getAvgDailyFailure())
                .standardLaborInput(eir.getLaborInput())
                .intervalCountAndLaborInputMap(laborInputMap)
                .totalRepairComplexity(
                        laborInputMap.values().stream().mapToDouble(CountAndLaborInput::getLaborInput).sum())
                .build();
    }

    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(
            UUID sessionId,
            Long repairTypeId,
            Long stageId,
            List<Long> equipmentTypeIds) {
        Map<EquipmentType, Map<EquipmentSubType, Map<LaborDistributionData.CompositeKey, List<LaborDistributionData>>>> grouped =
                laborDistributionRepository.findAllGrouped(sessionId, repairTypeId, stageId, equipmentTypeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> result = new HashMap<>();

        grouped.forEach((equipmentType, subTypeMap) -> subTypeMap
                .forEach((subType, compositeKeyMap) -> compositeKeyMap
                        .forEach((key, equipmentInRepairDataList) ->
                                         result.computeIfAbsent(equipmentType, k -> new HashMap<>())
                                               .computeIfAbsent(subType, k -> new ArrayList<>())
                                               .add(buildLaborInputDistribution(equipmentInRepairDataList)))));
        return result;
    }

    private LaborDistribution calculateLaborDistribution(UUID sessionId,
                                                         Long formationId,
                                                         Long equipmentId,
                                                         Double avgDailyFailure,
                                                         int standardLaborInput,
                                                         WorkhoursDistributionInterval interval,
                                                         Long stageId,
                                                         Long repairTypeId) {
        double count = calculationService.calculateEquipmentInRepairCount(interval.getUpperBound(),
                                                                          interval.getLowerBound(),
                                                                          avgDailyFailure,
                                                                          standardLaborInput);
        double laborInput = calculationService.calculateEquipmentInRepairLaborInput(count, interval.getUpperBound());

        return new LaborDistribution(new LaborDistributionPK(formationId,
                                                             equipmentId,
                                                             interval.getId(),
                                                             stageId,
                                                             repairTypeId,
                                                             sessionId),
                                     count,
                                     laborInput);
    }

    private Stream<LaborDistribution> calculateEquipmentLaborInputDistribution(
            UUID sessionId,
            Long stageId,
            Long repairTypeId,
            Double avgDailyFailure,
            EquipmentPerFormationFailureIntensityAndLaborInput equipmentPerFormationAndLaborInput) {
        return StreamSupport
                .stream(workhoursDistributionIntervalRepository.findAll().spliterator(), false)
                .map(interval -> calculateLaborDistribution(sessionId,
                                                            equipmentPerFormationAndLaborInput.getFormationId(),
                                                            equipmentPerFormationAndLaborInput.getEquipmentId(),
                                                            avgDailyFailure,
                                                            equipmentPerFormationAndLaborInput.getLaborInput(),
                                                            interval,
                                                            stageId,
                                                            repairTypeId));
    }

    private void calculateAndSave(Long repairTypeId,
                                  UUID sessionId,
                                  List<EquipmentPerFormationFailureIntensityAndLaborInput> equipmentPerFormations) {
        List<LaborDistribution> calculated =
                equipmentPerFormations
                        .stream()
                        .flatMap(epb -> this.calculateEquipmentLaborInputDistribution(sessionId,
                                                                                      epb.getStageId(),
                                                                                      repairTypeId,
                                                                                      epb.getAvgDailyFailure(),
                                                                                      epb))
                        .collect(Collectors.toList());
        laborDistributionRepository.saveAll(calculated);
    }

    @Override
    public void updateLaborInputDistribution(UUID sessionId) {
        List<RepairType> repairTypeList = repairTypeService.list(true);
        repairTypeList
                .forEach(repairType -> {
                    List<EquipmentPerFormationFailureIntensityAndLaborInput> equipmentPerFormations =
                            equipmentPerFormationService.listWithIntensityAndLaborInput(sessionId, repairType.getId());
                    calculateAndSave(repairType.getId(), sessionId, equipmentPerFormations);
                });
    }

    @Override
    public void copyLaborInputDistributionData(UUID originalSessionId, UUID newSessionId) {
        List<LaborDistribution> laborDistributionList =
                laborDistributionRepository.findByTehoSessionId(originalSessionId);

        List<LaborDistribution> updatedLaborDistributionList =
                laborDistributionList.stream().map(eir -> eir.copy(newSessionId)).collect(Collectors.toList());

        laborDistributionRepository.saveAll(updatedLaborDistributionList);
    }

    @Override
    public List<WorkhoursDistributionInterval> getDistributionIntervals() {
        return (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
    }

    public List<LaborDistributionAggregatedData> getAggregatedDataForSessionAndFormation(Long formationId,
                                                                                         UUID sessionId) {
        return laborDistributionRepository.selectLaborDistributionAggregatedData(formationId, sessionId);
    }

    @Override
    @Transactional
    public void distribute(UUID sessionId) {
        List<Tree<Formation>> treeList = formationService.listHierarchy();
        for (Tree<Formation> t : treeList) {
            Map<Integer, Set<LaborDistributionAggregatedData>> sentUpper = new HashMap<>();
            for (int currentLevel = t.findLowestLevel(); currentLevel > 0; currentLevel--) {
                List<Tree.Node<Formation>> nodesWithLevel = t.findNodesWithLevel(currentLevel);
                for (Tree.Node<Formation> formationNode : nodesWithLevel) {
                    //Get formation's RepairFormations
                    Set<RepairFormation> repFormations = formationNode.getData().getRepairFormations();
                    //Get formation's equipment requiring repair
                    List<LaborDistributionAggregatedData> aggregatedDataForSession =
                            getAggregatedDataForSessionAndFormation(formationNode.getData().getId(), sessionId);

                    //Include data from previous level (unrepaired equipment)
                    aggregatedDataForSession.addAll(sentUpper.getOrDefault(currentLevel + 1, Collections.emptySet()));

                    List<EquipmentRFUDistribution> distributed =
                            distributeEquipmentPerRFU(sessionId, repFormations, aggregatedDataForSession);

                    List<LaborDistributionAggregatedData> undistributedData =
                            aggregatedDataForSession
                                    .stream()
                                    .filter(ldad -> ldad.getCount() > 0)
                                    .collect(Collectors.toList());

                    sentUpper.computeIfAbsent(currentLevel, (e) -> new HashSet<>()).addAll(undistributedData);

                    equipmentRFUDistributionRepository.saveAll(distributed);
                }
            }
        }
    }

    private List<EquipmentRFUDistribution> distributeEquipmentPerRFU(UUID sessionId,
                                                                     Set<RepairFormation> repFormations,
                                                                     List<LaborDistributionAggregatedData> aggregatedDataForSession) {
        List<EquipmentRFUDistribution> distributed = new ArrayList<>();
        Map<RepairFormationUnit, Map<Equipment, Double>> repairFormationUnitCapabilityMap = new HashMap<>();
        for (RepairFormation rf : repFormations) {
            for (LaborDistributionAggregatedData distributionAggregatedData : aggregatedDataForSession) {
                //If restoration level of RF is higher than interval's
                if (distributionAggregatedData
                        .getInterval()
                        .getRestorationType()
                        .getWeight() <= rf.getRepairFormationType().getRestorationType().getWeight()) {
                    EquipmentRFUDistribution distribution =
                            findCapableRFUAndDistributeEquipment(sessionId,
                                                                 repairFormationUnitCapabilityMap,
                                                                 distributionAggregatedData,
                                                                 rf);
                    if (distribution != null) {
                        distributed.add(distribution);
                    }
                }
            }
        }
        return distributed;
    }

    private EquipmentRFUDistribution findCapableRFUAndDistributeEquipment(UUID sessionId,
                                                                          Map<RepairFormationUnit, Map<Equipment, Double>> repairFormationUnitCapabilityMap,
                                                                          LaborDistributionAggregatedData distributionAggregatedData,
                                                                          RepairFormation rf) {
        //Find first RFU able to repair the equipment
        Optional<RepairFormationUnitRepairCapability> capableRFU =
                rf
                        .getRepairFormationUnitSet()
                        .stream()
                        .flatMap(rfu -> rfu
                                .getRepairCapabilities()
                                .stream()
                                .filter(rc -> rc.getEquipment()
                                                .equals(distributionAggregatedData.getEquipment()) &&
                                        rc.getCapability() > 0))
                        .findFirst();
        return capableRFU
                .flatMap(repairFormationUnitRepairCapability -> distributeEquipmentToRFU(sessionId,
                                                                                         repairFormationUnitCapabilityMap,
                                                                                         distributionAggregatedData,
                                                                                         repairFormationUnitRepairCapability))
                .orElse(null);
    }

    private Optional<EquipmentRFUDistribution> distributeEquipmentToRFU(UUID sessionId,
                                                                        Map<RepairFormationUnit, Map<Equipment, Double>> repairFormationUnitCapabilityMap,
                                                                        LaborDistributionAggregatedData distributionAggregatedData,
                                                                        RepairFormationUnitRepairCapability repairFormationUnitRepairCapability) {
        //Get current RFU capability
        Double capability =
                repairFormationUnitCapabilityMap
                        .computeIfAbsent(repairFormationUnitRepairCapability.getRepairFormationUnit(),
                                         (rfu) -> new HashMap<>())
                        .computeIfAbsent(repairFormationUnitRepairCapability.getEquipment(),
                                         (k) -> repairFormationUnitRepairCapability.getCapability());
        if (capability > 0 && distributionAggregatedData.getCount() > 0) {
            double repairing = distributionAggregatedData.getCount();
            double unable = 0.0;
            if (capability < distributionAggregatedData.getCount()) {
                repairing = capability;
                unable = distributionAggregatedData.getCount() - capability;
            }
            //Update equipment count requiring repair
            distributionAggregatedData.setCount(distributionAggregatedData.getCount() - repairing);
            //Update Capability of RFU
            RepairFormationUnit repairFormationUnit =
                    repairFormationUnitRepairCapability.getRepairFormationUnit();

            repairFormationUnitCapabilityMap
                    .get(repairFormationUnit)
                    .put(repairFormationUnitRepairCapability.getEquipment(), capability - repairing);
            //Create distribution data
            Formation formation = distributionAggregatedData.getFormation();
            Equipment equipment = distributionAggregatedData.getEquipment();
            WorkhoursDistributionInterval interval = distributionAggregatedData.getInterval();
            return Optional.of(
                    new EquipmentRFUDistribution(
                            new EquipmentRFUDistributionPK(formation.getId(),
                                                           equipment.getId(),
                                                           repairFormationUnit.getId(),
                                                           interval.getId(),
                                                           sessionId),
                            formation,
                            equipment,
                            repairFormationUnit,
                            interval,
                            repairing,
                            unable));
        }
        return Optional.empty();
    }

}
