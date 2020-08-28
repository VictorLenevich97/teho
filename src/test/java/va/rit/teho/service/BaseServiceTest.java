package va.rit.teho.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.Base;
import va.rit.teho.repository.BaseRepository;
import va.rit.teho.repository.EquipmentPerBaseRepository;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.service.implementation.BaseServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseServiceTest {

    private final BaseRepository baseRepository = Mockito.mock(BaseRepository.class);

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);

    private final EquipmentPerBaseRepository equipmentPerBaseRepository = Mockito.mock(EquipmentPerBaseRepository.class);

    private final BaseService service =
            new BaseServiceImpl(baseRepository, equipmentRepository, equipmentPerBaseRepository);

    @Test
    public void testAdd() {
        Base b = new Base("short", "full");
        b.setId(1L);

        when(baseRepository.save(any())).thenReturn(b);

        Assertions.assertEquals(service.add(b.getShortName(), b.getFullName()), b.getId());

        verify(baseRepository).save(new Base(b.getShortName(), b.getFullName()));
    }


}
