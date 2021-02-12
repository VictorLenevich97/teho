package va.rit.teho.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.config.ServerConfig;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.enums.*;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.common.RepairTypeRepository;
import va.rit.teho.repository.common.StageRepository;
import va.rit.teho.repository.config.ServerConfigRepository;
import va.rit.teho.repository.labordistribution.RestorationTypeRepository;
import va.rit.teho.repository.labordistribution.WorkhoursDistributionIntervalRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataLoader.class);

    private final RepairTypeRepository repairTypeRepository;
    private final RestorationTypeRepository restorationTypeRepository;
    private final WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository;
    private final StageRepository stageRepository;

    private final ServerConfigRepository serverConfigRepository;

    public InitialDataLoader(RepairTypeRepository repairTypeRepository,
                             RestorationTypeRepository restorationTypeRepository,
                             WorkhoursDistributionIntervalRepository workhoursDistributionIntervalRepository,
                             StageRepository stageRepository,
                             ServerConfigRepository serverConfigRepository) {
        this.repairTypeRepository = repairTypeRepository;
        this.restorationTypeRepository = restorationTypeRepository;
        this.workhoursDistributionIntervalRepository = workhoursDistributionIntervalRepository;
        this.stageRepository = stageRepository;
        this.serverConfigRepository = serverConfigRepository;
    }

    private void loadRepairTypes() {
        LOGGER.info("Загружаются типы ремонта...");
        List<String> existingRepairTypes =
                ((List<RepairType>) this.repairTypeRepository.findAll())
                        .stream()
                        .map(RepairType::getShortName)
                        .collect(Collectors.toList());
        this.repairTypeRepository.saveAll(
                Arrays
                        .stream(RepairTypeEnum.values())
                        .map(repairTypeEnum -> new RepairType(repairTypeEnum.getFullName(),
                                                              repairTypeEnum.getShortName(),
                                                              repairTypeEnum.isCalculatable(),
                                                              repairTypeEnum.isRepairable(),
                                                              repairTypeEnum.isSplitToIntervals()))
                        .filter(rt -> !existingRepairTypes.contains(rt.getShortName()))
                        .collect(Collectors.toList()));
    }

    private List<RestorationType> loadRestorationTypes() {
        LOGGER.info("Загружаются типы восстановления...");
        List<String> existingRestorationTypes =
                ((List<RestorationType>) this.restorationTypeRepository.findAll())
                        .stream()
                        .map(RestorationType::getName)
                        .collect(Collectors.toList());
        List<RestorationType> restorationTypes = Arrays
                .stream(RestorationTypeEnum.values())
                .map(restorationTypeEnum -> new RestorationType(
                        restorationTypeEnum.getName(),
                        restorationTypeEnum.getWeight()))
                .filter(rt -> !existingRestorationTypes.contains(rt.getName()))
                .collect(Collectors.toList());
        restorationTypeRepository.saveAll(restorationTypes);
        return (List<RestorationType>) this.restorationTypeRepository.findAll();
    }

    private void loadWorkhoursDistributionIntervals(List<RestorationType> restorationTypes) {
        LOGGER.info("Загружаются интервалы рабочего времени...");
        List<WorkhoursDistributionInterval> existingIntervals =
                (List<WorkhoursDistributionInterval>) workhoursDistributionIntervalRepository.findAll();
        List<WorkhoursDistributionInterval> intervals = Arrays
                .stream(WorkhoursDistributionIntervalEnum.values())
                .map(wdiEnum -> new WorkhoursDistributionInterval(
                        wdiEnum.getLowerBound(),
                        wdiEnum.getUpperBound(),
                        restorationTypes
                                .stream()
                                .filter(rt -> rt.getName().equals(wdiEnum.getRestorationTypeEnum().getName()) &&
                                        rt.getWeight() == wdiEnum.getRestorationTypeEnum().getWeight())
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException("Тип восстановления " + wdiEnum
                                        .getRestorationTypeEnum()
                                        .getName() + " не найден!"))))
                .filter(interval ->
                                existingIntervals
                                        .stream()
                                        .noneMatch(existingInterval ->
                                                           Objects.equals(interval.getLowerBound(),
                                                                          existingInterval.getLowerBound()) &&
                                                                   Objects.equals(interval.getUpperBound(),
                                                                                  existingInterval.getUpperBound()) &&
                                                                   interval.getRestorationType()
                                                                           .equals(existingInterval.getRestorationType())))
                .collect(Collectors.toList());

        long latestNewId = workhoursDistributionIntervalRepository.getMaxId() + 1;

        for (WorkhoursDistributionInterval interval : intervals) {
            interval.setId(latestNewId);
            latestNewId++;
        }
        this.workhoursDistributionIntervalRepository.saveAll(intervals);
    }

    private void loadStages() {
        LOGGER.info("Загружаются этапы операции...");
        List<Integer> stages = StreamSupport
                .stream(stageRepository.findAll().spliterator(), false)
                .map(Stage::getStageNum)
                .collect(Collectors.toList());
        long maxId = stageRepository.getMaxId() + 1;
        List<Stage> newStages = new ArrayList<>();
        for (int i = 0; i < StageEnum.values().length; i++) {
            StageEnum value = StageEnum.values()[i];
            if (!stages.contains(value.getStageNum())) {
                newStages.add(new Stage(maxId + i, value.getStageNum()));
            }
        }
        stageRepository.saveAll(newStages);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (initRequired()) {
            LOGGER.info("Флаг первоначальной загрузки отсутствует, требуется инициализация данных.");
            loadRepairTypes();
            List<RestorationType> restorationTypes = loadRestorationTypes();
            loadWorkhoursDistributionIntervals(restorationTypes);
            loadStages();
            setInitCompleted();
            LOGGER.info("Инициализация данных завершена.");
        } else {
            LOGGER.info("Присутствует флаг первоначальной загрузки, инициализация данных не требуется.");
        }
    }

    private boolean initRequired() {
        Optional<ServerConfig> dataInitializedConfig =
                serverConfigRepository.findByKeyIgnoreCase(ServerConfigEnum.DATA_INITIALIZED.name());
        return !dataInitializedConfig.map(ServerConfig::isDone).orElse(false);
    }

    private void setInitCompleted() {
        serverConfigRepository.save(new ServerConfig(ServerConfigEnum.DATA_INITIALIZED.name(), true));
    }
}
