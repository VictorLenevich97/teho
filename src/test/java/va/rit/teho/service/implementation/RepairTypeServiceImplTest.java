package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.repository.common.RepairTypeRepository;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.implementation.common.RepairTypeServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class RepairTypeServiceImplTest {
    private final RepairTypeRepository repairTypeRepository = Mockito.mock(RepairTypeRepository.class);
    private final RepairTypeService repairTypeService = new RepairTypeServiceImpl(repairTypeRepository);

    @Test
    public void testList() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", "", true, false, false));

        when(repairTypeRepository.findAll()).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list());
    }

    @Test
    public void testListRepairableTrue() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", "", true, false, false));

        when(repairTypeRepository.findAllByCalculatable(true)).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list(true));
    }

    @Test
    public void testListRepairableFalse() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", "", true, false, false));

        when(repairTypeRepository.findAllByCalculatable(false)).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list(false));
    }
}
