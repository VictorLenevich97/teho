package va.rit.teho.repository.config;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.config.ServerConfig;

import java.util.Optional;

@Repository
public interface ServerConfigRepository extends CrudRepository<ServerConfig, String> {

    Optional<ServerConfig> findByKeyIgnoreCase(String key);
}
