package va.rit.teho.service.implementation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.RepairStationType;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.RepairStationTypeService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

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
}
