package by.varb.teho.service;

public interface CalculationService {
    double[] calculateEquipmentFailure(int[] groups, double[] dailyAverageLoss, double kM);

    double[][] calculateEquipmentRequiringRepair(int[] qMinArray, int[] qMaxArray, double[] wJ, int[] qjMax);

    double[][] calculateAverageComplexityRepair(double[][] wjArray, int[] qMaxArray);

    /**
     * Расчет производственных возможностей по ремонту
     *
     * @param totalStaff    общее количество специалистов-ремонтников
     * @param workingTime   время работы ремонтника, часы
     * @param avgLaborInput средняя трудоемкость ремонта, чел-часы
     * @return производственные возможности по ремонту, ед./сут.
     */
    double calculateRepairCapabilities(int totalStaff, int workingTime, long avgLaborInput);
}
