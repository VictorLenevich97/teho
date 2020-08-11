package by.varb.teho.service;

import by.varb.teho.entity.CalculatedRepairCapabilitesPerDay;
import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.RepairStation;
import by.varb.teho.entity.RepairStationEquipmentStaff;

import java.util.Map;
import java.util.Optional;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    /**
     * Получить производственные возможности (кол-во ремонтников) РВО по ремонту ВВСТ.
     *
     * @param equipmentId     идентификатор ВВСТ
     * @param repairStationId идентификатор РВО
     * @return объект, включающий в себя количество ремонтников в РВО, занимающихся ремонтом конкретного ВВСТ
     */
    Optional<RepairStationEquipmentStaff> getRepairStationEquipmentStaff(Long equipmentId, Long repairStationId);

    void saveCalculatedRepairCapabilities(CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay);

    Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> getTotalCalculatedRepairCapabilities();
}
