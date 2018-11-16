package os.dtakac.feritraspored.model.programmes;

public enum Year {
    FIRST("1", "Prva"), SECOND("2", "Druga"), THIRD("3", "Treća");

    private final String id;
    private final String name;

    Year(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
