package va.rit.teho.service.report;

import org.springframework.stereotype.Service;
import va.rit.teho.report.ReportRow;
import va.rit.teho.repository.equipment.EquipmentLaborInputPerTypeRepository;
import va.rit.teho.repository.labordistribution.LaborDistributionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaborIntensityRepairNormReportDataCreator implements ReportDataCreator {

    private final LaborDistributionRepository laborDistributionRepository;
    private final EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository;

    public LaborIntensityRepairNormReportDataCreator(LaborDistributionRepository laborDistributionRepository,
                                                     EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository) {
        this.laborDistributionRepository = laborDistributionRepository;
        this.equipmentLaborInputPerTypeRepository = equipmentLaborInputPerTypeRepository;
    }

    @Override
    public List<ReportRow> createReportData() {
        List<ReportRow> reportData = new ArrayList<>();
        List<List<Object>> data = laborDistributionRepository.findAllGroupedByEquipmentName();

        for (List<Object> e : data) {
            String equipmentName = (String) e.get(0);
            List<Object> row = new ArrayList<>();
            row.addAll(e);
            row.addAll(equipmentLaborInputPerTypeRepository
                               .findRepairTypesByEquipmentName(equipmentName)
                               .stream()
                               .findFirst()
                               .orElseThrow(IllegalStateException::new));
            row.addAll(laborDistributionRepository
                               .sumEquipmentByRestorationLevelTypes(equipmentName)
                               .stream()
                               .findFirst()
                               .orElseThrow(IllegalStateException::new));
            reportData.add(new ReportRow(row));
        }

        return reportData;
    }
}
