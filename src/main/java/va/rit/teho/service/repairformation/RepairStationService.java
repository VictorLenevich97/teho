package va.rit.teho.service.repairformation;

import va.rit.teho.entity.repairformation.RepairStationType;

import java.util.List;

public interface RepairStationService {
    List<RepairStationType> listTypes();

    RepairStationType addType(String name);

    RepairStationType updateType(Long id, String name);
}
