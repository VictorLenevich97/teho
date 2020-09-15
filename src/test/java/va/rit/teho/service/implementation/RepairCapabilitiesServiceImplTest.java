package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.*;
import va.rit.teho.enums.RepairTypeEnum;
import va.rit.teho.repository.CalculatedRepairCapabilitiesPerDayRepository;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.service.CalculationService;
import va.rit.teho.service.RepairCapabilitiesService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepairCapabilitiesServiceImplTest {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository =
            Mockito.mock(RepairStationEquipmentCapabilitiesRepository.class);
    private final CalculatedRepairCapabilitiesPerDayRepository calculatedRepairCapabilitiesPerDayRepository =
            Mockito.mock(CalculatedRepairCapabilitiesPerDayRepository.class);

    private final CalculationService calculationService = Mockito.mock(CalculationService.class);

    private final RepairCapabilitiesService repairCapabilitiesService =
            new RepairCapabilitiesServiceImpl(
                    repairStationEquipmentCapabilitiesRepository,
                    calculatedRepairCapabilitiesPerDayRepository,
                    calculationService);

    @Test
    public void testGetAllCalculatedRepairCapabilities() {
        testGetCalculatedRepairCapabilities(
                (List<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDayList) ->
                        when(calculatedRepairCapabilitiesPerDayRepository.findByIds(null,
                                                                                    null,
                                                                                    null,
                                                                                    null)).thenReturn(
                                calculatedRepairCapabilitesPerDayList),
                Collections.emptyList());
    }

    private void testGetCalculatedRepairCapabilities(Consumer<List<CalculatedRepairCapabilitesPerDay>> stub,
                                                     List<Long> repairStationIds) {
        Long repairStationId = 1L;
        Long equipmentId = 2L;
        EquipmentPerRepairStation equipmentPerRepairStation = new EquipmentPerRepairStation(repairStationId,
                                                                                            equipmentId);
        Base b = new Base("s", "f");
        RepairStation repairStation =
                new RepairStation("rs", new RepairStationType("", 1, 2), b, 3);
        Equipment e = new Equipment("eq", new EquipmentSubType("s", "f", new EquipmentType("s", "f")));
        List<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDayList = Collections.singletonList(
                new CalculatedRepairCapabilitesPerDay(equipmentPerRepairStation, repairStation, e, 150));
        stub.accept(calculatedRepairCapabilitesPerDayList);
        Map<RepairStation, Map<Equipment, Double>> result = Collections.singletonMap(repairStation,
                                                                                     Collections.singletonMap(e,
                                                                                                              calculatedRepairCapabilitesPerDayList
                                                                                                                      .get(0)
                                                                                                                      .getCapability()));

        Assertions.assertEquals(result, repairCapabilitiesService.getCalculatedRepairCapabilities(repairStationIds, null, null, null));
    }

    @Test
    public void testGetCalculatedRepairCapabilitiesByIds() {
        List<Long> repairStationIds = Collections.singletonList(3L);
        testGetCalculatedRepairCapabilities(
                (List<CalculatedRepairCapabilitesPerDay> calculatedRepairCapabilitesPerDayList) ->
                        when(calculatedRepairCapabilitiesPerDayRepository
                                     .findByIds(repairStationIds, null, null, null))
                                .thenReturn(calculatedRepairCapabilitesPerDayList),
                repairStationIds);
    }

    @Test
    public void testCalculateAndUpdateRepairCapabilities() {
        Long repairStationId = 1L;
        Long equipmentId = 2L;
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(new EquipmentPerRepairStation(
                repairStationId,
                equipmentId), 3, 2);
        Equipment equipment = new Equipment("", null);
        int laborInput = 200;
        equipment.setLaborInputPerTypes(Collections.singleton(new EquipmentLaborInputPerType(new RepairType(
                RepairTypeEnum.AVG_REPAIR.getName(), true), laborInput)));
        RepairStation repairStation = new RepairStation("rs", new RepairStationType("rst", 2, 5), null, 0);
        repairStationEquipmentStaff.setRepairStation(repairStation);
        repairStationEquipmentStaff.setEquipment(equipment);

        List<RepairStationEquipmentStaff> repairStationEquipmentStaffList = Collections.singletonList(
                repairStationEquipmentStaff);

        when(repairStationEquipmentCapabilitiesRepository.findAll()).thenReturn(repairStationEquipmentStaffList);
        double calculationResult = 15.4;
        when(calculationService.calculateRepairCapabilities(repairStationEquipmentStaff.getTotalStaff(),
                                                            repairStation.getRepairStationType().getWorkingHoursMax(),
                                                            laborInput)).thenReturn(calculationResult);

        CalculatedRepairCapabilitesPerDay result = new CalculatedRepairCapabilitesPerDay(repairStationEquipmentStaff.getEquipmentPerRepairStation(),
                                                                                         repairStation,
                                                                                         equipment,
                                                                                         calculationResult);

        repairCapabilitiesService.calculateAndUpdateRepairCapabilities();

        verify(calculatedRepairCapabilitiesPerDayRepository).saveAll(Collections.singletonList(result));
    }

    @Test
    public void testCalculateAndUpdateRepairCapabilitiesPerStation() {
        Long repairStationId = 1L;
        Long equipmentId = 2L;
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(new EquipmentPerRepairStation(
                repairStationId,
                equipmentId), 3, 2);
        Equipment equipment = new Equipment("", null);
        int laborInput = 200;
        equipment.setLaborInputPerTypes(Collections.singleton(new EquipmentLaborInputPerType(new RepairType(
                RepairTypeEnum.AVG_REPAIR.getName(), true), laborInput)));
        RepairStation repairStation = new RepairStation("rs", new RepairStationType("rst", 2, 5), null, 0);
        repairStationEquipmentStaff.setRepairStation(repairStation);
        repairStationEquipmentStaff.setEquipment(equipment);

        List<RepairStationEquipmentStaff> repairStationEquipmentStaffList = Collections.singletonList(
                repairStationEquipmentStaff);

        when(repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId)).thenReturn(
                repairStationEquipmentStaffList);
        double calculationResult = 15.4;
        when(calculationService.calculateRepairCapabilities(repairStationEquipmentStaff.getTotalStaff(),
                                                            repairStation.getRepairStationType().getWorkingHoursMax(),
                                                            laborInput)).thenReturn(calculationResult);

        CalculatedRepairCapabilitesPerDay result = new CalculatedRepairCapabilitesPerDay(repairStationEquipmentStaff.getEquipmentPerRepairStation(),
                                                                                         repairStation,
                                                                                         equipment,
                                                                                         calculationResult);

        repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(repairStationId);

        verify(calculatedRepairCapabilitiesPerDayRepository).saveAll(Collections.singletonList(result));
    }
}
