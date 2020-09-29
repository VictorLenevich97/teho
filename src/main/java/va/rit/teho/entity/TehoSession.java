package va.rit.teho.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "teho_session")
public class TehoSession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    @Column(name = "creation_ts")
    private Instant creationTimestamp;

    public TehoSession() {
    }

    public TehoSession(String name) {
        this.name = name;
        this.creationTimestamp = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

}
