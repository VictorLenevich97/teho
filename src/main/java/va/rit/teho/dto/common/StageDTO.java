package va.rit.teho.dto.common;

import va.rit.teho.entity.common.Stage;

public class StageDTO {
    private final Long id;
    private final Integer num;

    public Long getId() {
        return id;
    }

    public Integer getNum() {
        return num;
    }

    public StageDTO(Long id, Integer num) {
        this.id = id;
        this.num = num;
    }

    public static StageDTO from(Stage stage) {
        return new StageDTO(stage.getId(), stage.getStageNum());
    }
}
