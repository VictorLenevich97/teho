package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborInputDistributionCombinedData;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LaborInputDistributionExcelReportService
        extends AbstractExcelReportService<LaborInputDistributionCombinedData, EquipmentLaborInputDistribution> {

    private static Stream<ReportCellFunction<EquipmentLaborInputDistribution>> getCountAndLaborInputFunctions(
            WorkhoursDistributionInterval wdi) {
        return
                Stream.of(
                        ReportCellFunction.of(elid -> elid
                                                      .getIntervalCountAndLaborInputMap()
                                                      .get(wdi.getId())
                                                      .getCount(),
                                              ReportCell.CellType.NUMERIC),
                        ReportCellFunction.of(elid -> elid
                                                      .getIntervalCountAndLaborInputMap()
                                                      .get(wdi.getId())
                                                      .getLaborInput(),
                                              ReportCell.CellType.NUMERIC));
    }

    @Override
    protected List<ReportCellFunction<EquipmentLaborInputDistribution>> populateCellFunctions(
            LaborInputDistributionCombinedData data) {
        ReportCellFunction<EquipmentLaborInputDistribution> formationNameFunction =
                ReportCellFunction.of(EquipmentLaborInputDistribution::getFormationName,
                                      ReportCell.CellType.TEXT,
                                      HorizontalAlignment.LEFT);
        ReportCellFunction<EquipmentLaborInputDistribution> equipmentNameFunction =
                ReportCellFunction.of(EquipmentLaborInputDistribution::getEquipmentName,
                                      ReportCell.CellType.TEXT,
                                      HorizontalAlignment.LEFT);
        ReportCellFunction<EquipmentLaborInputDistribution> avgDailyFailureFunction =
                ReportCellFunction.of(EquipmentLaborInputDistribution::getAvgDailyFailure, ReportCell.CellType.NUMERIC);
        ReportCellFunction<EquipmentLaborInputDistribution> standardLaborInputFunction =
                ReportCellFunction.of(EquipmentLaborInputDistribution::getStandardLaborInput,
                                      ReportCell.CellType.NUMERIC);

        List<ReportCellFunction<EquipmentLaborInputDistribution>> intervalFunctions =
                data
                        .getWorkhoursDistributionIntervals()
                        .stream()
                        .flatMap(LaborInputDistributionExcelReportService::getCountAndLaborInputFunctions)
                        .collect(Collectors.toList());

        ReportCellFunction<EquipmentLaborInputDistribution> totalLaborInputFunction =
                ReportCellFunction.of(EquipmentLaborInputDistribution::getTotalRepairComplexity,
                                      ReportCell.CellType.NUMERIC);

        List<ReportCellFunction<EquipmentLaborInputDistribution>> functionsList = new ArrayList<>(Arrays.asList(
                formationNameFunction,
                equipmentNameFunction,
                avgDailyFailureFunction,
                standardLaborInputFunction));

        functionsList.addAll(intervalFunctions);
        functionsList.add(totalLaborInputFunction);

        return functionsList;
    }

    @Override
    protected String reportName() {
        return "Распределение производственного фонда по трудоемкости ремонта";
    }

    @Override
    protected List<ReportHeader> buildHeader(LaborInputDistributionCombinedData data) {
        ReportHeader formationHeader = header("Воинская часть (подразделение)");
        ReportHeader equipmentNameHeader = header("Наименование ВВСТ");
        ReportHeader avgDailyFailureHeader = header("Среднесуточный выход в ремонт, ед.", true);
        ReportHeader standardLaborInputHeader =
                header("Нормативная трудоемкость ремонта, чел.-час.", true);

        ReportHeader distributionTopHeader = header("Распределение ремонтного фонда по трудоемкости ремонта");

        data
                .getWorkhoursDistributionIntervals()
                .sort(Comparator.comparing(WorkhoursDistributionInterval::getUpperBound));
        data
                .getWorkhoursDistributionIntervals()
                .forEach(wdi -> {
                    ReportHeader intervalHeader = header(
                            ((wdi.getLowerBound() == null ? "" : ("от " + wdi.getLowerBound())) +
                                    (wdi.getUpperBound() == null ? "" : " до " + wdi.getUpperBound()) + " чел.-час."));
                    intervalHeader.addSubHeader(header("Кол., ед."));
                    intervalHeader.addSubHeader(header("Qij, чел.-час."));
                    distributionTopHeader.addSubHeader(intervalHeader);
                });

        ReportHeader totalLaborInputHeader = header("Суммарная трудоемксоть ремонта, чел.-час.", true);
        return Arrays.asList(formationHeader,
                             equipmentNameHeader,
                             avgDailyFailureHeader,
                             standardLaborInputHeader,
                             distributionTopHeader,
                             totalLaborInputHeader);
    }

    @Override
    protected void writeData(LaborInputDistributionCombinedData data, Sheet sheet, int lastRowIndex) {
        int colSize = (data.getWorkhoursDistributionIntervals().size() * 2) + 4;
        for (Map.Entry<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> e : data
                .getLaborInputDistribution()
                .entrySet()) {
            createRowWideCell(sheet, lastRowIndex, colSize, e.getKey().getFullName(), true, false);
            for (Map.Entry<EquipmentSubType, List<EquipmentLaborInputDistribution>> entry : e.getValue().entrySet()) {
                EquipmentSubType equipmentSubType = entry.getKey();
                List<EquipmentLaborInputDistribution> equipmentLaborInputDistributions = entry.getValue();
                createRowWideCell(sheet,
                                  lastRowIndex + 1,
                                  colSize,
                                  "Итого " + equipmentSubType.getFullName(),
                                  true,
                                  false);
                writeRows(sheet, lastRowIndex + 2, data, equipmentLaborInputDistributions);

                lastRowIndex += equipmentLaborInputDistributions.size() + 1;
            }
        }
    }
}
