package va.rit.teho.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import va.rit.teho.RepositoryTestRunner;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = RepositoryTestRunner.class)
@DataJpaTest
public class EquipmentRepositoryTest {

    @Resource
    private EquipmentRepository equipmentRepository;

    @BeforeEach
    public void beforeEachTest() {
        equipmentRepository.deleteAll();
    }

    @Test
    public void testGetEquipmentGroupedByType() {
        Equipment equipment = new Equipment("equipment",
                                            new EquipmentSubType("short",
                                                                 "full",
                                                                 new EquipmentType("shortType", "fullType")));
        Equipment savedEquipment1 = equipmentRepository.save(equipment);

        Equipment equipment2 = new Equipment("equipment2", savedEquipment1.getEquipmentSubType());
        Equipment equipment3 = new Equipment("equipment3", new EquipmentSubType("short2",
                                                                                "full2",
                                                                                new EquipmentType("shortType2",
                                                                                                  "fullType2")));

        Equipment savedEquipment2 = equipmentRepository.save(equipment2);
        Equipment savedEquipment3 = equipmentRepository.save(equipment3);

        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentGroupedByType =
                equipmentRepository.getEquipmentGroupedByType(null, null, null);

        Assertions.assertEquals(2, equipmentGroupedByType.size());
        Map<EquipmentSubType, List<Equipment>> firstSubTypeMap = equipmentGroupedByType.get(equipment.getEquipmentSubType()
                                                                                                     .getEquipmentType());
        Assertions.assertEquals(1, firstSubTypeMap.size());
        List<Equipment> firstEquipmentList = firstSubTypeMap.get(equipment.getEquipmentSubType());
        Assertions.assertEquals(2, firstEquipmentList.size());
        Assertions.assertTrue(firstEquipmentList.contains(savedEquipment1) &&
                                      firstEquipmentList.contains(savedEquipment2));
        Map<EquipmentSubType, List<Equipment>> secondSubTypeMap = equipmentGroupedByType.get(equipment3.getEquipmentSubType()
                                                                                                       .getEquipmentType());
        Assertions.assertEquals(1, secondSubTypeMap.size());
        List<Equipment> secondEquipmentList = secondSubTypeMap.get(savedEquipment3.getEquipmentSubType());
        Assertions.assertEquals(1, secondEquipmentList.size());
        Assertions.assertTrue(secondEquipmentList.contains(savedEquipment3));

    }

    @Test
    public void testGetEquipmentGroupedByTypeFilterById() {
        Equipment equipment = new Equipment("equipment",
                                            new EquipmentSubType("short",
                                                                 "full",
                                                                 new EquipmentType("shortType", "fullType")));
        Equipment equipment2 = new Equipment("equipment2",
                                             new EquipmentSubType("short2",
                                                                  "full2",
                                                                  new EquipmentType("shortType2", "fullType2")));
        Equipment savedId = equipmentRepository.save(equipment);
        equipmentRepository.save(equipment2);

        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentGroupedByType =
                equipmentRepository.getEquipmentGroupedByType(Collections.singletonList(savedId.getId()), null, null);

        Assertions.assertEquals(1, equipmentGroupedByType.size());
        equipmentGroupedByType.forEach((key, value) -> {
            Assertions.assertEquals(1, value.size());
            value.forEach((key1, value1) -> {
                Assertions.assertEquals(1, value1.size());
                Equipment savedEquipment = value1.get(0);
                Assertions.assertEquals(equipment.getName(), savedEquipment.getName());
                Assertions.assertEquals(equipment.getEquipmentSubType().getFullName(),
                                        savedEquipment.getEquipmentSubType().getFullName());
                Assertions.assertEquals(equipment.getEquipmentSubType().getShortName(),
                                        savedEquipment.getEquipmentSubType().getShortName());
            });
        });
    }

    @Test
    public void testGetEquipmentGroupedByTypeFilterByTypeId() {
        Equipment equipment = new Equipment("equipment",
                                            new EquipmentSubType("short",
                                                                 "full",
                                                                 new EquipmentType("shortType", "fullType")));
        Equipment equipment2 = new Equipment("equipment2",
                                             new EquipmentSubType("short2",
                                                                  "full2",
                                                                  new EquipmentType("shortType2", "fullType2")));
        equipmentRepository.save(equipment);
        equipmentRepository.save(equipment2);

        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentGroupedByType =
                equipmentRepository.getEquipmentGroupedByType(null, null, Collections.singletonList(2L));

        Assertions.assertEquals(1, equipmentGroupedByType.size());
        equipmentGroupedByType.forEach((key, value) -> {
            Assertions.assertEquals(1, value.size());
            value.forEach((key1, value1) -> {
                Assertions.assertEquals(1, value1.size());
                Equipment savedEquipment = value1.get(0);
                Assertions.assertEquals(equipment2.getName(), savedEquipment.getName());
                Assertions.assertEquals(equipment2.getEquipmentSubType().getFullName(),
                                        savedEquipment.getEquipmentSubType().getFullName());
                Assertions.assertEquals(equipment2.getEquipmentSubType().getShortName(),
                                        savedEquipment.getEquipmentSubType().getShortName());
            });
        });
    }
}
