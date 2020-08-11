package by.varb.teho.service;

import by.varb.teho.entity.RepairStation;
import by.varb.teho.entity.RepairStationType;

import java.util.List;
import java.util.Optional;

public interface RepairStationService {

    List<RepairStation> list();

    Optional<RepairStation> find(Long repairStationId);

    Long add(String name, Long baseId, Long typeId, int amount);

    Long addType(String name, int workingHoursMin, int workingHoursMax);

    List<RepairStationType> listTypes();
}
