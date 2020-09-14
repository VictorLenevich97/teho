package va.rit.teho.service.implementation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.RepairStationTypeService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class RepairStationTypeServiceImplTest {

    private final RepairStationTypeRepository repairStationTypeRepository = Mockito.mock(RepairStationTypeRepository.class);

    private final RepairStationTypeService repairStationTypeService = new RepairStationTypeServiceImpl(
            repairStationTypeRepository);

    @Test
    public void testListTypes() {
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        List<RepairStationType> repairStationTypeList = Collections.singletonList(repairStationType);
        when(repairStationTypeRepository.findAll()).thenReturn(repairStationTypeList);

        Assertions.assertEquals(repairStationTypeList, repairStationTypeService.listTypes());
    }

    @Test
    public void testAddType() {
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        RepairStationType addedRepairStationType = new RepairStationType(repairStationType.getName(),
                                                                         repairStationType.getWorkingHoursMin(),
                                                                         repairStationType.getWorkingHoursMax());
        when(repairStationTypeRepository.save(repairStationType)).thenReturn(addedRepairStationType);

        Assertions.assertEquals(
                addedRepairStationType.getId(),
                repairStationTypeService.addType(
                        repairStationType.getName(),
                        repairStationType.getWorkingHoursMin(),
                        repairStationType.getWorkingHoursMax()));
    }

    @Test
    public void testAddTypeAlreadyExists() {
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        when(repairStationTypeRepository.findByName(repairStationType.getName())).thenReturn(Optional.of(
                repairStationType));

        Assertions.assertThrows(AlreadyExistsException.class, () ->
                repairStationTypeService.addType(
                        repairStationType.getName(),
                        repairStationType.getWorkingHoursMin(),
                        repairStationType.getWorkingHoursMax()));
    }


    @Test
    public void testAddIncorrectParams() {
        RepairStationType repairStationType = new RepairStationType("type", 3, 2);
        when(repairStationTypeRepository.findByName(repairStationType.getName())).thenReturn(Optional.empty());

        Assertions.assertThrows(IncorrectParamException.class, () ->
                repairStationTypeService.addType(
                        repairStationType.getName(),
                        repairStationType.getWorkingHoursMin(),
                        repairStationType.getWorkingHoursMax()));
    }


    @Test
    public void testUpdateType() {
        Long repairStationTypeId = 3L;
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        RepairStationType addedRepairStationType = new RepairStationType(repairStationType.getName(),
                                                                         repairStationType.getWorkingHoursMin(),
                                                                         repairStationType.getWorkingHoursMax());
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));
        when(repairStationTypeRepository.save(repairStationType)).thenReturn(addedRepairStationType);

        repairStationTypeService.updateType(
                repairStationTypeId,
                repairStationType.getName(),
                repairStationType.getWorkingHoursMin(),
                repairStationType.getWorkingHoursMax());

        verify(repairStationTypeRepository).findById(repairStationTypeId);
        verify(repairStationTypeRepository).save(repairStationType);
    }

    @Test
    public void testUpdateTypeNotFound() {
        Long repairStationTypeId = 3L;
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> repairStationTypeService.updateType(
                repairStationTypeId,
                repairStationType.getName(),
                repairStationType.getWorkingHoursMin(),
                repairStationType.getWorkingHoursMax()));

        verify(repairStationTypeRepository).findById(repairStationTypeId);
        verifyNoMoreInteractions(repairStationTypeRepository);
    }

    @Test
    public void testUpdateIncorrectParams() {
        Long repairStationTypeId = 3L;
        RepairStationType repairStationType = new RepairStationType("type", 5, 2);
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));

        Assertions.assertThrows(IncorrectParamException.class, () -> repairStationTypeService.updateType(
                repairStationTypeId,
                repairStationType.getName(),
                repairStationType.getWorkingHoursMin(),
                repairStationType.getWorkingHoursMax()));

        verify(repairStationTypeRepository).findById(repairStationTypeId);
        verifyNoMoreInteractions(repairStationTypeRepository);
    }

    @Test
    public void testGetById() {
        Long repairStationTypeId = 2L;
        RepairStationType repairStationType = new RepairStationType("type", 1, 5);
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));

        Assertions.assertEquals(repairStationType, repairStationTypeService.get(repairStationTypeId));
    }

    @Test
    public void testGetByIdNotFound() {
        Long repairStationTypeId = 2L;
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> repairStationTypeService.get(repairStationTypeId));
    }
}
