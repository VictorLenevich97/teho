package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentPerFormation;

public class LaborDistributionData {

    private EquipmentPerFormation equipmentPerFormation;
    private RepairType repairType;
    private Integer laborInput;
    private Long intervalId;
    private Double count;
    private Double avgLaborInput;
    private Double avgDailyFailure;

    public LaborDistributionData() {
    }

    public LaborDistributionData(EquipmentPerFormation equipmentPerFormation,
                                 Integer laborInput,
                                 Long intervalId,
                                 Double count,
                                 Double avgLaborInput,
                                 Double avgDailyFailure) {
        this.equipmentPerFormation = equipmentPerFormation;
        this.laborInput = laborInput;
        this.intervalId = intervalId;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
        this.avgDailyFailure = avgDailyFailure;
    }

    public LaborDistributionData(EquipmentPerFormation equipmentPerFormation,
                                 RepairType repairType,
                                 Integer laborInput,
                                 Long intervalId,
                                 Double count,
                                 Double avgLaborInput,
                                 Double avgDailyFailure) {
        this(equipmentPerFormation, laborInput, intervalId, count, avgLaborInput, avgDailyFailure);
        this.repairType = repairType;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setAvgDailyFailure(Double avgDailyFailure) {
        this.avgDailyFailure = avgDailyFailure;
    }

    public Double getAvgDailyFailure() {
        return avgDailyFailure;
    }

    public EquipmentPerFormation getEquipmentPerFormation() {
        return equipmentPerFormation;
    }

    public Integer getLaborInput() {
        return laborInput;
    }


    public Long getIntervalId() {
        return intervalId;
    }

    public Double getCount() {
        return count;
    }

    public Double getAvgLaborInput() {
        return avgLaborInput;
    }

}