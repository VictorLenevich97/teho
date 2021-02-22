package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborInputDistributionCombinedData;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service(value = "oneRepairType")
public class LaborInputDistributionExcelReportService extends AbstractLaborDistributionExcelReport {

    private static Stream<ReportCell> getCountAndLaborInputCells(
            EquipmentLaborInputDistribution elid,
            WorkhoursDistributionInterval wdi) {
        return elid
                .getCountAndLaborInputCombinedData()
                .entrySet()
                .stream()
                .flatMap(entry ->
                                 Stream.of(
                                         new ReportCell(entry
                                                                .getValue()
                                                                .getCountAndLaborInputMap()
                                                                .get(wdi.getId())
                                                                .getCount(),
                                                        ReportCell.CellType.NUMERIC),
                                         new ReportCell(entry
                                                                .getValue()
                                                                .getCountAndLaborInputMap()
                                                                .get(wdi.getId())
                                                                .getLaborInput(),
                                                        ReportCell.CellType.NUMERIC)));
    }

    @Override
    protected List<ReportCell> populateRowCells(
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
        ReportHeader standardLaborInputHeader =
                header("Нормативная трудоемкость ремонта, чел.-час.", true);

        ReportHeader distributionTopHeader = header("Распределение ремонтного фонда по трудоемкости ремонта");

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

        ReportHeader totalLaborInputHeader = header("Суммарная трудоемкость ремонта, чел.-час.", true);
        return Arrays.asList(FORMATION_HEADER,
                             EQ_NAME_HEADER,
                             AVG_DAILY_FAILURE_HEADER,
                             standardLaborInputHeader,
                             distributionTopHeader,
                             totalLaborInputHeader);
    }

    @Override
    protected int columnCount(LaborInputDistributionCombinedData data) {
        return (data.getWorkhoursDistributionIntervals().size() * 2) + 4;
    }

}
