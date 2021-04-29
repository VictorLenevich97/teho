package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import va.rit.teho.entity.equipment.*;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.repository.equipment.EquipmentPerFormationFailureIntensityRepository;
import va.rit.teho.repository.equipment.EquipmentPerFormationRepository;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.equipment.EquipmentPerFormationService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.formation.FormationService;
import va.rit.teho.service.implementation.equipment.EquipmentPerFormationServiceImpl;
import va.rit.teho.service.intensity.IntensityService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class EquipmentPerFormationServiceImplTest {

    private final static Formation FORMATION = new Formation(1L, "short", "full");

    private final CalculationService calculationService = Mockito.mock(CalculationService.class);
    private final FormationService formationService = Mockito.mock(FormationService.class);
    private final EquipmentService equipmentService = Mockito.mock(EquipmentService.class);
    private final IntensityService intensityService = Mockito.mock(IntensityService.class);

    private final EquipmentPerFormationRepository equipmentPerFormationRepository = Mockito.mock(EquipmentPerFormationRepository.class);
    private final EquipmentPerFormationFailureIntensityRepository equipmentPerFormationFailureIntensityRepository = Mockito.mock(EquipmentPerFormationFailureIntensityRepository.class);

    private final EquipmentPerFormationService service =
            new EquipmentPerFormationServiceImpl(
                    calculationService,
                    formationService,
                    equipmentService,
                    intensityService,
                    equipmentPerFormationRepository,
                    equipmentPerFormationFailureIntensityRepository);


    @Test
    public void testAddEquipmentToFormation() {
        Long equipmentId = 1L;
        List<Long> equipmentIds = Collections.singletonList(equipmentId);
        Long amount = 15L;
        Equipment e = new Equipment(equipmentId, "n", new EquipmentType("", ""));
        when(formationService.get(FORMATION.getId())).thenReturn(FORMATION);
        when(equipmentService.list(equipmentIds)).thenReturn(Collections.singletonList(e));
        EquipmentPerFormation epf = new EquipmentPerFormation(FORMATION, e, amount);

        service.addEquipmentToFormation(FORMATION.getId(), equipmentIds, amount);

        verify(equipmentPerFormationRepository).saveAll(Collections.singletonList(epf));
    }

    @Test
    public void testAddExistingEquipmentToFormationThrowsError() {
        Long equipmentId = 1L;
        List<Long> equipmentIds = Collections.singletonList(equipmentId);
        Long amount = 15L;
        Equipment e = new Equipment(equipmentId, "n", new EquipmentType("", ""));
        when(formationService.get(FORMATION.getId())).thenReturn(FORMATION);
        when(equipmentService.list(equipmentIds)).thenReturn(Collections.singletonList(e));
        EquipmentPerFormation epf = new EquipmentPerFormation(FORMATION, e, amount);
        when(equipmentPerFormationRepository.findAllByFormationId(FORMATION.getId(), equipmentIds)).thenReturn(Collections.singletonList(epf));

        Assertions.assertThrows(AlreadyExistsException.class, () -> service.addEquipmentToFormation(FORMATION.getId(), equipmentIds, amount));
    }

    @Test
    public void testUpdateEquipmentInFormation() {
        Long formationId = 1L;
        Long equipmentId = 1L;
        List<Long> equipmentIds = Collections.singletonList(equipmentId);
        Long amount = 15L;
        Long newAmount = 20L;
        Equipment e = new Equipment(equipmentId, "n", new EquipmentType("", ""));
        when(formationService.get(FORMATION.getId())).thenReturn(FORMATION);
        when(equipmentService.list(equipmentIds)).thenReturn(Collections.singletonList(e));
        EquipmentPerFormation epf = new EquipmentPerFormation(FORMATION, e, amount);
        EquipmentPerFormation newEPF = new EquipmentPerFormation(FORMATION, e, newAmount);
        when(equipmentPerFormationRepository.findById(new EquipmentPerFormationPK(formationId, equipmentId)))
                .thenReturn(Optional.of(epf));

        service.updateEquipmentInFormation(formationId, equipmentId, newAmount);

        verify(equipmentPerFormationRepository).save(newEPF);
    }

    @Test
    public void testSetNewEquipmentPerFormationDailyFailure() {
        UUID sessionId = UUID.randomUUID();
        Long formationId = 1L;
        Long equipmentId = 1L;
        Long repairTypeId = 2L;
        Long stageId = 3L;
        Double dailyFailure = 123.123;
        Equipment e = new Equipment(equipmentId, "n", new EquipmentType("", ""));
        when(formationService.get(FORMATION.getId())).thenReturn(FORMATION);
        when(equipmentService.get(equipmentId)).thenReturn(e);
        when(equipmentPerFormationFailureIntensityRepository.find(sessionId, formationId, equipmentId, stageId, repairTypeId))
                .thenReturn(Optional.empty());
        EquipmentPerFormationFailureIntensity expected = new EquipmentPerFormationFailureIntensity(sessionId,
                formationId,
                equipmentId,
                stageId,
                repairTypeId,
                dailyFailure);

        service.setEquipmentPerFormationDailyFailure(sessionId, formationId, equipmentId, repairTypeId, stageId, dailyFailure);

        InOrder inOrder = Mockito.inOrder(equipmentPerFormationFailureIntensityRepository);
        inOrder.verify(equipmentPerFormationFailureIntensityRepository).find(sessionId, formationId, equipmentId, stageId, repairTypeId);
        inOrder.verify(equipmentPerFormationFailureIntensityRepository).save(expected);
        verifyNoMoreInteractions(equipmentPerFormationFailureIntensityRepository);
    }


}
