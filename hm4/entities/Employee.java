package hm4.entities;

public class Employee {
    private Integer employeeId;
    private String fullName;
    private String phone;
    private Integer experience;
    public Employee() {
    }
    public Employee(Employee copy) {
        this(copy.employeeId, copy.fullName, copy.phone, copy.experience);
    }
    public Employee(Integer employeeId, String fullName, String phone, Integer experience) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phone = phone;
        this.experience = experience;
    }
    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Integer getExperience() {
        return experience;
    }
    public void setExperience(Integer experience) {
        this.experience = experience;
    }
    @Override
    public String toString() {
        final String na = "не указан";
        return String.format("%s: %s, Тел.: %s, Стаж: %s",
                employeeId,
                fullName,
                phone == null || phone.isBlank() ? na : phone,
                experience == null ? na : Integer.toString(experience));
    }
}
