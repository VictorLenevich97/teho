package va.rit.teho.service.common;

import va.rit.teho.entity.common.RepairType;

import java.util.List;

public interface RepairTypeService {

    List<RepairType> list(boolean calculatable);

    List<RepairType> list();

}
