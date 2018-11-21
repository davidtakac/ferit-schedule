package os.dtakac.feritraspored;

import android.app.Application;

import os.dtakac.feritraspored.model.programmes.Programmes;

public class App extends Application {
    private static Programmes programmes;

    @Override
    public void onCreate() {
        super.onCreate();
        programmes = new Programmes(getAssets());
    }

    public static Programmes getProgrammes() {
        return programmes;
    }
}
