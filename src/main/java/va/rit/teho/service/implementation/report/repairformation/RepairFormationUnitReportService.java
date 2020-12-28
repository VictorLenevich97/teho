package va.rit.teho.service.implementation.report.repairformation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.report.Header;
import va.rit.teho.report.ReportCell;
import va.rit.teho.service.implementation.report.AbstractReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RepairFormationUnitReportService
        extends AbstractReportService<RepairFormationUnitReportData, RepairFormationUnit> {

    private final ThreadLocal<RepairFormationUnitReportData> data = new ThreadLocal<>();

    @Override
    public byte[] generateReport(RepairFormationUnitReportData data) {
        this.data.set(data);
        return super.generateReport(data);
    }

    @Override
    protected List<Function<RepairFormationUnit, ReportCell>> populateCellFunctions() {
        List<Function<RepairFormationUnit, ReportCell>> populateCellFunctions =
                new ArrayList<>(Arrays.asList(rfu -> new ReportCell(rfu.getName(), HorizontalAlignment.LEFT),
                                              rfu -> new ReportCell(rfu.getRepairStationType().getName()),
                                              rfu -> new ReportCell("" + rfu.getStationAmount())));
        List<EquipmentSubType> subTypes = data.get().getTypesWithSubTypes().entrySet().stream().flatMap(e -> e
                .getValue()
                .stream()).collect(Collectors.toList());
        populateCellFunctions.addAll(subTypes
                                             .stream()
                                             .flatMap(st -> Stream.of(getStaff(st,
                                                                               RepairFormationUnitEquipmentStaff::getTotalStaff)))
                                             .collect(Collectors.toList()));
        populateCellFunctions.addAll(subTypes
                                             .stream()
                                             .flatMap(st -> Stream.of(getStaff(st,
                                                                               RepairFormationUnitEquipmentStaff::getAvailableStaff)))
                                             .collect(Collectors.toList()));
        return populateCellFunctions;
    }

    private Function<RepairFormationUnit, ReportCell> getStaff(EquipmentSubType st,
                                                               Function<RepairFormationUnitEquipmentStaff, Integer> f) {
        return (RepairFormationUnit rfu) -> new ReportCell("" + f.apply(data
                                                                                .get()
                                                                                .getRepairFormationUnitEquipmentStaff()
                                                                                .get(rfu)
                                                                                .get(st)));
    }

    @Override
    protected String reportName() {
        return "Производственные возможности РВО по ремонту ВВСТ";
    }

    @Override
    protected List<Header> buildHeader() {
        Header nameHeader = new Header("Наименование ремонтного органа формирования", true, true);
        Header repairStationTypeHeader = new Header("Тип мастерской", true, true);
        Header rstConutHeader = new Header("Кол-во", true, true);
        return Arrays.asList(nameHeader,
                             repairStationTypeHeader,
                             rstConutHeader,
                             getSubHeaders("По штату, чел."),
                             getSubHeaders("В наличии, чел."));
    }

    private Header getSubHeaders(String topHeader) {
        List<Header> subHeaders = data
                .get()
                .getTypesWithSubTypes()
                .entrySet()
                .stream()
                .flatMap(e -> {
                    if (e.getKey() == null) {
                        return e
                                .getValue()
                                .stream()
                                .map(est -> new Header(est.getShortName(), true, true));
                    } else {
                        return Stream.of(new Header(e.getKey().getShortName(),
                                                    true,
                                                    e.getValue()
                                                     .stream()
                                                     .map(est -> new Header(est.getShortName(), true, true))
                                                     .collect(Collectors.toList())));
                    }
                })
                .collect(Collectors.toList());
        return new Header(topHeader, true, subHeaders);
    }

    @Override
    protected void writeData(RepairFormationUnitReportData data, Sheet sheet, int[] lastRow) {
        writeRows(sheet, lastRow[0], data.getRepairFormationUnitList());
    }
}
