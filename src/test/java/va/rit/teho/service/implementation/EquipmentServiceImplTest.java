package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.repository.equipment.EquipmentLaborInputPerTypeRepository;
import va.rit.teho.repository.equipment.EquipmentRepository;
import va.rit.teho.service.common.RepairTypeService;
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
                    new EquipmentType("s", "f"));

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);
    private final EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository = Mockito.mock(
            EquipmentLaborInputPerTypeRepository.class);
    private final EquipmentTypeService equipmentTypeService = Mockito.mock(EquipmentTypeService.class);
    private final RepairTypeService repairTypeService = Mockito.mock(RepairTypeService.class);

    private final EquipmentService equipmentService =
            new EquipmentServiceImpl(equipmentTypeService,
                                     repairTypeService,
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
        Long equipmentTypeId = 2L;
        EquipmentType equipmentType = new EquipmentType("", "");
        Equipment equipmentToAdd = new Equipment(1L, "eqName", equipmentType);
        Equipment addedEquipment = new Equipment(15L, equipmentToAdd.getName(), equipmentToAdd.getEquipmentType());
        when(equipmentRepository.getMaxId()).thenReturn(0L);
        when(equipmentTypeService.get(equipmentTypeId)).thenReturn(equipmentType);
        when(equipmentRepository.save(equipmentToAdd)).thenReturn(addedEquipment);
        Assertions.assertEquals(addedEquipment.getId(),
                                equipmentService.add(equipmentToAdd.getName(), equipmentTypeId).getId());

        verify(equipmentRepository).save(equipmentToAdd);
    }

    @Test
    public void testUpdate() {
        Long equipmentTypeId = 2L;
        EquipmentType equipmentType = new EquipmentType("", "");
        Equipment equipmentToAdd = new Equipment(0L, "eqName", equipmentType);
        Equipment addedEquipment = new Equipment(15L, equipmentToAdd.getName(), equipmentToAdd.getEquipmentType());
        when(equipmentTypeService.get(equipmentTypeId)).thenReturn(equipmentType);
        when(equipmentRepository.findById(addedEquipment.getId())).thenReturn(Optional.of(addedEquipment));
        when(equipmentRepository.save(equipmentToAdd)).thenReturn(addedEquipment);

        Equipment update = equipmentService.update(addedEquipment.getId(),
                                                   equipmentToAdd.getName(),
                                                   equipmentTypeId,
                                                   Collections.emptyMap());
        Assertions.assertEquals(update.getId(), addedEquipment.getId());
        Assertions.assertEquals(update.getName(), equipmentToAdd.getName());
    }

    @Test
    public void testListGroupedByTypes() {
        Map<EquipmentType, List<Equipment>> result = Collections.singletonMap(EQUIPMENT.getEquipmentType(),
                                                                              Collections.singletonList(EQUIPMENT));

        when(equipmentRepository.findFiltered(null, null, null)).thenReturn(Collections.singletonList(EQUIPMENT));

        Assertions.assertEquals(result, equipmentService.listGroupedByTypes(null, null));
    }

}
