package va.rit.teho.dto;

import java.util.List;

public class RepairCapabilitiesDTO {
    private final RepairStationDTO repairStation;
    private final List<EquipmentRepairCapabilityDTO> capabilities;

    public RepairCapabilitiesDTO(RepairStationDTO repairStation,
                                 List<EquipmentRepairCapabilityDTO> capabilities) {
        this.repairStation = repairStation;
        this.capabilities = capabilities;
    }

    public RepairStationDTO getRepairStation() {
        return repairStation;
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
