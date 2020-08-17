package by.varb.teho.model;

import java.util.Objects;

public class Pair<L, R> {
    private L left;
    private R right;

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public static <Left, Right> Pair<Left, Right> of(Left left, Right right) {
        return new Pair<>(left, right);
    }
}
