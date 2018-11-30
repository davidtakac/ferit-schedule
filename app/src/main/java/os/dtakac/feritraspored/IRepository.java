package os.dtakac.feritraspored;

public interface IRepository {

    void add(String key, String value);

    void add(String key, int value);

    void add(String key, boolean value);

    String get(String key, String defaultValue);

    int get(String key, int defaultValue);

    boolean get(String key, boolean defaultValue);
}
