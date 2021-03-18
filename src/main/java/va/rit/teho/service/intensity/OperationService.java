package va.rit.teho.service.intensity;

import va.rit.teho.entity.intensity.Operation;

import java.util.List;

public interface OperationService {

    List<Operation> list();

    Operation get(Long id);

    Operation add(String name);

    Operation update(Long id, String name);

    Operation delete(Long id);

    Operation setActive(Long id);

}
