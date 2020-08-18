package va.rit.teho.service;

import va.rit.teho.exception.TehoException;

public interface ReportService {

    byte[] generateLaborIntensityRepairNormReport() throws TehoException;

    byte[] generateAverageDailyOutputReport() throws TehoException;
}
