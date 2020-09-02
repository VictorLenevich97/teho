package va.rit.teho.service;

import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.entity.RepairStationType;
import va.rit.teho.model.Pair;

import java.util.List;

public interface RepairStationService {

    List<RepairStation> list();

    Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId);

    Long add(String name, Long baseId, Long typeId, int amount);

    void setEquipmentStaff(Long repairStationId, Long equipmentId, int availableStaff, int totalStaff);

    Long addType(String name, int workingHoursMin, int workingHoursMax);

    List<RepairStationType> listTypes();
}
