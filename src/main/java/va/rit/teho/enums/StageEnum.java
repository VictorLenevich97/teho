package va.rit.teho.enums;

public enum StageEnum {
    FIRST(1), SECOND(2), THIRD(3), FOURTH(4);

    private final int stageNum;

    StageEnum(int stageNum) {
        this.stageNum = stageNum;
    }

    public int getStageNum() {
        return stageNum;
    }
}
