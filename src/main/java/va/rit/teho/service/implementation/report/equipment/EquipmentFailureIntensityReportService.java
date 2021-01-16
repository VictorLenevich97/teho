package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.EquipmentFailureIntensityCombinedData;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentPerFormationFailureIntensity;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentFailureIntensityReportService
        extends AbstractExcelReportService<EquipmentFailureIntensityCombinedData, EquipmentPerFormation> {

    @Override
    protected List<ReportCellFunction<EquipmentPerFormation>> populateCellFunctions(
            EquipmentFailureIntensityCombinedData data) {
        List<ReportCellFunction<EquipmentPerFormation>> cellFunctions =
                new ArrayList<>(Arrays.asList(ReportCellFunction.of(epf -> epf.getEquipment().getName()),
                                              ReportCellFunction.of(EquipmentPerFormation::getAmount)));
        List<ReportCellFunction<EquipmentPerFormation>> avgDailyFailureFunctions =
                data
                        .getStages()
                        .stream()
                        .flatMap(s -> data.getRepairTypes().stream().map(rt -> getAvgDailyFailureFunction(data, s, rt)))
                        .collect(Collectors.toList());
        cellFunctions.addAll(avgDailyFailureFunctions);
        return cellFunctions;
    }

    private ReportCellFunction<EquipmentPerFormation> getAvgDailyFailureFunction(EquipmentFailureIntensityCombinedData data,
                                                                                 Stage s,
                                                                                 RepairType rt) {
        return ReportCellFunction.of(
                epf -> Optional.ofNullable(data
                                                   .getFailureIntensityData()
                                                   .getOrDefault(epf.getFormation(), Collections.emptyMap())
                                                   .getOrDefault(epf.getEquipment(), Collections.emptyMap())
                                                   .getOrDefault(rt, Collections.emptyMap())
                                                   .getOrDefault(s, null))
                               .map(EquipmentPerFormationFailureIntensity::getAvgDailyFailure)
                               .orElse(0.0),
                ReportCell.CellType.NUMERIC);
    }

    @Override
    protected String reportName() {
        return "Среднесуточный выход ВВСТ в текущий и средний ремонты на этапах операции";
    }

    @Override
    protected List<ReportHeader> buildHeader(EquipmentFailureIntensityCombinedData data) {
        ReportHeader nameHeader = header("Наименование ВВСТ");
        ReportHeader countHeader = header("Количество, ед.");
        ReportHeader topHeader = header("Среднесуточный выход в текущий и средний ремонты на этапах операции, ед.");
        data.getStages().forEach(stage -> {
            ReportHeader stageHeader = header(stage.getStageNum() + " этап");
            data.getRepairTypes().forEach(rt -> stageHeader.addSubHeader(header(rt.getShortName())));
            topHeader.addSubHeader(stageHeader);
        });
        return Arrays.asList(nameHeader, countHeader, topHeader);
    }

    @Override
    protected int writeData(EquipmentFailureIntensityCombinedData data, Sheet sheet, int lastRowIndex) {
        int colSize = data.getStages().size() * data.getRepairTypes().size() + 1;

        for (Map.Entry<Formation, Map<EquipmentSubType, List<EquipmentPerFormation>>> entry : data
                .getEquipmentPerFormations()
                .entrySet()) {
            Formation formation = entry.getKey();
            Map<EquipmentSubType, List<EquipmentPerFormation>> equipmentSubTypeMap = entry.getValue();

            createRowWideCell(sheet, lastRowIndex, colSize, formation.getFullName(), true, true);

            for (Map.Entry<EquipmentSubType, List<EquipmentPerFormation>> e : equipmentSubTypeMap.entrySet()) {
                EquipmentSubType equipmentSubType = e.getKey();
                List<EquipmentPerFormation> equipmentPerFormations = e.getValue();

                createRowWideCell(sheet, lastRowIndex + 1, colSize, equipmentSubType.getFullName(), true, false);

                writeRows(sheet, lastRowIndex + 2, data, equipmentPerFormations);

                lastRowIndex += equipmentPerFormations.size() + 2;
            }
        }
        return lastRowIndex;
    }
}
