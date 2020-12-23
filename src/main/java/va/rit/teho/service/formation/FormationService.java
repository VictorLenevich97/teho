package va.rit.teho.service.formation;

import va.rit.teho.entity.formation.Formation;

import java.util.List;

public interface FormationService {

    Formation add(String shortName, String fullName);

    Formation add(String shortName, String fullName, Long parentFormationId);

    void update(Long formationId, String shortName, String fullName);

    void update(Long formationId, String shortName, String fullName, Long parentFormationId);

    Formation get(Long formationId);

    List<Formation> list();
}
