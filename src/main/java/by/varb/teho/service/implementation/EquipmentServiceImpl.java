package by.varb.teho.service.implementation;

import by.varb.teho.dto.AddNewEquipmentDTO;
import by.varb.teho.exception.EmptyFieldException;
import by.varb.teho.exception.NotFoundException;
import by.varb.teho.exception.TehoException;
import by.varb.teho.exception.EquipmentNotUniqueException;
import by.varb.teho.model.Equipment;
import by.varb.teho.model.EquipmentType;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.repository.EquipmentTypeRepository;
import by.varb.teho.service.EquipmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    private final EquipmentTypeRepository equipmentTypeRepository;

    public static final Logger log = LogManager.getLogger(EquipmentServiceImpl.class);

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public List<Equipment> getEquipmentInfo() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public void addNewEquipment(AddNewEquipmentDTO addNewEquipmentDTO) throws TehoException {
        checkDTOFieldsForEmptiness(addNewEquipmentDTO);
        checkNewEquipmentUniqueness(addNewEquipmentDTO);

        EquipmentType equipmentType = equipmentTypeRepository.findById(addNewEquipmentDTO.getEquipmentTypeId())
                .orElseThrow(() -> new NotFoundException("Запись с введённым equipmentTypeId не найдена"));
        Equipment equipment = new Equipment(addNewEquipmentDTO.getName(), equipmentType);
        equipmentRepository.save(equipment);
    }

    private void checkDTOFieldsForEmptiness(AddNewEquipmentDTO addNewEquipmentDTO) throws EmptyFieldException {
        boolean oneOfDTOFieldsIsEmpty = addNewEquipmentDTO.getName() == null || addNewEquipmentDTO.getName().isEmpty()
                || addNewEquipmentDTO.getEquipmentTypeId() == null;

        if (oneOfDTOFieldsIsEmpty) {
            throw new EmptyFieldException();
        }
    }

    private void checkNewEquipmentUniqueness(AddNewEquipmentDTO addNewEquipmentDTO) throws EquipmentNotUniqueException {
        List<Equipment> equipmentList = (List<Equipment>) equipmentRepository.findAll();

        for (Equipment equipment : equipmentList) {
            boolean newEquipmentIsNotUnique = equipment.getName().equals(addNewEquipmentDTO.getName())
                    && equipment.getEquipmentType().getId().equals(addNewEquipmentDTO.getEquipmentTypeId());

            if (newEquipmentIsNotUnique) {
                throw new EquipmentNotUniqueException();
            }
        }
    }

    @Override
    public List<EquipmentType> getEquipmentTypes() {
        return (List<EquipmentType>) equipmentTypeRepository.findAll();
    }
    
}
