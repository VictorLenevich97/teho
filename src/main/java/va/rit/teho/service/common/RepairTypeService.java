package va.rit.teho.service.common;

import va.rit.teho.entity.common.RepairType;

import java.util.List;

public interface RepairTypeService {

    RepairType get(Long id);

    List<RepairType> list(boolean calculatable);

    List<RepairType> list();

    RepairType switchCalculatableFlag(Long id);
}
