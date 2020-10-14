package va.rit.teho.dto;

public class CountAndLaborInputDTO {
    private final Double count;
    private final Double laborInput;

    public CountAndLaborInputDTO(Double count, Double laborInput) {
        this.count = count;
        this.laborInput = laborInput;
    }

    public Double getCount() {
        return count;
    }

    public Double getLaborInput() {
        return laborInput;
    }
}