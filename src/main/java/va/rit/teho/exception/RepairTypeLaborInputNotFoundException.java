package va.rit.teho.exception;

import va.rit.teho.entity.Equipment;
import va.rit.teho.enums.RepairTypeEnum;

public class RepairTypeLaborInputNotFoundException extends NotFoundException {
    public RepairTypeLaborInputNotFoundException(RepairTypeEnum repairTypeEnum, Equipment equipment) {
        super("Отсутствует значение нормативной трудоемкости " + repairTypeEnum.getName() + " ремонта для ВВСТ с id = " + equipment
                .getId());
    }
}
