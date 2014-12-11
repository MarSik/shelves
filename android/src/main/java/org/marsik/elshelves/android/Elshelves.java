package org.marsik.elshelves.android;

import android.app.Activity;
import android.os.Bundle;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.main)
public class Elshelves extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
