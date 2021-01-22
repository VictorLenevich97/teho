package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentExcelReportService
        extends AbstractExcelReportService<Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>>, Equipment> {

    private final List<RepairType> repairTypes;

    public EquipmentExcelReportService(RepairTypeService repairTypeService) {
        this.repairTypes = repairTypeService.list(true);
    }

    @Override
    protected List<ReportHeader> buildHeader(Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> data) {
        List<ReportHeader> result = new ArrayList<>(Arrays.asList(header("Тип ВВСТ, марка техники"), header("Вид")));

        ReportHeader repairTypeReportHeader = header("Вид ремонта");
        repairTypes.forEach(repairType -> repairTypeReportHeader.addSubHeader(header(repairType.getFullName())));

        result.add(repairTypeReportHeader);
        return result;
    }

    @Override
    protected void writeData(Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> data,
                             Sheet sheet,
                             int lastRowIndex) {
        for (Map.Entry<EquipmentType, Map<EquipmentSubType, List<Equipment>>> entry : data.entrySet()) {
            EquipmentType eqType = entry.getKey();
            Map<EquipmentSubType, List<Equipment>> subTypeListMap = entry.getValue();

            createRowWideCell(sheet, lastRowIndex, repairTypes.size() + 1, eqType.getShortName(), false, true);

            List<Equipment> equipmentList =
                    subTypeListMap.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());

            writeRows(sheet, lastRowIndex + 1, data, equipmentList);

            lastRowIndex += subTypeListMap.size() + 1;
        }
    }

    @Override
    protected List<ReportCell> populatedRowCells(Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> data,
                                                 Equipment equipment) {

        List<ReportCell> reportCells =
                new ArrayList<>(Arrays.asList(
                        new ReportCell(equipment.getName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT),
                        new ReportCell(equipment.getEquipmentSubType().getShortName())));

        List<ReportCell> repairTypeCells =
                repairTypes
                        .stream()
                        .map(rt -> new ReportCell(equipment.getLaborInputPerTypes()
                                                           .stream()
                                                           .filter(elipt -> elipt.getRepairType().equals(rt))
                                                           .findFirst()
                                                           .map(EquipmentLaborInputPerType::getAmount)
                                                           .orElse(0)))
                        .collect(Collectors.toList());
        reportCells.addAll(repairTypeCells);
        return reportCells;
    }

    @Override
    protected String reportName() {
        return "Нормативы трудоемкости ремонта";
    }
}
