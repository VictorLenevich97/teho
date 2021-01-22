package va.rit.teho.entity.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "stage")
public class Stage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stageNum;

    public Stage() {
        //Для инициализации
    }

    public Long getId() {
        return id;
    }

    public Integer getStageNum() {
        return stageNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stage stage = (Stage) o;
        return Objects.equals(id, stage.id) &&
                Objects.equals(stageNum, stage.stageNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stageNum);
    }
}
