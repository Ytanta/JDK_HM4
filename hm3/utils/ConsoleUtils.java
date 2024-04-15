package hm3.utils;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
public class ConsoleUtils {
    public static final String PLEASE_REPEAT = "Пожалуйста попробуйте снова.";
    private static final String PROMPT_ENTER = "Нажмите Ввод чтобы продолжить...";
    private static final String WARN_WRONG_MENU_ITEM = "Некорректный ввод: требуется выбрать пункт меню. "
            + PLEASE_REPEAT;
    private static final String ERR_NOT_INT = "Некорректный ввод: Требуется целое число. " + PLEASE_REPEAT;
    private static final String ERR_INT_MUST_BE_IN_RANGE = "Число должно быть в интервале от %d до %d! "
            + PLEASE_REPEAT;
    private static final String ERR_INT_TOO_LOW = "Число не должно быть меньше %d! " + PLEASE_REPEAT;
    private static final String ERR_INT_TOO_HIGH = "Число не должно быть больше %d! " + PLEASE_REPEAT;
    private static final String ERR_WRONG_DATE_FORMAT = "Некорректный ввод: не соответствует формату YYYY-MM-DD! "
            + PLEASE_REPEAT;
    public static final Scanner scanner = new Scanner(System.in, Charset.forName("UTF-8"));
    public static void clear() {
        System.out.printf("\u001b[1;1H\u001b[2J");
    }
    public static void waitToProceed() {
        System.out.println(PROMPT_ENTER);
        scanner.nextLine();
    }
    public static void println(Object view) {
        System.out.println(view.toString());
    }
    public static void println() {
        System.out.println();
    }
    public static void printlnEmphasized(String text) {
        System.out.println("\u001b[1;4;97m" + text + "\u001b[0m");
    }
    public static boolean askYesNo(String prompt, boolean isYesDefault) {
        System.out.print(prompt);
        var answer = scanner.nextLine();
        if (answer.isBlank()) {
            return isYesDefault;
        }
        if (answer.startsWith("y") || answer.startsWith("д") || answer.startsWith("l")) {
            return true;
        } else if (answer.startsWith("n") || answer.startsWith("н") || answer.startsWith("т")) {
            return false;
        } else {
            return isYesDefault;
        }
    }
    public static Set<String> askUserChoice(String prompt, Set<Set<String>> options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        if (options.isEmpty()) {
            throw new IllegalArgumentException("options");
        }
        boolean outOfRange = false;
        while (true) {
            if (outOfRange) {
                outOfRange = false;
                printError(WARN_WRONG_MENU_ITEM);
            }
            System.out.print(prompt);
            String rawInp = scanner.nextLine();
            final String inp = rawInp.strip();
            var resOpt = options.stream().filter(s -> s.contains(inp)).findAny();
            if (resOpt.isPresent()) {
                return resOpt.get();
            }
            outOfRange = true;
        }
    }
    public static OptionalInt askInteger(String prompt, Integer min, Integer max) {
        Integer answer = getUserInputIntRange(scanner, prompt, min, max);
        if (answer != null) {
            return OptionalInt.of(answer);
        } else {
            return OptionalInt.empty();
        }
    }
    public static Integer getUserInputIntRange(
            Scanner inputScanner, String prompt,
            Integer min, Integer max) {
        boolean isMinSet = min != null && !min.equals(Integer.MIN_VALUE);
        boolean isMaxSet = max != null && !max.equals(Integer.MAX_VALUE);
        boolean wrongType = false;
        boolean outOfRange = false;
        while (true) {
            if (wrongType) {
                wrongType = false;
                printError(ERR_NOT_INT);
            }
            if (outOfRange) {
                outOfRange = false;
                String errOutOfRange;
                if (isMinSet && isMaxSet) {
                    errOutOfRange = String.format(ERR_INT_MUST_BE_IN_RANGE, min, max);
                } else if (isMinSet) {
                    errOutOfRange = String.format(ERR_INT_TOO_LOW, min);
                } else {
                    errOutOfRange = String.format(ERR_INT_TOO_HIGH, max);
                }
                printError(errOutOfRange);
            }
            System.out.print(prompt);
            var rawInp = inputScanner.nextLine();
            if (rawInp.isBlank()) {
                return null;
            }
            var value = tryParseInt(rawInp);
            if (value == null) {
                wrongType = true;
            } else {
                if (!(outOfRange = isOutOfRange(value, min, max))) {
                    return value;
                }
            }
        }
    }
    private static boolean isOutOfRange(Integer value, Integer min, Integer max) {
        return (min != null && value < min) || (max != null && value > max);
    }
    public OptionalInt askInteger(String prompt, Function<Integer, Boolean> checkValidity, String wrongWarn) {
        Integer answer = getUserInputInt(scanner, prompt, checkValidity, wrongWarn);
        if (answer != null) {
            return OptionalInt.of(answer);
        } else {
            return OptionalInt.empty();
        }
    }
    public static Integer getUserInputInt(
            Scanner inputScanner, String prompt,
            Function<Integer, Boolean> checkIfValid,
            String warnOutOfRange) {
        boolean wrongType = false;
        boolean outOfRange = false;
        while (true) {
            if (wrongType) {
                wrongType = false;
                printError(ERR_NOT_INT);
            }
            if (outOfRange) {
                outOfRange = false;
                if (warnOutOfRange != null)
                    printError(warnOutOfRange);
            }
            System.out.print(prompt);
            var rawInp = inputScanner.nextLine();
            if (rawInp.isBlank()) {
                return null;
            }
            var value = tryParseInt(rawInp);
            if (value != null) {
                if (checkIfValid == null || checkIfValid.apply(value)) {
                    return value;
                }
                outOfRange = true;
            } else {
                wrongType = true;
            }
        }
    }
    public static Optional<String> askString(String prompt, Function<String, Boolean> checkValidity, String wrongWarn) {
        String answer = getUserInput(scanner, prompt, checkValidity, wrongWarn);
        if (answer != null) {
            return Optional.<String>of(answer);
        } else {
            return Optional.<String>empty();
        }
    }
    public static Optional<LocalDate> askDate(String prompt) {
        while (true) {
            var strDate = askString(prompt,
                    s -> s.matches("^\\d{4}-\\d{2}-\\d{2}$"),
                    ERR_WRONG_DATE_FORMAT);
            if (strDate.isEmpty()) {
                return Optional.empty();
            }
            try {
                LocalDate date = LocalDate.parse(strDate.get());
                return Optional.of(date);
            } catch (DateTimeParseException e) {
                printError(ERR_WRONG_DATE_FORMAT);
            }
        }
    }
    public static String getUserInput(Scanner inputScanner, String prompt,
                                      Function<String, Boolean> checkIfValid, String wrongWarn) {
        boolean wrong = false;
        while (true) {
            if (wrong) {
                wrong = false;
                if (wrongWarn != null) {
                    printError(wrongWarn);
                }
            }
            System.out.print(prompt);
            String inp = inputScanner.nextLine();
            if (inp.isEmpty()) {
                return null;
            }
            if (checkIfValid == null || checkIfValid.apply(inp)) {
                return inp;
            }
            wrong = true;
        }
    }
    // aux
    private static void printError(String errorMessage) {
        System.err.println(errorMessage);
    }
    private static Integer tryParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}