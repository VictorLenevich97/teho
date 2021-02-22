package va.rit.teho.entity.labordistribution.combined;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.labordistribution.RestorationType;

import java.util.List;

public class EquipmentDistributionCombinedData {

    private final List<RepairType> repairTypeList;
    private final List<RestorationType> restorationTypeList;
    private final List<EquipmentPerFormationDistributionData> equipmentPerFormationDistributionDataList;

    public EquipmentDistributionCombinedData(List<RepairType> repairTypeList,
                                             List<RestorationType> restorationTypeList,
                                             List<EquipmentPerFormationDistributionData> equipmentPerFormationDistributionDataList) {
        this.repairTypeList = repairTypeList;
        this.restorationTypeList = restorationTypeList;
        this.equipmentPerFormationDistributionDataList = equipmentPerFormationDistributionDataList;
    }

    public List<RepairType> getRepairTypeList() {
        return repairTypeList;
    }

    public List<RestorationType> getRestorationTypeList() {
        return restorationTypeList;
    }

    public List<EquipmentPerFormationDistributionData> getEquipmentPerFormationDistributionDataList() {
        return equipmentPerFormationDistributionDataList;
    }
}
