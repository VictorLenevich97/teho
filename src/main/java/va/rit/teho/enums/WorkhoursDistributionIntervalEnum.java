package va.rit.teho.enums;

public enum WorkhoursDistributionIntervalEnum {

    ZERO_TO_TEN(null, 10, RestorationTypeEnum.TACTICAL),
    TEN_TO_TWENTY(10, 20, RestorationTypeEnum.TACTICAL),
    TWENTY_TO_FIFTY(20, 50, RestorationTypeEnum.OPERATIONAL),
    FIFTY_TO_HUNDRED(50, 100, RestorationTypeEnum.OPERATIONAL),
    HUNDRED_TO_TWO_HUNDRED(100, 200, RestorationTypeEnum.OPERATIONAL),
    TWO_HUNDRED_TO_FOUR_HUNDRED(200, 400, RestorationTypeEnum.STRATEGIC);


    private final Integer lowerBound;
    private final Integer upperBound;
    private final RestorationTypeEnum restorationTypeEnum;

    WorkhoursDistributionIntervalEnum(Integer lowerBound,
                                      Integer upperBound,
                                      RestorationTypeEnum restorationTypeEnum) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.restorationTypeEnum = restorationTypeEnum;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public RestorationTypeEnum getRestorationTypeEnum() {
        return restorationTypeEnum;
    }
}
