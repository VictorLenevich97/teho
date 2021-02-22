package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.EquipmentDistributionCombinedData;
import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentDistributionExcelReportService
        extends AbstractExcelReportService<EquipmentDistributionCombinedData, EquipmentPerFormationDistributionData> {

    @Override
    protected List<ReportCell> populateRowCells(
            EquipmentDistributionCombinedData data,
            EquipmentPerFormationDistributionData epfdd) {
        ReportCell eqNameCell = new ReportCell(epfdd.getEquipment().getName());
        ReportCell eqCountCell = new ReportCell(epfdd.getAmount());
        ReportCell failureCountCell = new ReportCell(epfdd.getAvgDailyFailure(), ReportCell.CellType.NUMERIC);
        List<ReportCell> repairTypeCells =
                data
                        .getRepairTypeList()
                        .stream()
                        .map(rt -> new ReportCell(epfdd.getAmountPerRepairType().getOrDefault(rt, 0.0),
                                                  ReportCell.CellType.NUMERIC))
                        .collect(Collectors.toList());
        List<ReportCell> restorationTypeCells = data
                .getRestorationTypeList()
                .stream()
                .map(rt -> new ReportCell(epfdd.getAmountPerRestorationType().getOrDefault(rt, 0.0),
                                          ReportCell.CellType.NUMERIC))
                .collect(Collectors.toList());

        List<ReportCell> cells =
                new ArrayList<>(Arrays.asList(eqNameCell, eqCountCell, failureCountCell));
        cells.addAll(repairTypeCells);
        cells.addAll(restorationTypeCells);
        return cells;
    }

    @Override
    protected String reportName() {
        return "Результаты решения задачи";
    }

    @Override
    protected List<ReportHeader> buildHeader(EquipmentDistributionCombinedData data) {
        ReportHeader eqNameHeader = header("Наименование ВВСТ");
        ReportHeader eqCountHeader = header("Количество ВВСТ по штату");
        ReportHeader eqFailingCountHeader = header("Количество вышедшего ВВСТ из строя");

        ReportHeader repairTypeHeader = header("Вид ремонта");
        data.getRepairTypeList().forEach(rt -> repairTypeHeader.addSubHeader(header(rt.getShortName())));

        ReportHeader restorationTypeHeader = header("Уровень восстановления");
        data.getRestorationTypeList().forEach(rt -> restorationTypeHeader.addSubHeader(header(rt.getName())));

        return Arrays.asList(eqNameHeader,
                             eqCountHeader,
                             eqFailingCountHeader,
                             repairTypeHeader,
                             restorationTypeHeader);
    }

    @Override
    protected int writeData(EquipmentDistributionCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getEquipmentPerFormationDistributionDataList());
        return lastRowIndex + data.getEquipmentPerFormationDistributionDataList().size();
    }

}
