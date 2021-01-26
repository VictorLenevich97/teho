package va.rit.teho.dto.common;

import va.rit.teho.entity.common.Stage;

import javax.validation.constraints.Positive;

public class StageDTO {

    private final Long id;

    @Positive
    private final Integer num;

    public StageDTO(Long id, Integer num) {
        this.id = id;
        this.num = num;
    }

    public static StageDTO from(Stage stage) {
        return new StageDTO(stage.getId(), stage.getStageNum());
    }

    public Long getId() {
        return id;
    }

    public Integer getNum() {
        return num;
    }
}
