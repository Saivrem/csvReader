package model;


import annotation.CsvMapping;
import annotation.CsvRow;

import java.util.Objects;

@CsvRow
public class EnumDTO {
    @CsvMapping(name = "id")
    private int id;
    @CsvMapping(name = "name")
    private String name;
    @CsvMapping(name = "sex")
    private Sex sex;
    @CsvMapping(name = "age")
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

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
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
        if (!(o instanceof EnumDTO that)) return false;
        return getId() == that.getId() && getAge() == that.getAge() && Objects.equals(getName(), that.getName()) && Objects.equals(getSex(), that.getSex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSex(), getAge());
    }
}
