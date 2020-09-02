package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.*;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.RepairStationService;
import va.rit.teho.service.implementation.RepairStationServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepairStationServiceImplTest {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository =
            Mockito.mock(RepairStationEquipmentCapabilitiesRepository.class);
    private final RepairStationRepository repairStationRepository = Mockito.mock(RepairStationRepository.class);
    private final RepairStationTypeRepository repairStationTypeRepository = Mockito.mock(RepairStationTypeRepository.class);
    private final BaseService baseService = Mockito.mock(BaseService.class);

    private final RepairStationService repairStationService =
            new RepairStationServiceImpl(
                    repairStationEquipmentCapabilitiesRepository,
                    repairStationRepository,
                    repairStationTypeRepository,
                    baseService);

    @Test
    public void testList() {
        Base b = new Base("s", "f");
        RepairStationType repairStationType = new RepairStationType("first", 1, 2);
        List<RepairStation> repairStations =
                Collections
                        .singletonList(new RepairStation("station", repairStationType, b, 2));
        when(repairStationRepository.findAll()).thenReturn(repairStations);

        Assertions.assertEquals(repairStations, repairStationService.list());
    }

    @Test
    public void testGet() {
        Long repairStationId = 12L;
        Base b = new Base("s", "f");
        RepairStationType repairStationType = new RepairStationType("first", 1, 2);
        RepairStation repairStation = new RepairStation("station", repairStationType, b, 2);
        List<RepairStationEquipmentStaff> repairStationEquipmentStaffList =
                Collections
                        .singletonList(new RepairStationEquipmentStaff(null, 2, 1));
        Pair<RepairStation, List<RepairStationEquipmentStaff>> result = Pair.of(repairStation,
                                                                                repairStationEquipmentStaffList);

        when(repairStationRepository.findById(repairStationId)).thenReturn(Optional.of(repairStation));
        when(repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId)).thenReturn(
                repairStationEquipmentStaffList);

        Assertions.assertEquals(result, repairStationService.get(repairStationId));
    }

    @Test
    public void testAdd() {
        Long baseId = 1L;
        Long repairStationTypeId = 2L;
        Base b = new Base("s", "f");
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        when(baseService.get(baseId)).thenReturn(b);
        when(repairStationTypeRepository.findById(repairStationTypeId)).thenReturn(Optional.of(repairStationType));
        RepairStation repairStation = new RepairStation("repair-station", repairStationType, b, 2);
        RepairStation addedRepairStation = new RepairStation(repairStation.getName(),
                                                             repairStationType,
                                                             b,
                                                             repairStation.getStationAmount());
        addedRepairStation.setId(3L);
        when(repairStationRepository.save(repairStation)).thenReturn(addedRepairStation);
        Assertions.assertEquals(addedRepairStation.getId(),
                                repairStationService.add(repairStation.getName(), baseId, repairStationTypeId, 2));
    }

    @Test
    public void testListTypes() {
        RepairStationType repairStationType = new RepairStationType("type", 1, 2);
        List<RepairStationType> repairStationTypeList = Collections.singletonList(repairStationType);
        when(repairStationTypeRepository.findAll()).thenReturn(repairStationTypeList);

        Assertions.assertEquals(repairStationTypeList, repairStationService.listTypes());
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
                repairStationService.addType(
                        repairStationType.getName(),
                        repairStationType.getWorkingHoursMin(),
                        repairStationType.getWorkingHoursMax()));
    }

    @Test
    public void testSetEquipmentStaff() {
        Long repairStationId = 1L;
        Long equipmentId = 2L;
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(new EquipmentPerRepairStation(
                1L,
                2L), 2, 1);

        repairStationService.setEquipmentStaff(repairStationId,
                                               equipmentId,
                                               repairStationEquipmentStaff.getAvailableStaff(),
                                               repairStationEquipmentStaff.getTotalStaff());

        verify(repairStationEquipmentCapabilitiesRepository).save(repairStationEquipmentStaff);
    }

}
