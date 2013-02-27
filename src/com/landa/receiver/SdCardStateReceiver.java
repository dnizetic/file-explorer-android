package com.landa.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.landa.features.BrowseHandler;

public class SdCardStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Toast.makeText(context, "SD card state changed", Toast.LENGTH_LONG).show();

		//Activity ac = (Activity) context;
		BrowseHandler bh = BrowseHandler.getInstance();
		//bh.displaySdCardUnmountedView();
		
		Bundle extras = intent.getExtras();

		if (extras != null) {

			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			Log.w("DEBUG", state);

			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				String phoneNumber = extras
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

				Log.w("DEBUG", phoneNumber);
			}
		}

	}
}