package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentFailureIntensityCombinedData;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EquipmentFailureIntensityReportService
        extends AbstractExcelReportService<EquipmentFailureIntensityCombinedData, EquipmentPerFormation> {

    private final ThreadLocal<EquipmentFailureIntensityCombinedData> data = new ThreadLocal<>();

    @Override
    protected List<Function<EquipmentPerFormation, ReportCell>> populateCellFunctions() {
        List<Function<EquipmentPerFormation, ReportCell>> cellFunctions =
                new ArrayList<>(Arrays.asList(epf -> new ReportCell(epf.getEquipment().getName()),
                                              epf -> new ReportCell("" + epf.getAmount())));
        List<Function<EquipmentPerFormation, ReportCell>> avgDailyFailureFunctions =
                this.data
                        .get()
                        .getStages()
                        .stream()
                        .flatMap(s ->
                                         this.data
                                                 .get()
                                                 .getRepairTypes()
                                                 .stream()
                                                 .map(rt -> getAvgDailyFailureFunction(s, rt)))
                        .collect(Collectors.toList());
        cellFunctions.addAll(avgDailyFailureFunctions);
        return cellFunctions;
    }

    private Function<EquipmentPerFormation, ReportCell> getAvgDailyFailureFunction(va.rit.teho.entity.common.Stage s,
                                                                                   va.rit.teho.entity.common.RepairType rt) {
        return (EquipmentPerFormation epf) -> {
            EquipmentPerFormationFailureIntensity intensity =
                    this.data.get()
                             .getFailureIntensityData()
                             .getOrDefault(epf.getFormation(), Collections.emptyMap())
                             .getOrDefault(epf.getEquipment(), Collections.emptyMap())
                             .getOrDefault(rt, Collections.emptyMap())
                             .getOrDefault(s, null);

            return new ReportCell("" + Optional.ofNullable(intensity)
                                               .map(EquipmentPerFormationFailureIntensity::getAvgDailyFailure)
                                               .orElse(0.0));
        };
    }

    @Override
    protected String reportName() {
        return "Среднесуточный выход ВВСТ в текущий и средний ремонты на этапах операции";
    }

    @Override
    protected List<ReportHeader> buildHeader() {
        ReportHeader nameHeader = new ReportHeader("Наименование ВВСТ", true, false);
        ReportHeader countHeader = new ReportHeader("Количество, ед.", true, false);
        ReportHeader topHeader = new ReportHeader(
                "Среднесуточный выход в текущий и средний ремонты на этапах операции, ед.",
                true,
                false);
        this.data.get().getStages().forEach(stage -> {
            ReportHeader stageHeader = new ReportHeader(stage.getStageNum() + " этап", true, false);
            this.data.get().getRepairTypes().forEach(rt -> {
                ReportHeader repairTypeHeader = new ReportHeader(rt.getShortName(), true, false);
                stageHeader.addSubHeader(repairTypeHeader);
            });
            topHeader.addSubHeader(stageHeader);
        });
        return Arrays.asList(nameHeader, countHeader, topHeader);
    }

    @Override
    protected void writeData(EquipmentFailureIntensityCombinedData data, Sheet sheet, int[] lastRow) {
        int colSize = this.data.get().getStages().size() * this.data.get().getRepairTypes().size() + 1;

        data.getEquipmentPerFormations().forEach((formation, equipmentSubTypeMap) -> {
            createRowWideCell(sheet, lastRow[0], colSize, formation.getFullName(), true, true);

            equipmentSubTypeMap.forEach((equipmentSubType, equipmentPerFormations) -> {
                createRowWideCell(sheet, lastRow[0] + 1, colSize, equipmentSubType.getFullName(), true, false);

                writeRows(sheet, lastRow[0] + 2, equipmentPerFormations);

                lastRow[0] += equipmentPerFormations.size() + 2;
            });
        });
    }

    @Override
    public byte[] generateReport(EquipmentFailureIntensityCombinedData data) {
        this.data.set(data);
        byte[] bytes = super.generateReport(data);
        this.data.remove();
        return bytes;
    }
}
