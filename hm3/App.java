package hm3;



import hm3.abstractions.EmployeeRepository;
import hm3.entities.Employee;
import hm3.utils.ConsoleUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
public class App {
    public static final Locale LOCALE = Locale.forLanguageTag("ru");
    private static final Employee[] initialEmployees = {
            new Employee(null, "Сафия Фадеева", "+02(672)714-67-69", 3),
            new Employee(5, "Тимофей Горбачев", "+7(956)221-74-25", 2),
            new Employee(4, "Аглая Чернова", "+39(652)761-90-87", 8),
            new Employee(1, "Анна Трошина", "804(99)411-59-68", 9),
            new Employee(null, "Георгий Васильев", null, null),
            null,
            new Employee(3, "Василиса Миронова", "31(743)389-54-81", 7),
            new Employee(null, "Таисия Карпова", "+2(56)451-72-73", 15),
            new Employee(15, "Леон Симонов", "463(8853)370-22-50", 1),
            null,
            new Employee(null, "Василиса Денисова", null, 0),
            new Employee(null, "Мирон Лебедев", "+18(946)742-96-70 ", 2)
    };
    public static void main(String[] args) throws Exception {
        runLifecycle();
    }
    private static void runLifecycle() {
        EmployeeRepository employeeRepo = new EmployeeRepositoryImpl(initialEmployees);
        ConsoleUtils.printlnEmphasized("\nСОТРУДНИКИ\n");
        ConsoleUtils.printlnEmphasized("Исходная картотека:\n");
        employeeRepo.forEach(ConsoleUtils::println);
        final var menuAllowedOptions = Set.of(Set.of("1"), Set.of("2"), Set.of("3"), Set.of("4"), Set.of("0"));
        boolean suppressMenuPrint = false;
        do {
            if (!suppressMenuPrint) {
                printMenu();
                suppressMenuPrint = false;
            }
            var choice = ConsoleUtils.askUserChoice("Введите пункт меню: ", menuAllowedOptions);
            if (choice.contains("0")) {
                break;
            } else if (choice.contains("1")) {
                // 1. Найти сотрудников по стажу
                var answer = ConsoleUtils.askInteger(
                        "Введите стаж в полных годах, либо"
                                + "\n\t-1 для поиска сотрудников с не указанным стажем,"
                                + "\n\tили пустой ввод чтобы отменить поиск: ",
                        -1, Integer.MAX_VALUE);
                if (answer.isEmpty()) {
                    suppressMenuPrint = true;
                    continue;
                }
                Integer experience = answer.getAsInt() == -1 ? null : answer.getAsInt();
                var result = employeeRepo.getByExperience(experience);
                ConsoleUtils.println();
                if (result.isEmpty()) {
                    ConsoleUtils.println("Не найдено сотрудников с заданным стажем.");
                } else {
                    ConsoleUtils.printlnEmphasized("Найденные сотрудники:");
                    result.forEach(ConsoleUtils::println);
                }
            } else if (choice.contains("2")) {
                // 2. Найти телефон(ы) по имени
                var answer = ConsoleUtils.askString(
                        "Введите имя сотрудника, частично или полностью"
                                + "\n\tили пустой ввод чтобы отменить поиск: ",
                        null, null);
                if (answer.isEmpty()) {
                    suppressMenuPrint = true;
                    continue;
                }
                var result = employeeRepo.getPhonesByName(answer.get());
                ConsoleUtils.println();
                if (result.isEmpty()) {
                    ConsoleUtils.println("Не найдено сотрудников с таким именем.");
                } else {
                    ConsoleUtils.printlnEmphasized("Найденные сотрудники и их телефонные номера:");
                    result.forEach(e -> {
                        ConsoleUtils.println(
                                e.fullName() + " -- Тел.: " + Objects.requireNonNullElse(e.phone(), "не указан!"));
                    });
                }
            } else if (choice.contains("3")) {
                // 3. Найти сотрудника по табельному номеру
                var answer = ConsoleUtils.askInteger("Введите табельный номер сотрудника"
                        + "\n\tили пустой ввод чтобы отменить поиск: ", 1, Integer.MAX_VALUE);
                if (answer.isEmpty()) {
                    suppressMenuPrint = true;
                    continue;
                }
                int id = answer.getAsInt();
                var result = employeeRepo.getById(id);
                ConsoleUtils.println();
                if (result == null) {
                    ConsoleUtils.println("Не найдено сотрудника с заданным табельным номером.");
                } else {
                    ConsoleUtils.printlnEmphasized("Найденный сотрудник:");
                    ConsoleUtils.println(result);
                }
            } else if (choice.contains("4")) {
                // 4. Добавить сотрудника
                ConsoleUtils.printlnEmphasized("\nНовый сотрудник\n");
                var fullNameOpt = ConsoleUtils.askString("Введите полное имя (пустой ввод для отмены): ",
                        s -> !s.isBlank(), "Вы не ввели имя. Попробуйте ещё раз.");
                if (fullNameOpt.isEmpty()) {
                    ConsoleUtils.println("Отменено");
                    suppressMenuPrint = true;
                    continue;
                }
                var phoneOpt = ConsoleUtils.askString("Введите номер телефона (пустой ввод для отмены): ", null, null);
                if (phoneOpt.isEmpty()) {
                    ConsoleUtils.println("Отменено");
                    suppressMenuPrint = true;
                    continue;
                }
                String phone = phoneOpt.get().isBlank() ? null : phoneOpt.get();
                var experienceOpt = ConsoleUtils.askInteger(
                        "Введите стаж в полных годах,"
                                + "\n\tлибо -1, если стаж неизвестен"
                                + "\n\t(пустой ввод для отмены): ",
                        -1, Integer.MAX_VALUE);
                if (experienceOpt.isEmpty()) {
                    ConsoleUtils.println("Отменено");
                    suppressMenuPrint = true;
                    continue;
                }
                Integer experience = experienceOpt.getAsInt() == -1 ? null : experienceOpt.getAsInt();
                employeeRepo.add(fullNameOpt.get(), phone, experience);
                ConsoleUtils.println("Новый сотрудник успешно добавлен в базу!");
                ConsoleUtils.printlnEmphasized("\nКартотека:");
                employeeRepo.forEach(ConsoleUtils::println);
            }
        } while (ConsoleUtils.askYesNo("-\nПродолжить (Y/n)? ", true));
        ConsoleUtils.println("-\nПриложение завершено.");
    }
    private static void printMenu() {
        ConsoleUtils.printlnEmphasized("\nГлавное меню:");
        ConsoleUtils.println("1. Найти сотрудников по стажу");
        ConsoleUtils.println("2. Найти телефон(ы) по имени");
        ConsoleUtils.println("3. Найти сотрудника по табельному номеру");
        ConsoleUtils.println("4. Добавить сотрудника");
        ConsoleUtils.println("0. Завершить");
    }
}