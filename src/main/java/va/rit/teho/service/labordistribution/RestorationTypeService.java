package va.rit.teho.service.labordistribution;

import va.rit.teho.entity.labordistribution.RestorationType;

import java.util.List;

public interface RestorationTypeService {

    List<RestorationType> list();

    RestorationType add(String name, int weight);

    RestorationType update(Long id, String name, int weight);

}
