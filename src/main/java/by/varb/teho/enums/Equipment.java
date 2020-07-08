package by.varb.teho.enums;

public enum Equipment {
    EQUIPMENT_TYPE_KEY("equipmentType"),
    NAME_KEY("name");

    private String key;
    Equipment(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
