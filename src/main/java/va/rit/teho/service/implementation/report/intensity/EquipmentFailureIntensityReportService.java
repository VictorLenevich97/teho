package va.rit.teho.service.implementation.report.intensity;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.intensity.combined.EquipmentIntensityCombinedData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentFailureIntensityReportService
        extends AbstractExcelReportService<EquipmentIntensityCombinedData, Equipment> {

    @Override
    protected List<ReportCell> populateRowCells(
            EquipmentIntensityCombinedData data,
            Equipment equipment) {
        List<ReportCell> cellFunctions =
                new ArrayList<>(Collections.singletonList(new ReportCell(equipment.getName())));
        List<ReportCell> avgDailyFailureFunctions =
                data
                        .getStages()
                        .stream()
                        .flatMap(s -> data
                                .getRepairTypes()
                                .stream()
                                .map(rt -> new ReportCell(data.getIntensitiesForOperation().get(equipment, rt, s), ReportCell.CellType.NUMERIC)))
                        .collect(Collectors.toList());
        cellFunctions.addAll(avgDailyFailureFunctions);
        return cellFunctions;
    }


    @Override
    protected String reportName() {
        return "Интенсивность выхода ВВСТ в ремонт";
    }

    @Override
    protected List<ReportHeader> buildHeader(EquipmentIntensityCombinedData data) {
        ReportHeader nameHeader = header("Наименование ВВСТ");
        ReportHeader topHeader = header("Интенсивность выхода ВВСТ в ремонт на этапах операции, %");
        data.getStages().forEach(stage -> {
            ReportHeader stageHeader = header(stage.getStageNum() + " этап");
            data.getRepairTypes().forEach(rt -> stageHeader.addSubHeader(header(rt.getShortName())));
            topHeader.addSubHeader(stageHeader);
        });
        return Arrays.asList(nameHeader, topHeader);
    }

    @Override
    protected int writeData(EquipmentIntensityCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getEquipmentList());
        return lastRowIndex;
    }
}
