package com.eduplatform.console;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

/** Reads and validates console input; re-prompts until the input is valid. */
public class ConsoleInput {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInput(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    public String readLine(String prompt) {
        out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            out.println("  Value must not be empty.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message below
            }
            out.printf("  Please enter a whole number between %d and %d.%n", min, max);
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (y/n): ").toLowerCase(Locale.ROOT);
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            out.println("  Please answer 'y' or 'n'.");
        }
    }

    /**
     * Shows a numbered list and lets the user pick one item.
     * Returns empty (after printing {@code emptyMessage}) when there is nothing to choose from.
     */
    public <T> Optional<T> choose(String title, List<T> items, Function<T, String> label, String emptyMessage) {
        if (items.isEmpty()) {
            out.println(emptyMessage);
            return Optional.empty();
        }
        out.println(title);
        for (int i = 0; i < items.size(); i++) {
            out.printf("  %d. %s%n", i + 1, label.apply(items.get(i)));
        }
        int choice = readInt("Choose (1-" + items.size() + "): ", 1, items.size());
        return Optional.of(items.get(choice - 1));
    }
}
