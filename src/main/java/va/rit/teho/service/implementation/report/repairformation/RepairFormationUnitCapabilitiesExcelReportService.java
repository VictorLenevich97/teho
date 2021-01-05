package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RepairFormationUnitCapabilitiesExcelReportService extends
        AbstractExcelReportService<RepairFormationUnitRepairCapabilityCombinedData, RepairFormationUnit> {

    private final ThreadLocal<RepairFormationUnitRepairCapabilityCombinedData> data = new ThreadLocal<>();

    @Override
    protected List<Function<RepairFormationUnit, ReportCell>> populateCellFunctions() {
        List<Function<RepairFormationUnit, ReportCell>> functions = new ArrayList<>(Collections.singletonList((rfu) -> new ReportCell(
                rfu.getName(),
                HorizontalAlignment.LEFT)));
        List<Equipment> equipmentList =
                this
                        .data
                        .get()
                        .getGroupedEquipmentData()
                        .values()
                        .stream()
                        .flatMap(subTypeListMap -> subTypeListMap.values().stream())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<Function<RepairFormationUnit, ReportCell>> capabilityFunctions = equipmentList
                .stream()
                .map(this::getCapabilitiesFunction)
                .collect(Collectors.toList());
        functions.addAll(capabilityFunctions);
        return functions;
    }

    private Function<RepairFormationUnit, ReportCell> getCapabilitiesFunction(Equipment e) {
        return (RepairFormationUnit rfu) -> new ReportCell("" + this.data
                .get()
                .getCalculatedRepairCapabilities()
                .get(rfu)
                .get(e));
    }

    @Override
    protected String reportName() {
        return "Производственные возможности РВО по ремонту ВВСТ";
    }

    @Override
    public byte[] generateReport(RepairFormationUnitRepairCapabilityCombinedData data) {
        this.data.set(data);
        return super.generateReport(data);
    }

    @Override
    protected List<ReportHeader> buildHeader() {
        ReportHeader nameHeader = new ReportHeader("Наименование ремонтного органа формирования", true, true);
        ReportHeader topHeader = new ReportHeader("Производственные возможности по ремонту ВВСТ, ед./сут.",
                                                  true,
                                                  false);
        data.get().getGroupedEquipmentData().forEach(((equipmentType, subTypeListMap) -> {
            ReportHeader eqTypeHeader = equipmentType == null ? null : new ReportHeader(equipmentType.getShortName(),
                                                                                        true,
                                                                                        false);
            subTypeListMap.forEach(((equipmentSubType, equipment) -> {
                ReportHeader eqSubTypeHeader = new ReportHeader(equipmentSubType.getShortName(), true, true);
                equipment
                        .stream()
                        .map(e -> new ReportHeader(e.getName(), true, true))
                        .forEach(eqSubTypeHeader::addSubHeader);
                (eqTypeHeader == null ? topHeader : eqTypeHeader).addSubHeader(eqSubTypeHeader);
            }));
            if (eqTypeHeader != null) {
                topHeader.addSubHeader(eqTypeHeader);
            }
        }));
        return Arrays.asList(nameHeader, topHeader);
    }

    @Override
    protected void writeData(RepairFormationUnitRepairCapabilityCombinedData data, Sheet sheet, int[] lastRow) {
        writeRows(sheet, lastRow[0], data.getRepairFormationUnitList());
    }

    @Override
    protected byte[] writeSheet(Sheet sheet) {
        byte[] result = super.writeSheet(sheet);
        this.data.remove();
        return result;
    }
}
