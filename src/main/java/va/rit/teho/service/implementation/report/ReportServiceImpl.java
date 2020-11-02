package va.rit.teho.service.implementation.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.exception.TehoException;
import va.rit.teho.report.*;
import va.rit.teho.service.report.ReportService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static va.rit.teho.enums.ReportTemplatePathEnum.*;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    @Override
    public byte[] generateLaborIntensityRepairNormReport() throws TehoException {
        List<LaborIntensityRepairNormReportRecord> reportData = createTestLaborIntensityRepairNormReportData();
        ExcelReportGenerator<LaborIntensityRepairNormReportRecord> excelReportGenerator = new ExcelReportGenerator<>();

        return excelReportGenerator.generateFileFromTemplate(REPAIR_NORM_REPORT_TEMPLATE_PATH.getPath(),
                                                             reportData);
    }

    //todo Метод-заглушка для генерации тестовых данных для отчёта.
    // Потом нужно будет удалить
    private List<LaborIntensityRepairNormReportRecord> createTestLaborIntensityRepairNormReportData() {
        List<LaborIntensityRepairNormReportRecord> list = new ArrayList<>();
        list.add(new LaborIntensityRepairNormReportRecord("Т-72", "Танк", 180, 540, 1500));
        list.add(new LaborIntensityRepairNormReportRecord("БТР-60П", "БТР", 50, 150, 450));

        return list;
    }

    @Override
    public byte[] generateAverageDailyOutputReport() throws TehoException {
        List<AverageDailyOutputReportRecord> reportData = createTestAverageDailyOutputReportData();
        ExcelReportGenerator<AverageDailyOutputReportRecord> excelReportGenerator = new ExcelReportGenerator<>();

        return excelReportGenerator.generateFileFromTemplate(AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH.getPath(),
                                                             reportData);
    }

    //todo Метод-заглушка для генерации тестовых данных для отчёта.
    // Потом нужно будет удалить
    private List<AverageDailyOutputReportRecord> createTestAverageDailyOutputReportData() {
        List<AverageDailyOutputReportRecord> list = new ArrayList<>();
        list.add(new AverageDailyOutputReportRecord("Танки", 2));
        list.add(new AverageDailyOutputReportRecord("БТР", 5));

        return list;
    }

    @Override
    public byte[] generateRepairFundDistributionReport() throws TehoException {
        List<ReportRow> reportData = createTestRepairFundDistributionData();
        ExcelReportGenerator<ReportRow> excelReportGenerator = new ExcelReportGenerator<>();
        ReportRowStyler reportRowStyler = new RepairFundDistributionReportStyler();

        return reportRowStyler.styleRows(
                excelReportGenerator.generateFileFromTemplate(REPAIR_FUND_DISTRIBUTION_TEMPLATE_PATH.getPath(), reportData)
        );
    }

    //todo Метод-заглушка для генерации тестовых данных для отчёта.
    // Потом нужно будет удалить
    private List<ReportRow> createTestRepairFundDistributionData() {
        return Arrays.asList(
                new ReportRow(Arrays.asList("Ракетно-артиллерийское вооружение")),
                new ReportRow(Arrays.asList("ГрА", "БМП-21", 21, 43, 21, 31, 22, 32, 23, 33, 24, 34, 25, 35, 26, 36, 27, 37, 2)),
                new ReportRow(Arrays.asList("омб", "9П516", 1, 8, 21, 31, 22, 32, 23, 33, 24, 34, 25, 35, 26, 36, 27, 37, 7)),
                new ReportRow(Arrays.asList("итого", null, 0, 0, null, null, null))
        );
    }
}
