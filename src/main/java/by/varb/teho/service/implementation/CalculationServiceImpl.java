package by.varb.teho.service.implementation;

import by.varb.teho.service.CalculationService;
import org.springframework.stereotype.Service;

@Service
public class CalculationServiceImpl implements CalculationService {

    @Override
    public double calculateEquipmentFailureAmount(int totalAmount, int intensity, double k) {
        return (totalAmount * intensity * k) / 100;
    }

    @Override
    public double calculateEquipmentRequiringRepair(
            Integer upperBound,
            Integer lowerBound,
            double avgDailyFailure,
            int standardLaborInput) {
        return Math.abs(avgDailyFailure *
                                (Math.sin((Math.PI * upperBound) / (2 * standardLaborInput)) -
                                        Math.sin((Math.PI * lowerBound) / (2 * standardLaborInput))));
    }

    @Override
    public double calculateEquipmentRepairComplexity(double count, Integer upperBound) {
        return 0.75 * count * upperBound;
    }

    public double calculateRepairCapabilities(int totalStaff, int workingTime, long avgLaborInput) {
        //0.78 - коэффициент использования рабочего времени на основные работы. Находится в интервале 0.75 - 0.8
        return (totalStaff * workingTime * 0.78) / avgLaborInput;
    }
}
