package by.varb.teho.utils;

import by.varb.teho.entity.RepairType;
import by.varb.teho.entity.RestorationType;
import by.varb.teho.enums.RepairTypeEnum;
import by.varb.teho.enums.RestorationTypeEnum;
import by.varb.teho.repository.RepairTypeRepository;
import by.varb.teho.repository.RestorationTypeRepository;
import by.varb.teho.repository.WorkhoursDistributionIntervalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataLoader.class);

    private final RepairTypeRepository repairTypeRepository;
    private final RestorationTypeRepository restorationTypeRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;

    public InitialDataLoader(RepairTypeRepository repairTypeRepository,
                             RestorationTypeRepository restorationTypeRepository,
                             WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository) {
        this.repairTypeRepository = repairTypeRepository;
        this.restorationTypeRepository = restorationTypeRepository;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
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

    private Iterable<RestorationType> loadRestorationTypes() {
        LOGGER.info("Загружаются типы восстановления...");
        List<String> restorationTypes = ((List<RestorationType>) this.restorationTypeRepository.findAll())
                .stream()
                .map(RestorationType::getName)
                .collect(Collectors.toList());
        Iterable<RestorationType> types =
                this.restorationTypeRepository.saveAll(Arrays
                                                               .stream(RestorationTypeEnum.values())
                                                               .map(restorationTypeEnum -> new RestorationType(
                                                                       restorationTypeEnum.getName()))
                                                               .filter(rt -> !restorationTypes.contains(rt.getName()))
                                                               .collect(Collectors.toList()));
        LOGGER.info("Типы восстановления загружены!");
        return types;
    }

    @Override
    public void run(ApplicationArguments args) {
        loadRepairTypes();
        loadRestorationTypes();
    }
}
