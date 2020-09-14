package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.service.EquipmentService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EquipmentServiceImplTest {

    private final static Equipment EQUIPMENT =
            new Equipment(
                    "equipment",
                    new EquipmentSubType("s", "f", new EquipmentType("s", "f")));

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);
    private final EquipmentSubTypeRepository equipmentSubTypeRepository = Mockito.mock(EquipmentSubTypeRepository.class);

    private final EquipmentService equipmentService =
            new EquipmentServiceImpl(equipmentRepository, equipmentSubTypeRepository);

    @Test
    public void testList() {
        List<Equipment> equipmentList = Collections.singletonList(EQUIPMENT);
        when(equipmentRepository.findAll()).thenReturn(equipmentList);

        Assertions.assertEquals(equipmentList, equipmentService.list());
    }

    @Test
    public void testGetEquipment() {
        Long equipmentId = 2L;
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(EQUIPMENT));

        Assertions.assertEquals(EQUIPMENT, equipmentService.getEquipment(equipmentId));
    }

    @Test
    public void testAdd() {
        Long equipmentSubTypeId = 2L;
        EquipmentSubType equipmentSubType = new EquipmentSubType("short", "full", new EquipmentType("", ""));
        Equipment equipmentToAdd = new Equipment("eqName", equipmentSubType);
        Equipment addedEquipment = new Equipment(equipmentToAdd.getName(), equipmentToAdd.getEquipmentSubType());
        addedEquipment.setId(15L);
        when(equipmentSubTypeRepository.findById(equipmentSubTypeId)).thenReturn(Optional.of(equipmentSubType));
        when(equipmentRepository.save(equipmentToAdd)).thenReturn(addedEquipment);
        Assertions.assertEquals(addedEquipment.getId(),
                                equipmentService.add(equipmentToAdd.getName(), equipmentSubTypeId));

        verify(equipmentRepository).save(equipmentToAdd);
    }


    @Test
    public void testListGroupedByTypes() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> result = Collections.singletonMap(equipmentType,
                                                                                                     Collections.singletonMap(
                                                                                                             equipmentSubType,
                                                                                                             Collections
                                                                                                                     .singletonList(
                                                                                                                             EQUIPMENT)));

        when(equipmentRepository.getEquipmentGroupedByType(null, null, null)).thenReturn(result);

        Assertions.assertEquals(result, equipmentService.listGroupedByTypes(null, null, null));
    }

}
