package os.dtakac.feritraspored.model.resources;

import android.content.res.Resources;

import os.dtakac.feritraspored.R;

public class AndroidResourceManager implements ResourceManager {

    private Resources r;

    public AndroidResourceManager(Resources r) {
        this.r = r;
    }

    public String get(int resId){
        return r.getString(resId);
    }

    public String[] getStringArray(int resId){
        return r.getStringArray(resId);
    }

    @Override
    public String getUndergradProgrammeId(int index) {
        String[] undergrad = getStrArray(R.array.values_undergrad);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.values_undergrad)[index];
        }
        return null;
    }

    @Override
    public String getUndergradYearId(int index) {
        String[] undergrad = getStrArray(R.array.values_years_undergrad);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.values_years_undergrad)[index];
        }
        return null;
    }

    @Override
    public String getHighlightScriptPath() {
        return get(R.string.highlight_script_path);
    }

    private String[] getStrArray(int id){
        return r.getStringArray(id);
    }
}
