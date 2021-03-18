package va.rit.teho.service.implementation.intensity;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.intensity.Operation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.intensity.OperationRepository;
import va.rit.teho.service.intensity.OperationService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public List<Operation> list() {
        return StreamSupport
                .stream(operationRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Operation get(Long id) {
        return operationRepository.findById(id).orElseThrow(() -> new NotFoundException("Операция с id = \"" + id + "\" не найдена!"));
    }

    @Override
    public Operation add(String name) {
        operationRepository.findByName(name).ifPresent(op -> {
            throw new AlreadyExistsException("Операция с названием \"" + name + "\" уже существует!");
        });
        Optional<Operation> activeOp = operationRepository.findByActiveIsTrue();
        boolean shouldCurrentBeActive = !activeOp.isPresent();
        long id = operationRepository.getMaxId() + 1;
        return operationRepository.save(new Operation(id, name, shouldCurrentBeActive));
    }

    @Override
    public Operation update(Long id, String name) {
        Operation operation = get(id);
        operationRepository.findByName(name).ifPresent(op -> {
            if (!op.getId().equals(id)) {
                throw new AlreadyExistsException("Операция с названием \"" + name + "\" уже существует!");
            }
        });
        operation.setName(name);
        return operationRepository.save(operation);
    }

    @Override
    @Transactional
    public Operation delete(Long id) {
        Operation deletedOp = get(id);
        boolean isActive = deletedOp.isActive();
        operationRepository.deleteById(id);
        if (isActive) {
            list().stream().findAny().ifPresent(op -> {
                op.setActive(true);
                operationRepository.save(op);
            });
        }
        return deletedOp;
    }

    @Override
    @Transactional
    public Operation setActive(Long id) {
        Operation operation = get(id);
        Optional<Operation> activeOp = operationRepository.findByActiveIsTrue();
        activeOp.ifPresent(active -> {
            active.setActive(false);
            operationRepository.save(active);
        });
        operation.setActive(true);
        return operationRepository.save(operation);
    }
}
