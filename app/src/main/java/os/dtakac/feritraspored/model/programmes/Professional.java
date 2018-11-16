package os.dtakac.feritraspored.model.programmes;

public enum Professional implements Programme {
    RAC("53", "Elektrotehnika - Računarstvo"), INF("7", "Elektrotehnika - Informatika"),
    ELA("8", "Automatika"), ELE("9", "Elektroenergetika");

    private final String id;
    private final String name;

    Professional(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
