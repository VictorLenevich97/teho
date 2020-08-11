package by.varb.teho.exception;

import by.varb.teho.entity.Equipment;
import by.varb.teho.enums.RepairTypeEnum;

public class RepairTypeLaborInputNotFoundException extends NotFoundException {
    public RepairTypeLaborInputNotFoundException(RepairTypeEnum repairTypeEnum, Equipment equipment) {
        super("Отсутствует значение нормативной трудоемкости " + repairTypeEnum.getName() + " ремонта для ВВСТ с id = " + equipment.getId());
    }
}
