package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.labordistribution.WorkhoursDistributionIntervalRepository;
import va.rit.teho.service.labordistribution.RestorationTypeService;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;

import java.util.List;
import java.util.Optional;

@Service
public class WorkhoursDistributionIntervalServiceImpl implements WorkhoursDistributionIntervalService {

    private final RestorationTypeService restorationTypeService;

    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public WorkhoursDistributionIntervalServiceImpl(RestorationTypeService restorationTypeService,
                                                    WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository) {
        this.restorationTypeService = restorationTypeService;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
    }

    @Override
    public List<WorkhoursDistributionInterval> list() {
        return (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
    }

    @Override
    public WorkhoursDistributionInterval get(Long id) {
        return workhoursDistributionIntervalRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Интервал распределения с id = \"" + id + "\" не найден!"));
    }

    @Override
    public WorkhoursDistributionInterval add(Integer lowerBound, Integer upperBound, Long restorationTypeId) {
        RestorationType restorationType = restorationTypeService.get(restorationTypeId);
        Optional<WorkhoursDistributionInterval> existing =
                workhoursDistributionIntervalRepository.findByLowerBoundAndUpperBoundAndRestorationType(lowerBound,
                                                                                                        upperBound,
                                                                                                        restorationType);
        if (existing.isPresent()) {
            throw new AlreadyExistsException("Интервал распределения",
                                             "(нижняя граница, верхняя граница, тип восстановления)",
                                             "(" + lowerBound + ", " + upperBound + ", " + restorationType.getName() + ")");
        }
        long newId = workhoursDistributionIntervalRepository.getMaxId() + 1;
        WorkhoursDistributionInterval intervalToAdd = new WorkhoursDistributionInterval(newId,
                                                                                        lowerBound,
                                                                                        upperBound,
                                                                                        restorationType);
        return workhoursDistributionIntervalRepository.save(intervalToAdd);
    }

    @Override
    public WorkhoursDistributionInterval update(Long id,
                                                Integer lowerBound,
                                                Integer upperBound,
                                                Long restorationTypeId) {
        WorkhoursDistributionInterval existing = get(id);
        RestorationType restorationType = restorationTypeService.get(restorationTypeId);
        existing.setLowerBound(lowerBound);
        existing.setUpperBound(upperBound);
        existing.setRestorationType(restorationType);
        return workhoursDistributionIntervalRepository.save(existing);
    }

    @Override
    public WorkhoursDistributionInterval delete(Long id) {
        WorkhoursDistributionInterval interval = get(id);
        workhoursDistributionIntervalRepository.deleteById(id);
        return interval;
    }
}
