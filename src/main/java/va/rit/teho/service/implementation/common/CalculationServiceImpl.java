package va.rit.teho.service.implementation.common;

import org.springframework.stereotype.Service;
import va.rit.teho.service.common.CalculationService;

@Service
public class CalculationServiceImpl implements CalculationService {

    @Override
    public double calculateAvgDailyFailure(int totalAmount, int intensity, double k) {
        return (totalAmount * intensity * k) / 100;
    }

    @Override
    public double calculateEquipmentInRepairCount(
            Integer upperBound,
            Integer lowerBound,
            Double avgDailyFailure,
            int standardLaborInput) {
        if (avgDailyFailure == null) {
            return 0.0;
        } else {
            int upper = upperBound == null ? 1000 : upperBound;
            int lower = lowerBound == null ? 0 : lowerBound;
            return Math.abs(avgDailyFailure *
                                    (Math.sin((Math.PI * upper) / (2 * standardLaborInput)) -
                                            Math.sin((Math.PI * lower) / (2 * standardLaborInput))));
        }
    }

    @Override
    public double calculateEquipmentInRepairLaborInput(double count, Integer upperBound) {
        return 0.75 * count * (upperBound == null ? 1000 : upperBound);
    }

    public double calculateRepairCapabilities(int totalStaff, int workingTime, long avgLaborInput) {
        //0.78 - коэффициент использования рабочего времени на основные работы. Находится в интервале 0.75 - 0.8
        return (totalStaff * workingTime * 0.78) / avgLaborInput;
    }
}
