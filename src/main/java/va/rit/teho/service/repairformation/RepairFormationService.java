package va.rit.teho.service.repairformation;

import va.rit.teho.entity.repairformation.RepairFormation;

import java.util.List;

public interface RepairFormationService {

    List<RepairFormation> list();

    List<RepairFormation> list(Long formationId);

    RepairFormation get(Long id);

    RepairFormation add(String name, Long typeId, Long formationId);

    RepairFormation update(Long id, String name, Long typeId, Long formationId);

    void delete(Long id);

}
