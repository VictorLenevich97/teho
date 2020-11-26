package va.rit.teho.entity.repairdivision;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairDivisionUnitRepairCapability {

    @EmbeddedId
    RepairDivisionUnitRepairCapabilityPK equipmentPerRepairDivisionUnitWithRepairType;

    @ManyToOne
    @MapsId("repair_division_unit_id")
    @JoinColumn(name = "repair_division_unit_id")
    RepairDivisionUnit repairDivisionUnit;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    double capability;

    public RepairDivisionUnitRepairCapability() {
    }

    public RepairDivisionUnitRepairCapability(RepairDivisionUnitRepairCapabilityPK equipmentPerRepairDivisionUnitWithRepairType,
                                              double capability) {
        this.equipmentPerRepairDivisionUnitWithRepairType = equipmentPerRepairDivisionUnitWithRepairType;
        this.capability = capability;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairDivisionUnitRepairCapability that = (RepairDivisionUnitRepairCapability) o;
        return Double.compare(that.capability, capability) == 0 &&
                Objects.equals(equipmentPerRepairDivisionUnitWithRepairType, that.equipmentPerRepairDivisionUnitWithRepairType) &&
                Objects.equals(repairDivisionUnit, that.repairDivisionUnit) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(repairType, that.repairType) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairDivisionUnitWithRepairType,
                            repairDivisionUnit,
                            equipment,
                            repairType,
                            capability,
                            tehoSession);
    }

    public RepairDivisionUnitRepairCapabilityPK getEquipmentPerDivisionUnit() {
        return equipmentPerRepairDivisionUnitWithRepairType;
    }

    public void setEquipmentPerRepairDivisionUnit(RepairDivisionUnitRepairCapabilityPK equipmentPerRepairDivisionUnit) {
        this.equipmentPerRepairDivisionUnitWithRepairType = equipmentPerRepairDivisionUnit;
    }

    public RepairDivisionUnit getRepairDivisionUnit() {
        return repairDivisionUnit;
    }

    public void setRepairDivisionUnit(RepairDivisionUnit repairDivisionUnit) {
        this.repairDivisionUnit = repairDivisionUnit;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public double getCapability() {
        return capability;
    }

    public void setCapability(double capability) {
        this.capability = capability;
    }

    public RepairDivisionUnitRepairCapability copy(UUID newSessionId) {
        return new RepairDivisionUnitRepairCapability(getEquipmentPerDivisionUnit().copy(newSessionId),
                                                      getCapability());
    }
}