package va.rit.teho.repository.labordistribution;

import org.springframework.data.repository.CrudRepository;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistributionPK;

public interface EquipmentRFUDistributionRepository
        extends CrudRepository<EquipmentRFUDistribution, EquipmentRFUDistributionPK> {
}
