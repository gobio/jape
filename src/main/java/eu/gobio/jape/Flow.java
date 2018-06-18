package eu.gobio.jape;

public class Flow {
    Stage from;
    String value;

    public Flow(Stage from, String value) {
        this.from = from;
        this.value = value;
    }

    public Stage getFrom() {
        return from;
    }

    public void setFrom(Stage from) {
        this.from = from;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
