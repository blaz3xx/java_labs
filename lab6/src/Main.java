import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Random random = new Random();
    static RedBlackTree tree = new RedBlackTree();

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(sc.nextLine().trim());
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }

    static void showTree() {
        System.out.println();
        System.out.println(tree.draw());
        System.out.println("Nodes: " + tree.size() + ", height: " + tree.height());
    }

    static void addNumber() {
        int number = readInt("Number to add: ", -999, 999);
        if (tree.insert(number)) {
            System.out.println(number + " added.");
            showTree();
        } else {
            System.out.println(number + " is already in the tree.");
        }
    }

    static void fillTree(boolean sorted) {
        int count = readInt("How many numbers (1-30): ", 1, 30);

        int[] numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = random.nextInt(99) + 1;
        }
        if (sorted) {
            Arrays.sort(numbers);
        }
        System.out.println((sorted ? "Sorted array: " : "Random array: ") + Arrays.toString(numbers));

        int skipped = 0;
        for (int number : numbers) {
            if (!tree.insert(number)) {
                skipped++;
            }
        }
        if (skipped > 0) {
            System.out.println("Duplicates skipped: " + skipped);
        }
        showTree();
    }

    static void deleteNumber() {
        if (tree.size() == 0) {
            System.out.println("The tree is empty.");
            return;
        }
        int number = readInt("Number to delete: ", -999, 999);
        if (tree.delete(number)) {
            System.out.println(number + " deleted.");
            showTree();
        } else {
            System.out.println(number + " is not in the tree.");
        }
    }

    static void showTraversals() {
        if (tree.size() == 0) {
            System.out.println("The tree is empty.");
            return;
        }
        System.out.println("In-order   (ascending):  " + tree.inOrder());
        System.out.println("Pre-order  (node first): " + tree.preOrder());
        System.out.println("Post-order (node last):  " + tree.postOrder());
    }

    static void searchNumber() {
        int number = readInt("Number to find: ", -999, 999);
        System.out.println(number + (tree.contains(number) ? " is in the tree." : " is not in the tree."));
    }

    static void checkTree() {
        System.out.println(tree.isValid()
                ? "All red-black rules are fulfilled."
                : "A red-black rule is broken!");
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("1 - Add a number");
            System.out.println("2 - Fill the tree with random numbers");
            System.out.println("3 - Fill the tree with sorted numbers");
            System.out.println("4 - Delete a number");
            System.out.println("5 - Show the tree");
            System.out.println("6 - Show traversals");
            System.out.println("7 - Find a number");
            System.out.println("8 - Check red-black rules");
            System.out.println("9 - Clear the tree");
            System.out.println("0 - Exit");
            System.out.print("Your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> addNumber();
                case "2" -> fillTree(false);
                case "3" -> fillTree(true);
                case "4" -> deleteNumber();
                case "5" -> showTree();
                case "6" -> showTraversals();
                case "7" -> searchNumber();
                case "8" -> checkTree();
                case "9" -> {
                    tree = new RedBlackTree();
                    System.out.println("The tree is cleared.");
                }
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown command, enter a number from 0 to 9.");
            }
        }
    }
}
