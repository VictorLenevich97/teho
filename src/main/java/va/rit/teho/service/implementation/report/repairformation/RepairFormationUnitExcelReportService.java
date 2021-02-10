package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitCombinedData;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.report.ReportCell;
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
    protected List<ReportCell> populatedRowCells(RepairFormationUnitCombinedData data, RepairFormationUnit rfu) {
        List<ReportCell> populateCellFunctions =
                new ArrayList<>(Arrays.asList(
                        new ReportCell(rfu.getName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT),
                        new ReportCell(rfu.getRepairStationType().getName()),
                        new ReportCell(rfu.getStationAmount(), ReportCell.CellType.NUMERIC)));
        List<EquipmentType> subTypes = data
                .getEquipmentTypes()
                .stream()
                .flatMap(EquipmentType::collectLowestLevelTypes)
                .collect(Collectors.toList());
        populateCellFunctions
                .addAll(subTypes.stream()
                                .flatMap(st -> getStaff(rfu,
                                                        data,
                                                        st,
                                                        RepairFormationUnitEquipmentStaff::getTotalStaff))
                                .collect(Collectors.toList()));
        populateCellFunctions
                .addAll(subTypes.stream()
                                .flatMap(st -> getStaff(rfu,
                                                        data,
                                                        st,
                                                        RepairFormationUnitEquipmentStaff::getAvailableStaff))
                                .collect(Collectors.toList()));
        return populateCellFunctions;
    }

    private Stream<ReportCell> getStaff(RepairFormationUnit rfu,
                                        RepairFormationUnitCombinedData data,
                                        EquipmentType st,
                                        Function<RepairFormationUnitEquipmentStaff, Integer> f) {
        return Stream.of(new ReportCell(f.apply(data
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
        List<ReportHeader> equipmentTypeHeaders = equipmentTypeSubHeaders(data.getEquipmentTypes()).collect(Collectors.toList());
        return Arrays.asList(nameReportHeader,
                             repairStationTypeReportHeader,
                             rstCountReportHeader,
                             new ReportHeader("По штату, чел.", equipmentTypeHeaders),
                             new ReportHeader("В наличии, чел.", equipmentTypeHeaders));
    }


    private Stream<ReportHeader> equipmentTypeSubHeaders(Collection<EquipmentType> equipmentTypes) {
        return equipmentTypes
                .stream()
                .map(et -> new ReportHeader(et.getShortName(),
                                            equipmentTypeSubHeaders(et.getEquipmentTypes()).collect(Collectors.toList())));
    }

    @Override
    protected int writeData(RepairFormationUnitCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getRepairFormationUnitList());
        return lastRowIndex + data.getRepairFormationUnitList().size();
    }

}
