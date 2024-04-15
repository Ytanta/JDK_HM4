package hm3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;


import hm3.abstractions.EmployeeRepository;
import hm3.entities.Employee;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    public final static int MIN_ID = 1; // минимально допустимый табельный номер
    private final Collection<Employee> employees;
    // табельные номера могут только увеличиваться, допустимы пропуски
    private int nextEmployeeId;
    public EmployeeRepositoryImpl() {
        this.employees = new ArrayList<>();
        this.nextEmployeeId = MIN_ID;
    }
    public EmployeeRepositoryImpl(Employee... employees) {
        this();
        if (employees == null) {
            return;
        }
        // если в инициализирующем массиве сотрудник не имеет табельного номера,
        // то он будет присвоен ему в процессе инициализации коллекции:
        var draftEmployees = Arrays.stream(employees)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Employee::getEmployeeId, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        for (Employee e : draftEmployees) {
            Integer id = e.getEmployeeId();
            if (id != null) {
                if (id > nextEmployeeId) {
                    nextEmployeeId = id + 1;
                }
            } else {
                id = nextEmployeeId++;
                e.setEmployeeId(id);
            }
            this.employees.add(e);
        }
    }
    @Override
    public Employee getById(int id) {
        return employees.stream()
                .filter(e -> e.getEmployeeId() == Integer.valueOf(id)).findAny().orElse(null);
    }
    @Override
    public Collection<Employee> getByExperience(Integer experience) {
        return employees.stream().filter(e -> e.getExperience() == experience).toList();
    }
    @Override
    public Collection<NamePhoneTuple> getPhonesByName(String namePattern) {
        Objects.requireNonNull(namePattern, "namePattern argument must not be null");
        final String namePatternLower = namePattern.toLowerCase(App.LOCALE);
        return employees.stream()
                .filter(e -> e.getFullName().toLowerCase(App.LOCALE).contains(namePatternLower))
                .map(e -> new EmployeeRepository.NamePhoneTuple(e.getFullName(), e.getPhone())).toList();
    }
    @Override
    public Collection<Employee> getByPhone(String phone) {
        return employees.stream()
                .filter(e -> checkPhonesEquality(phone, e.getPhone())).toList();
    }
    @Override
    public Employee add(Employee e) {
        if (e.getEmployeeId() == null || e.getEmployeeId() < nextEmployeeId) {
            e.setEmployeeId(nextEmployeeId);
        }
        ++nextEmployeeId;
        employees.add(new Employee(e)); // сохраняем клон, т.к. сущности в "БД"
        // должны быть недоступны для непосредственной модификации извне
        return e;
    }
    @Override
    public Employee add(String fullName, String phone, Integer experience) {
        return add(new Employee(null, fullName, phone, experience));
    }
    /**
     * Null-friendly equality check of two phones
     *
     * @param phoneA
     * @param phoneB
     * @return true if both phones are canonically equal or both are null,
     *         otherwise false.
     */
    private static boolean checkPhonesEquality(String phoneA, String phoneB) {
        if (phoneA == null) {
            if (phoneB == null)
                return true;
            return false;
        }
        if (phoneB == null) {
            return false;
        }
        // digits & letters are allowed
        String regex = "[\\D&&[^\\p{L}]]";
        String canonicalPhoneA = phoneA.replaceAll(regex, "");
        String canonicalPhoneB = phoneB.replaceAll(regex, "");
        return canonicalPhoneA.equalsIgnoreCase(canonicalPhoneB);
    }
    @Override
    public Iterator<Employee> iterator() {
        var clones = employees.stream().map(Employee::new).toList();
        return clones.iterator();
    }
}