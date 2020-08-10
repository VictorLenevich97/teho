package by.varb.teho.service.implementation;

import by.varb.teho.service.CalculationService;
import org.springframework.stereotype.Service;

@Service
public class CalculationServiceImpl implements CalculationService {

    //расчёт предполагаемого выхода из строя групп ВВСТ
    public double [] calculateEquipmentFailure(int [] groups, double [] dailyAverageLoss, double kM) {
        double[] wJ = new double [groups.length];
        for (int i = 0; i < groups.length; i++) {
            wJ[i] = (groups[i] * dailyAverageLoss[i]) / 100 * kM;
        }
        return wJ;
    }

    //вычисление количества образцов ВВСТ, требующих ремонта(текущий и средний)
    public double [][] calculateEquipmentRequiringRepair(int[] qMinArray, int[] qMaxArray, double [] wJ, int [] qjMax) {
        double[][] wjArray = new double[wJ.length][qMinArray.length];
        for (int i = 0; i < wJ.length; i++)
            for (int j = 0; j < qMinArray.length; j++)
            {
                wjArray[i][j] = Math.abs((wJ[i] * (Math.sin((Math.PI * qMaxArray[j]) / (2 * qjMax[i])) - Math.sin((Math.PI * qMinArray[j]) / (2 * qjMax[i])))));
            }
        return wjArray;
    }

    //расчёт средней трудоёмкости ремонта ВВСТ
    public double [][] calculateAverageComplexityRepair(double [][] wjArray, int[] qMaxArray) {
        double[][] qjArray = new double[wjArray.length][wjArray[0].length];
        for (int i = 0; i < qjArray.length; i++)
            for (int j = 0; j < qMaxArray.length; j++)
            {
                qjArray[i][j] = 0.75 * wjArray[i][j] * qMaxArray[i];
            }
        return qjArray;
    }

    public double calculateRepairCapabilities(int totalStaff, int workingTime, long avgLaborInput) {
        //0.78 - коэффициент использования рабочего времени на основные работы. Находится в интервале 0.75 - 0.8
        return (totalStaff * workingTime * 0.78) / avgLaborInput;
    }
}
