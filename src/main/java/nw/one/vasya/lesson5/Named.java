package nw.one.vasya.lesson5;

import java.util.ArrayList;
import java.util.List;

public class Named {

    String id;
    List<Named> named = new ArrayList<>();
    List<SubNamed> subNamed = new ArrayList<>();

    public Named(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Named> getNamed() {
        return named;
    }

    public void setNamed(List<Named> named) {
        this.named = named;
    }

    public List<SubNamed> getSubNamed() {
        return subNamed;
    }

    public void setSubNamed(List<SubNamed> subNamed) {
        this.subNamed = subNamed;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"named\":" + named +
                ", \"subNamed\":" + subNamed +
                '}';
    }
}
