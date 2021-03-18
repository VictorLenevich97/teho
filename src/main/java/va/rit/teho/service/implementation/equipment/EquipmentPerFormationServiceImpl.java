package va.rit.teho.service.implementation.equipment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.*;
import va.rit.teho.entity.equipment.combined.EquipmentPerFormationFailureIntensityAndLaborInput;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.intensity.IntensityData;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.equipment.EquipmentPerFormationRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.intensity.IntensityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<EquipmentPerFormation> getEquipmentInFormation(Long formationId, List<Long> equipmentIds) {
        return equipmentPerFormationRepository.findAllByFormationId(formationId, equipmentIds);
    }

    @Override
    public List<EquipmentPerFormation> getEquipmentInAllFormations(List<Long> equipmentIds) {
        return equipmentPerFormationRepository.findTotal(equipmentIds);
    }

    @Override
    public Map<EquipmentType, List<EquipmentPerFormation>> getGroupedEquipmentInFormation(Long formationId,
                                                                                          List<Long> equipmentIds) {
        return equipmentPerFormationRepository
                .findAllByFormationId(formationId, equipmentIds)
                .stream()
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
        List<EquipmentPerFormationFailureIntensity> updatedWithAvgDailyFailureData =
                equipmentPerFormationFailureIntensityRepository
                        .findAllByTehoSessionIdAndFormationId(sessionId, formationId, null)
                        .stream()
                        .map(equipmentPerFormationFailureIntensity -> {
                            double avgDailyFailure =
                                    calculationService.calculateAvgDailyFailure(
                                            equipmentPerFormationFailureIntensity
                                                    .getEquipmentPerFormationWithRepairTypeId()
                                                    .getEquipmentPerFormation()
                                                    .getAmount(),
                                            activeIntensitiesGrouped.get(
                                                    equipmentPerFormationFailureIntensity.getEquipment(),
                                                    equipmentPerFormationFailureIntensity.getRepairType(),
                                                    equipmentPerFormationFailureIntensity.getStage()),
                                            coefficient);
                            return equipmentPerFormationFailureIntensity.setAvgDailyFailure(avgDailyFailure);
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
            result
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getFormation(), e -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getEquipment(), e -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getRepairType(), e -> new HashMap<>())
                    .put(equipmentPerFormationFailureIntensity.getStage(), equipmentPerFormationFailureIntensity);

        }
        return result;
    }

    @Override
    public EquipmentPerFormation addEquipmentToFormation(Long formationId, Long equipmentId, Long amount) {
        Formation formation = formationService.get(formationId);
        Equipment equipment = equipmentService.get(equipmentId);
        equipmentPerFormationRepository
                .findById(new EquipmentPerFormationPK(formationId, equipmentId))
                .ifPresent(epb -> {
                    throw new AlreadyExistsException("ВВСТ в ВЧ",
                                                     "(id ВЧ, id ВВСТ)",
                                                     "(" + formationId + ", " + equipmentId + ")");
                });

        return this.equipmentPerFormationRepository.save(new EquipmentPerFormation(equipment, formation, amount));
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
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getEquipment(), e -> new HashMap<>())
                    .computeIfAbsent(equipmentPerFormationFailureIntensity.getRepairType(), e -> new HashMap<>())
                    .put(equipmentPerFormationFailureIntensity.getStage(), equipmentPerFormationFailureIntensity);
        }
        return result;
    }

    @Override
    public List<EquipmentPerFormationFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                                   Long repairTypeId,
                                                                                                   List<Long> equipmentIds,
                                                                                                   List<Long> formationIds) {
        return equipmentPerFormationFailureIntensityRepository.findAllWithLaborInput(sessionId,
                                                                                     repairTypeId,
                                                                                     equipmentIds,
                                                                                     formationIds);
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
        if (equipmentPerFormationRepository.existsById(id)) {
            equipmentPerFormationRepository.deleteById(id);
        }
    }


}
