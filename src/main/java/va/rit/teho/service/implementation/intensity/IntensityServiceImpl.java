package va.rit.teho.service.implementation.intensity;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.intensity.ActiveIntensityData;
import va.rit.teho.entity.intensity.Intensity;
import va.rit.teho.repository.intensity.IntensityRepository;
import va.rit.teho.service.intensity.IntensityService;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntensityServiceImpl implements IntensityService {

    private final IntensityRepository intensityRepository;

    public IntensityServiceImpl(IntensityRepository intensityRepository) {
        this.intensityRepository = intensityRepository;
    }

    @Override
    public void setIntensities(Long operationId, Long equipmentId, Map<Long, Map<Long, Double>> repairTypeStageMap) {

    }

    @Override
    public ActiveIntensityData getActiveIntensitiesGrouped() {
        return new ActiveIntensityData(
                intensityRepository
                        .listActive()
                        .stream()
                        .collect(Collectors.groupingBy(Intensity::getEquipment,
                                Collectors.groupingBy(Intensity::getRepairType,
                                        Collectors.toMap(Intensity::getStage, Intensity::getValue)))));
    }
}
