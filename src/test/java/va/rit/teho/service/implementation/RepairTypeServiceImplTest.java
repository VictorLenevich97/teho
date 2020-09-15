package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.RepairType;
import va.rit.teho.repository.RepairTypeRepository;
import va.rit.teho.service.RepairTypeService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class RepairTypeServiceImplTest {
    private final RepairTypeRepository repairTypeRepository = Mockito.mock(RepairTypeRepository.class);
    private final RepairTypeService repairTypeService = new RepairTypeServiceImpl(repairTypeRepository);

    @Test
    public void testList() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", true));

        when(repairTypeRepository.findAll()).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list());
    }

    @Test
    public void testListRepairableTrue() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", true));

        when(repairTypeRepository.findAllByRepairableTrue()).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list(true));
    }

    @Test
    public void testListRepairableFalse() {
        List<RepairType> repairTypeList = Collections.singletonList(new RepairType("a", true));

        when(repairTypeRepository.findAllByRepairableFalse()).thenReturn(repairTypeList);

        Assertions.assertEquals(repairTypeList, repairTypeService.list(false));
    }
}
