package os.dtakac.feritraspored.common;

import android.content.res.Resources;

public class ResourceManager {
    private Resources r;
    public ResourceManager(Resources r) {
        this.r = r;
    }
    public String getString(int resId){
        return r.getString(resId);
    }
}
