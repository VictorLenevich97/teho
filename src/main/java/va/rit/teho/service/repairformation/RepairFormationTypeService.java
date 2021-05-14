package va.rit.teho.service.repairformation;

import va.rit.teho.entity.repairformation.RepairFormationType;

import java.util.List;

public interface RepairFormationTypeService {

    RepairFormationType get(Long id);

    RepairFormationType addType(String name, Long restorationTypeId, int workingHoursMin, int workingHoursMax);

    RepairFormationType updateType(Long id, Long restorationTypeId, String name, int workingHoursMin, int workingHoursMax);

    void deleteType(Long id);

    List<RepairFormationType> listTypes();
}
