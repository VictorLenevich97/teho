package va.rit.teho.entity.repairformation;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class RepairStationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public RepairStationType(String name) {
        this.name = name;
    }

    public RepairStationType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RepairStationType() {
        //Пустой конструктор для автоматической инициализации
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairStationType that = (RepairStationType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
