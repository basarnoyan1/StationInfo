package com.noyansoft.stationinfo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.service.quicksettings.TileService;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.O)
public class QuickLocation extends TileService{

    @Override
    public void onClick() {

        boolean isCurrentlyLocked = this.isLocked();
        if (!isCurrentlyLocked) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            QuickLocation.this.sendBroadcast(it);
            Resources resources = getApplication().getResources();
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(intent);
        }
    }

}