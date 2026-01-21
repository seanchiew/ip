import java.util.Scanner;

public class Orion {
    private static final String LINE = "    ____________________________________________________________";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Greet
        System.out.println(LINE);
        System.out.println("    Hello! I'm Orion");
        System.out.println("    What can I do for you?");
        System.out.println(LINE);

        // Echo user input
        while (true) {
            String userInput = scanner.nextLine();

            if (userInput.equals("bye")) {
                break;
            }
            System.out.println(LINE);
            System.out.println("    " + userInput);
            System.out.println(LINE);
        }

        // Exit
        System.out.println(LINE);
        System.out.println("    Bye. Hope to see you again soon!");
        System.out.println(LINE);
        scanner.close();
    }
}
