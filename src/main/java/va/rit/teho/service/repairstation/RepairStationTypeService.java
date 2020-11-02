package va.rit.teho.service.repairstation;

import va.rit.teho.entity.repairstation.RepairStationType;

import java.util.List;

public interface RepairStationTypeService {

    RepairStationType get(Long id);

    Long addType(String name, int workingHoursMin, int workingHoursMax);

    void updateType(Long id, String name, int workingHoursMin, int workingHoursMax);

    List<RepairStationType> listTypes();
}
