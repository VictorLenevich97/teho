package va.rit.teho.dto;

import java.util.List;

public class RepairCapabilitiesDTO {
    private String repairStation;
    private List<EquipmentRepairCapabilityDTO> capabilities;

    public RepairCapabilitiesDTO(String repairStation,
                                 List<EquipmentRepairCapabilityDTO> capabilities) {
        this.repairStation = repairStation;
        this.capabilities = capabilities;
    }

    public String getRepairStation() {
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
