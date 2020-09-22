package va.rit.teho.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import va.rit.teho.entity.EquipmentSubType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentSubTypeRepository extends CrudRepository<EquipmentSubType, Long> {

    List<EquipmentSubType> findByEquipmentTypeId(Long typeId);

    Optional<EquipmentSubType> findByFullName(String fullName);

    @Query("SELECT est FROM EquipmentSubType est WHERE " +
            "(coalesce(:ids, null) is null or est.id in (:ids)) AND " +
            "(coalesce(:typeIds, null) is null or est.equipmentType.id in (:typeIds)) " +
            "ORDER BY est.equipmentType.id ASC, est.id ASC")
    List<EquipmentSubType> findByIds(List<Long> ids, List<Long> typeIds);

}
