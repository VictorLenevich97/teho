package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairFormationUnitRepairCapability {

    @EmbeddedId
    private RepairFormationUnitRepairCapabilityPK equipmentPerRepairFormationUnitWithRepairType;

    @ManyToOne
    @MapsId("repair_formation_unit_id")
    @JoinColumn(name = "repair_formation_unit_id")
    private RepairFormationUnit repairFormationUnit;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    private TehoSession tehoSession;

    private double capability;

    public RepairFormationUnitRepairCapability() {
    }

    public RepairFormationUnitRepairCapability(Long repairFormationUnitId,
                                               Long equipmentId,
                                               Long repairTypeId,
                                               UUID sessionId,
                                               double capability) {
        this.equipmentPerRepairFormationUnitWithRepairType = new RepairFormationUnitRepairCapabilityPK(
                repairFormationUnitId,
                equipmentId,
                repairTypeId,
                sessionId);
        this.capability = capability;
    }

    public RepairFormationUnitRepairCapability(RepairFormationUnitRepairCapabilityPK equipmentPerRepairFormationUnitWithRepairType,
                                               double capability) {
        this.equipmentPerRepairFormationUnitWithRepairType = equipmentPerRepairFormationUnitWithRepairType;
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
        RepairFormationUnitRepairCapability that = (RepairFormationUnitRepairCapability) o;
        return Double.compare(that.capability, capability) == 0 &&
                Objects.equals(equipmentPerRepairFormationUnitWithRepairType,
                               that.equipmentPerRepairFormationUnitWithRepairType) &&
                Objects.equals(repairFormationUnit, that.repairFormationUnit) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(repairType, that.repairType) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairFormationUnitWithRepairType,
                            repairFormationUnit,
                            equipment,
                            repairType,
                            capability,
                            tehoSession);
    }

    public RepairFormationUnitRepairCapabilityPK getEquipmentPerRepairFormationUnitPK() {
        return equipmentPerRepairFormationUnitWithRepairType;
    }

    public void setEquipmentPerRepairFormationUnit(RepairFormationUnitRepairCapabilityPK equipmentPerRepairFormationUnit) {
        this.equipmentPerRepairFormationUnitWithRepairType = equipmentPerRepairFormationUnit;
    }

    public RepairFormationUnit getRepairFormationUnit() {
        return repairFormationUnit;
    }

    public void setRepairFormationUnit(RepairFormationUnit repairFormationUnit) {
        this.repairFormationUnit = repairFormationUnit;
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

    public RepairFormationUnitRepairCapability copy(UUID newSessionId) {
        return new RepairFormationUnitRepairCapability(getEquipmentPerRepairFormationUnitPK().copy(newSessionId),
                                                       getCapability());
    }
}