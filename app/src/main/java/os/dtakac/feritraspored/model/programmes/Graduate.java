package os.dtakac.feritraspored.model.programmes;

public enum Graduate implements Programme {
    DKA("34"), DKB("35"),
    DEA("31"), DEB("32"), DEC("33"),
    DRA("36"), DRB("37"), DRC("38"), DRD("39"),
    DA("52");

    private final String id;

    Graduate(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
