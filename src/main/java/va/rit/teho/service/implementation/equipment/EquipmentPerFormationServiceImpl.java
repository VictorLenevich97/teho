package va.rit.teho.service.implementation.equipment;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.*;
import va.rit.teho.entity.equipment.combined.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.intensity.IntensityData;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.equipment.EquipmentPerFormationRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.intensity.IntensityService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class EquipmentPerFormationServiceImpl implements EquipmentPerFormationService {

    private final CalculationService calculationService;

    private final FormationService formationService;
    private final EquipmentService equipmentService;
    private final IntensityService intensityService;

    private final EquipmentPerFormationRepository equipmentPerFormationRepository;
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository;

    public EquipmentPerFormationServiceImpl(CalculationService calculationService,
                                            FormationService formationService,
                                            EquipmentService equipmentService,
                                            IntensityService intensityService,
                                            EquipmentPerFormationRepository equipmentPerFormationRepository,
                                            EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository) {
        this.calculationService = calculationService;
        this.formationService = formationService;
        this.equipmentService = equipmentService;
        this.intensityService = intensityService;
        this.equipmentPerFormationRepository = equipmentPerFormationRepository;
        this.equipmentPerFormationFailureIntensityRepository = equipmentPerFormationFailureIntensityRepository;
    }

    @Override
    public EquipmentPerFormation updateEquipmentInFormation(Long formationId, Long equipmentId, int amount) {
        formationService.get(formationId);
        equipmentService.get(equipmentId);
        EquipmentPerFormation epb =
                equipmentPerFormationRepository
                        .findById(new EquipmentPerFormationPK(formationId, equipmentId))
                        .orElseThrow(() -> new NotFoundException("ВЧ (id = " + formationId + ") не содержит ВВСТ (id = " + equipmentId + ")!"));
        epb.setAmount(amount);
        return equipmentPerFormationRepository.save(epb);
    }

    @Override
    public void setEquipmentPerFormationDailyFailure(UUID sessionId,
                                                     Long formationId,
                                                     Long equipmentId,
                                                     Long repairTypeId,
                                                     Long stageId,
                                                     Double dailyFailure) {
        formationService.get(formationId);
        equipmentService.get(equipmentId);

        EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                equipmentPerFormationFailureIntensityRepository
                        .find(sessionId, formationId, equipmentId, stageId, repairTypeId)
                        .map(epffi -> epffi.setAvgDailyFailure(dailyFailure))
                        .orElse(new EquipmentPerFormationFailureIntensity(sessionId,
                                formationId,
                                equipmentId,
                                stageId,
                                repairTypeId,
                                dailyFailure));


        equipmentPerFormationFailureIntensityRepository.save(equipmentPerFormationFailureIntensity);
    }

    @Override
    public List<EquipmentPerFormation> getEquipmentInFormation(Long formationId) {
        return equipmentPerFormationRepository.findAllByFormationIdOrderByEquipmentIdAsc(formationId);
    }

    @Override
    public List<EquipmentPerFormation> getEquipmentInAllFormations() {
        return StreamSupport.stream(equipmentPerFormationRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Map<EquipmentType, List<EquipmentPerFormation>> getGroupedEquipmentInFormation(Long formationId,
                                                                                          String equipmentName) {
        List<EquipmentPerFormation> equipmentInFormations =
                equipmentName == null || equipmentName.isEmpty() ?
                        equipmentPerFormationRepository.findAllByFormationIdOrderByEquipmentIdAsc(formationId) :
                        equipmentPerFormationRepository.findByFormationIdAndEquipmentNameLikeIgnoreCaseOrderByEquipmentIdAsc(formationId, equipmentName);
        return equipmentInFormations
                .stream()
                .filter(equipmentPerFormation -> equipmentPerFormation.getAmount() > 0)
                .collect(Collectors.groupingBy(epf -> epf.getEquipment().getEquipmentType()));
    }

    @Override
    public Map<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> getTotalGroupedEquipmentInFormations(List<Long> equipmentIds) {
        return equipmentPerFormationRepository
                .findTotal(equipmentIds)
                .stream()
                .collect(Collectors.groupingBy(EquipmentPerFormation::getFormation,
                        Collectors.groupingBy(epf -> epf.getEquipment().getEquipmentType())));
    }

    @Override
    public void calculateAndSetEquipmentPerFormationDailyFailure(UUID sessionId,
                                                                 Long formationId,
                                                                 double coefficient) {
        IntensityData activeIntensitiesGrouped = intensityService.getActiveIntensitiesGrouped();

        List<Long> equipmentIds =
                activeIntensitiesGrouped.getEquipmentSet().stream().map(Equipment::getId).collect(Collectors.toList());

        MultiKeyMap<Long, EquipmentPerFormationFailureIntensity> epfMap = getFailureIntensitiesAsMap(sessionId, formationId, equipmentIds);

        Map<Long, Integer> equipmentAmountMap = getEquipmentAmountData(formationId, activeIntensitiesGrouped);

        List<EquipmentPerFormationFailureIntensity> updatedWithAvgDailyFailureData = new ArrayList<>();

        activeIntensitiesGrouped
                .getData()
                .entrySet()
                .stream()
                .filter(equipmentStageMapEntry ->
                        equipmentAmountMap.containsKey(equipmentStageMapEntry.getKey().getId()) &&
                                equipmentAmountMap.get(equipmentStageMapEntry.getKey().getId()) > 0)
                .forEach(equipmentStageMapEntry ->
                        equipmentStageMapEntry.getValue().forEach((stage, repairTypeMap) ->
                                repairTypeMap.forEach((repairType, intensity) -> {
                                    Long equipmentId = equipmentStageMapEntry.getKey().getId();
                                    Integer equipmentAmount = equipmentAmountMap.get(equipmentId);
                                    Long stageId = stage.getId();
                                    Long repairTypeId = repairType.getId();


                                    double avgDailyFailure =
                                            calculationService.calculateAvgDailyFailure(equipmentAmount, intensity, coefficient);

                                    EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                                            Optional.ofNullable(epfMap.get(equipmentId, stageId, repairTypeId))
                                                    .map(epffi -> epffi.setAvgDailyFailure(avgDailyFailure))
                                                    .orElse(new EquipmentPerFormationFailureIntensity(
                                                            sessionId,
                                                            formationId,
                                                            equipmentId,
                                                            stageId,
                                                            repairTypeId,
                                                            avgDailyFailure));

                                    updatedWithAvgDailyFailureData.add(equipmentPerFormationFailureIntensity);
                                })));
        equipmentPerFormationFailureIntensityRepository.saveAll(updatedWithAvgDailyFailureData);
    }

    private Map<Long, Integer> getEquipmentAmountData(Long formationId, IntensityData activeIntensitiesGrouped) {
        List<EquipmentPerFormationPK> epfKeys = new ArrayList<>();

        activeIntensitiesGrouped.getData().forEach((equipment, stageMap) ->
                stageMap.forEach((stage, repairTypeMap) ->
                        repairTypeMap.forEach((repairType, intensity) ->
                                epfKeys.add(new EquipmentPerFormationPK(formationId, equipment.getId())))));

        return StreamSupport
                .stream(equipmentPerFormationRepository.findAllById(epfKeys).spliterator(), true)
                .collect(Collectors.toMap(epf -> epf.getEquipment().getId(), EquipmentPerFormation::getAmount));
    }

    private MultiKeyMap<Long, EquipmentPerFormationFailureIntensity> getFailureIntensitiesAsMap(UUID sessionId, Long formationId, List<Long> equipmentIds) {
        MultiKeyMap<Long, EquipmentPerFormationFailureIntensity> epfMap = new MultiKeyMap<>();
        List<EquipmentPerFormationFailureIntensity> intensities =
                equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionIdAndFormationId(sessionId, formationId, equipmentIds);
        for (EquipmentPerFormationFailureIntensity intensity : intensities) {
            epfMap.put(intensity.getEquipment().getId(), intensity.getStage().getId(), intensity.getRepairType().getId(), intensity);
        }
        return epfMap;
    }

    @Override
    public List<EquipmentPerFormation> getEquipmentInFormation(Long formationId, String nameFilter) {
        if (nameFilter == null || nameFilter.isEmpty()) {
            return getEquipmentInFormation(formationId);
        } else {
            return equipmentPerFormationRepository.findByFormationIdAndEquipmentNameLikeIgnoreCaseOrderByEquipmentIdAsc(formationId, "%" + nameFilter + "%");
        }
    }

    @Override
    public Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> getTotalFailureIntensityData(
            UUID sessionId) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensityList =
                equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionId(sessionId);

        return equipmentPerFormationFailureIntensityList
                .stream()
                .collect(Collectors.groupingBy(EquipmentPerFormationFailureIntensity::getFormation,
                        Collectors.groupingBy(EquipmentPerFormationFailureIntensity::getEquipment,
                                Collectors.groupingBy(EquipmentPerFormationFailureIntensity::getRepairType,
                                        Collectors.toMap(EquipmentPerFormationFailureIntensity::getStage, Function.identity())))));
    }

    @Override
    public EquipmentPerFormation addEquipmentToFormation(Long formationId, Long equipmentId, Long amount) {
        Formation formation = formationService.get(formationId);
        Equipment equipment = equipmentService.get(equipmentId);
        equipmentPerFormationRepository
                .findById(new EquipmentPerFormationPK(formationId, equipmentId))
                .ifPresent(epb -> {
                    throw equipmentIsPresentInFormation(epb.getFormation(), epb.getEquipment());
                });

        return this.equipmentPerFormationRepository.save(new EquipmentPerFormation(equipment, formation, amount));
    }

    private AlreadyExistsException equipmentIsPresentInFormation(Formation formation, Equipment equipment) {
        return new AlreadyExistsException("ВВСТ \"" + equipment.getName() + "\" уже существует в Формировании \"" + formation.getFullName() + "\"");
    }

    @Override
    public List<EquipmentPerFormation> addEquipmentToFormation(Long formationId, List<Long> equipmentIds, Long amount) {
        Formation formation = formationService.get(formationId);
        List<Equipment> equipmentList = equipmentService.list(equipmentIds);

        if (equipmentList.size() != equipmentIds.size()) {
            throw new IncorrectParamException("Невозможно добавить: один (или более) ВВСТ не существуют в БД!");
        }

        equipmentPerFormationRepository
                .findAllByFormationId(formationId, equipmentIds)
                .stream()
                .findAny()
                .ifPresent(epf -> {
                    throw equipmentIsPresentInFormation(epf.getFormation(), epf.getEquipment());
                });

        List<EquipmentPerFormation> equipmentPerFormationList =
                equipmentList
                        .stream()
                        .map(equipment -> new EquipmentPerFormation(equipment, formation, amount))
                        .collect(Collectors.toList());

        Iterable<EquipmentPerFormation> saved = equipmentPerFormationRepository.saveAll(equipmentPerFormationList);

        return StreamSupport
                .stream(saved.spliterator(), true)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long formationId,
            String equipmentName) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensityList =
                equipmentName == null || equipmentName.isEmpty() ?
                        equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionIdAndFormationId(sessionId, formationId, (List<Long>) null) :
                        equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionIdAndFormationId(sessionId, formationId, equipmentName);

        return equipmentPerFormationFailureIntensityList
                .stream()
                .collect(Collectors.groupingBy(EquipmentPerFormationFailureIntensity::getEquipment,
                        Collectors.groupingBy(EquipmentPerFormationFailureIntensity::getRepairType,
                                Collectors.toMap(EquipmentPerFormationFailureIntensity::getStage, Function.identity()))));
    }

    @Override
    public List<EquipmentPerFormationFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                                   Long repairTypeId,
                                                                                                   List<Long> equipmentIds,
                                                                                                   List<Long> formationIds) {
        return equipmentPerFormationFailureIntensityRepository.findAllWithLaborInput(sessionId, repairTypeId, equipmentIds, formationIds);
    }

    @Override
    public void copyEquipmentPerFormationData(UUID originalSessionId, UUID newSessionId) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensities =
                equipmentPerFormationFailureIntensityRepository
                        .findAllByTehoSessionId(originalSessionId)
                        .stream()
                        .map(epffi -> epffi.copy(newSessionId))
                        .collect(Collectors.toList());

        equipmentPerFormationFailureIntensityRepository.saveAll(equipmentPerFormationFailureIntensities);
    }

    @Override
    @Transactional
    public void deleteEquipmentFromFormation(Long formationId, Long equipmentId) {
        EquipmentPerFormationPK id = new EquipmentPerFormationPK(formationId, equipmentId);
        if (equipmentPerFormationRepository.existsById(id)) {
            equipmentPerFormationRepository.deleteById(id);
        }
    }


}
