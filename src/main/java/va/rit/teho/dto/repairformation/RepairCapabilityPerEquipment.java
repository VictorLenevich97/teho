package va.rit.teho.dto.repairformation;

import va.rit.teho.dto.common.IdAndNameDTO;

public class RepairCapabilityPerEquipment extends IdAndNameDTO {

    private final Double capability;

    public RepairCapabilityPerEquipment(Long id, String name, Double capability) {
        super(id, name);
        this.capability = capability;
    }

    public Double getCapability() {
        return capability;
    }
}
