

package com.robin.lazy.sample;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * <一句话功能简述>
 */
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }
}
