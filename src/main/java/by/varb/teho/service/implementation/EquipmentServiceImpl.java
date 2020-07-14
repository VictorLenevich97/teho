package by.varb.teho.service.implementation;

import by.varb.teho.model.Equipment;
import by.varb.teho.model.EquipmentType;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.repository.EquipmentTypeRepository;
import by.varb.teho.service.EquipmentService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static by.varb.teho.enums.Equipment.*;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    private final EquipmentTypeRepository equipmentTypeRepository;

    public static final Logger log = LogManager.getLogger(EquipmentServiceImpl.class);

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    private static final String EMPTY_STRING = "";

    public List<Equipment> getEquipmentInfo() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public void addNewVehicle(Map<String, Object> data) throws ChangeSetPersister.NotFoundException {
        if (checkDataForNull(data) || checkDataIfEmpty(data)) {
            log.error("Недостаточно данных - одно или несколько полей не заполнены");
            return;
        }
        List<Equipment> equipmentList = (List<Equipment>) equipmentRepository.findAll();
        for (Equipment element : equipmentList) {
            if (checkEquipmentExistence(data, element)) {
                log.error("Такой образец ВВСТ уже существует в базе даныых");
                return;
            }
        }
        Equipment equipment = new Equipment();
        EquipmentType equipmentType = equipmentTypeRepository.findById((Long) data.get(EQUIPMENT_TYPE_KEY)).orElseThrow(ChangeSetPersister.NotFoundException::new);
        equipment.setEquipmentType(equipmentType);
        equipment.setName((String) data.get(NAME_KEY));
        equipmentRepository.save(equipment);
    }

    @Override
    public List<EquipmentType> getEquipmentTypes() {
        return (List<EquipmentType>) equipmentTypeRepository.findAll();
    }

    private boolean checkDataForNull(Map<String, Object> data) {
        return data.get(EQUIPMENT_TYPE_KEY) == null || data.get(NAME_KEY) == null;
    }


    private boolean checkDataIfEmpty(Map<String, Object> data) {
        return data.get(EQUIPMENT_TYPE_KEY).equals(EMPTY_STRING) || data.get(NAME_KEY).equals(EMPTY_STRING);
    }

    private boolean checkEquipmentExistence(Map<String, Object> data, Equipment equipment) {
        return data.get(NAME_KEY).equals(equipment.getName()) ||
                data.get(EQUIPMENT_TYPE_KEY).equals(equipment.getEquipmentType().toString());
    }
    
	//расчёт предполагаемого выхода из строя групп ВВСТ
	private double [] calculationEquipmentFailure(int [] groups, double [] dailyAverageLoss, double kM) {
		double[] wJ = new double [groups.length];
		for (int i = 0; i < groups.length; i++) {
			wJ[i] = (groups[i] * dailyAverageLoss[i]) / 100 * kM;
		}
		return wJ;
	}
	
	//вычисление количества образцов ВВСТ, требующих ремонта(текущий и средний)
	private double [][] сalculationEquipmentRequiringRepair(int[] qMinArray, int[] qMaxArray, double [] wJ, int [] qjMax) {
		double[][] wjArray = new double[wJ.length][qMinArray.length];
        for (int i = 0; i < wJ.length; i++) 
        	for (int j = 0; j < qMinArray.length; j++) 
	        {
	        	wjArray[i][j] = Math.abs((wJ[i] * (Math.sin((Math.PI * qMaxArray[j]) / (2 * qjMax[i])) - Math.sin((Math.PI * qMinArray[j]) / (2 * qjMax[i])))));
	        }
        return wjArray;
	}
	
	//расчёт средней трудоёмкости ремонта ВВСТ
	private double [][] calculationAverageComplexityRepair(double [][] wjArray, int[] qMaxArray) {
		double[][] qjArray = new double[wjArray.length][wjArray[0].length];
		for (int i = 0; i < qjArray.length; i++) 
			for (int j = 0; j < qMaxArray.length; j++) 
			{
				qjArray[i][j] = 0.75 * wjArray[i][j] * qMaxArray[i];
			}
		return qjArray;
	}
}
