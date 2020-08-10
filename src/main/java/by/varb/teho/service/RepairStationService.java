package by.varb.teho.service;

import by.varb.teho.entity.RepairStation;
import by.varb.teho.entity.RepairStationType;

import java.util.List;
import java.util.Optional;

public interface RepairStationService {

    List<RepairStation> getAll();

    Optional<RepairStation> find(Long repairStationId);

    void add(RepairStation repairStation);

    void addType(RepairStationType repairStationType);

    List<RepairStationType> getAllTypes();
}
