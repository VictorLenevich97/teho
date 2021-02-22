package va.rit.teho.dto.labordistribution;

import va.rit.teho.controller.helper.Formatter;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;

public class EquipmentRFUDistributionDTO {

    private final String formationName;
    private final String equipmentName;
    private final String restorationType;
    private final Double repairing;
    private final Double unable;

    public EquipmentRFUDistributionDTO(String formationName,
                                       String equipmentName,
                                       String restorationType,
                                       Double repairing,
                                       Double unable) {
        this.formationName = formationName;
        this.equipmentName = equipmentName;
        this.restorationType = restorationType;
        this.repairing = repairing;
        this.unable = unable;
    }

    public static EquipmentRFUDistributionDTO from(EquipmentRFUDistribution equipmentRFUDistribution) {
        return new EquipmentRFUDistributionDTO(equipmentRFUDistribution.getFormation().getFullName(),
                                               equipmentRFUDistribution.getEquipment().getName(),
                                               equipmentRFUDistribution
                                                       .getWorkhoursDistributionInterval()
                                                       .getRestorationType()
                                                       .getName(),
                                               Formatter.formatDouble(equipmentRFUDistribution.getRepairing()),
                                               Formatter.formatDouble(equipmentRFUDistribution.getUnable()));
    }

    public String getFormationName() {
        return formationName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getRestorationType() {
        return restorationType;
    }

    public Double getRepairing() {
        return repairing;
    }

    public Double getUnable() {
        return unable;
    }
}
