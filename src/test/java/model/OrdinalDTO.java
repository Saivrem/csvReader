package model;

import annotation.CsvMapping;
import annotation.CsvRow;

import java.util.Objects;

@CsvRow
public class OrdinalDTO {
    @CsvMapping(ordinal = 0)
    private int id;
    @CsvMapping(ordinal = 1)
    private String name;
    @CsvMapping(ordinal = 2)
    private String sex;
    @CsvMapping(ordinal = 3)
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrdinalDTO that)) return false;
        return getId() == that.getId() && getAge() == that.getAge() && Objects.equals(getName(), that.getName()) && Objects.equals(getSex(), that.getSex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSex(), getAge());
    }
}
