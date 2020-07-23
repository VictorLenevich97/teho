package by.varb.teho.service;

import by.varb.teho.exception.TehoException;

public interface ReportService {

    byte[] generateLaborIntensityRepairNormReport() throws TehoException;

    byte[] generateAverageDailyOutputReport() throws TehoException;
}
