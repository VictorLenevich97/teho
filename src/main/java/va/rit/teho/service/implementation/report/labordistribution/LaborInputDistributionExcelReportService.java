package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentType;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LaborInputDistributionExcelReportService
        extends AbstractExcelReportService<LaborInputDistributionCombinedData, EquipmentLaborInputDistribution> {

    private static Stream<ReportCell> getCountAndLaborInputCells(
            EquipmentLaborInputDistribution elid,
            WorkhoursDistributionInterval wdi) {
        return
                Stream.of(
                        new ReportCell(elid.getIntervalCountAndLaborInputMap()
                                           .get(wdi.getId())
                                           .getCount(),
                                       ReportCell.CellType.NUMERIC),
                        new ReportCell(elid.getIntervalCountAndLaborInputMap()
                                           .get(wdi.getId())
                                           .getLaborInput(),
                                       ReportCell.CellType.NUMERIC));
    }

    @Override
    protected List<ReportCell> populatedRowCells(
            LaborInputDistributionCombinedData data,
            EquipmentLaborInputDistribution elid) {
        ReportCell formationNameCell =
                new ReportCell(elid.getFormationName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT);
        ReportCell equipmentNameCell =
                new ReportCell(elid.getEquipmentName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT);
        ReportCell avgDailyFailureCell = new ReportCell(elid.getAvgDailyFailure(), ReportCell.CellType.NUMERIC);
        ReportCell standardLaborInputCell = new ReportCell(elid.getStandardLaborInput(),
                                                           ReportCell.CellType.NUMERIC);

        List<ReportCell> intervalCell =
                data
                        .getWorkhoursDistributionIntervals()
                        .stream()
                        .flatMap(wdi -> LaborInputDistributionExcelReportService.getCountAndLaborInputCells(elid, wdi))
                        .collect(Collectors.toList());

        ReportCell totalLaborInputCell =
                new ReportCell(elid.getTotalRepairComplexity(), ReportCell.CellType.NUMERIC);

        List<ReportCell> reportCells = new ArrayList<>(Arrays.asList(
                formationNameCell,
                equipmentNameCell,
                avgDailyFailureCell,
                standardLaborInputCell));

        reportCells.addAll(intervalCell);
        reportCells.add(totalLaborInputCell);

        return reportCells;
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

    private void writeTypeData(List<EquipmentType> equipmentTypes,
                               LaborInputDistributionCombinedData data,
                               Sheet sheet,
                               int lastRowIndex,
                               int colSize) {
        for (EquipmentType equipmentType : equipmentTypes) {
            String prefix = equipmentType.getParentType() == null ? "" : "Итого: ";
            createRowWideCell(sheet, lastRowIndex, colSize, prefix + equipmentType.getFullName(), true, false);

            List<EquipmentLaborInputDistribution> equipmentLaborInputDistributions =
                    data.getLaborInputDistribution().get(equipmentType);

            writeRows(sheet, lastRowIndex + 1, data, equipmentLaborInputDistributions);

            lastRowIndex += equipmentLaborInputDistributions.size() + 1;
        }
    }

    @Override
    protected int writeData(LaborInputDistributionCombinedData data, Sheet sheet, int lastRowIndex) {
        int colSize = (data.getWorkhoursDistributionIntervals().size() * 2) + 4;

        List<EquipmentType> highestLevelTypes = data
                .getLaborInputDistribution()
                .keySet()
                .stream()
                .filter(et -> et.getParentType() == null)
                .collect(Collectors.toList());

        writeTypeData(highestLevelTypes, data, sheet, lastRowIndex, colSize);

        return lastRowIndex;
    }
}
