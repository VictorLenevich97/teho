package va.rit.teho.service;

import va.rit.teho.entity.EquipmentLaborInputDistribution;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;

/**
 * Распределение ремонтного фонда подразделения по трудоемкости ремонта
 */
public interface LaborInputDistributionService {

    /**
     * Получение данных о распределении ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     *
     * @return Map, ключ это тип ВВСТ, значение - список данных о записи
     */
    Map<EquipmentType, List<EquipmentLaborInputDistribution>> getLaborInputDistribution();

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     */
    void updateLaborInputDistribution();

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ, относящихся к Base с id = baseId.
     *
     * @param baseId id Base
     */
    void updateLaborInputDistribution(Long baseId);

}
