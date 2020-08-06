package by.varb.teho.report;

public class AverageDailyOutputReportRecord {

    private String name;
    private int amount;

    public AverageDailyOutputReportRecord() {
    }

    public AverageDailyOutputReportRecord(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
