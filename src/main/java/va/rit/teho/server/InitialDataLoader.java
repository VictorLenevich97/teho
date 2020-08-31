package va.rit.teho.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import va.rit.teho.entity.RepairType;
import va.rit.teho.entity.RestorationType;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.enums.RestorationTypeEnum;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.repository.RestorationTypeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataLoader.class);

    private final RepairTypeRepository repairTypeRepository;
    private final RestorationTypeRepository restorationTypeRepository;

    public InitialDataLoader(RepairTypeRepository repairTypeRepository,
                             RestorationTypeRepository restorationTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
        this.restorationTypeRepository = restorationTypeRepository;
    }

    private void loadRepairTypes() {
        LOGGER.info("Загружаеются типы ремонта...");
        List<String> repairTypes =
                ((List<RepairType>) this.repairTypeRepository.findAll())
                        .stream()
                        .map(RepairType::getName)
                        .collect(Collectors.toList());
        this.repairTypeRepository.saveAll(
                Arrays
                        .stream(RepairTypeEnum.values())
                        .map(repairTypeEnum -> new RepairType(repairTypeEnum.getName()))
                        .filter(rt -> !repairTypes.contains(rt.getName()))
                        .collect(Collectors.toList()));
        LOGGER.info("Типы ремонта загружены!");
    }

    private void loadRestorationTypes() {
        LOGGER.info("Загружаются типы восстановления...");
        List<String> restorationTypes = ((List<RestorationType>) this.restorationTypeRepository.findAll())
                .stream()
                .map(RestorationType::getName)
                .collect(Collectors.toList());
        this.restorationTypeRepository.saveAll(Arrays
                                                       .stream(RestorationTypeEnum.values())
                                                       .map(restorationTypeEnum -> new RestorationType(
                                                               restorationTypeEnum.getName()))
                                                       .filter(rt -> !restorationTypes.contains(rt.getName()))
                                                       .collect(Collectors.toList()));
        LOGGER.info("Типы восстановления загружены!");
    }

    @Override
    public void run(ApplicationArguments args) {
        loadRepairTypes();
        loadRestorationTypes();
    }
}
