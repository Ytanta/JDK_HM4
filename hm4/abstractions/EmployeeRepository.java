package hm4.abstractions;



import hm4.entities.Employee;

import java.util.Collection;
// Добавить метод, который ищет сотрудника по стажу (может быть список)
// Добавить метод, который выводит номер телефона сотрудника по имени (может быть список)
// Добавить метод, который ищет сотрудника по табельному номеру
// Добавить метод добавление нового сотрудника в справочник сотрудников
public interface EmployeeRepository extends Iterable<Employee> {
    Employee getById(int id);
    Collection<Employee> getByExperience(Integer experience);
    /**
     * Ищет номера по заданному имени; допускается частичное совпадение имени.
     *
     * @param name Имя, частично или полностью.
     * @return Коллекцию найденных пар значений имени и соответствующего ему
     *         номера телефона (поскольку, ввиду допущения неполного совпадения
     *         имени, могут существовать как несколько полностью идентичных,
     *         так и различающихся имён, соответствующих заданному шаблону).
     */
    Collection<NamePhoneTuple> getPhonesByName(String namePattern);
    Collection<Employee> getByPhone(String phone);
    Employee add(Employee e);
    Employee add(String fullName, String phone, Integer experience);
    public static record NamePhoneTuple(String fullName, String phone) {
    }
}