package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EquipmentExcelReportService
        extends AbstractExcelReportService<Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>>, Equipment> {

    private final RepairTypeService repairTypeService;
    private final List<RepairType> repairTypes;

    public EquipmentExcelReportService(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
        this.repairTypes = repairTypeService.list(true);
    }

    @Override
    protected List<ReportHeader> buildHeader() {
        List<ReportHeader> result = new ArrayList<>();
        result.add(new ReportHeader("Тип ВВСТ, марка техники", true, false));
        result.add(new ReportHeader("Вид", true, false));
        ReportHeader repairTypeReportHeader = new ReportHeader("Вид ремонта", true, false);
        repairTypes.forEach(repairType -> repairTypeReportHeader.addSubHeader(new ReportHeader(repairType.getFullName(),
                                                                                               true,
                                                                                               false)));
        result.add(repairTypeReportHeader);
        return result;
    }

    @Override
    protected int writeData(Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> data,
                             Sheet sheet,
                             int[] lastRow) {
        data.forEach((eqType, subTypeListMap) -> {
            int lastRowIndex = lastRow[0];

            createRowWideCell(sheet, lastRowIndex, repairTypes.size() + 1, eqType.getShortName(), false, true);

            List<Equipment> equipmentList =
                    subTypeListMap.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());

            writeRows(sheet, lastRowIndex + 1, equipmentList);

            lastRow[0] += subTypeListMap.size() + 1;
        });
        return lastRow[0];
    }

    private Function<Equipment, ReportCell> equipmentLaborInputFunction(RepairType rt) {
        return ((Equipment e) -> new ReportCell("" + e.getLaborInputPerTypes()
                                                      .stream()
                                                      .filter(elipt -> elipt.getRepairType().equals(rt))
                                                      .findFirst()
                                                      .orElseThrow(() -> new NotFoundException("Тип ремонта не найден"))
                                                      .getAmount()));
    }

    @Override
    protected List<Function<Equipment, ReportCell>> populateCellFunctions() {
        List<RepairType> repairTypes = repairTypeService.list(true);
        List<Function<Equipment, ReportCell>> populateCellFunctions =
                new ArrayList<>(Arrays.asList(e -> new ReportCell(e.getName(),
                                                                  ReportCell.CellType.TEXT, HorizontalAlignment.LEFT),
                                              (e) -> new ReportCell(e.getEquipmentSubType().getShortName())));
        List<Function<Equipment, ReportCell>> rtFunctions = repairTypes
                .stream()
                .map(this::equipmentLaborInputFunction)
                .collect(Collectors.toList());
        populateCellFunctions.addAll(rtFunctions);
        return populateCellFunctions;
    }

    @Override
    protected String reportName() {
        return "Нормативы трудоемкости ремонта";
    }
}
