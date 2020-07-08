package by.varb.teho.model;

import javax.persistence.*;

@Entity
@Table(name = "equipment_labor_input_per_type")
public class EquipmentLaborInputPerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long equipmentId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repair_type_id", referencedColumnName = "id")
    private RepairType repairType;
    private Integer amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
