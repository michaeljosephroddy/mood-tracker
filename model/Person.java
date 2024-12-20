package model;

abstract sealed class Person permits User {

    private String name;
    private int age;
    private String dob;

    public Person() {

    }

    public Person(String name, int age, String dob) {
        this.name = name;
        this.age = age;
        this.dob = dob;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", dob='" + dob + "'}";
    }

}
