package va.rit.teho.entity.intensity;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.Equipment;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "intensity")
public class Intensity implements Serializable {

    @EmbeddedId
    private IntensityPK id;

    @ManyToOne
    @MapsId("operation_id")
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("stage_id")
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @Column(nullable = false)
    private Double value;

    public Intensity() {
    }

    public Intensity(IntensityPK id, Double value) {
        this.id = id;
        this.value = value;
    }

    public IntensityPK getId() {
        return id;
    }

    public Operation getOperation() {
        return operation;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Stage getStage() {
        return stage;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
