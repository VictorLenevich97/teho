package va.rit.teho.service.base;

import va.rit.teho.entity.base.Base;

import java.util.List;

public interface BaseService {

    Long add(String shortName, String fullName);

    void update(Long baseId, String shortName, String fullName);

    Base get(Long baseId);

    List<Base> list();
}
