package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.exception.TehoException;
import va.rit.teho.report.ExcelReportGenerator;
import va.rit.teho.report.RepairFundDistributionReportStyler;
import va.rit.teho.report.ReportRow;
import va.rit.teho.report.ReportRowStyler;
import va.rit.teho.service.ReportService;
import va.rit.teho.service.report.ReportDataCreator;

import static va.rit.teho.enums.ReportTemplatePathEnum.*;

@Service
public class ReportServiceImpl implements ReportService {


    private final ReportDataCreator averageDailyOutputReportDataCreator;
    private final ReportDataCreator laborIntensityRepairNormReportDataCreator;
    private final ReportDataCreator repairFundDistributionDataCreator;

    public ReportServiceImpl(ReportDataCreator averageDailyOutputReportDataCreator,
                             ReportDataCreator laborIntensityRepairNormReportDataCreator,
                             ReportDataCreator repairFundDistributionDataCreator
    ) {
        this.averageDailyOutputReportDataCreator = averageDailyOutputReportDataCreator;
        this.laborIntensityRepairNormReportDataCreator = laborIntensityRepairNormReportDataCreator;
        this.repairFundDistributionDataCreator = repairFundDistributionDataCreator;
    }

    @Override
    public byte[] generateLaborIntensityRepairNormReport() throws TehoException {
        ExcelReportGenerator<ReportRow> excelReportGenerator = new ExcelReportGenerator<>();
        return excelReportGenerator.generateFileFromTemplate(
                REPAIR_NORM_REPORT_TEMPLATE_PATH.getPath(),
                laborIntensityRepairNormReportDataCreator.createReportData()
        );
    }

    @Override
    public byte[] generateAverageDailyOutputReport() throws TehoException {
        ExcelReportGenerator<ReportRow> excelReportGenerator = new ExcelReportGenerator<>();
        return excelReportGenerator.generateFileFromTemplate(
                AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH.getPath(),
                averageDailyOutputReportDataCreator.createReportData()
        );
    }

    @Override
    public byte[] generateRepairFundDistributionReport() throws TehoException {
        ExcelReportGenerator<ReportRow> excelReportGenerator = new ExcelReportGenerator<>();
        ReportRowStyler reportRowStyler = new RepairFundDistributionReportStyler();
        return reportRowStyler.styleRows(
                excelReportGenerator.generateFileFromTemplate(REPAIR_FUND_DISTRIBUTION_TEMPLATE_PATH.getPath(),
                        repairFundDistributionDataCreator.createReportData())
        );
    }
}
