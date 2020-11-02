package va.rit.teho.controller.report;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import va.rit.teho.exception.TehoException;
import va.rit.teho.service.report.ReportService;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/labor-intensity-repair-norm-report", produces = "application/vnd.ms-excel")
    @ResponseBody
    public byte[] generateLaborIntensityRepairNormReport() throws TehoException {
        return reportService.generateLaborIntensityRepairNormReport();
    }

    @GetMapping(value = "/average-daily-output-report", produces = "application/vnd.ms-excel")
    @ResponseBody
    public byte[] generateAverageDailyOutputReport() throws TehoException {
        return reportService.generateAverageDailyOutputReport();
    }

    @GetMapping(value = "/repair-fund-distribution-report", produces = "application/vnd.ms-excel")
    @ResponseBody
    public byte[] generateRepairFundDistributionReport() throws TehoException {
        return reportService.generateRepairFundDistributionReport();
    }
}
