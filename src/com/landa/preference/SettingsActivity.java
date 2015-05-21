package com.landa.preference;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.landa.fileexplorermanager.R;
import com.landa.adapter.HiddenFilesListAdapter;
import com.landa.features.HiddenFileHandler;
import com.landa.features.OperationsHandler;
import com.landa.fileexplorermanager.MainActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        
        setHomeDirectoryPreference(Environment.getExternalStorageDirectory().toString());
    }
    
    //some comment
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
        final EditTextPreference etp = new EditTextPreference(this);
        
        etp.setKey("home_directory");
        etp.setTitle("Home directory");
        etp.setDialogTitle("Set Home Directory");
        etp.setDefaultValue(home_dir);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        etp.setSummary(prefs.getString("home_directory", home_dir));
        
        
        etp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                    Object newValue) {
                
            	String full_path = (String) newValue;
            	File f = new File(full_path);
            	
            	if(!f.exists()) {
            		
            		showCreateYesOrNoDialog(f, etp);
            		return false;
            		
            	} 
            		
            	//Log.v("preference_full_path", full_path);
            	etp.setSummary(full_path);
                return true;
            }

	    });
        
        ((PreferenceScreen) findPreference("initial_preference")).addPreference(etp);
    }
    
    
	// prompt for creating a non existent file
	private void showCreateYesOrNoDialog(File f, final EditTextPreference etp) {

		final File new_file = f;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("File does not exist. Do you want to create it?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								OperationsHandler oph = OperationsHandler.getInstance();
								if(oph.executeCreateNewWithoutMessages("Folder", new_file)) {
									Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
									oph.setDirectoryAsHome(new_file);
									etp.setSummary(new_file.getAbsolutePath());
									
								} else {
									Toast.makeText(getApplicationContext(), "Directory creation failed.", 
											Toast.LENGTH_LONG).show();
								}
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						
					}
				})
				.setTitle("File does not exist. ")
				.setIcon(R.drawable.warning);

		builder.show();
	}
	

}