package va.rit.teho.entity;

import java.util.Objects;

public class CountAndLaborInput {
    private final double count;
    private final double laborInput;

    public CountAndLaborInput(double count, double laborInput) {
        this.count = count;
        this.laborInput = laborInput;
    }

    public double getCount() {
        return count;
    }

    public double getLaborInput() {
        return laborInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CountAndLaborInput that = (CountAndLaborInput) o;
        return count == that.count &&
                laborInput == that.laborInput;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, laborInput);
    }
}
