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

            // Else, command is a "add-new-task" command
            Task newTask;

            if (userInput.startsWith("todo ")) {
                // todo <desc>
                String description = userInput.substring(5).trim();
                newTask = new Todo(description);

            } else if (userInput.startsWith("deadline ")) {
                // deadline <desc> /by <by>
                String rest = userInput.substring(9).trim();
                String[] parts = rest.split(" /by ", 2);

                if (parts.length < 2) {
                    System.out.println(LINE);
                    System.out.println("    Invalid deadline command.");
                    System.out.println(LINE);
                    continue;
                }

                String description = parts[0].trim();
                String by = parts[1].trim();
                newTask = new Deadline(description, by);

            } else if (userInput.startsWith("event ")) {
                // event <desc> /from <from> /to <to>
                String rest = userInput.substring(6).trim();
                String[] fromSplit = rest.split(" /from ", 2);

                if (fromSplit.length < 2) {
                    System.out.println(LINE);
                    System.out.println("    Invalid event command.");
                    System.out.println(LINE);
                    continue;
                }

                String description = fromSplit[0].trim();
                String[] toSplit = fromSplit[1].split(" /to ", 2);

                if (toSplit.length < 2) {
                    System.out.println(LINE);
                    System.out.println("    Invalid event command.");
                    System.out.println(LINE);
                    continue;
                }

                String from = toSplit[0].trim();
                String to = toSplit[1].trim();
                newTask = new Event(description, from, to);

            } else {
                // Just treat as a Todo
                newTask = new Todo(userInput.trim());
            }

            tasks.add(newTask);

            System.out.println(LINE);
            System.out.println("    Got it. I've added this task:");
            System.out.println("      " + newTask);
            System.out.println("    Now you have " + tasks.size() + " tasks in the list.");
            System.out.println(LINE);
        }

        // Exit
        System.out.println(LINE);
        System.out.println("    Bye. Hope to see you again soon!");
        System.out.println(LINE);
        scanner.close();
    }
}
