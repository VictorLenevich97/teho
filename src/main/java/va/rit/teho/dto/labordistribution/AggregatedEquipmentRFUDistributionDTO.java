package va.rit.teho.dto.labordistribution;

import java.util.List;

public class AggregatedEquipmentRFUDistributionDTO {
    private final String rfuName;
    private final List<EquipmentRFUDistributionDTO> distributed;

    public AggregatedEquipmentRFUDistributionDTO(String rfuName,
                                                 List<EquipmentRFUDistributionDTO> distributed) {
        this.rfuName = rfuName;
        this.distributed = distributed;
    }

    public String getRfuName() {
        return rfuName;
    }

    public List<EquipmentRFUDistributionDTO> getDistributed() {
        return distributed;
    }
}
