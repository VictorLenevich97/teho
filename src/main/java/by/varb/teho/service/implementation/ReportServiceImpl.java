package by.varb.teho.service.implementation;

import by.varb.teho.exception.TehoException;
import by.varb.teho.report.AverageDailyOutputReportRecord;
import by.varb.teho.report.ExcelReportGenerator;
import by.varb.teho.report.LaborIntensityRepairNormReportRecord;
import by.varb.teho.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static by.varb.teho.enums.ReportTemplatePathEnum.AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH;
import static by.varb.teho.enums.ReportTemplatePathEnum.REPAIR_NORM_REPORT_TEMPLATE_PATH;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public byte[] generateLaborIntensityRepairNormReport() throws TehoException {
        List<LaborIntensityRepairNormReportRecord> reportData = createTestLaborIntensityRepairNormReportData();
        ExcelReportGenerator<LaborIntensityRepairNormReportRecord> excelReportGenerator = new ExcelReportGenerator<>();

        return excelReportGenerator.generateFileFromTemplate(REPAIR_NORM_REPORT_TEMPLATE_PATH.getPath(), reportData);
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

        return excelReportGenerator.generateFileFromTemplate(AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH.getPath(), reportData);
    }

    //todo Метод-заглушка для генерации тестовых данных для отчёта.
    // Потом нужно будет удалить
    private List<AverageDailyOutputReportRecord> createTestAverageDailyOutputReportData() {
        List<AverageDailyOutputReportRecord> list = new ArrayList<>();
        list.add(new AverageDailyOutputReportRecord("Танки", 2));
        list.add(new AverageDailyOutputReportRecord("БТР", 5));

        return list;
    }
}
