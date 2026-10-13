import java.util.ArrayList;
import java.util.List;

public class RedBlackTree {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        int value;
        boolean color;
        Node left = nil;
        Node right = nil;
        Node parent = nil;

        Node(int value, boolean color) {
            this.value = value;
            this.color = color;
        }
    }

    private final Node nil = new Node(0, BLACK);
    private Node root = nil;
    private int size;

    public record Step(String text, NodeView tree, Integer marked) {
    }

    private List<Step> steps;

    public RedBlackTree() {
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
    }

    public int size() {
        return size;
    }

    public boolean contains(int value) {
        return find(value) != nil;
    }

    private Node find(int value) {
        Node current = root;
        while (current != nil && current.value != value) {
            current = value < current.value ? current.left : current.right;
        }
        return current;
    }

    // inserts the value and returns the picture of the tree after every step of the algorithm
    public List<Step> insertWithSteps(int value) {
        return runWithSteps(() -> insert(value));
    }

    // deletes the value and returns the picture of the tree after every step of the algorithm
    public List<Step> deleteWithSteps(int value) {
        return runWithSteps(() -> delete(value));
    }

    private List<Step> runWithSteps(Runnable action) {
        steps = new ArrayList<>();
        try {
            action.run();
            return steps;
        } finally {
            steps = null;
        }
    }

    private void record(String text, Node marked) {
        if (steps != null) {
            steps.add(new Step(text, toView(root), marked == null || marked == nil ? null : marked.value));
        }
    }

    public boolean insert(int value) {
        Node parent = nil;
        Node current = root;
        record("Start: insert " + value + (root == nil ? " into an empty tree" : ", go down from the root"), root);
        while (current != nil) {
            if (value == current.value) {
                record(value + " is already in the tree, nothing to add", current);
                return false;
            }
            record("Compare " + value + " with " + current.value + ": go " + (value < current.value ? "left" : "right"), current);
            parent = current;
            current = value < current.value ? current.left : current.right;
        }

        Node node = new Node(value, RED);
        node.parent = parent;
        if (parent == nil) {
            root = node;
        } else if (value < parent.value) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        size++;
        record("An empty place is found: " + value + " is added as a new RED node", node);
        fixAfterInsert(node);
        record("Done: all red-black rules are fulfilled", node);
        return true;
    }

    // a new node is red; the only possible problem is a red node with a red parent
    private void fixAfterInsert(Node node) {
        while (node.parent.color == RED) {
            Node grandparent = node.parent.parent;
            if (node.parent == grandparent.left) {
                Node uncle = grandparent.right;
                if (uncle.color == RED) {
                    // case 1: red uncle - recolor and move the problem two levels up
                    record("Two red nodes in a row: parent " + node.parent.value + " and uncle " + uncle.value
                            + " are red", node);
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    record("Case 1: recolor parent and uncle black, grandparent " + grandparent.value
                            + " red; the problem moves up", grandparent);
                    node = grandparent;
                } else {
                    record("Two red nodes in a row: parent " + node.parent.value + " is red, uncle is black", node);
                    // case 2: node is an inner child - turn it into case 3
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                        record("Case 2: the node is an inner child, rotate left around " + node.value, node);
                    }
                    // case 3: node is an outer child - one rotation fixes everything
                    int top = node.parent.parent.value;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                    record("Case 3: recolor and rotate right around " + top, node.parent);
                }
            } else {
                Node uncle = grandparent.left;
                if (uncle.color == RED) {
                    record("Two red nodes in a row: parent " + node.parent.value + " and uncle " + uncle.value
                            + " are red", node);
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    record("Case 1: recolor parent and uncle black, grandparent " + grandparent.value
                            + " red; the problem moves up", grandparent);
                    node = grandparent;
                } else {
                    record("Two red nodes in a row: parent " + node.parent.value + " is red, uncle is black", node);
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                        record("Case 2: the node is an inner child, rotate right around " + node.value, node);
                    }
                    int top = node.parent.parent.value;
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                    record("Case 3: recolor and rotate left around " + top, node.parent);
                }
            }
        }
        if (root.color == RED) {
            root.color = BLACK;
            record("The root must be black: recolor it", root);
        }
    }

    private void recordSearch(int value) {
        if (steps == null) {
            return;
        }
        record("Start: delete " + value + ", search for it from the root", root);
        Node current = root;
        while (current != nil && current.value != value) {
            record("Compare " + value + " with " + current.value + ": go " + (value < current.value ? "left" : "right"), current);
            current = value < current.value ? current.left : current.right;
        }
    }

    public boolean delete(int value) {
        recordSearch(value);
        Node node = find(value);
        if (node == nil) {
            record(value + " is not in the tree, nothing to delete", null);
            return false;
        }
        record("Found " + value + ", now remove it", node);

        Node removed = node;
        boolean removedColor = removed.color;
        Node child;
        if (node.left == nil) {
            child = node.right;
            replace(node, node.right);
            record(node.right == nil ? value + " is a leaf: it is simply removed"
                    : value + " has only a right child: the child takes its place", child != nil ? child : child.parent);
        } else if (node.right == nil) {
            child = node.left;
            replace(node, node.left);
            record(value + " has only a left child: the child takes its place", child);
        } else {
            removed = minimum(node.right);
            record(value + " has two children: the smallest node of its right subtree is " + removed.value, removed);
            removedColor = removed.color;
            child = removed.right;
            if (removed.parent == node) {
                child.parent = removed;
            } else {
                replace(removed, removed.right);
                removed.right = node.right;
                removed.right.parent = removed;
            }
            replace(node, removed);
            removed.left = node.left;
            removed.left.parent = removed;
            removed.color = node.color;
            record(removed.value + " takes the place and the color of " + value, removed);
        }
        size--;

        if (removedColor == BLACK) {
            record("The removed node was black: one path has one black node less, fix it",
                    child != nil ? child : child.parent);
            fixAfterDelete(child);
        } else {
            record("The removed node was red: no rule is broken", child != nil ? child : child.parent);
        }
        record("Done: all red-black rules are fulfilled", null);
        return true;
    }

    private void fixAfterDelete(Node child) {
        while (child != root && child.color == BLACK) {
            int parentValue = child.parent.value;
            if (child == child.parent.left) {
                Node sibling = child.parent.right;
                if (sibling.color == RED) {
                    int siblingValue = sibling.value;
                    sibling.color = BLACK;
                    child.parent.color = RED;
                    rotateLeft(child.parent);
                    sibling = child.parent.right;
                    record("Case 1: the sibling " + siblingValue + " is red: recolor and rotate left around "
                            + parentValue, child.parent.parent);
                }
                if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                    sibling.color = RED;
                    record("Case 2: the sibling " + sibling.value + " is black with black children: recolor it red, "
                            + "the problem moves up", sibling);
                    child = child.parent;
                } else {
                    if (sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = child.parent.right;
                        record("Case 3: only the inner nephew is red: recolor and rotate right around "
                                + sibling.right.value + ", it becomes case 4", sibling);
                    }
                    sibling.color = child.parent.color;
                    child.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(child.parent);
                    record("Case 4: the outer nephew is red: recolor and rotate left around " + parentValue
                            + ", the tree is fixed", sibling);
                    child = root;
                }
            } else {
                Node sibling = child.parent.left;
                if (sibling.color == RED) {
                    int siblingValue = sibling.value;
                    sibling.color = BLACK;
                    child.parent.color = RED;
                    rotateRight(child.parent);
                    sibling = child.parent.left;
                    record("Case 1: the sibling " + siblingValue + " is red: recolor and rotate right around "
                            + parentValue, child.parent.parent);
                }
                if (sibling.right.color == BLACK && sibling.left.color == BLACK) {
                    sibling.color = RED;
                    record("Case 2: the sibling " + sibling.value + " is black with black children: recolor it red, "
                            + "the problem moves up", sibling);
                    child = child.parent;
                } else {
                    if (sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = child.parent.left;
                        record("Case 3: only the inner nephew is red: recolor and rotate left around "
                                + sibling.left.value + ", it becomes case 4", sibling);
                    }
                    sibling.color = child.parent.color;
                    child.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(child.parent);
                    record("Case 4: the outer nephew is red: recolor and rotate right around " + parentValue
                            + ", the tree is fixed", sibling);
                    child = root;
                }
            }
        }
        boolean wasRed = child.color == RED;
        child.color = BLACK;
        if (wasRed) {
            record("The node with the extra black is red: recolor it black, the rules are restored", child);
        }
    }

    private Node minimum(Node node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private void replace(Node oldNode, Node newNode) {
        if (oldNode.parent == nil) {
            root = newNode;
        } else if (oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }
        newNode.parent = oldNode.parent;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        replace(x, y);
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) {
            y.right.parent = x;
        }
        replace(x, y);
        y.right = x;
        x.parent = y;
    }

    public record NodeView(int value, boolean red, NodeView left, NodeView right) {
    }

    public NodeView getRoot() {
        return toView(root);
    }

    private NodeView toView(Node node) {
        if (node == nil) {
            return null;
        }
        return new NodeView(node.value, node.color == RED, toView(node.left), toView(node.right));
    }

    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<Integer> result) {
        if (node == nil) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.value);
        inOrder(node.right, result);
    }

    public List<Integer> preOrder() {
        List<Integer> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    private void preOrder(Node node, List<Integer> result) {
        if (node == nil) {
            return;
        }
        result.add(node.value);
        preOrder(node.left, result);
        preOrder(node.right, result);
    }

    public List<Integer> postOrder() {
        List<Integer> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    private void postOrder(Node node, List<Integer> result) {
        if (node == nil) {
            return;
        }
        postOrder(node.left, result);
        postOrder(node.right, result);
        result.add(node.value);
    }

    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == nil) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public String draw() {
        if (root == nil) {
            return "(the tree is empty)";
        }
        StringBuilder text = new StringBuilder();
        draw(root, 0, text);
        return text.toString();
    }

    private void draw(Node node, int depth, StringBuilder text) {
        if (node == nil) {
            return;
        }
        draw(node.right, depth + 1, text);
        text.append("    ".repeat(depth))
                .append(node.value)
                .append(node.color == RED ? " (R)" : " (B)")
                .append('\n');
        draw(node.left, depth + 1, text);
    }

    public boolean isValid() {
        return root.color == BLACK && blackHeight(root) != -1;
    }

    private int blackHeight(Node node) {
        if (node == nil) {
            return 1;
        }
        if (node.color == RED && (node.left.color == RED || node.right.color == RED)) {
            return -1;
        }
        int left = blackHeight(node.left);
        int right = blackHeight(node.right);
        if (left == -1 || right == -1 || left != right) {
            return -1;
        }
        return left + (node.color == BLACK ? 1 : 0);
    }
}
