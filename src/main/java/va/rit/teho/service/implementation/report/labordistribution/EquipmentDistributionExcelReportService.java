package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.EquipmentDistributionCombinedData;
import va.rit.teho.entity.labordistribution.EquipmentPerFormationDistributionData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
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
    protected List<ReportCellFunction<EquipmentPerFormationDistributionData>> populateCellFunctions(
            EquipmentDistributionCombinedData data) {
        ReportCellFunction<EquipmentPerFormationDistributionData> eqNameF =
                ReportCellFunction.of(epfdd -> epfdd.getEquipment().getName());
        ReportCellFunction<EquipmentPerFormationDistributionData> eqCountF =
                ReportCellFunction.of(EquipmentPerFormationDistributionData::getAmount);
        ReportCellFunction<EquipmentPerFormationDistributionData> failureCountF =
                ReportCellFunction.of(EquipmentPerFormationDistributionData::getAvgDailyFailure);
        List<ReportCellFunction<EquipmentPerFormationDistributionData>> repairTypeFunctions =
                data
                        .getRepairTypeList()
                        .stream()
                        .map(rt -> ReportCellFunction.of((EquipmentPerFormationDistributionData epfdd) ->
                                                                 epfdd.getAmountPerRepairType().getOrDefault(rt, 0.0),
                                                         ReportCell.CellType.NUMERIC))
                        .collect(Collectors.toList());
        List<ReportCellFunction<EquipmentPerFormationDistributionData>> restorationTypeFunctions = data
                .getRestorationTypeList()
                .stream()
                .map(rt -> ReportCellFunction.of((EquipmentPerFormationDistributionData epfdd) ->
                                                         epfdd.getAmountPerRestorationType().getOrDefault(rt, 0.0),
                                                 ReportCell.CellType.NUMERIC))
                .collect(Collectors.toList());

        List<ReportCellFunction<EquipmentPerFormationDistributionData>> functions =
                new ArrayList<>(Arrays.asList(eqNameF, eqCountF, failureCountF));
        functions.addAll(repairTypeFunctions);
        functions.addAll(restorationTypeFunctions);
        return functions;
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
    protected void writeData(EquipmentDistributionCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getEquipmentPerFormationDistributionDataList());
    }

}
