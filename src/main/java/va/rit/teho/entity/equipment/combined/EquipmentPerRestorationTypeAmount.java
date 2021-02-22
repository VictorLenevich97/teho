package va.rit.teho.entity.equipment.combined;

import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.labordistribution.RestorationType;

public class EquipmentPerRestorationTypeAmount {
    private final Equipment equipment;
    private final RestorationType restorationType;
    private final Double amount;

    public EquipmentPerRestorationTypeAmount(Equipment equipment,
                                             RestorationType restorationType, Double amount) {
        this.equipment = equipment;
        this.restorationType = restorationType;
        this.amount = amount;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public RestorationType getRestorationType() {
        return restorationType;
    }

    public Double getAmount() {
        return amount;
    }
}
