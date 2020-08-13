package by.varb.teho.service;

import by.varb.teho.entity.CalculatedRepairCapabilitesPerDay;
import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.RepairStation;

import java.util.Map;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void calculateAndUpdateRepairCapabilities();

    Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> getTotalCalculatedRepairCapabilities();
}
