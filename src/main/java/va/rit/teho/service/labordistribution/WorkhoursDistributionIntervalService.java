package va.rit.teho.service.labordistribution;

import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

import java.util.List;

public interface WorkhoursDistributionIntervalService {

    List<WorkhoursDistributionInterval> listSorted();

    WorkhoursDistributionInterval get(Long id);

    WorkhoursDistributionInterval add(Integer lowerBound, Integer upperBound, Long restorationTypeId);

    WorkhoursDistributionInterval update(Long id,
                                         Integer lowerBound,
                                         Integer upperBound,
                                         Long restorationTypeId);

    WorkhoursDistributionInterval delete(Long id);


}
