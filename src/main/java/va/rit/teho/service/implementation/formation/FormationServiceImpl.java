package va.rit.teho.service.implementation.formation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.exception.FormationNotFoundException;
import va.rit.teho.repository.formation.FormationRepository;
import va.rit.teho.service.formation.FormationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Long add(String shortName, String fullName) {
        checkIfEmptyField(shortName);
        checkIfEmptyField(fullName);
        formationRepository.findByFullName(fullName).ifPresent(b -> {
            throw new AlreadyExistsException("ВЧ", "название", fullName);
        });
        Formation formation = formationRepository.save(new Formation(shortName, fullName));
        return formation.getId();
    }

    @Override
    public void update(Long formationId, String shortName, String fullName) {
        Formation formation = getFormationOrThrow(formationId);
        formation.setFullName(fullName);
        formation.setShortName(shortName);
        formationRepository.save(formation);
    }

    private Formation getFormationOrThrow(Long formationId) {
        return formationRepository.findById(formationId).orElseThrow(() -> new FormationNotFoundException(formationId));
    }

    @Override
    public Formation get(Long formationId) {
        return getFormationOrThrow(formationId);
    }

    @Override
    public List<Formation> list() {
        return (List<Formation>) formationRepository.findAll();
    }

    private void populateTree(Tree.Node<Formation> node, Set<Formation> formations) {
        for (Formation formation : formations) {
            Tree.Node<Formation> childrenFormationNode = node.addChildren(formation);
            Set<Formation> childFormations = formation.getChildFormations();
            if (!childFormations.isEmpty()) {
                populateTree(childrenFormationNode, childFormations);
            }
        }
    }

    @Override
    public List<Tree<Formation>> listHierarchy() {
        List<Formation> rootFormations = formationRepository.findFormationByParentFormationIsNull();
        List<Tree<Formation>> treeList = new ArrayList<>();
        for (Formation formation : rootFormations) {
            Tree<Formation> formationTree = new Tree<>(formation);
            treeList.add(formationTree);
            populateTree(formationTree.getRoot(), formation.getChildFormations());
        }
        return treeList;
    }
}
