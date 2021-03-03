package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.common.Stage;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.equipment.combined.EquipmentFailureIntensityCombinedData;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentFailureIntensityReportService
        extends AbstractExcelReportService<EquipmentFailureIntensityCombinedData, EquipmentPerFormation> {

    @Override
    protected List<ReportCell> populateRowCells(
            EquipmentFailureIntensityCombinedData data,
            EquipmentPerFormation epf) {
        List<ReportCell> cellFunctions =
                new ArrayList<>(Arrays.asList(new ReportCell(epf.getEquipment().getName()),
                                              new ReportCell(epf.getAmount())));
        List<ReportCell> avgDailyFailureFunctions =
                data
                        .getStages()
                        .stream()
                        .flatMap(s -> data
                                .getRepairTypes()
                                .stream()
                                .map(rt -> getAvgDailyFailureFunction(epf, data, s, rt))).collect(
                        Collectors.toList());
        cellFunctions.addAll(avgDailyFailureFunctions);
        return cellFunctions;
    }

    private ReportCell getAvgDailyFailureFunction(EquipmentPerFormation epf,
                                                  EquipmentFailureIntensityCombinedData data,
                                                  Stage s,
                                                  RepairType rt) {
        Formation key = data.getFailureIntensityData().get(null) != null ? null : epf.getFormation();
        return new ReportCell(Optional.ofNullable(data
                                                          .getFailureIntensityData()
                                                          .getOrDefault(key, Collections.emptyMap())
                                                          .getOrDefault(epf.getEquipment(), Collections.emptyMap())
                                                          .getOrDefault(rt, Collections.emptyMap())
                                                          .getOrDefault(s, null))
                                      .map(data.getIntensityFunction())
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
        ReportHeader topHeader = header("Среднесуточный выход в текущий и средний ремонты на этапах операции, " + data.getUnitIndicator());
        data.getStages().forEach(stage -> {
            ReportHeader stageHeader = header(stage.getStageNum() + " этап");
            data.getRepairTypes().forEach(rt -> stageHeader.addSubHeader(header(rt.getShortName())));
            topHeader.addSubHeader(stageHeader);
        });
        return Arrays.asList(nameHeader, countHeader, topHeader);
    }

    @Override
    protected int writeData(EquipmentFailureIntensityCombinedData data, Sheet sheet, int lastRowIndex) {
        if (data.getEquipmentPerFormations().size() == 1) {
            List<EquipmentPerFormation> equipmentPerFormations = data
                    .getEquipmentPerFormations()
                    .values()
                    .stream()
                    .flatMap(m -> m.values().stream())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            writeRows(sheet, lastRowIndex, data, equipmentPerFormations);
            lastRowIndex += equipmentPerFormations.size();
        } else {
            int colSize = data.getStages().size() * data.getRepairTypes().size() + 1;
            for (Map.Entry<Formation, Map<EquipmentType, List<EquipmentPerFormation>>> entry : data
                    .getEquipmentPerFormations()
                    .entrySet()) {
                Formation formation = entry.getKey();
                Map<EquipmentType, List<EquipmentPerFormation>> equipmentTypeMap = entry.getValue();

                createRowWideCell(sheet, lastRowIndex, colSize, formation.getFullName(), true, true);

                for (Map.Entry<EquipmentType, List<EquipmentPerFormation>> e : equipmentTypeMap.entrySet()) {
                    EquipmentType equipmentType = e.getKey();
                    List<EquipmentPerFormation> equipmentPerFormations = e.getValue();

                    createRowWideCell(sheet, lastRowIndex + 1, colSize, equipmentType.getFullName(), true, false);

                    writeRows(sheet, lastRowIndex + 2, data, equipmentPerFormations);

                    lastRowIndex += equipmentPerFormations.size() + 2;
                }
            }
        }
        return lastRowIndex;
    }
}
