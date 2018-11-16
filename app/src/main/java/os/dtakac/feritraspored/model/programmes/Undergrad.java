package os.dtakac.feritraspored.model.programmes;

public enum Undergrad implements Programme{
    RAC("2", "Računarstvo"), EE("1", "Elektrotehnika/elektroenergetika"), KI("21", "Komunikacije i informatika");

    private final String id;
    private final String name;
    Undergrad(String id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId(){
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
