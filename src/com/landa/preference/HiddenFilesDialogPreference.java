package com.landa.preference;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.example.fileexplorermanager.R;
import com.landa.adapter.HiddenFilesListAdapter;
import com.landa.features.HiddenFileHandler;

/**
 * The OptionDialogPreference will display a dialog, and will persist the
 * <code>true</code> when pressing the positive button and <code>false</code>
 * otherwise. It will persist to the android:key specified in xml-preference.
 */
public class HiddenFilesDialogPreference extends DialogPreference {

	public static Context ctx;
	
    public HiddenFilesDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.ctx = context;
    }

    
    @Override
    protected View onCreateDialogView() {
    	
    	LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
    	
    	View vw = inflater.inflate(R.layout.content_view, null);
    	
    	ListView lv = (ListView) vw.findViewById(android.R.id.list);
    	
    	File[] hidden_files = HiddenFileHandler.getAllHiddenFilesAsFiles();
    	
    	HiddenFilesListAdapter adapter = new HiddenFilesListAdapter(ctx, hidden_files);
    	lv.setAdapter(adapter);
    	
    	return vw;
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        persistBoolean(positiveResult);
    }
    
    @Override
    public void onClick (DialogInterface dialog, int which)
    {
    	super.onClick(dialog, which);
    	
    	Log.v("which", Integer.toString(which));
    	
    	if(which == -1) { //Clear all
    		HiddenFileHandler hfh = new HiddenFileHandler(ctx);
    		
    		hfh.clearHiddenFiles();
    	}
    }

}