package va.rit.teho.dto;

import java.util.List;

public class RepairCapabilitiesDTO {
    private final Long repairStationId;
    private final List<EquipmentRepairCapabilityDTO> capabilities;

    public RepairCapabilitiesDTO(Long repairStationId,
                                 List<EquipmentRepairCapabilityDTO> capabilities) {
        this.repairStationId = repairStationId;
        this.capabilities = capabilities;
    }

    public Long getRepairStationId() {
        return repairStationId;
    }

    public List<EquipmentRepairCapabilityDTO> getCapabilities() {
        return capabilities;
    }

    public static class EquipmentRepairCapabilityDTO {
        private final Long id;
        private final double capability;

        public EquipmentRepairCapabilityDTO(Long id, double capability) {
            this.id = id;
            this.capability = capability;
        }

        public Long getId() {
            return id;
        }

        public double getCapability() {
            return capability;
        }
    }
}
