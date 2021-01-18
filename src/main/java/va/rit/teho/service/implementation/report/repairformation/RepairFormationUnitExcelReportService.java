package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitCombinedData;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RepairFormationUnitExcelReportService
        extends AbstractExcelReportService<RepairFormationUnitCombinedData, RepairFormationUnit> {

    @Override
    protected List<ReportCellFunction<RepairFormationUnit>> populateCellFunctions(RepairFormationUnitCombinedData data) {
        List<ReportCellFunction<RepairFormationUnit>> populateCellFunctions =
                new ArrayList<>(Arrays.asList(
                        ReportCellFunction.of(RepairFormationUnit::getName,
                                              ReportCell.CellType.TEXT,
                                              HorizontalAlignment.LEFT),
                        ReportCellFunction.of(rfu -> rfu.getRepairStationType().getName()),
                        ReportCellFunction.of(RepairFormationUnit::getStationAmount, ReportCell.CellType.NUMERIC)));
        List<EquipmentSubType> subTypes =
                data
                        .getTypesWithSubTypes()
                        .entrySet()
                        .stream()
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.toList());
        populateCellFunctions
                .addAll(subTypes.stream()
                                .flatMap(st -> getStaff(data, st, RepairFormationUnitEquipmentStaff::getTotalStaff))
                                .collect(Collectors.toList()));
        populateCellFunctions
                .addAll(subTypes.stream()
                                .flatMap(st -> getStaff(data, st, RepairFormationUnitEquipmentStaff::getAvailableStaff))
                                .collect(Collectors.toList()));
        return populateCellFunctions;
    }

    private Stream<ReportCellFunction<RepairFormationUnit>> getStaff(RepairFormationUnitCombinedData data,
                                                                     EquipmentSubType st,
                                                                     Function<RepairFormationUnitEquipmentStaff, Integer> f) {
        return Stream.of(
                ReportCellFunction.of(
                        rfu -> f.apply(data
                                               .getRepairFormationUnitEquipmentStaff()
                                               .getOrDefault(rfu, Collections.emptyMap())
                                               .getOrDefault(st, RepairFormationUnitEquipmentStaff.EMPTY))));
    }

    @Override
    protected String reportName() {
        return "Производственные возможности РВО по ремонту ВВСТ";
    }

    @Override
    protected List<ReportHeader> buildHeader(RepairFormationUnitCombinedData data) {
        ReportHeader nameReportHeader = header("Наименование ремонтного органа формирования", true);
        ReportHeader repairStationTypeReportHeader = header("Тип мастерской", true);
        ReportHeader rstCountReportHeader = header("Кол-во", true);
        return Arrays.asList(nameReportHeader,
                             repairStationTypeReportHeader,
                             rstCountReportHeader,
                             getSubHeaders("По штату, чел.", data),
                             getSubHeaders("В наличии, чел.", data));
    }

    private ReportHeader getSubHeaders(String topHeader, RepairFormationUnitCombinedData data) {
        List<ReportHeader> subReportHeaders = data
                .getTypesWithSubTypes()
                .entrySet()
                .stream()
                .flatMap(e -> Optional.ofNullable(e.getKey())
                                      .map(key -> Stream.of(
                                              new ReportHeader(key.getShortName(),
                                                               e.getValue()
                                                                .stream()
                                                                .map(est -> header(est.getShortName(), true))
                                                                .collect(Collectors.toList()))))
                                      .orElse(e.getValue().stream().map(est -> header(est.getShortName(), true))))
                .collect(Collectors.toList());
        return new ReportHeader(topHeader, subReportHeaders);
    }

    @Override
    protected void writeData(RepairFormationUnitCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getRepairFormationUnitList());
    }

}
