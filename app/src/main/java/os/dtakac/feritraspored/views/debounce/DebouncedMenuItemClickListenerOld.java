package os.dtakac.feritraspored.views.debounce;

import android.os.SystemClock;
import android.view.MenuItem;

@Deprecated
public abstract class DebouncedMenuItemClickListenerOld implements MenuItem.OnMenuItemClickListener {

    private long lastClickTime;
    private final long threshold;

    public DebouncedMenuItemClickListenerOld(long threshold){
        this.threshold = threshold;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(SystemClock.elapsedRealtime() - lastClickTime < threshold){
            //consume click and prevent others from executing
            return true;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        onDebouncedClick();
        return false;
    }

    public abstract void onDebouncedClick();
}
