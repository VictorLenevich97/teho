package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborInputDistributionCombinedData;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LaborInputDistributionExcelReportService
        extends AbstractExcelReportService<LaborInputDistributionCombinedData, EquipmentLaborInputDistribution> {

    private final ThreadLocal<LaborInputDistributionCombinedData> data = new ThreadLocal<>();

    private static Stream<Function<EquipmentLaborInputDistribution, ReportCell>> getCountAndLaborInputFunctions(
            WorkhoursDistributionInterval wdi) {
        return Stream.of(
                (EquipmentLaborInputDistribution elid) -> new ReportCell(elid
                                                                                 .getIntervalCountAndLaborInputMap()
                                                                                 .get(wdi.getId())
                                                                                 .getCount(),
                                                                         ReportCell.CellType.NUMERIC),
                (EquipmentLaborInputDistribution elid) -> new ReportCell(elid
                                                                                 .getIntervalCountAndLaborInputMap()
                                                                                 .get(wdi.getId())
                                                                                 .getLaborInput(),
                                                                         ReportCell.CellType.NUMERIC));
    }

    @Override
    protected List<Function<EquipmentLaborInputDistribution, ReportCell>> populateCellFunctions() {
        Function<EquipmentLaborInputDistribution, ReportCell> formationNameFunction = (elid) -> new ReportCell(elid.getFormationName(),
                                                                                                               ReportCell.CellType.TEXT,
                                                                                                               HorizontalAlignment.LEFT);
        Function<EquipmentLaborInputDistribution, ReportCell> equipmentNameFunction = (elid) -> new ReportCell(elid.getEquipmentName(),
                                                                                                               ReportCell.CellType.TEXT,
                                                                                                               HorizontalAlignment.LEFT);
        Function<EquipmentLaborInputDistribution, ReportCell> avgDailyFailureFunction = (elid) -> new ReportCell(elid.getAvgDailyFailure(),
                                                                                                                 ReportCell.CellType.NUMERIC);
        Function<EquipmentLaborInputDistribution, ReportCell> standardLaborInputFunction = (elid) -> new ReportCell(elid.getStandardLaborInput(),
                                                                                                                    ReportCell.CellType.NUMERIC);

        List<Function<EquipmentLaborInputDistribution, ReportCell>> intervalFunctions = this.data
                .get()
                .getWorkhoursDistributionIntervals()
                .stream()
                .flatMap(LaborInputDistributionExcelReportService::getCountAndLaborInputFunctions)
                .collect(Collectors.toList());

        Function<EquipmentLaborInputDistribution, ReportCell> totalLaborInputFunction = (elid) -> new ReportCell(elid.getTotalRepairComplexity(),
                                                                                                                 ReportCell.CellType.NUMERIC);

        List<Function<EquipmentLaborInputDistribution, ReportCell>> functionsList = new ArrayList<>(Arrays.asList(
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
    protected List<ReportHeader> buildHeader() {
        ReportHeader formationHeader = new ReportHeader("Воинская часть (подразделение)", true, false);
        ReportHeader equipmentNameHeader = new ReportHeader("Наименование ВВСТ", true, false);
        ReportHeader avgDailyFailureHeader = new ReportHeader("Среднесуточный выход в ремонт, ед.", true, true);
        ReportHeader standardLaborInputHeader = new ReportHeader("Нормативная трудоемкость ремонта, чел.-час.",
                                                                 true,
                                                                 true);

        ReportHeader distributionTopHeader = new ReportHeader("Распределение ремонтного фонда по трудоемкости ремонта",
                                                              true,
                                                              false);

        this.data
                .get()
                .getWorkhoursDistributionIntervals()
                .sort(Comparator.comparing(WorkhoursDistributionInterval::getUpperBound));
        this.data.get().getWorkhoursDistributionIntervals().forEach(wdi -> {
            ReportHeader intervalHeader = new ReportHeader(((wdi.getLowerBound() == null ? "" : ("от " + wdi.getLowerBound())) + (wdi
                    .getUpperBound() == null ? "" : " до " + wdi.getUpperBound()) + " чел.-час."), true, false);
            intervalHeader.addSubHeader(new ReportHeader("Кол., ед.", true, false));
            intervalHeader.addSubHeader(new ReportHeader("Qij, чел.-час.", true, false));
            distributionTopHeader.addSubHeader(intervalHeader);
        });

        ReportHeader totalLaborInputHeader = new ReportHeader("Суммарная трудоемксоть ремонта, чел.-час.", true, true);
        return Arrays.asList(formationHeader,
                             equipmentNameHeader,
                             avgDailyFailureHeader,
                             standardLaborInputHeader,
                             distributionTopHeader,
                             totalLaborInputHeader);
    }

    @Override
    protected int writeData(LaborInputDistributionCombinedData data, Sheet sheet, int[] lastRow) {
        int colSize = (data.getWorkhoursDistributionIntervals().size() * 2) + 4;
        data.getLaborInputDistribution().forEach((equipmentType, equipmentSubTypeListMap) -> {
            createRowWideCell(sheet, lastRow[0], colSize, equipmentType.getFullName(), true, false);
            equipmentSubTypeListMap.forEach((equipmentSubType, equipmentLaborInputDistributions) -> {
                createRowWideCell(sheet,
                                  lastRow[0] + 1,
                                  colSize,
                                  "Итого " + equipmentSubType.getFullName(),
                                  true,
                                  false);
                writeRows(sheet, lastRow[0] + 2, equipmentLaborInputDistributions);

                lastRow[0] += equipmentLaborInputDistributions.size() + 1;
            });
        });
        return lastRow[0];
    }

    @Override
    public byte[] generateReport(LaborInputDistributionCombinedData data) {
        this.data.set(data);
        byte[] bytes = super.generateReport(data);
        this.data.remove();
        return bytes;
    }
}
