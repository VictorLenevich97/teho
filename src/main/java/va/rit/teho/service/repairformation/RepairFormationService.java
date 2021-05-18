package va.rit.teho.service.repairformation;

import va.rit.teho.entity.repairformation.RepairFormation;

import java.util.List;
import java.util.UUID;

public interface RepairFormationService {

    List<RepairFormation> list(UUID sessionId);

    List<RepairFormation> list(Long formationId);

    RepairFormation get(Long id);

    RepairFormation add(String name, Long typeId, Long formationId);

    RepairFormation update(Long id, String name, Long typeId, Long formationId);

    void delete(Long id);

}
