package va.rit.teho.service.implementation.report.labordistribution;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.labordistribution.CountAndLaborInput;
import va.rit.teho.entity.labordistribution.CountAndLaborInputCombinedData;
import va.rit.teho.entity.labordistribution.EquipmentLaborInputDistribution;
import va.rit.teho.entity.labordistribution.LaborInputDistributionCombinedData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(value = "allRepairTypes")
public class LaborDistributionForAllRepairTypesExcelReportService extends AbstractLaborDistributionExcelReport {

    @Override
    protected List<ReportCell> populateRowCells(LaborInputDistributionCombinedData data,
                                                EquipmentLaborInputDistribution elid) {
        ReportCell formationNameCell =
                new ReportCell(elid.getFormationName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT);
        ReportCell equipmentNameCell =
                new ReportCell(elid.getEquipmentName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT);
        ReportCell equipmentAmountCell = new ReportCell(elid.getEquipmentAmount());
        ReportCell avgDailyFailureCell = new ReportCell(elid.getAvgDailyFailure(), ReportCell.CellType.NUMERIC);

        List<ReportCell> valueCells = data
                .getRepairTypes()
                .stream()
                .flatMap(repairType -> buildReportCell(data, elid, repairType))
                .collect(Collectors.toList());

        List<ReportCell> reportCells = new ArrayList<>(Arrays.asList(formationNameCell,
                                                                     equipmentNameCell,
                                                                     equipmentAmountCell,
                                                                     avgDailyFailureCell));

        reportCells.addAll(valueCells);

        return reportCells;
    }

    private Stream<ReportCell> buildReportCell(LaborInputDistributionCombinedData data,
                                               EquipmentLaborInputDistribution elid,
                                               RepairType repairType) {
        if (repairType.includesIntervals()) {
            return data
                    .getWorkhoursDistributionIntervals()
                    .stream()
                    .map(wdi -> new ReportCell(elid
                                                       .getCountAndLaborInputCombinedData()
                                                       .getOrDefault(repairType,
                                                                     CountAndLaborInputCombinedData.EMPTY)
                                                       .getCountAndLaborInputMap()
                                                       .getOrDefault(wdi.getId(),
                                                                     CountAndLaborInput.EMPTY)
                                                       .getCount(),
                                               ReportCell.CellType.NUMERIC));
        } else {
            return Stream.of(new ReportCell(elid
                                                    .getCountAndLaborInputCombinedData()
                                                    .getOrDefault(repairType,
                                                                  CountAndLaborInputCombinedData.EMPTY)
                                                    .getTotalFailureAmount(),
                                            ReportCell.CellType.NUMERIC));
        }
    }

    @Override
    protected String reportName() {
        return "Распределение ремонтного фонда по трудоемкости ремонта (для всех типов ремонта)";
    }

    @Override
    protected List<ReportHeader> buildHeader(LaborInputDistributionCombinedData data) {
        ReportHeader equipmentAmountHeader = header("Количество ВВСТ, ед.");

        ReportHeader distributionTopHeader = header("Распределение ремонтного фонда по трудоемкости ремонта");


        List<ReportHeader> intervalHeaders = data
                .getWorkhoursDistributionIntervals()
                .stream()
                .map(wdi -> header(
                        ((wdi.getLowerBound() == null ? "" : ("от " + wdi.getLowerBound())) +
                                (wdi.getUpperBound() == null ? "" : " до " + wdi.getUpperBound()) + " чел.-час.")))
                .collect(Collectors.toList());

        data
                .getRepairTypes()
                .forEach(rt -> {
                    ReportHeader repairTypeHeader = new ReportHeader(rt.getShortName(), false);
                    if (rt.includesIntervals()) {
                        intervalHeaders.forEach(repairTypeHeader::addSubHeader);
                    }
                    distributionTopHeader.addSubHeader(repairTypeHeader);
                });

        return Arrays.asList(FORMATION_HEADER,
                             EQ_NAME_HEADER,
                             equipmentAmountHeader,
                             AVG_DAILY_FAILURE_HEADER,
                             distributionTopHeader);
    }

    @Override
    protected int columnCount(LaborInputDistributionCombinedData data) {
        long repairTypesWithIntervalsCount =
                data.getRepairTypes().stream().filter(RepairType::includesIntervals).count();
        return (int) ((data.getWorkhoursDistributionIntervals().size() * repairTypesWithIntervalsCount) +
                (data.getRepairTypes().size() - repairTypesWithIntervalsCount) + 3);
    }

}
