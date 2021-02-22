package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.Sheet;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.entity.labordistribution.combined.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.combined.LaborInputDistributionCombinedData;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;

public abstract class AbstractLaborDistributionExcelReport extends
        AbstractExcelReportService<LaborInputDistributionCombinedData, EquipmentLaborInputDistribution> {

    protected static final ReportHeader FORMATION_HEADER = header("Воинская часть (подразделение)");
    protected static final ReportHeader EQ_NAME_HEADER = header("Наименование ВВСТ");
    protected static final ReportHeader AVG_DAILY_FAILURE_HEADER = header("Среднесуточный выход в ремонт, ед.");

    protected int writeTypeData(Collection<EquipmentType> equipmentTypes,
                                LaborInputDistributionCombinedData data,
                                Sheet sheet,
                                int lastRowIndex,
                                int colSize) {
        for (EquipmentType equipmentType : equipmentTypes) {
            if (data.getLaborInputDistribution().containsKey(equipmentType) || equipmentType
                    .getEquipmentTypes()
                    .stream()
                    .anyMatch(data.getLaborInputDistribution()::containsKey)) {
                String prefix = equipmentType.getParentType() == null ? "" : "Итого: ";
                createRowWideCell(sheet, lastRowIndex, colSize, prefix + equipmentType.getFullName(), true, false);

                List<EquipmentLaborInputDistribution> equipmentLaborInputDistributions =
                        Optional
                                .ofNullable(data.getLaborInputDistribution().get(equipmentType))
                                .orElse(Collections.emptyList());


                writeRows(sheet, lastRowIndex + 1, data, equipmentLaborInputDistributions);

                lastRowIndex = writeTypeData(equipmentType.getEquipmentTypes(),
                                             data,
                                             sheet,
                                             lastRowIndex + equipmentLaborInputDistributions.size() + 1,
                                             colSize);
            }
        }
        return lastRowIndex;
    }

    protected abstract int columnCount(LaborInputDistributionCombinedData data);

    @Override
    public byte[] generateReport(LaborInputDistributionCombinedData data) {
        data
                .getWorkhoursDistributionIntervals()
                .sort(Comparator.comparing(WorkhoursDistributionInterval::getUpperBound));

        data.getRepairTypes().sort(Comparator.comparing(RepairType::getShortName).reversed());
        return super.generateReport(data);
    }

    @Override
    protected int writeData(LaborInputDistributionCombinedData data, Sheet sheet, int lastRowIndex) {
        return writeTypeData(data.getEquipmentTypes(), data, sheet, lastRowIndex, columnCount(data));
    }

}
