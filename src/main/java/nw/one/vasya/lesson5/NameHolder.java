package nw.one.vasya.lesson5;

public class NameHolder {
    Named named;

    public Named getNamed() {
        return named;
    }

    public void setNamed(Named named) {
        this.named = named;
    }

    @Override
    public String toString() {
        return "{" +
                "\"named\":" + named +
                '}';
    }
}
