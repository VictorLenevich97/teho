package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.repository.equipment.EquipmentLaborInputPerTypeRepository;
import va.rit.teho.repository.equipment.EquipmentRepository;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.implementation.equipment.EquipmentServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EquipmentServiceImplTest {

    private final static Equipment EQUIPMENT =
            new Equipment(
                    1L,
                    "equipment",
                    new EquipmentSubType("s", "f", new EquipmentType("s", "f")));

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);
    private final EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository = Mockito.mock(
            EquipmentLaborInputPerTypeRepository.class);
    private final EquipmentTypeService equipmentTypeService = Mockito.mock(EquipmentTypeService.class);

    private final EquipmentService equipmentService =
            new EquipmentServiceImpl(equipmentTypeService,
                                     equipmentRepository,
                                     equipmentLaborInputPerTypeRepository);

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

        Assertions.assertEquals(EQUIPMENT, equipmentService.get(equipmentId));
    }

    @Test
    public void testAdd() {
        Long equipmentSubTypeId = 2L;
        EquipmentSubType equipmentSubType = new EquipmentSubType("short", "full", new EquipmentType("", ""));
        Equipment equipmentToAdd = new Equipment(0L,"eqName", equipmentSubType);
        Equipment addedEquipment = new Equipment(15L, equipmentToAdd.getName(), equipmentToAdd.getEquipmentSubType());
        when(equipmentTypeService.getSubType(equipmentSubTypeId)).thenReturn(equipmentSubType);
        when(equipmentRepository.save(equipmentToAdd)).thenReturn(addedEquipment);
        Assertions.assertEquals(addedEquipment.getId(),
                                equipmentService.add(equipmentToAdd.getName(), equipmentSubTypeId));

        verify(equipmentRepository).save(equipmentToAdd);
    }

    @Test
    public void testUpdate() {
        Long equipmentSubTypeId = 2L;
        EquipmentSubType equipmentSubType = new EquipmentSubType("short", "full", new EquipmentType("", ""));
        Equipment equipmentToAdd = new Equipment(0L, "eqName", equipmentSubType);
        Equipment addedEquipment = new Equipment(15L, equipmentToAdd.getName(), equipmentToAdd.getEquipmentSubType());
        when(equipmentTypeService.getSubType(equipmentSubTypeId)).thenReturn(equipmentSubType);
        when(equipmentRepository.findById(addedEquipment.getId())).thenReturn(Optional.of(addedEquipment));
        when(equipmentRepository.save(equipmentToAdd)).thenReturn(addedEquipment);

        equipmentService.update(addedEquipment.getId(),
                                equipmentToAdd.getName(),
                                equipmentSubTypeId,
                                Collections.emptyMap());
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
