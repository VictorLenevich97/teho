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
        private final Long key;
        private final double capability;

        public EquipmentRepairCapabilityDTO(Long key, double capability) {
            this.key = key;
            this.capability = capability;
        }

        public Long getKey() {
            return key;
        }

        public double getCapability() {
            return capability;
        }
    }
}
