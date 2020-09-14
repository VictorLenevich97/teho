package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.EquipmentTypeService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class EquipmentTypeServiceImplTest {

    private final EquipmentTypeRepository equipmentTypeRepository = Mockito.mock(EquipmentTypeRepository.class);
    private final EquipmentSubTypeRepository equipmentSubTypeRepository = Mockito.mock(EquipmentSubTypeRepository.class);

    private final EquipmentTypeService equipmentTypeService = new EquipmentTypeServiceImpl(equipmentTypeRepository,
                                                                                           equipmentSubTypeRepository);

    @Test
    public void testListTypes() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        when(equipmentTypeRepository.findAll()).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentTypeService.listTypes(Collections.emptyList()));
    }

    @Test
    public void testAddType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentType addedEquipmentType = new EquipmentType(equipmentType.getShortName(), equipmentType.getFullName());
        addedEquipmentType.setId(15L);
        when(equipmentTypeRepository.save(equipmentType)).thenReturn(addedEquipmentType);

        Assertions.assertEquals(
                addedEquipmentType.getId(),
                equipmentTypeService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
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
                equipmentTypeService.addSubType(equipmentType.getId(),
                                                equipmentSubType.getShortName(),
                                                equipmentSubType.getFullName()));
    }

    @Test
    public void testListTypesWithSubTypes() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentSubType equipmentSubType = new EquipmentSubType("s", "f", equipmentType);
        Map<EquipmentType, List<EquipmentSubType>> result = Collections.singletonMap(equipmentType,
                                                                                     Collections.singletonList(
                                                                                             equipmentSubType));

        when(equipmentSubTypeRepository.findByIds(Collections.emptyList(),
                                                  Collections.emptyList()))
                .thenReturn(Collections.singletonList(equipmentSubType));

        Assertions.assertEquals(result,
                                equipmentTypeService.listTypesWithSubTypes(Collections.emptyList(),
                                                                           Collections.emptyList()));
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

        Assertions.assertEquals(result, equipmentTypeService.getTypeWithSubTypes(equipmentTypeId));
    }
}
