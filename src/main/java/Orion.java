import java.util.ArrayList;
import java.util.Scanner;

public class Orion {
    private static final String LINE = "    ____________________________________________________________";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<String> tasks = new ArrayList<>();

        // Greet
        System.out.println(LINE);
        System.out.println("    Hello! I'm Orion");
        System.out.println("    What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("bye")) {
                break;
            }

            if (userInput.equals("list")) {
                // List all stored tasks
                System.out.println(LINE);
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("    " + (i + 1) + ". " + tasks.get(i));
                }
                System.out.println(LINE);
                continue;
            }

            tasks.add(userInput);

            System.out.println(LINE);
            System.out.println("    added: " + userInput);
            System.out.println(LINE);
        }

        // Exit
        System.out.println(LINE);
        System.out.println("    Bye. Hope to see you again soon!");
        System.out.println(LINE);
        scanner.close();
    }
}
