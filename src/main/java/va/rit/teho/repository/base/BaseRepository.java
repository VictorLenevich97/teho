package va.rit.teho.repository.base;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.base.Base;

import java.util.Optional;

@Repository
public interface BaseRepository extends CrudRepository<Base, Long> {

    Optional<Base> findByFullName(String fullName);
}
