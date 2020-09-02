package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.implementation.EquipmentServiceImpl;

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
    private final EquipmentTypeRepository equipmentTypeRepository = Mockito.mock(EquipmentTypeRepository.class);
    private final EquipmentSubTypeRepository equipmentSubTypeRepository = Mockito.mock(EquipmentSubTypeRepository.class);

    private final EquipmentService equipmentService =
            new EquipmentServiceImpl(equipmentRepository, equipmentTypeRepository, equipmentSubTypeRepository);

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
    public void testListTypes() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        when(equipmentTypeRepository.findAll()).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentService.listTypes());
    }

    @Test
    public void testAddType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentType addedEquipmentType = new EquipmentType(equipmentType.getShortName(), equipmentType.getFullName());
        addedEquipmentType.setId(15L);
        when(equipmentTypeRepository.save(equipmentType)).thenReturn(addedEquipmentType);

        Assertions.assertEquals(
                addedEquipmentType.getId(),
                equipmentService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
    }

    @Test
    public void testAddSubType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(5L);
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        EquipmentSubType addedEquipmentSubType = new EquipmentSubType(equipmentSubType.getShortName(),
                                                                      equipmentSubType.getFullName(),
                                                                      equipmentType);
        addedEquipmentSubType.setId(10L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.save(equipmentSubType)).thenReturn(addedEquipmentSubType);

        Assertions.assertEquals(
                addedEquipmentSubType.getId(),
                equipmentService.addSubType(equipmentType.getId(),
                                            equipmentSubType.getShortName(),
                                            equipmentSubType.getFullName()));
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

        when(equipmentRepository.getEquipmentGroupedByType()).thenReturn(result);

        Assertions.assertEquals(result, equipmentService.listGroupedByTypes());
    }

    @Test
    public void testListTypesWithSubTypes() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Map<EquipmentType, List<EquipmentSubType>> result = Collections.singletonMap(equipmentType,
                                                                                     Collections.singletonList(
                                                                                             equipmentSubType));

        when(equipmentSubTypeRepository.findAllGroupedByType()).thenReturn(result);

        Assertions.assertEquals(result, equipmentService.listTypesWithSubTypes());
    }

    @Test
    public void testGetTypeWithSubTypes() {
        Long equipmentTypeId = 11L;
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Pair<EquipmentType, List<EquipmentSubType>> result = Pair.of(equipmentType,
                                                                     Collections.singletonList(equipmentSubType));

        when(equipmentTypeRepository.findById(equipmentTypeId)).thenReturn(Optional.of(equipmentType));
        when(equipmentSubTypeRepository.findByEquipmentTypeId(equipmentTypeId)).thenReturn(result.getRight());

        Assertions.assertEquals(result, equipmentService.getTypeWithSubTypes(equipmentTypeId));
    }
}
