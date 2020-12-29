package va.rit.teho.entity.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tree<T> {
    public Node<T> getRoot() {
        return root;
    }

    private final Node<T> root;

    public Tree(T rootData) {
        root = new Node<>();
        root.data = rootData;
        root.children = new ArrayList<>();
    }

    private int internalFindLowestLevel(Node<T> n) {
        int newLevel =
                n.getChildren().stream().map(this::internalFindLowestLevel).mapToInt(Integer::intValue).max().orElse(1);
        return Math.max(newLevel, n.getLevel());
    }

    public int findLowestLevel() {
        return internalFindLowestLevel(root);
    }

    public Stream<Node<T>> internalFindNodesWithLevel(Node<T> node, int level) {
        if (node.getLevel() == level) {
            return Stream.of(node);
        }
        return node.getChildren().stream().flatMap(n -> internalFindNodesWithLevel(n, level));
    }

    public List<Node<T>> findNodesWithLevel(int level) {
        return internalFindNodesWithLevel(root, level).collect(Collectors.toList());
    }

    public String toString() {
        return root.toString();
    }

    public static class Node<T> {
        private T data;
        private List<Node<T>> children;
        private int level;

        public Node() {
            this.data = null;
            this.children = new ArrayList<>();
            this.level = 1;
        }

        public Node(T data, Node<T> parent) {
            this();
            this.data = data;
            this.level = parent.level + 1;
        }

        public Node<T> addChildren(T data) {
            Node<T> children = new Node<>(data, this);
            this.children.add(children);
            return children;
        }

        public T getData() {
            return data;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public int getLevel() {
            return level;
        }
    }
}