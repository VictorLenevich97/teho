package va.rit.teho.service.implementation.formation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.session.TehoSession;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.exception.FormationNotFoundException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.formation.FormationRepository;
import va.rit.teho.service.formation.FormationService;

import java.util.*;

@Service
@Transactional
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;

    public FormationServiceImpl(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    private void checkIfEmptyField(String field) {
        if (!Optional.ofNullable(field).map(s -> !s.isEmpty()).isPresent()) {
            throw new EmptyFieldException();
        }
    }

    @Override
    @Transactional
    public Formation add(TehoSession session, String shortName, String fullName) {
        checkPreRequisites(shortName, fullName);
        Long newId = formationRepository.getMaxId() + 1;
        return formationRepository.save(new Formation(newId, session, shortName, fullName));
    }

    private void checkPreRequisites(String shortName, String fullName) {
        checkIfEmptyField(shortName);
        checkIfEmptyField(fullName);
        formationRepository.findByFullName(fullName).ifPresent(b -> {
            throw new AlreadyExistsException("Формирование", "полное название", fullName);
        });
    }

    @Override
    public Formation add(TehoSession session, String shortName, String fullName, Long parentFormationId) {
        checkPreRequisites(shortName, fullName);
        Formation parentFormation = null;
        if (parentFormationId != null) {
            Optional<Formation> optionalFormation = formationRepository.findById(parentFormationId);
            if (!optionalFormation.isPresent()) {
                throw new NotFoundException("Формирование не найдено (id = " + parentFormationId + ")");
            }
            parentFormation = optionalFormation.get();
        }
        long newId = formationRepository.getMaxId() + 1;
        return formationRepository.save(new Formation(newId, session, shortName, fullName, parentFormation));
    }

    @Override
    public Formation update(Long formationId, String shortName, String fullName) {
        Formation formation = getFormationOrThrow(formationId);
        formation.setFullName(fullName);
        formation.setShortName(shortName);
        return formationRepository.save(formation);
    }

    @Override
    public Formation update(Long formationId, String shortName, String fullName, Long parentFormationId) {
        Formation formation = getFormationOrThrow(formationId);
        Formation parentFormation = null;
        if (parentFormationId != null) {
            Optional<Formation> optionalFormation = formationRepository.findById(parentFormationId);
            if (!optionalFormation.isPresent()) {
                throw new NotFoundException("Формирование не найдено (id = " + parentFormationId + ")");
            }
            parentFormation = optionalFormation.get();
        }

        formation.setFullName(fullName);
        formation.setShortName(shortName);
        formation.setParentFormation(parentFormation);
        return formationRepository.save(formation);
    }

    private Formation getFormationOrThrow(Long formationId) {
        return formationRepository.findById(formationId).orElseThrow(() -> new FormationNotFoundException(formationId));
    }

    @Override
    public Formation get(Long formationId) {
        return getFormationOrThrow(formationId);
    }

    @Override
    public List<Formation> list(UUID sessionId) {
        return formationRepository.findFormationByTehoSessionId(sessionId);
    }

    private void populateTree(Tree.Node<Formation> node, Set<Formation> formations, List<Long> formationIds) {
        for (Formation formation : formations) {
            if (formationIds == null || formationIds.contains(formation.getId())) {
                Tree.Node<Formation> childrenFormationNode = node.addChildren(formation);
                if (!formation.getChildFormations().isEmpty()) {
                    populateTree(childrenFormationNode, formation.getChildFormations(), formationIds);
                }
            }
        }
    }

    @Override
    public List<Tree<Formation>> listHierarchy(UUID sessionId, List<Long> formationIds) {
        List<Formation> rootFormations =
                formationIds == null ?
                        formationRepository.findFormationByParentFormationIsNullAndTehoSessionIdEquals(sessionId) :
                        formationRepository.findFormationByParentFormationIsNullAndTehoSessionIdEqualsAndIdIn(sessionId, formationIds);
        List<Tree<Formation>> treeList = new ArrayList<>();
        for (Formation formation : rootFormations) {
            Tree<Formation> formationTree = new Tree<>(formation);
            treeList.add(formationTree);
            populateTree(formationTree.getRoot(), formation.getChildFormations(), formationIds);
        }
        return treeList;
    }

    @Override
    public void delete(Long formationId) {
        formationRepository.deleteById(formationId);
    }
}
