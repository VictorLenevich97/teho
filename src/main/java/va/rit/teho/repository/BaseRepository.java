package va.rit.teho.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.Base;

@Repository
public interface BaseRepository extends CrudRepository<Base, Long> {
}
