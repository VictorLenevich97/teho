package va.rit.teho.service.implementation.common;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.Stage;
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
}
