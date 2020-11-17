package va.rit.teho.service.repairdivision;

import va.rit.teho.entity.repairdivision.RepairDivisionUnitType;

import java.util.List;

public interface RepairDivisionUnitTypeService {

    RepairDivisionUnitType get(Long id);

    Long addType(String name, int workingHoursMin, int workingHoursMax);

    void updateType(Long id, String name, int workingHoursMin, int workingHoursMax);

    List<RepairDivisionUnitType> listTypes();
}
