package va.rit.teho.entity.labordistribution.combined;

import java.util.Objects;

public class CountAndLaborInput {

    public static final CountAndLaborInput EMPTY = createEmpty();

    private double count;
    private double laborInput;

    public CountAndLaborInput(double count, double laborInput) {
        this.count = count;
        this.laborInput = laborInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountAndLaborInput that = (CountAndLaborInput) o;
        return Double.compare(that.count, count) == 0 &&
                Double.compare(that.laborInput, laborInput) == 0;
    }

    public static CountAndLaborInput createEmpty() {
        return new CountAndLaborInput(0.0, 0.0);
    }

    public void add(double count, double laborInput) {
        this.count += count;
        this.laborInput += laborInput;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, laborInput);
    }

    public double getCount() {
        return count;
    }

    public double getLaborInput() {
        return laborInput;
    }

}
