package va.rit.teho.service;

import va.rit.teho.entity.RepairType;

import java.util.List;

public interface RepairTypeService {

    List<RepairType> list(boolean repairable);

    List<RepairType> list();

}
