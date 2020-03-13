package os.dtakac.feritraspored.model.resources;

import android.content.res.Resources;

import os.dtakac.feritraspored.R;

public class ResourceManager {

    private Resources r;

    public ResourceManager(Resources r) {
        this.r = r;
    }

    public String get(int resId){
        return r.getString(resId);
    }

    public String[] getArray(int resId){
        return r.getStringArray(resId);
    }

    public String getUndergradProgrammeId(int index) {
        String[] undergrad = getArray(R.array.values_undergrad);
        if(index <= undergrad.length - 1){
            return getArray(R.array.values_undergrad)[index];
        }
        return null;
    }

    public String getUndergradYearId(int index) {
        String[] undergrad = getArray(R.array.values_years_undergrad);
        if(index <= undergrad.length - 1){
            return getArray(R.array.values_years_undergrad)[index];
        }
        return null;
    }
}
