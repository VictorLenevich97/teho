package va.rit.teho.service.implementation.formation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.Tree;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.exception.FormationNotFoundException;
import va.rit.teho.exception.NotFoundException;
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
    public Formation add(String shortName, String fullName) {
        checkPreRequisites(shortName, fullName);
        return formationRepository.save(new Formation(shortName, fullName));
    }

    private void checkPreRequisites(String shortName, String fullName) {
        checkIfEmptyField(shortName);
        checkIfEmptyField(fullName);
        formationRepository.findByFullName(fullName).ifPresent(b -> {
            throw new AlreadyExistsException("Формирование", "название", fullName);
        });
    }

    @Override
    public Formation add(String shortName, String fullName, Long parentFormationId) {
        checkPreRequisites(shortName, fullName);
        Optional<Formation> optionalFormation = formationRepository.findById(parentFormationId);
        if (!optionalFormation.isPresent()) {
            throw new NotFoundException("Формирование не найдено (id = " + parentFormationId + ")");
        }
        return formationRepository.save(new Formation(shortName, fullName, optionalFormation.get()));
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
        Optional<Formation> optionalFormation = formationRepository.findById(parentFormationId);
        if (!optionalFormation.isPresent()) {
            throw new NotFoundException("Формирование не найдено (id = " + parentFormationId + ")");
        }

        formation.setFullName(fullName);
        formation.setShortName(shortName);
        formation.setParentFormation(optionalFormation.get());
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
