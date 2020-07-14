package by.varb.teho.model;

public class EquipmentTypeModel {
    private String shortName;
    private String fullName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public EquipmentTypeModel(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }
}
