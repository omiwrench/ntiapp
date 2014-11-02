package com.nti.flux.ntiapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.nti.flux.ntiapp.helper.DatabaseUpdater;

public class UpdateDatabaseBroadcastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		DatabaseUpdater du = new DatabaseUpdater(context);
		du.updateDatabase();
	}

}
