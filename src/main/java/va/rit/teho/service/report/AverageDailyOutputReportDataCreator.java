package va.rit.teho.service.report;

import org.springframework.stereotype.Service;
import va.rit.teho.report.ReportRow;
import va.rit.teho.repository.EquipmentInRepairRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AverageDailyOutputReportDataCreator implements ReportDataCreator {

    private final EquipmentInRepairRepository equipmentInRepairRepository;

    public AverageDailyOutputReportDataCreator(EquipmentInRepairRepository equipmentInRepairRepository) {
        this.equipmentInRepairRepository = equipmentInRepairRepository;
    }

    @Override
    public List<ReportRow> createReportData() {
        List<ReportRow> reportData = new ArrayList<>();
        List<List<Object>> data = equipmentInRepairRepository.findAllGroupedByEquipmentName();

        data.forEach(e -> {
            reportData.add(new ReportRow(Arrays.asList(e.get(0), e.get(2))));
        });

        return reportData;
    }
}
