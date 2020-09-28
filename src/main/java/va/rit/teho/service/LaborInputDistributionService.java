package va.rit.teho.service;

import va.rit.teho.entity.EquipmentLaborInputDistribution;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.entity.WorkhoursDistributionInterval;

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
    Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> getLaborInputDistribution(UUID sessionId, List<Long> equipmentTypeIds);

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     */
    void updateLaborInputDistribution(UUID sessionId);

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ, относящихся к Base с id = baseId.
     *
     * @param baseId id Base
     */
    void updateLaborInputDistributionPerBase(UUID sessionId, Long baseId);

    void updateLaborInputDistributionPerEquipmentSubType(UUID sessionId, Long equipmentSubTypeId);

    void updateLaborInputDistributionPerEquipmentType(UUID sessionId, Long equipmentType);

    List<WorkhoursDistributionInterval> getDistributionIntervals();

}
