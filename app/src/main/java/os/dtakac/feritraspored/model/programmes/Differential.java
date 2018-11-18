package os.dtakac.feritraspored.model.programmes;

public enum Differential implements Programme {
    RI("42", "Računarstvo blok I"), RA("24", "Računarstvo blok A"),
    EE("41", "Elektroenergetika blok E"), EA("23", "Elektroenergetika blok A"),
    KI("40", "Komunikacije i informatika blok I"), KA("22", "Komunikacije i informatika blok A");

    private final String id;
    private final String name;

    Differential(String id, String name) {
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
