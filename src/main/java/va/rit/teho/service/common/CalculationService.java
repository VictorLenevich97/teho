package va.rit.teho.service.common;

public interface CalculationService {

    /**
     * Расчет предполагаемого выхода из строя в ремонт
     *
     * @param totalAmount количество образцов ВВСТ
     * @param intensity   интенсивность выхода в ремонт (среднесуточная, в %)
     * @param k           поправочный коэффициент
     * @return значение среднесуточного выхода в ремонт (в единицах, штуках, комплектах)
     */
    double calculateAvgDailyFailure(int totalAmount, double intensity, double k);

    /**
     * Расчет количества образцов ВВСТ, требующих ремонта в данном диапазоне
     *
     * @param upperBound         верхняя граница диапазона
     * @param lowerBound         нижняя граница диапазона
     * @param avgDailyFailure    значение среднесуточного выхода в ремонт
     * @param standardLaborInput нормативная трудоемкость ремонта
     * @return количество образцов, требующих ремонта в данном диапазоне
     */
    double calculateEquipmentInRepairCount(Integer upperBound,
                                           Integer lowerBound,
                                           Double avgDailyFailure,
                                           int standardLaborInput);

    /**
     * Расчет средней трудоемкости ремонта ВВСТ для диапазона
     *
     * @param count      количество образцов ВВСТ, требующих ремонта в конкретном диапазоне
     * @param upperBound верхняя граница диапазона
     * @return средняя трудоемкость ВВСТ в заданном диапазоне
     */
    double calculateEquipmentInRepairLaborInput(double count, Integer upperBound);

    /**
     * Расчет производственных возможностей по ремонту
     *
     * @param totalStaff    общее количество специалистов-ремонтников
     * @param workingTime   время работы ремонтника, часы
     * @param intervalUpperBound верхняя граница диапазона трудоемкости, в котором работает данное РВО (используется для расчета средней трудоемкости: нужно умножить верхнюю границу на 0.75)
     * @return производственные возможности по ремонту, ед./сут.
     */
    double calculateRepairCapabilities(int totalStaff, int workingTime, Integer intervalUpperBound);
}
