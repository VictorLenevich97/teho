package va.rit.teho.service.implementation.formation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.exception.FormationNotFoundException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.formation.FormationRepository;
import va.rit.teho.service.formation.FormationService;

import java.util.List;
import java.util.Optional;

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
        Formation formation = formationRepository.save(new Formation(shortName, fullName));
        return formation;
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
        Formation formation = formationRepository.save(new Formation(shortName, fullName, optionalFormation.get()));
        return formation;
    }

    @Override
    public void update(Long formationId, String shortName, String fullName) {
        Formation formation = getFormationOrThrow(formationId);
        formation.setFullName(fullName);
        formation.setShortName(shortName);
        formationRepository.save(formation);
    }

    @Override
    public void update(Long formationId, String shortName, String fullName, Long parentFormationId) {
        Formation formation = getFormationOrThrow(formationId);
        Optional<Formation> optionalFormation = formationRepository.findById(parentFormationId);
        if (!optionalFormation.isPresent()) {
            throw new NotFoundException("Формирование не найдено (id = " + parentFormationId + ")");
        }

        formation.setFullName(fullName);
        formation.setShortName(shortName);
        formation.setParentFormation(optionalFormation.get());
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
}
