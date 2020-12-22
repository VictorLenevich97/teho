package va.rit.teho.service.repairformation;

import va.rit.teho.entity.repairformation.RepairFormationType;

import java.util.List;

public interface RepairFormationTypeService {

    RepairFormationType get(Long id);

    Long addType(String name, Long restorationTypeId, int workingHoursMin, int workingHoursMax);

    void updateType(Long id, String name, int workingHoursMin, int workingHoursMax);

    List<RepairFormationType> listTypes();
}
