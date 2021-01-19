package va.rit.teho.service.implementation.labordistribution;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.labordistribution.RestorationTypeRepository;
import va.rit.teho.service.labordistribution.RestorationTypeService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RestorationTypeServiceImpl implements RestorationTypeService {

    private final RestorationTypeRepository restorationTypeRepository;

    public RestorationTypeServiceImpl(RestorationTypeRepository restorationTypeRepository) {
        this.restorationTypeRepository = restorationTypeRepository;
    }

    @Override
    public List<RestorationType> list() {
        return StreamSupport
                .stream(restorationTypeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public RestorationType add(String name, int weight) {
        if (!restorationTypeRepository.findByNameIgnoreCase(name).isEmpty()) {
            throw new AlreadyExistsException("Тип восстановления", "название", name);
        }

        long newId = restorationTypeRepository.getMaxId() + 1;

        return restorationTypeRepository.save(new RestorationType(newId, name, weight));
    }

    @Override
    public RestorationType update(Long id, String name, int weight) {
        if (!restorationTypeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Тип восстановления с id = " + id + " не найден!");
        }

        return restorationTypeRepository.save(new RestorationType(id, name, weight));
    }
}
