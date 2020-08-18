package va.rit.teho.report;

public class LaborIntensityRepairNormReportRecord {

    private String equipmentBrand;
    private String equipmentTypeName;
    private int currentRepairCost;
    private int averageRepairCost;
    private int capitalRepairCost;

    public LaborIntensityRepairNormReportRecord() {
    }

    public LaborIntensityRepairNormReportRecord(String equipmentBrand,
                                                String equipmentTypeName,
                                                int currentRepairCost,
                                                int averageRepairCost,
                                                int capitalRepairCost) {
        this.equipmentBrand = equipmentBrand;
        this.equipmentTypeName = equipmentTypeName;
        this.currentRepairCost = currentRepairCost;
        this.averageRepairCost = averageRepairCost;
        this.capitalRepairCost = capitalRepairCost;
    }

    public String getEquipmentBrand() {
        return equipmentBrand;
    }

    public void setEquipmentBrand(String equipmentBrand) {
        this.equipmentBrand = equipmentBrand;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public void setEquipmentTypeName(String equipmentTypeName) {
        this.equipmentTypeName = equipmentTypeName;
    }

    public int getCurrentRepairCost() {
        return currentRepairCost;
    }

    public void setCurrentRepairCost(int currentRepairCost) {
        this.currentRepairCost = currentRepairCost;
    }

    public int getAverageRepairCost() {
        return averageRepairCost;
    }

    public void setAverageRepairCost(int averageRepairCost) {
        this.averageRepairCost = averageRepairCost;
    }

    public int getCapitalRepairCost() {
        return capitalRepairCost;
    }

    public void setCapitalRepairCost(int capitalRepairCost) {
        this.capitalRepairCost = capitalRepairCost;
    }
}
