import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AscendingWords {

    static boolean isAscending(String word) {
        for (int i = 0; i < word.length() - 1; i++) {
            if (word.charAt(i) >= word.charAt(i + 1)) {
                return false;
            }
        }
        return true;
    }

    static String[] findWords(String text) {
        String[] words = text.trim().split("\s+");
        ArrayList<String> result = new ArrayList<>();

        for (String word : words) {
            if (!word.isEmpty() && isAscending(word)) {
                result.add(word);
            }
        }

        return result.toArray(new String[0]);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введіть рядок зі словами: ");
        String text = sc.nextLine();

        String[] result = findWords(text);

        System.out.println("Знайдено слів: " + result.length);
        System.out.println(Arrays.toString(result));

        sc.close();
    }
}
