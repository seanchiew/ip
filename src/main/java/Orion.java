import java.util.ArrayList;
import java.util.Scanner;

public class Orion {
    private static final String LINE = "    ____________________________________________________________";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Task> tasks = new ArrayList<>();

        // Greet
        System.out.println(LINE);
        System.out.println("    Hello! I'm Orion");
        System.out.println("    What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String userInput = scanner.nextLine();

            // Exit condition
            if (userInput.equals("bye")) {
                break;
            }

            // List command
            if (userInput.equals("list")) {
                System.out.println(LINE);
                System.out.println("    Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("    " + (i + 1) + ". " + tasks.get(i));
                }
                System.out.println(LINE);
                continue;
            }

            // Mark command
            if (userInput.startsWith("mark ")) {
                System.out.println(LINE);
                try {
                    int taskNumber = Integer.parseInt(userInput.substring(5).trim());
                    int index = taskNumber - 1; // convert to 0-based index

                    tasks.get(index).markDone();
                    System.out.println("    Nice! I've marked this task as done:");
                    System.out.println("      " + tasks.get(index));
                } catch (Exception e) {
                    // Catch bad input or index
                    System.out.println("    Invalid mark command.");
                }
                System.out.println(LINE);
                continue;
            }

            // Unmark command
            if (userInput.startsWith("unmark ")) {
                System.out.println(LINE);
                try {
                    int taskNumber = Integer.parseInt(userInput.substring(7).trim());
                    int index = taskNumber - 1; // convert to 0-based index

                    tasks.get(index).markUndone();
                    System.out.println("    OK, I've marked this task as not done yet:");
                    System.out.println("      " + tasks.get(index));
                } catch (Exception e) {
                    // Catch bad input or index
                    System.out.println("    Invalid unmark command.");
                }
                System.out.println(LINE);
                continue;
            }

            // Add new task
            Task newTask = new Task(userInput);
            tasks.add(newTask);

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
