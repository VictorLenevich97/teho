package va.rit.teho.controller;

import va.rit.teho.exception.TehoException;
import va.rit.teho.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/generateLaborIntensityRepairNormReport", produces = "application/vnd.ms-excel")
    @ResponseBody
    public byte[] generateLaborIntensityRepairNormReport() throws TehoException {
        return reportService.generateLaborIntensityRepairNormReport();
    }

    @GetMapping(value = "/generateAverageDailyOutputReport", produces = "application/vnd.ms-excel")
    @ResponseBody
    public byte[] generateAverageDailyOutputReport() throws TehoException {
        return reportService.generateAverageDailyOutputReport();
    }
}
