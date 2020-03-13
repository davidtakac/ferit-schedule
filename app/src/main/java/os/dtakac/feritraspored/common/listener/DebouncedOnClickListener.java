package os.dtakac.feritraspored.common.listener;

import android.os.SystemClock;
import android.view.View;

public abstract class DebouncedOnClickListener implements View.OnClickListener {

    private long lastClickTime;
    private final long threshold;

    public DebouncedOnClickListener(long threshold){
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
