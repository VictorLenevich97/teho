package va.rit.teho.service.implementation.common;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.common.StageRepository;
import va.rit.teho.service.common.StageService;

import java.util.List;

@Service
public class StageServiceImpl implements StageService {

    private final StageRepository stageRepository;

    public StageServiceImpl(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public List<Stage> list() {
        return (List<Stage>) stageRepository.findAll();
    }

    @Override
    public Stage get(Long id) {
        return stageRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Этап операции с id = " + id + " не найден!"));
    }

    @Override
    public Stage add(int stageNum) {
        stageRepository.findByStageNum(stageNum).ifPresent(stage -> {
            throw new AlreadyExistsException("Этап операции", "номер", stageNum);
        });
        long newId = stageRepository.getMaxId() + 1;
        return stageRepository.save(new Stage(newId, stageNum));
    }

    @Override
    public void delete(Long id) {
        stageRepository.deleteById(id);
    }
}
