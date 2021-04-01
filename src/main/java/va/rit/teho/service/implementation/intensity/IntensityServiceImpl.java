package va.rit.teho.service.implementation.intensity;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.intensity.Intensity;
import va.rit.teho.entity.intensity.IntensityData;
import va.rit.teho.entity.intensity.IntensityPK;
import va.rit.teho.repository.intensity.IntensityRepository;
import va.rit.teho.service.intensity.IntensityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntensityServiceImpl implements IntensityService {

    private final IntensityRepository intensityRepository;

    public IntensityServiceImpl(IntensityRepository intensityRepository) {
        this.intensityRepository = intensityRepository;
    }

    @Override
    public void setIntensities(Long operationId, Long equipmentId, Map<Long, Map<Long, Double>> stageRepairTypeMap) {
        List<Intensity> updatedIntensities = new ArrayList<>();
        stageRepairTypeMap.forEach((stageId, repairTypeMap) ->
                repairTypeMap.forEach((repairTypeId, value) -> {
                    IntensityPK id = new IntensityPK(operationId, equipmentId, stageId, repairTypeId);
                    Optional<Intensity> existing = intensityRepository.findById(id);
                    existing.ifPresent(intensity -> intensity.setValue(value));
                    updatedIntensities.add(existing.orElse(new Intensity(id, value)));
                }));
        intensityRepository.saveAll(updatedIntensities);
    }

    @Override
    public IntensityData getActiveIntensitiesGrouped() {
        return new IntensityData(
                intensityRepository
                        .findByOperationActiveIsTrue()
                        .stream()
                        .collect(Collectors.groupingBy(Intensity::getEquipment,
                                Collectors.groupingBy(Intensity::getStage,
                                        Collectors.toMap(Intensity::getRepairType, Intensity::getValue)))));
    }

    @Override
    public IntensityData getIntensitiesForOperation(Long operationId) {
        return new IntensityData(
                intensityRepository
                        .findByOperationId(operationId)
                        .stream()
                        .collect(Collectors.groupingBy(Intensity::getEquipment,
                                Collectors.groupingBy(Intensity::getStage,
                                        Collectors.toMap(Intensity::getRepairType, Intensity::getValue)))));
    }
}
