package va.rit.teho.dto.labordistribution;

import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;

public class EquipmentRFUDistributionDTO {

    private final String formationName;
    private final String equipmentName;
    private final Integer intervalStart;
    private final Integer intervalEnd;
    private final Double repairing;
    private final Double unable;

    public EquipmentRFUDistributionDTO(String formationName,
                                       String equipmentName,
                                       Integer intervalStart,
                                       Integer intervalEnd,
                                       Double repairing,
                                       Double unable) {
        this.formationName = formationName;
        this.equipmentName = equipmentName;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.repairing = repairing;
        this.unable = unable;
    }

    public static EquipmentRFUDistributionDTO from(EquipmentRFUDistribution equipmentRFUDistribution) {
        return new EquipmentRFUDistributionDTO(equipmentRFUDistribution.getFormation().getFullName(),
                                               equipmentRFUDistribution.getEquipment().getName(),
                                               equipmentRFUDistribution
                                                       .getWorkhoursDistributionInterval()
                                                       .getLowerBound(),
                                               equipmentRFUDistribution
                                                       .getWorkhoursDistributionInterval()
                                                       .getUpperBound(),
                                               equipmentRFUDistribution.getRepairing(),
                                               equipmentRFUDistribution.getUnable());
    }

    public String getFormationName() {
        return formationName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Integer getIntervalStart() {
        return intervalStart;
    }

    public Integer getIntervalEnd() {
        return intervalEnd;
    }

    public Double getRepairing() {
        return repairing;
    }

    public Double getUnable() {
        return unable;
    }
}
