package nw.one.vasya.lesson5;

public class SubNamed {

    String name;

    public SubNamed(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                '}';
    }
}
