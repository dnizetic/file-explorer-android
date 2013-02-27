package com.landa.preference;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.adapter.HiddenFilesListAdapter;
import com.landa.features.HiddenFileHandler;
import com.landa.fileexplorermanager.MainActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        
        setHomeDirectoryPreference(Environment.getExternalStorageDirectory().toString());
    }
    
    
    public void restoreHiddenFileButton(View v)
    {
    	
    	File f = getFileClicked(v);
    	
    	//delete from DB
    	HiddenFileHandler hfh = new HiddenFileHandler(getApplicationContext());
    	hfh.deleteHiddenFile(f);
    	
    	//reassemble hashmap
    	HiddenFileHandler.assembleHiddenFilesHashmap();

    	//get list view & update adapter
    	ListView lv = (ListView) v.getRootView().findViewById(android.R.id.list);

    	File[] hidden_files = HiddenFileHandler.getAllHiddenFilesAsFiles();
    	HiddenFilesListAdapter adapter = new HiddenFilesListAdapter(HiddenFilesDialogPreference.ctx, hidden_files);
    	lv.setAdapter(adapter);
    	adapter.notifyDataSetChanged();
    	
    	Log.v("Restore", "Opened");
    }
    
    
    public void openHiddenFileButton(View v)
    {
    	File f = getFileClicked(v);
    	
	    Intent intent = new Intent(this,
	            MainActivity.class);
	    
	    intent.setAction(Intent.ACTION_MAIN);
	    intent.putExtra("shortcut_path", f.getAbsolutePath());
	    
	    startActivity(intent);
    }
    
    private File getFileClicked(View v)
    {
    	View vw = (View) v.getParent().getParent();
        
    	TextView tv = (TextView) vw.findViewById(R.id.full_path);
    	String f_path = tv.getText().toString();
    	
    	File f = new File(f_path);
    	
    	if(!f.exists())
    		return null;
    	
    	return f;
    }
    
    
    private void setHomeDirectoryPreference(String home_dir)
    {
        EditTextPreference etp = new EditTextPreference(this);
        
        etp.setKey("home_directory");
        etp.setTitle("Home directory");
        etp.setDialogTitle("Set Home Directory");
        etp.setSummary(home_dir);
        etp.setDefaultValue(home_dir);
        
        ((PreferenceScreen) findPreference("initial_preference")).addPreference(etp);
    }
}