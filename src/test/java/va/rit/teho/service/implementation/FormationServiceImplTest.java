package va.rit.teho.service.implementation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.FormationNotFoundException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.repository.formation.FormationRepository;
import va.rit.teho.repository.equipment.EquipmentPerFormationRepository;
import va.rit.teho.repository.equipment.EquipmentRepository;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.implementation.formation.FormationServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FormationServiceImplTest {

    private final static Formation FORMATION = new Formation(1L, "short", "full");

    private final FormationRepository formationRepository = Mockito.mock(FormationRepository.class);

    private final EquipmentRepository equipmentRepository = Mockito.mock(EquipmentRepository.class);

    private final EquipmentPerFormationRepository equipmentPerFormationRepository = Mockito.mock(
            EquipmentPerFormationRepository.class);

    private final FormationService service = new FormationServiceImpl(formationRepository);

    @Test
    public void testAdd() {
        FORMATION.setId(1L);

        when(formationRepository.getMaxId()).thenReturn(1L);
        when(formationRepository.save(any())).thenReturn(FORMATION);

        Assertions.assertEquals(service.add(FORMATION.getShortName(), FORMATION.getFullName()), FORMATION.getId());

        verify(formationRepository).save(new Formation(2L, FORMATION.getShortName(), FORMATION.getFullName()));
    }

    @Test
    public void testAddEmptyField() {
        FORMATION.setId(1L);

        Assertions.assertThrows(EmptyFieldException.class, () -> service.add(null, FORMATION.getFullName()));

        verifyNoInteractions(formationRepository);
    }

    @Test
    public void testUpdate() {
        FORMATION.setId(1L);

        when(formationRepository.findById(FORMATION.getId())).thenReturn(Optional.of(FORMATION));
        when(formationRepository.save(any())).thenReturn(FORMATION);

        service.update(FORMATION.getId(), FORMATION.getShortName(), FORMATION.getFullName());

        verify(formationRepository).save(FORMATION);
    }
//
//    @Test
//    public void testAddEquipmentToBase() {
//        Long formationId = 1L;
//        Long equipmentId = 1L;
//        int intensity = 10;
//        int amount = 15;
//        Equipment e = new Equipment("n", new EquipmentSubType("", "", new EquipmentType("", "")));
//        when(baseRepository.findById(formationId)).thenReturn(Optional.of(BASE));
//        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(e));
//        EquipmentPerBase epb = new EquipmentPerBase(BASE.getId(), e.getId(), amount);
//
//        service.addEquipmentToBase(formationId, equipmentId, amount);
//
//        verify(equipmentPerBaseRepository).save(epb);
//    }
//
//    @Test
//    public void testAddEquipmentToBaseBaseNotFound() {
//        Long formationId = 1L;
//        when(baseRepository.findById(formationId)).thenReturn(Optional.empty());
//        Assertions.assertThrows(BaseNotFoundException.class, () -> service.addEquipmentToBase(1L, 1L, 0));
//    }
//
//    @Test
//    public void testAddEquipmentToBaseEquipmentNotFound() {
//        Long formationId = 1L;
//        Long equipmentId = 1L;
//        when(baseRepository.findById(formationId)).thenReturn(Optional.of(BASE));
//        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());
//        Assertions.assertThrows(EquipmentNotFoundException.class, () -> service.addEquipmentToBase(1L, 1L, 0));
//    }
//
//    @Test
//    public void testUpdateEquipmentInBase() {
//        Long formationId = 1L;
//        Long equipmentId = 1L;
//        int intensity = 10;
//        int amount = 15;
//        Equipment e = new Equipment("n", new EquipmentSubType("", "", new EquipmentType("", "")));
//        when(baseRepository.findById(formationId)).thenReturn(Optional.of(BASE));
//        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(e));
//        EquipmentPerBase epb = new EquipmentPerBase(BASE.getId(), e.getId(), amount);
//        when(equipmentPerBaseRepository.findById(new EquipmentPerformationId(formationId,
//                                                                        equipmentId))).thenReturn(Optional.of(epb));
//
//        service.updateEquipmentInBase(formationId, equipmentId, amount);
//
//        verify(equipmentPerBaseRepository).save(epb);
//    }

    @Test
    public void testGet() {
        Long formationId = 1L;
        when(formationRepository.findById(formationId)).thenReturn(Optional.of(FORMATION));

        Assertions.assertEquals(FORMATION, service.get(formationId));
    }

    @Test
    public void testGetBaseNotFound() {
        Long formationId = 1L;
        when(formationRepository.findById(formationId)).thenReturn(Optional.empty());

        Assertions.assertThrows(FormationNotFoundException.class, () -> service.get(formationId));
    }

    @Test
    public void testList() {
        when(formationRepository.findAll()).thenReturn(Collections.singletonList(FORMATION));
        Assertions.assertEquals(Collections.singletonList(FORMATION), service.list());
    }

}
