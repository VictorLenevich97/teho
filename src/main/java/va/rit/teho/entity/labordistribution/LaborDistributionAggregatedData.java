package va.rit.teho.entity.labordistribution;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.formation.Formation;

import java.util.Objects;

public class LaborDistributionAggregatedData {

    private final Equipment equipment;
    private final Formation formation;
    private final WorkhoursDistributionInterval interval;


    private Double count;
    private final Double avgLaborInput;

    public LaborDistributionAggregatedData(Equipment equipment,
                                           Formation formation,
                                           WorkhoursDistributionInterval interval,
                                           Double count,
                                           Double avgLaborInput) {
        this.equipment = equipment;
        this.formation = formation;
        this.interval = interval;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Formation getFormation() {
        return formation;
    }

    public WorkhoursDistributionInterval getInterval() {
        return interval;
    }

    public Double getCount() {
        return count;
    }

    public Double getAvgLaborInput() {
        return avgLaborInput;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaborDistributionAggregatedData that = (LaborDistributionAggregatedData) o;
        return Objects.equals(equipment.getId(), that.equipment.getId()) && Objects.equals(formation.getId(),
                                                                           that.formation.getId()) && Objects
                .equals(interval.getId(), that.interval.getId()) && Objects.equals(count, that.count) && Objects.equals(
                avgLaborInput,
                that.avgLaborInput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipment, formation, interval, count, avgLaborInput);
    }
}
