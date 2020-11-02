package va.rit.teho.dto.labordistribution;

public class CountAndLaborInputDTO {
    private final String count;
    private final String laborInput;

    public CountAndLaborInputDTO(String count, String laborInput) {
        this.count = count;
        this.laborInput = laborInput;
    }

    public String getCount() {
        return count;
    }

    public String getLaborInput() {
        return laborInput;
    }
}