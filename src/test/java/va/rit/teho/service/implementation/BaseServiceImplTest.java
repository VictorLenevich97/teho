package va.rit.teho.service.implementation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.*;
import va.rit.teho.exception.BaseNotFoundException;
import va.rit.teho.exception.EquipmentNotFoundException;
import va.rit.teho.repository.BaseRepository;
import va.rit.teho.repository.EquipmentPerBaseRepository;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.service.BaseService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BaseServiceImplTest {

    private final static Base BASE = new Base("short", "full");

    private final BaseRepository baseRepository = Mockito.mock(BaseRepository.class);

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);

    private final EquipmentPerBaseRepository equipmentPerBaseRepository = Mockito.mock(EquipmentPerBaseRepository.class);

    private final BaseService service =
            new BaseServiceImpl(baseRepository, equipmentRepository, equipmentPerBaseRepository);

    @Test
    public void testAdd() {
        BASE.setId(1L);

        when(baseRepository.save(any())).thenReturn(BASE);

        Assertions.assertEquals(service.add(BASE.getShortName(), BASE.getFullName()), BASE.getId());

        verify(baseRepository).save(new Base(BASE.getShortName(), BASE.getFullName()));
    }

    @Test
    public void testAddEquipmentToBase() {
        Long baseId = 1L;
        Long equipmentId = 1L;
        int intensity = 10;
        int amount = 15;
        Equipment e = new Equipment("n", new EquipmentSubType("", "", new EquipmentType("", "")));
        when(baseRepository.findById(baseId)).thenReturn(Optional.of(BASE));
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(e));
        EquipmentPerBase epb = new EquipmentPerBase(BASE, e, intensity, amount);

        service.addEquipmentToBase(baseId, equipmentId, intensity, amount);

        verify(equipmentPerBaseRepository).save(epb);
    }

    @Test
    public void testAddEquipmentToBaseBaseNotFound() {
        Long baseId = 1L;
        when(baseRepository.findById(baseId)).thenReturn(Optional.empty());
        Assertions.assertThrows(BaseNotFoundException.class, () -> service.addEquipmentToBase(1L, 1L, 0, 0));
    }

    @Test
    public void testAddEquipmentToBaseEquipmentNotFound() {
        Long baseId = 1L;
        Long equipmentId = 1L;
        when(baseRepository.findById(baseId)).thenReturn(Optional.of(BASE));
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EquipmentNotFoundException.class, () -> service.addEquipmentToBase(1L, 1L, 0, 0));
    }

    @Test
    public void testGet() {
        Long baseId = 1L;
        when(baseRepository.findById(baseId)).thenReturn(Optional.of(BASE));

        Assertions.assertEquals(BASE, service.get(baseId));
    }

    @Test
    public void testGetBaseNotFound() {
        Long baseId = 1L;
        when(baseRepository.findById(baseId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BaseNotFoundException.class, () -> service.get(baseId));
    }

    @Test
    public void testList() {
        when(baseRepository.findAll()).thenReturn(Collections.singletonList(BASE));
        Assertions.assertEquals(Collections.singletonList(BASE), service.list());
    }

}
