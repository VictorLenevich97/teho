package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.EquipmentRFUDistribution;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentDistributionPerRFUExcelReportService extends
        AbstractExcelReportService<Map<RepairFormationUnit, List<EquipmentRFUDistribution>>, EquipmentRFUDistribution> {

    @Override
    protected List<ReportCell> populatedRowCells(Map<RepairFormationUnit, List<EquipmentRFUDistribution>> combinedData,
                                                 EquipmentRFUDistribution row) {
        ReportCell formationNameCell = new ReportCell(row.getFormation().getFullName());
        ReportCell equipmentNameCell = new ReportCell(row.getEquipment().getName());
        ReportCell restorationTypeCell = new ReportCell(row.getWorkhoursDistributionInterval()
                                                           .getRestorationType()
                                                           .getName());
        ReportCell repairingAmountCell = new ReportCell(row.getRepairing(), ReportCell.CellType.NUMERIC);
        ReportCell unableAmountCell = new ReportCell(row.getUnable());
        return Arrays.asList(formationNameCell,
                             equipmentNameCell,
                             restorationTypeCell,
                             repairingAmountCell,
                             unableAmountCell);
    }

    @Override
    protected String reportName() {
        return "Распределение вышедшего из строя ВВСТ по РВО";
    }

    @Override
    protected List<ReportHeader> buildHeader(Map<RepairFormationUnit, List<EquipmentRFUDistribution>> combinedData) {
        ReportHeader formationNameHeader = header("Воинская часть (подразделение)");
        ReportHeader equipmentNameHeader = header("Наименование ВВСТ");
        ReportHeader restorationTypeHeader = header("Тип восстановления");
        ReportHeader repairingAmountHeader = header("В ремонте, ед.");
        ReportHeader unableAmountHeader = header("Отправлено на другой уровень, ед.");

        return Arrays.asList(formationNameHeader,
                             equipmentNameHeader,
                             restorationTypeHeader,
                             repairingAmountHeader,
                             unableAmountHeader);
    }

    @Override
    protected void writeData(Map<RepairFormationUnit, List<EquipmentRFUDistribution>> data,
                             Sheet sheet,
                             int lastRowIndex) {
        int colSize = 4;
        for (Map.Entry<RepairFormationUnit, List<EquipmentRFUDistribution>> repairFormationUnitListEntry : data.entrySet()) {
            createRowWideCell(sheet,
                              lastRowIndex,
                              colSize,
                              repairFormationUnitListEntry.getKey().getName(),
                              true,
                              true);
            List<EquipmentRFUDistribution> EquipmentRFUDistributions = repairFormationUnitListEntry.getValue();
            EquipmentRFUDistributions.sort(Comparator.comparing(erd -> erd.getEquipment().getName()));
            writeRows(sheet, lastRowIndex + 1, data, EquipmentRFUDistributions);

            lastRowIndex += EquipmentRFUDistributions.size() + 1;
        }
    }
}
