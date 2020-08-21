package va.rit.teho.service;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.RepairStation;

import java.util.List;
import java.util.Map;

/**
 * Сервис по расчету/получению производственных возможностей РВО по ремонту ВВСТ.
 */
public interface RepairCapabilitiesService {

    void calculateAndUpdateRepairCapabilities();

    void calculateAndUpdateRepairCapabilitiesPerStation(Long repairStationId);

    Map<RepairStation, Map<Equipment, Double>> getCalculatedRepairCapabilities(List<Long> repairStationIds);
}
