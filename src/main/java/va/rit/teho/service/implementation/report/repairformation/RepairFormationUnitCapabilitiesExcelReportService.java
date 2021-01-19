package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairFormationUnitCapabilitiesExcelReportService extends
        AbstractExcelReportService<RepairFormationUnitRepairCapabilityCombinedData, RepairFormationUnit> {

    @Override
    protected List<ReportCellFunction<RepairFormationUnit>> populateCellFunctions(
            RepairFormationUnitRepairCapabilityCombinedData data) {
        List<ReportCellFunction<RepairFormationUnit>> functions =
                new ArrayList<>(Collections.singletonList(ReportCellFunction.of(RepairFormationUnit::getName,
                                                                                ReportCell.CellType.TEXT,
                                                                                HorizontalAlignment.LEFT)));
        List<Equipment> equipmentList =
                data
                        .getGroupedEquipmentData()
                        .values()
                        .stream()
                        .flatMap(subTypeListMap -> subTypeListMap.values().stream())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<ReportCellFunction<RepairFormationUnit>> capabilityFunctions = equipmentList
                .stream()
                .map(e -> ReportCellFunction.of((RepairFormationUnit rfu) -> data
                        .getCalculatedRepairCapabilities()
                        .getOrDefault(rfu, Collections.emptyMap())
                        .getOrDefault(e, 0.0)))
                .collect(Collectors.toList());
        functions.addAll(capabilityFunctions);
        return functions;
    }

    @Override
    protected String reportName() {
        return "Производственные возможности РВО по ремонту ВВСТ";
    }

    @Override
    protected List<ReportHeader> buildHeader(RepairFormationUnitRepairCapabilityCombinedData data) {
        ReportHeader nameHeader = header("Наименование ремонтного органа формирования", true);
        ReportHeader topHeader = header("Производственные возможности по ремонту ВВСТ, ед./сут.");
        data.getGroupedEquipmentData().forEach(((equipmentType, subTypeListMap) -> {
            ReportHeader eqTypeHeader =
                    Optional.ofNullable(equipmentType).map(et -> header(et.getShortName())).orElse(topHeader);
            subTypeListMap.forEach(((equipmentSubType, equipment) -> {
                ReportHeader eqSubTypeHeader = header(equipmentSubType.getShortName(), true);
                equipment.stream().map(e -> header(e.getName(), true)).forEach(eqSubTypeHeader::addSubHeader);
                eqTypeHeader.addSubHeader(eqSubTypeHeader);
            }));
            if (equipmentType != null) {
                topHeader.addSubHeader(eqTypeHeader);
            }
        }));
        return Arrays.asList(nameHeader, topHeader);
    }

    @Override
    protected void writeData(RepairFormationUnitRepairCapabilityCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getRepairFormationUnitList());
    }

}
