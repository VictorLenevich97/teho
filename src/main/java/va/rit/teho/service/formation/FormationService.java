package va.rit.teho.service.formation;

import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.session.TehoSession;

import java.util.List;
import java.util.UUID;

public interface FormationService {

    Formation add(TehoSession session, String shortName, String fullName);

    Formation add(TehoSession session, String shortName, String fullName, Long parentFormationId);

    Formation update(Long formationId, String shortName, String fullName);

    Formation update(Long formationId, String shortName, String fullName, Long parentFormationId);

    Formation get(Long formationId);

    List<Formation> list(UUID sessionId);

    List<Tree<Formation>> listHierarchy(UUID sessionId, List<Long> formationIds);

    void delete(Long formationId);
}
