package va.rit.teho.service.implementation.equipment;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.base.Base;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.*;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentPerBaseFailureIntensityRepository;
import va.rit.teho.repository.equipment.EquipmentPerBaseRepository;
import va.rit.teho.service.base.BaseService;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.equipment.EquipmentPerBaseService;
import va.rit.teho.service.equipment.EquipmentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipmentPerBaseServiceImpl implements EquipmentPerBaseService {

    private final CalculationService calculationService;

    private final BaseService baseService;
    private final EquipmentService equipmentService;

    private final EquipmentPerBaseRepository equipmentPerBaseRepository;
    private final EquipmentPerBaseFailureIntensityRepository equipmentPerBaseFailureIntensityRepository;

    public EquipmentPerBaseServiceImpl(CalculationService calculationService,
                                       BaseService baseService,
                                       EquipmentService equipmentService,
                                       EquipmentPerBaseRepository equipmentPerBaseRepository,
                                       EquipmentPerBaseFailureIntensityRepository equipmentPerBaseFailureIntensityRepository) {
        this.calculationService = calculationService;
        this.baseService = baseService;
        this.equipmentService = equipmentService;
        this.equipmentPerBaseRepository = equipmentPerBaseRepository;
        this.equipmentPerBaseFailureIntensityRepository = equipmentPerBaseFailureIntensityRepository;
    }

    @Override
    public void updateEquipmentInBase(Long baseId, Long equipmentId, int amount) {
        baseService.get(baseId);
        equipmentService.get(equipmentId);
        EquipmentPerBase epb =
                equipmentPerBaseRepository.findById(new EquipmentPerBasePK(baseId, equipmentId))
                                          .orElseThrow(() -> new NotFoundException("ВЧ (id = " + baseId + ") не содержит ВВСТ (id = " + equipmentId + ")!"));
        epb.setAmount(amount);
        equipmentPerBaseRepository.save(epb);
    }

    @Override
    public void setEquipmentPerBaseFailureIntensity(
            UUID sessionId,
            Long baseId,
            Long equipmentId,
            Long repairTypeId,
            Long stageId,
            Integer intensity) {
        baseService.get(baseId);
        equipmentService.get(equipmentId);

        EquipmentPerBaseFailureIntensity equipmentPerBaseFailureIntensity =
                new EquipmentPerBaseFailureIntensity(
                        new EquipmentPerBaseFailureIntensityPK(baseId, equipmentId, stageId, repairTypeId, sessionId),
                        intensity,
                        null);
        equipmentPerBaseFailureIntensityRepository.save(equipmentPerBaseFailureIntensity);
    }

    @Override
    public List<EquipmentPerBase> getEquipmentInBase(Long baseId) {
        return equipmentPerBaseRepository.findAllByBaseId(baseId);
    }

    @Override
    public void updateAvgDailyFailureData(UUID sessionId, double coefficient) {
        List<EquipmentPerBaseFailureIntensity> updatedWithAvgDailyFailureData =
                equipmentPerBaseFailureIntensityRepository
                        .findAllWithIntensityAndAmount(sessionId)
                        .stream()
                        .map(equipmentPerBaseFailureIntensity -> {
                            double avgDailyFailure =
                                    calculationService.calculateAvgDailyFailure(
                                            equipmentPerBaseFailureIntensity.getEquipmentAmount(),
                                            equipmentPerBaseFailureIntensity.getFailureIntensity(),
                                            coefficient);
                            return new EquipmentPerBaseFailureIntensity(
                                    new EquipmentPerBaseFailureIntensityPK(equipmentPerBaseFailureIntensity.getBaseId(),
                                                                           equipmentPerBaseFailureIntensity.getEquipmentId(),
                                                                           equipmentPerBaseFailureIntensity.getStageId(),
                                                                           equipmentPerBaseFailureIntensity.getRepairTypeId(),
                                                                           sessionId),
                                    equipmentPerBaseFailureIntensity.getFailureIntensity(),
                                    avgDailyFailure);
                        })
                        .collect(Collectors.toList());
        equipmentPerBaseFailureIntensityRepository.saveAll(updatedWithAvgDailyFailureData);
    }

    @Override
    public void addEquipmentToBase(Long baseId, Long equipmentId, int amount) {
        baseService.get(baseId);
        equipmentService.get(equipmentId);
        equipmentPerBaseRepository.findById(new EquipmentPerBasePK(baseId, equipmentId)).ifPresent(epb -> {
            throw new AlreadyExistsException("ВВСТ в ВЧ", "(id ВЧ, id ВВСТ)", "(" + baseId + ", " + equipmentId + ")");
        });

        this.equipmentPerBaseRepository.save(new EquipmentPerBase(baseId, equipmentId, amount));
    }

    @Override
    public Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> getFailureIntensityData(
            UUID sessionId,
            Long baseId) {
        List<EquipmentPerBaseFailureIntensity> equipmentPerBaseFailureIntensityList =
                equipmentPerBaseFailureIntensityRepository.findAllByTehoSessionIdAndBaseId(sessionId, baseId);
        Map<Pair<Base, Equipment>, Map<RepairType, Map<Stage, EquipmentPerBaseFailureIntensity>>> result = new HashMap<>();

        for (EquipmentPerBaseFailureIntensity equipmentPerBaseFailureIntensity : equipmentPerBaseFailureIntensityList) {
            result
                    .computeIfAbsent(Pair.of(equipmentPerBaseFailureIntensity.getBase(),
                                             equipmentPerBaseFailureIntensity.getEquipment()), (e) -> new HashMap<>())
                    .computeIfAbsent(equipmentPerBaseFailureIntensity.getRepairType(), (e) -> new HashMap<>())
                    .put(equipmentPerBaseFailureIntensity.getStage(), equipmentPerBaseFailureIntensity);
        }
        return result;
    }

    @Override
    public List<EquipmentPerBaseFailureIntensityAndLaborInput> listWithIntensityAndLaborInput(UUID sessionId,
                                                                                              Long repairTypeId) {
        return equipmentPerBaseFailureIntensityRepository.findAllWithLaborInput(sessionId, repairTypeId);
    }

    @Override
    public void copyEquipmentPerBaseData(UUID originalSessionId, UUID newSessionId) {
        List<EquipmentPerBaseFailureIntensity> equipmentPerBaseFailureIntensities =
                equipmentPerBaseFailureIntensityRepository
                        .findAllByTehoSessionId(originalSessionId)
                        .stream()
                        .map(equipmentPerBaseFailureIntensity -> equipmentPerBaseFailureIntensity.copy(newSessionId))
                        .collect(Collectors.toList());

        equipmentPerBaseFailureIntensityRepository.saveAll(equipmentPerBaseFailureIntensities);
    }


}
