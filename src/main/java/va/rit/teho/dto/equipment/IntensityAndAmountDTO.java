package va.rit.teho.dto.equipment;

public class IntensityAndAmountDTO {
    private int intensity;
    private int amount;

    public IntensityAndAmountDTO(int intensity, int amount) {
        this.intensity = intensity;
        this.amount = amount;
    }

    public IntensityAndAmountDTO() {
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
