package va.rit.teho.service.implementation.equipment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.*;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.equipment.EquipmentPerFormationRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentPerFormationServiceImpl implements EquipmentPerFormationService {

    private final CalculationService calculationService;

    private final FormationService formationService;
    private final EquipmentService equipmentService;

    private final EquipmentPerFormationRepository equipmentPerFormationRepository;
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository;

    public EquipmentPerFormationServiceImpl(CalculationService calculationService,
                                            FormationService formationService,
                                            EquipmentService equipmentService,
                                            EquipmentPerFormationRepository equipmentPerFormationRepository,
                                            EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository) {
        this.calculationService = calculationService;
        this.formationService = formationService;
        this.equipmentService = equipmentService;
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
    public void setEquipmentPerFormationFailureIntensity(
            UUID sessionId,
            Long formationId,
            Long equipmentId,
            Long repairTypeId,
            Long stageId,
            Integer intensity) {
        formationService.get(formationId);
        equipmentService.get(equipmentId);

        EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                new EquipmentPerFormationFailureIntensity(
                        new EquipmentPerFormationFailureIntensityPK(formationId,
                                                                    equipmentId,
                                                                    stageId,
                                                                    repairTypeId,
                                                                    sessionId),
                        intensity,
                        null);

        equipmentPerFormationFailureIntensityRepository.save(equipmentPerFormationFailureIntensity);
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

        Integer intensity =
                Optional.ofNullable(equipmentPerFormationFailureIntensityRepository
                                            .getFailureIntensityEntry(sessionId,
                                                                      formationId,
                                                                      equipmentId,
                                                                      stageId,
                                                                      repairTypeId))
                        .map(EquipmentPerFormationFailureIntensity::getIntensityPercentage)
                        .orElse(0);

        EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity =
                new EquipmentPerFormationFailureIntensity(
                        new EquipmentPerFormationFailureIntensityPK(formationId,
                                                                    equipmentId,
                                                                    stageId,
                                                                    repairTypeId,
                                                                    sessionId),
                        intensity,
                        dailyFailure);

        equipmentPerFormationFailureIntensityRepository.save(equipmentPerFormationFailureIntensity);
    }

    @Override
    public List<EquipmentPerFormation> getEquipmentInFormation(Long formationId, List<Long> equipmentIds) {
        return equipmentPerFormationRepository.findAllByFormationId(formationId, equipmentIds);
    }

    @Override
    public Map<Formation, Map<EquipmentSubType, List<EquipmentPerFormation>>> getTotalEquipmentInFormations(List<Long> equipmentIds) {
        return equipmentPerFormationRepository
                .findTotal(equipmentIds)
                .stream()
                .collect(Collectors.groupingBy(EquipmentPerFormation::getFormation,
                                               Collectors.groupingBy(epf -> epf.getEquipment().getEquipmentSubType())));
    }

    @Override
    public void updateAvgDailyFailureData(UUID sessionId, double coefficient) {
        List<EquipmentPerFormationFailureIntensity> updatedWithAvgDailyFailureData =
                equipmentPerFormationFailureIntensityRepository
                        .findAllWithIntensityAndAmount(sessionId)
                        .stream()
                        .map(equipmentPerFormationFailureIntensity -> {
                            double avgDailyFailure =
                                    calculationService.calculateAvgDailyFailure(
                                            equipmentPerFormationFailureIntensity.getEquipmentAmount(),
                                            equipmentPerFormationFailureIntensity.getFailureIntensity(),
                                            coefficient);
                            return new EquipmentPerFormationFailureIntensity(
                                    new EquipmentPerFormationFailureIntensityPK(equipmentPerFormationFailureIntensity.getFormationId(),
                                                                                equipmentPerFormationFailureIntensity.getEquipmentId(),
                                                                                equipmentPerFormationFailureIntensity.getStageId(),
                                                                                equipmentPerFormationFailureIntensity.getRepairTypeId(),
                                                                                sessionId),
                                    equipmentPerFormationFailureIntensity.getFailureIntensity(),
                                    avgDailyFailure);
                        })
                        .collect(Collectors.toList());
        equipmentPerFormationFailureIntensityRepository.saveAll(updatedWithAvgDailyFailureData);
    }

    @Override
    public Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> getTotalFailureIntensityData(
            UUID sessionId) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensityList =
                equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionId(sessionId);
        Map<Formation, Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>>> result = new HashMap<>();

        for (EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity : equipmentPerFormationFailureIntensityList) {
            Map<Stage, EquipmentPerFormationFailureIntensity> map = result
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getFormation(), (e) -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getEquipment(), (e) -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getRepairType(), (e) -> new HashMap<>());
            EquipmentPerFormationFailureIntensity existing = map.getOrDefault(equipmentPerFormationFailureIntensity.getStage(),
                                                                              null);
            if (existing != null) {
                existing.setAvgDailyFailure((existing.getAvgDailyFailure() == null ? 0.0 : existing.getAvgDailyFailure()) + (equipmentPerFormationFailureIntensity
                        .getAvgDailyFailure() == null ? 0.0 : equipmentPerFormationFailureIntensity.getAvgDailyFailure()));
            } else {
                map.put(equipmentPerFormationFailureIntensity.getStage(), equipmentPerFormationFailureIntensity);
            }
        }
        return result;
    }

    @Override
    public List<EquipmentPerFormation> list(Long formationId) {
        return equipmentPerFormationRepository.findAllByFormationId(formationId, null);
    }

    @Override
    public void addEquipmentToFormation(Long formationId, Long equipmentId, Long amount) {
        formationService.get(formationId);
        equipmentService.get(equipmentId);
        equipmentPerFormationRepository
                .findById(new EquipmentPerFormationPK(formationId, equipmentId))
                .ifPresent(epb -> {
                    throw new AlreadyExistsException("ВВСТ в ВЧ",
                                                     "(id ВЧ, id ВВСТ)",
                                                     "(" + formationId + ", " + equipmentId + ")");
                });

        this.equipmentPerFormationRepository.save(new EquipmentPerFormation(formationId, equipmentId, amount));
    }

    @Override
    public void addEquipmentToFormation(Long formationId, List<Long> equipmentId, int amount) {
        List<EquipmentPerFormation> equipmentPerFormationList = equipmentId
                .stream()
                .map(id -> new EquipmentPerFormation(formationId, id, (long) amount))
                .collect(Collectors.toList());

        equipmentPerFormationRepository.saveAll(equipmentPerFormationList);
    }

    @Override
    public Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long formationId,
            List<Long> equipmentIds) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensityList =
                equipmentPerFormationFailureIntensityRepository.findAllByTehoSessionIdAndFormationId(sessionId,
                                                                                                     formationId,
                                                                                                     equipmentIds);
        Map<Equipment, Map<RepairType, Map<Stage, EquipmentPerFormationFailureIntensity>>> result = new HashMap<>();

        for (EquipmentPerFormationFailureIntensity equipmentPerFormationFailureIntensity : equipmentPerFormationFailureIntensityList) {
            result
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getEquipment(), (e) -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getRepairType(), (e) -> new HashMap<>())
                    .put(equipmentPerFormationFailureIntensity.getStage(), equipmentPerFormationFailureIntensity);
        }
        return result;
    }

    @Override
    public List<EquipmentPerFormationFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                                   Long repairTypeId) {
        return equipmentPerFormationFailureIntensityRepository.findAllWithLaborInput(sessionId, repairTypeId);
    }

    @Override
    public void copyEquipmentPerFormationData(UUID originalSessionId, UUID newSessionId) {
        List<EquipmentPerFormationFailureIntensity> equipmentPerFormationFailureIntensities =
                equipmentPerFormationFailureIntensityRepository
                        .findAllByTehoSessionId(originalSessionId)
                        .stream()
                        .map(equipmentPerFormationFailureIntensity ->
                                     equipmentPerFormationFailureIntensity.copy(newSessionId))
                        .collect(Collectors.toList());

        equipmentPerFormationFailureIntensityRepository.saveAll(equipmentPerFormationFailureIntensities);
    }

    @Override
    @Transactional
    public void deleteEquipmentFromFormation(Long formationId, Long equipmentId) {
        EquipmentPerFormationPK id = new EquipmentPerFormationPK(formationId, equipmentId);
        if(equipmentPerFormationRepository.existsById(id)) {
            equipmentPerFormationRepository.deleteById(id);
            equipmentPerFormationFailureIntensityRepository.deleteByFormationIdAndEquipmentId(formationId, equipmentId);
        }
    }


}
