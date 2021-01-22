package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentFailurePerRepairTypeAmount;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerRestorationTypeAmount;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.labordistribution.*;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapability;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.labordistribution.EquipmentRFUDistributionRepository;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;
import va.rit.teho.service.labordistribution.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentRFUDistributionServiceImpl implements EquipmentRFUDistributionService {

    private final FormationService formationService;
    private final LaborInputDistributionService laborInputDistributionService;

    private final EquipmentPerFormationService equipmentPerFormationService;
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository;

    private final EquipmentRFUDistributionRepository equipmentRFUDistributionRepository;

    public EquipmentRFUDistributionServiceImpl(FormationService formationService,
                                               LaborInputDistributionService laborInputDistributionService,
                                               EquipmentPerFormationService equipmentPerFormationService,
                                               EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository,
                                               EquipmentRFUDistributionRepository equipmentRFUDistributionRepository) {
        this.formationService = formationService;
        this.laborInputDistributionService = laborInputDistributionService;
        this.equipmentPerFormationService = equipmentPerFormationService;
        this.equipmentPerFormationFailureIntensityRepository = equipmentPerFormationFailureIntensityRepository;
        this.equipmentRFUDistributionRepository = equipmentRFUDistributionRepository;
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
                            laborInputDistributionService.listAggregatedDataForSessionAndFormation(formationNode
                                                                                                           .getData()
                                                                                                           .getId(),
                                                                                                   sessionId);

                    //Include data from previous level (unrepaired equipment)
                    aggregatedDataForSession.addAll(sentUpper.getOrDefault(currentLevel + 1, Collections.emptySet()));

                    List<EquipmentRFUDistribution> distributed =
                            distributeEquipmentPerRFU(sessionId, repFormations, aggregatedDataForSession);

                    List<LaborDistributionAggregatedData> undistributedData =
                            aggregatedDataForSession
                                    .stream()
                                    .filter(ldad -> ldad.getCount() > 0)
                                    .collect(Collectors.toList());

                    sentUpper.computeIfAbsent(currentLevel, e -> new HashSet<>()).addAll(undistributedData);

                    equipmentRFUDistributionRepository.saveAll(distributed);
                }
            }
        }
    }

    @Override
    public void copy(UUID oldSessionId, UUID newSessionId) {
        List<EquipmentRFUDistribution> equipmentRFUDistributions =
                equipmentRFUDistributionRepository.findByTehoSessionId(oldSessionId);

        List<EquipmentRFUDistribution> updated =
                equipmentRFUDistributions.stream().map(d -> d.copy(newSessionId)).collect(Collectors.toList());

        equipmentRFUDistributionRepository.saveAll(updated);
    }

    @Override
    public List<EquipmentRFUDistribution> listRFUDistributedEquipment(Long repairFormationUnitId, UUID sessionId) {
        return equipmentRFUDistributionRepository.findByRepairFormationUnitIdAndTehoSessionId(repairFormationUnitId,
                                                                                              sessionId);
    }

    @Override
    public List<EquipmentPerFormationDistributionData> listDistributionDataForFormation(UUID sessionId,
                                                                                        Long formationId) {
        List<EquipmentPerFormation> list = equipmentPerFormationService.list(formationId);
        Map<Equipment, Map<RepairType, Double>> equipmentFailurePerRepairTypes =
                getEquipmentFailurePerRepairTypeMap(sessionId, formationId);

        Map<Equipment, Map<RestorationType, Double>> equipmentInRepairPerRestorationType =
                getEquipmentInRepairPerRestorationTypeMap(sessionId, formationId);

        return list
                .stream()
                .map(epf -> getEquipmentPerFormationDistributionData(epf,
                                                                     equipmentFailurePerRepairTypes,
                                                                     equipmentInRepairPerRestorationType))
                .collect(Collectors.toList());
    }

    private EquipmentPerFormationDistributionData getEquipmentPerFormationDistributionData(
            EquipmentPerFormation epf,
            Map<Equipment, Map<RepairType, Double>> equipmentFailurePerRepairTypes,
            Map<Equipment, Map<RestorationType, Double>> equipmentInRepairPerRestorationType) {
        Map<RepairType, Double> repairTypeMap =
                equipmentFailurePerRepairTypes.getOrDefault(epf.getEquipment(), Collections.emptyMap());

        return new EquipmentPerFormationDistributionData(
                epf.getEquipment(),
                epf.getAmount(),
                repairTypeMap.values().stream().mapToDouble(Double::doubleValue).sum(),
                repairTypeMap,
                equipmentInRepairPerRestorationType.getOrDefault(epf.getEquipment(), Collections.emptyMap()));
    }

    private Map<Equipment, Map<RestorationType, Double>> getEquipmentInRepairPerRestorationTypeMap(UUID sessionId,
                                                                                                   Long formationId) {
        return equipmentRFUDistributionRepository
                .listRepairingAmountPerRestorationType(sessionId, formationId)
                .stream()
                .collect(Collectors.groupingBy(EquipmentPerRestorationTypeAmount::getEquipment,
                                               Collectors.groupingBy(EquipmentPerRestorationTypeAmount::getRestorationType,
                                                                     Collectors.mapping(
                                                                             EquipmentPerRestorationTypeAmount::getAmount,
                                                                             Collectors.averagingDouble(Double::doubleValue)))));
    }

    private Map<Equipment, Map<RepairType, Double>> getEquipmentFailurePerRepairTypeMap(UUID sessionId,
                                                                                        Long formationId) {
        return equipmentPerFormationFailureIntensityRepository
                .listFailureDataPerRepairType(sessionId, formationId)
                .stream()
                .collect(Collectors.groupingBy(EquipmentFailurePerRepairTypeAmount::getEquipment,
                                               Collectors.groupingBy(EquipmentFailurePerRepairTypeAmount::getRepairType,
                                                                     Collectors.mapping(
                                                                             EquipmentFailurePerRepairTypeAmount::getAmount,
                                                                             Collectors.averagingDouble(Double::doubleValue)))));
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
                                         rfu -> new HashMap<>())
                        .computeIfAbsent(repairFormationUnitRepairCapability.getEquipment(),
                                         k -> repairFormationUnitRepairCapability.getCapability());
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
