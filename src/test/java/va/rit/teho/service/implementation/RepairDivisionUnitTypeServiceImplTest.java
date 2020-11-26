//package va.rit.teho.service.implementation;
//
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import va.rit.teho.entity.repairdivision.RepairStationType;
//import va.rit.teho.exception.AlreadyExistsException;
//import va.rit.teho.exception.IncorrectParamException;
//import va.rit.teho.exception.NotFoundException;
//import va.rit.teho.repository.repairdivision.RepairStationTypeRepository;
//import va.rit.teho.service.implementation.repairdivision.RepairDivisionUnitTypeServiceImpl;
//import va.rit.teho.service.repairdivision.RepairDivisionUnitTypeService;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//public class RepairDivisionUnitTypeServiceImplTest {
//
//    private final RepairStationTypeRepository repairStationTypeRepository = Mockito.mock(RepairStationTypeRepository.class);
//
//    private final RepairDivisionUnitTypeService repairDivisionUnitTypeService =
//            new RepairDivisionUnitTypeServiceImpl(repairStationTypeRepository);
//
//    @Test
//    public void testListTypes() {
//        RepairStationType repairStationType = new RepairStationType("type");
//        List<RepairStationType> repairStationTypeList = Collections.singletonList(repairStationType);
//        when(repairStationTypeRepository.findAll()).thenReturn(repairStationTypeList);
//
//        Assertions.assertEquals(repairStationTypeList, repairDivisionUnitTypeService.listTypes());
//    }
//
//    @Test
//    public void testAddType() {
//        RepairStationType repairStationType = new RepairStationType("type");
//        RepairStationType addedRepairStationType = new RepairStationType(repairStationType.getName()
//        when(repairStationTypeRepository.save(repairStationType)).thenReturn(addedRepairStationType);
//
//        Assertions.assertEquals(
//                addedRepairStationType.getId(),
//                repairDivisionUnitTypeService.addType(                        repairStationType.getName()));
//    }
//
//    @Test
//    public void testAddTypeAlreadyExists() {
//        RepairStationType repairStationType = new RepairStationType("type");
//        when(repairStationTypeRepository.findByName(repairStationType.getName())).thenReturn(Optional.of(
//                repairStationType));
//
//        Assertions.assertThrows(AlreadyExistsException.class, () ->
//                repairDivisionUnitTypeService.addType(
//                        repairStationType.getName(),
//                        repairStationType.getWorkingHoursMin(),
//                        repairStationType.getWorkingHoursMax()));
//    }
//
//
//    @Test
//    public void testAddIncorrectParams() {
//        RepairStationType repairStationType = new RepairStationType("type", 3, 2);
//        when(repairStationTypeRepository.findByName(repairStationType.getName())).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(IncorrectParamException.class, () ->
//                repairDivisionUnitTypeService.addType(
//                        repairStationType.getName(),
//                        repairStationType.getWorkingHoursMin(),
//                        repairStationType.getWorkingHoursMax()));
//    }
//
//
//    @Test
//    public void testUpdateType() {
//        Long repairStationTypeId = 3L;
//        RepairStationType repairStationType = new RepairStationType("type");
//        RepairStationType addedRepairStationType = new RepairStationType(repairStationType.getName(),
//                                                                         repairStationType.getWorkingHoursMin(),
//                                                                         repairStationType.getWorkingHoursMax());
//        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));
//        when(repairStationTypeRepository.save(repairStationType)).thenReturn(addedRepairStationType);
//
//        repairDivisionUnitTypeService.updateType(
//                repairStationTypeId,
//                repairStationType.getName(),
//                repairStationType.getWorkingHoursMin(),
//                repairStationType.getWorkingHoursMax());
//
//        verify(repairStationTypeRepository).findById(repairStationTypeId);
//        verify(repairStationTypeRepository).save(repairStationType);
//    }
//
//    @Test
//    public void testUpdateTypeNotFound() {
//        Long repairStationTypeId = 3L;
//        RepairStationType repairStationType = new RepairStationType("type");
//        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(NotFoundException.class, () -> repairDivisionUnitTypeService.updateType(
//                repairStationTypeId,
//                repairStationType.getName(),
//                repairStationType.getWorkingHoursMin(),
//                repairStationType.getWorkingHoursMax()));
//
//        verify(repairStationTypeRepository).findById(repairStationTypeId);
//        verifyNoMoreInteractions(repairStationTypeRepository);
//    }
//
//    @Test
//    public void testUpdateIncorrectParams() {
//        Long repairStationTypeId = 3L;
//        RepairStationType repairStationType = new RepairStationType("type", 5, 2);
//        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));
//
//        Assertions.assertThrows(IncorrectParamException.class, () -> repairDivisionUnitTypeService.updateType(
//                repairStationTypeId,
//                repairStationType.getName(),
//                repairStationType.getWorkingHoursMin(),
//                repairStationType.getWorkingHoursMax()));
//
//        verify(repairStationTypeRepository).findById(repairStationTypeId);
//        verifyNoMoreInteractions(repairStationTypeRepository);
//    }
//
//    @Test
//    public void testGetById() {
//        Long repairStationTypeId = 2L;
//        RepairStationType repairStationType = new RepairStationType("type", 1, 5);
//        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));
//
//        Assertions.assertEquals(repairStationType, repairDivisionUnitTypeService.get(repairStationTypeId));
//    }
//
//    @Test
//    public void testGetByIdNotFound() {
//        Long repairStationTypeId = 2L;
//        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(NotFoundException.class, () -> repairDivisionUnitTypeService.get(repairStationTypeId));
//    }
//}
