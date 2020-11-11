package os.dtakac.feritraspored.views.debounce;

import android.os.SystemClock;
import android.view.View;

@Deprecated
public abstract class DebouncedOnClickListenerOld implements View.OnClickListener {

    private long lastClickTime;
    private final long threshold;

    public DebouncedOnClickListenerOld(long threshold){
        this.threshold = threshold;
    }

    @Override
    public void onClick(View view) {
        if(SystemClock.elapsedRealtime() - lastClickTime < threshold){
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        onDebouncedClick();
    }

    public abstract void onDebouncedClick();
}
