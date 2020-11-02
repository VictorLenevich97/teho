package va.rit.teho.service.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import va.rit.teho.service.common.CalculationService;
import va.rit.teho.service.implementation.common.CalculationServiceImpl;

public class CalculationServiceImplTest {

    private final CalculationService calculationService = new CalculationServiceImpl();

    @Test
    public void testCalculateEquipmentFailureAmount() {
        int totalAmount = 5;
        int intensity = 10;
        double k = 1.23;
        Assertions.assertEquals(
                (totalAmount * intensity * k) / 100,
                calculationService.calculateAvgDailyFailure(totalAmount, intensity, k));
    }

    @Test
    public void testCalculateEquipmentRequiringRepair() {
        double avgDailyFailure = 3.12;
        int upperBound = 5;
        int lowerBound = 3;
        int standardLaborInput = 123;
        double expectedResult = Math.abs(avgDailyFailure *
                                                 (Math.sin((Math.PI * upperBound) / (2 * standardLaborInput)) -
                                                         Math.sin((Math.PI * lowerBound) / (2 * standardLaborInput))));

        Assertions.assertEquals(
                expectedResult,
                calculationService.calculateEquipmentInRepairCount(upperBound,
                                                                   lowerBound,
                                                                   avgDailyFailure,
                                                                   standardLaborInput));
    }

    @Test
    public void testCalculateEquipmentRepairComplexity() {
        double count = 5.3;
        int upperBound = 12;
        double expectedResult = 0.75 * count * upperBound;
        Assertions.assertEquals(expectedResult,
                                calculationService.calculateEquipmentInRepairLaborInput(count, upperBound));
    }

    @Test
    public void testCalculateRepairCapabilities() {
        int totalStaff = 10;
        int workingTime = 20;
        long avgLaborInput = 25;
        double expectedResult = (totalStaff * workingTime * 0.78) / avgLaborInput;
        Assertions.assertEquals(
                expectedResult,
                calculationService.calculateRepairCapabilities(totalStaff, workingTime, avgLaborInput));
    }

}
