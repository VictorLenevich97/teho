package va.rit.teho.service.implementation.report.equipment;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentExcelReportService
        extends AbstractExcelReportService<Collection<EquipmentType>, Equipment> {

    private final RepairTypeService repairTypeService;

    public EquipmentExcelReportService(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @Override
    protected List<ReportHeader> buildHeader(Collection<EquipmentType> data) {
        List<ReportHeader> result = new ArrayList<>(Arrays.asList(header("Тип ВВСТ, марка техники"), header("Вид")));
        List<RepairType> repairTypes = repairTypeService.list(true).stream().filter(RepairType::isRepairable).collect(
                Collectors.toList());
        ReportHeader repairTypeReportHeader = header("Вид ремонта");
        repairTypes.forEach(repairType -> repairTypeReportHeader.addSubHeader(header(repairType.getFullName())));

        result.add(repairTypeReportHeader);
        return result;
    }

    @Override
    protected int writeData(Collection<EquipmentType> data,
                            Sheet sheet,
                            int lastRowIndex) {
        List<RepairType> repairTypes = repairTypeService.list(true).stream().filter(RepairType::isRepairable).collect(
                Collectors.toList());
        for (EquipmentType equipmentType : data) {
            if (equipmentType.getParentType() == null || !equipmentType.getEquipmentTypes().isEmpty()) {
                createRowWideCell(sheet,
                                  lastRowIndex,
                                  repairTypes.size() + 1,
                                  equipmentType.getShortName(),
                                  false,
                                  true);
                lastRowIndex += 1;
            }
            Set<Equipment> equipmentSet = equipmentType.getEquipmentSet();
            writeRows(sheet, lastRowIndex, data, equipmentSet);

            lastRowIndex = writeData(equipmentType.getEquipmentTypes(), sheet, equipmentSet.size() + lastRowIndex);
        }

        return lastRowIndex;
    }

    @Override
    protected List<ReportCell> populateRowCells(Collection<EquipmentType> data,
                                                Equipment equipment) {
        List<RepairType> repairTypes = repairTypeService.list(true).stream().filter(RepairType::isRepairable).collect(
                Collectors.toList());
        List<ReportCell> reportCells =
                new ArrayList<>(Arrays.asList(
                        new ReportCell(equipment.getName(), ReportCell.CellType.TEXT, HorizontalAlignment.LEFT),
                        new ReportCell(equipment.getEquipmentType().getShortName())));

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
