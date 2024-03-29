import java.util.HashSet;
import java.util.Set;

public class SyntaxTree {

    private BinaryTree bt;
    private Node root;
    private int numOfLeafs;
    private Set<Integer>[] followPos;

    public SyntaxTree(String regex) {
        bt = new BinaryTree();

        root = bt.generateTree(regex);
        numOfLeafs = bt.getNumberOfLeafs();
        followPos = new Set[numOfLeafs];
        for (int i = 0; i < numOfLeafs; i++) {
            followPos[i] = new HashSet<>();
        }

        generateNullable(root);
        generateFirstPosLastPos(root);
        generateFollowPos(root);
    }

    private void generateNullable(Node node) {
        if (node == null) {
            return;
        }
        if (!(node instanceof LeafNode)) {
            Node left = node.getLeft();
            Node right = node.getRight();
            generateNullable(left);
            generateNullable(right);
            switch (node.getSymbol()) {
                case "|":
                    node.setNullable(left.isNullable() | right.isNullable());
                    break;
                case "&":
                    node.setNullable(left.isNullable() & right.isNullable());
                    break;
                case "*":
                    node.setNullable(true);
                    break;
            }
        }
    }

    private void generateFirstPosLastPos(Node node) {
        if (node == null) {
            return;
        }
        if (node instanceof LeafNode leafNode) {
            node.addToFirstPos(leafNode.getNum());
            node.addToLastPos(leafNode.getNum());
        } else {
            Node left = node.getLeft();
            Node right = node.getRight();
            generateFirstPosLastPos(left);
            generateFirstPosLastPos(right);
            switch (node.getSymbol()) {
                case "|":
                    node.addAllToFirstPos(left.getFirstPos());
                    node.addAllToFirstPos(right.getFirstPos());
                    //
                    node.addAllToLastPos(left.getLastPos());
                    node.addAllToLastPos(right.getLastPos());
                    break;
                case "&":
                    if (left.isNullable()) {
                        node.addAllToFirstPos(left.getFirstPos());
                        node.addAllToFirstPos(right.getFirstPos());
                    } else {
                        node.addAllToFirstPos(left.getFirstPos());
                    }
                    //
                    if (right.isNullable()) {
                        node.addAllToLastPos(left.getLastPos());
                        node.addAllToLastPos(right.getLastPos());
                    } else {
                        node.addAllToLastPos(right.getLastPos());
                    }
                    break;
                case "*":
                    node.addAllToFirstPos(left.getFirstPos());
                    node.addAllToLastPos(left.getLastPos());
                    break;
            }
        }
    }

    private void generateFollowPos(Node node) {
        if (node == null) {
            return;
        }
        Node left = node.getLeft();
        Node right = node.getRight();
        switch (node.getSymbol()) {
            case "&":
                Object[] lastPos_c1 = left.getLastPos().toArray();
                Set<Integer> firstPos_c2 = right.getFirstPos();
                for (int i = 0; i < lastPos_c1.length; i++) {
                    followPos[(Integer) lastPos_c1[i] - 1].addAll(firstPos_c2);
                }
                break;
            case "*":
                Object[] lastPos_n = node.getLastPos().toArray();
                Set<Integer> firstPos_n = node.getFirstPos();
                for (int i = 0; i < lastPos_n.length; i++) {
                    followPos[(Integer) lastPos_n[i] - 1].addAll(firstPos_n);
                }
                break;
        }
        generateFollowPos(node.getLeft());
        generateFollowPos(node.getRight());

    }

    public Set<Integer>[] getFollowPos() {
        return followPos;
    }

    public Node getRoot() {
        return this.root;
    }
}
