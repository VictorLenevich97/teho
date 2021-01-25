package va.rit.teho.entity.config;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "server_config")
public class ServerConfig implements Serializable {

    @Id
    private String key;

    private boolean done;

    public ServerConfig() {
    }

    public ServerConfig(String key, boolean done) {
        this.key = key;
        this.done = done;
    }

    public String getKey() {
        return key;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConfig that = (ServerConfig) o;
        return done == that.done && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, done);
    }
}
