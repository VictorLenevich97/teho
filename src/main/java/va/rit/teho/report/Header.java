package va.rit.teho.report;

import java.util.ArrayList;
import java.util.List;

public class Header {
    private final String name;
    private final boolean centered;
    private final List<Header> children;

    public Header(String name, boolean centered) {
        this.name = name;
        this.centered = centered;
        this.children = new ArrayList<>();
    }

    public Header(String name, List<Header> children) {
        this.name = name;
        this.children = children;
        this.centered = false;
    }

    public boolean isCentered() {
        return centered;
    }

    public String getName() {
        return name;
    }

    public List<Header> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Header addChildren(Header children) {
        this.children.add(children);
        return this;
    }

    public int depth() {
        return depth(this, 0);
    }

    private int depth(Header header, int depth) {
        if (header.hasChildren()) {
            return header
                    .children
                    .stream()
                    .map(h -> depth(h, depth + 1))
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(depth);
        }
        return depth;
    }
}
