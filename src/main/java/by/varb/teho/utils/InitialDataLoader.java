package by.varb.teho.utils;

import by.varb.teho.entity.RepairType;
import by.varb.teho.enums.RepairTypeEnum;
import by.varb.teho.repository.RepairTypeRepository;
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

    public InitialDataLoader(RepairTypeRepository repairTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
    }

    private void loadRepairTypes() {
        LOGGER.info("Загружаеются типы ремонта...");
        List<String> repairTypes =
                ((List<RepairType>) this.repairTypeRepository.findAll()).stream().map(RepairType::getName).collect(Collectors.toList());
        Arrays
                .stream(RepairTypeEnum.values())
                .map(repairTypeEnum -> new RepairType(repairTypeEnum.getName()))
                .filter(rt -> !repairTypes.contains(rt.getName()))
                .forEach(this.repairTypeRepository::save);
        LOGGER.info("Типы ремонта загружены!");
    }

    @Override
    public void run(ApplicationArguments args) {
        loadRepairTypes();
    }
}
