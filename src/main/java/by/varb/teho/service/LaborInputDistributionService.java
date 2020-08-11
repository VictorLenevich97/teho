package by.varb.teho.service;

import by.varb.teho.entity.EquipmentLaborInputDistribution;
import by.varb.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;

/**
 * Распределение ремонтного фонда подразделения по трудоемкости ремонта
 */
public interface LaborInputDistributionService {

    /**
     * Расчет распределения ремонтного фонда по трудоемкости ремонта по всем ВВСТ.
     *
     * @return Map, ключ это тип ВВСТ, значение - список данных о записи
     */
    Map<EquipmentType, List<EquipmentLaborInputDistribution>> calculateLaborDistribution();

}
