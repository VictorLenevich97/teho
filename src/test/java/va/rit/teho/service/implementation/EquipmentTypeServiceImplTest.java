package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.implementation.equipment.EquipmentTypeServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EquipmentTypeServiceImplTest {

    private final EquipmentTypeRepository equipmentTypeRepository = Mockito.mock(EquipmentTypeRepository.class);

    private final EquipmentTypeService equipmentTypeService = new EquipmentTypeServiceImpl(equipmentTypeRepository);

    @Test
    public void testListTypes() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        when(equipmentTypeRepository.findAll()).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentTypeService.listTypes(Collections.emptyList()));
    }

    @Test
    public void testListTypesWithFilter() {
        List<EquipmentType> equipmentTypes = Collections.singletonList(new EquipmentType("s", "f"));
        Long equipmentId = 10L;
        when(equipmentTypeRepository.findAllById(Collections.singletonList(equipmentId))).thenReturn(equipmentTypes);

        Assertions.assertEquals(equipmentTypes, equipmentTypeService.listTypes(Collections.singletonList(equipmentId)));
    }

    @Test
    public void testAddType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        EquipmentType addedEquipmentType = new EquipmentType(equipmentType.getShortName(), equipmentType.getFullName());
        equipmentType.setId(2L);
        when(equipmentTypeRepository.findByFullName(equipmentType.getFullName())).thenReturn(Optional.empty());
        when(equipmentTypeRepository.getMaxId()).thenReturn(1L);
        when(equipmentTypeRepository.save(equipmentType)).thenReturn(addedEquipmentType);

        Assertions.assertEquals(
                addedEquipmentType,
                equipmentTypeService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
    }

    @Test
    public void testAddTypeAlreadyExists() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        when(equipmentTypeRepository.findByFullName(equipmentType.getFullName())).thenReturn(Optional.of(equipmentType));

        Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> equipmentTypeService.addType(equipmentType.getShortName(), equipmentType.getFullName()));
    }

    @Test
    public void testUpdateType() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(15L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.of(equipmentType));

        equipmentTypeService.updateType(equipmentType.getId(),
                                        equipmentType.getShortName(),
                                        equipmentType.getFullName());

        verify(equipmentTypeRepository).save(equipmentType);
    }


    @Test
    public void testUpdateTypeNotFound() {
        EquipmentType equipmentType = new EquipmentType("short", "full");
        equipmentType.setId(15L);

        when(equipmentTypeRepository.findById(equipmentType.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> equipmentTypeService.updateType(equipmentType.getId(),
                                                                                               equipmentType.getShortName(),
                                                                                               equipmentType.getFullName()));

        verify(equipmentTypeRepository).findById(equipmentType.getId());
        verifyNoMoreInteractions(equipmentTypeRepository);
    }
}
