package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.report.Header;
import va.rit.teho.report.ReportCell;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.implementation.report.AbstractReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EquipmentReportService
        extends AbstractReportService<Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>>, Equipment> {

    private final RepairTypeService repairTypeService;

    public EquipmentReportService(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    private List<Header> buildHeader(List<RepairType> repairTypes) {
        List<Header> result = new ArrayList<>();
        result.add(new Header("Тип ВВСТ, марка техники", true));
        result.add(new Header("Вид", true));
        Header repairTypeHeader = new Header("Вид ремонта", true);
        repairTypes.forEach(repairType -> repairTypeHeader.addChildren(new Header(repairType.getFullName(), true)));
        result.add(repairTypeHeader);
        return result;
    }

    @Override
    public byte[] generateReport(Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> data) {
        Sheet sheet = createSheet("Нормативы трудоемкости ремонта");
        List<RepairType> repairTypes = repairTypeService.list(true);

        final int[] lastRow = {writeHeader(sheet, buildHeader(repairTypes)) + 1};
        data.forEach((eqType, subTypeListMap) -> {
            int lastRowIndex = lastRow[0];

            Cell eqTypeCell = sheet.createRow(lastRowIndex).createCell(0);
            alignCellCenter(eqTypeCell).setCellValue(eqType.getShortName());
            mergeCells(sheet, lastRowIndex, lastRowIndex, 0, repairTypes.size() + 1);

            List<Equipment> equipmentList =
                    subTypeListMap.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
            writeRows(sheet, lastRowIndex + 1, equipmentList);

            lastRow[0] += subTypeListMap.size() + 1;
        });
        return writeSheet(sheet);
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
                new ArrayList<>(Arrays.asList(e -> new ReportCell(e.getName(), HorizontalAlignment.LEFT),
                                              (e) -> new ReportCell(e.getEquipmentSubType().getShortName())));
        List<Function<Equipment, ReportCell>> rtFunctions = repairTypes
                .stream()
                .map(this::equipmentLaborInputFunction)
                .collect(Collectors.toList());
        populateCellFunctions.addAll(rtFunctions);
        return populateCellFunctions;
    }
}
