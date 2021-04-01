package va.rit.teho.service.intensity;

import va.rit.teho.entity.intensity.IntensityData;

import java.util.Map;

public interface IntensityService {

    void setIntensities(Long operationId, Long equipmentId, Map<Long, Map<Long, Double>> repairTypeStageMap);

    IntensityData getActiveIntensitiesGrouped();

    IntensityData getIntensitiesForOperation(Long operationId);

}
