package va.rit.teho.service.report;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.CountAndLaborInput;
import va.rit.teho.entity.EquipmentLaborInputDistribution;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.report.ReportRow;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.LaborInputDistributionService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairFundDistributionDataCreator implements ReportDataCreator {

    private final LaborInputDistributionService laborInputDistributionService;
    private final EquipmentTypeRepository equipmentTypeRepository;

    public RepairFundDistributionDataCreator(LaborInputDistributionService laborInputDistributionService, EquipmentTypeRepository equipmentTypeRepository) {
        this.laborInputDistributionService = laborInputDistributionService;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    public List<ReportRow> createReportData() {
        List<ReportRow> reportData = new ArrayList<>();

        List<Long> allEquipmentTypeIdList = ((List<EquipmentType>) equipmentTypeRepository.findAll()).stream().map(EquipmentType::getId).collect(Collectors.toList());
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(allEquipmentTypeIdList);

        laborInputDistribution.keySet().forEach(equipmentType -> {
            reportData.add(new ReportRow(Collections.singletonList(equipmentType.getFullName())));
            Map<EquipmentSubType, List<EquipmentLaborInputDistribution>> map = laborInputDistribution.get(equipmentType);
            addSubTypeData(map, reportData);
        });

        return reportData;
    }

    private void addSubTypeData(Map<EquipmentSubType, List<EquipmentLaborInputDistribution>> map, List<ReportRow> reportData) {
    List<ReportRow> subtypeData = new ArrayList<>();

        map.keySet().forEach(equipmentSubType -> {
            map.get(equipmentSubType).forEach(distribution -> {
                List<Object> row = new ArrayList<>();
                row.addAll(Arrays.asList(
                        distribution.getBaseName(),
                        distribution.getEquipmentName(),
                        distribution.getAvgDailyFailure(),
                        (double) distribution.getStandardLaborInput())
                );
                row.addAll(composeLaborInputData(distribution));
                row.add(distribution.getTotalRepairComplexity());
                subtypeData.add(new ReportRow(row));
            });

            reportData.addAll(subtypeData);
            reportData.add(calculateTotalResultForSubType(subtypeData, "итого " + equipmentSubType.getShortName()));
        });
    }

    private List<Object> composeLaborInputData(EquipmentLaborInputDistribution distribution) {
        List<Object> data = new ArrayList<>(Collections.nCopies(14, (double) 0));
        int index = 0;

        for (CountAndLaborInput countAndLaborInput : distribution.getIntervalCountAndLaborInputMap().values()) {
            data.set(index, countAndLaborInput.getCount());
            data.set(index + 1, countAndLaborInput.getLaborInput());
            index += 2;
        }

        return data;
    }

    private ReportRow calculateTotalResultForSubType(List<ReportRow> data, String subTypeRowName) {
        List<Object> row = new ArrayList<>(Collections.nCopies(data.get(0).getRow().size(), (double) 0));
        row.set(0, subTypeRowName);
        row.set(1, null);

        for (int i = 2; i < row.size(); i++) {
            for (ReportRow reportRow : data) {
                row.set(i, (double) reportRow.getRow().get(i) + (double) row.get(i));
            }
        }

        return new ReportRow(row);
    }
}
