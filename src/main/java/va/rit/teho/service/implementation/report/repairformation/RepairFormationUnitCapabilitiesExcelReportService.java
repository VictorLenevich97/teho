package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitRepairCapabilityCombinedData;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.implementation.report.AbstractExcelReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairFormationUnitCapabilitiesExcelReportService extends
        AbstractExcelReportService<RepairFormationUnitRepairCapabilityCombinedData, RepairFormationUnit> {

    @Override
    protected List<ReportCell> populatedRowCells(
            RepairFormationUnitRepairCapabilityCombinedData data,
            RepairFormationUnit rfu) {
        List<ReportCell> functions =
                new ArrayList<>(Collections.singletonList(new ReportCell(rfu.getName(),
                                                                         ReportCell.CellType.TEXT,
                                                                         HorizontalAlignment.LEFT)));
        List<Equipment> equipmentList =
                data
                        .getEquipmentTypes()
                        .stream()
                        .flatMap(EquipmentType::collectRelatedEquipment)
                        .collect(Collectors.toList());
        List<ReportCell> capabilityFunctions = equipmentList
                .stream()
                .map(e -> new ReportCell(data
                                                 .getCalculatedRepairCapabilities()
                                                 .getOrDefault(rfu, Collections.emptyMap())
                                                 .getOrDefault(e, 0.0),
                                         ReportCell.CellType.NUMERIC))
                .collect(Collectors.toList());
        functions.addAll(capabilityFunctions);
        return functions;
    }

    @Override
    protected String reportName() {
        return "Производственные возможности РВО по ремонту ВВСТ";
    }

    private ReportHeader populateHeader(EquipmentType equipmentType) {
        ReportHeader header = header(equipmentType.getShortName());

        equipmentType.getEquipmentSet().forEach(e -> header.addSubHeader(header(e.getName(), true)));

        equipmentType.getEquipmentTypes().forEach(et -> {
            if (!(et.getEquipmentTypes().isEmpty() && et.getEquipmentSet().isEmpty())) {
                header.addSubHeader(populateHeader(et));
            }
        });

        return header;
    }

    @Override
    protected List<ReportHeader> buildHeader(RepairFormationUnitRepairCapabilityCombinedData data) {
        ReportHeader nameHeader = header("Наименование ремонтного органа формирования", true);
        ReportHeader topHeader = header("Производственные возможности по ремонту ВВСТ, ед./сут.");

        data.getEquipmentTypes().forEach(et -> topHeader.addSubHeader(populateHeader(et)));

        return Arrays.asList(nameHeader, topHeader);
    }

    @Override
    protected int writeData(RepairFormationUnitRepairCapabilityCombinedData data, Sheet sheet, int lastRowIndex) {
        writeRows(sheet, lastRowIndex, data, data.getRepairFormationUnitList());
        return lastRowIndex + data.getRepairFormationUnitList().size();
    }

}
