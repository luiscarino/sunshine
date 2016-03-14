package com.example.lucarino.sunshine;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by luiscarino on 3/13/16.
 */
public class ForecastService extends IntentService {

    public static final String SUNSHINE_SERVICE = "Sunshine service";

    public ForecastService() {
        super("SUNSHINE_SERVICE");
    }
    public ForecastService(String name) {
        super(SUNSHINE_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
