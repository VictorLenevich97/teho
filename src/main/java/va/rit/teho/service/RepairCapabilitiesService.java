package va.rit.teho.service;

import va.rit.teho.entity.CalculatedRepairCapabilitesPerDay;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.RepairStation;

import java.util.Map;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void calculateAndUpdateRepairCapabilities();

    Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> getTotalCalculatedRepairCapabilities();
}
