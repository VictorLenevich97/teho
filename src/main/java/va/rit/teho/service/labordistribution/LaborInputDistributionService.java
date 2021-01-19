package va.rit.teho.service.labordistribution;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborDistributionAggregatedData;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Распределение ремонтного фонда подразделения по трудоемкости ремонта
 */
public interface LaborInputDistributionService {

    /**
     * Получение данных о распределении ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     *
     * @return Map, ключ это тип ВВСТ, значение - список данных о записи
     */
    Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(UUID sessionId,
                                                                                                               Long repairTypeId,
                                                                                                               Long stageId,
                                                                                                               List<Long> equipmentTypeIds);

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     */
    void updateLaborInputDistribution(UUID sessionId);

    void copyLaborInputDistributionData(UUID originalSessionId, UUID newSessionId);

    List<WorkhoursDistributionInterval> listDistributionIntervals();

    List<LaborDistributionAggregatedData> listAggregatedDataForSessionAndFormation(Long formationId, UUID sessionId);

    void deleteDistributionData(Long formationId, Long equipmentId);

}
