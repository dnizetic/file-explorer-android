package com.landa.fileexplorermanager;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fileexplorermanager.R;
import com.landa.database.DatabaseManager;
import com.landa.datatypes.PasteFile;
import com.landa.dialog.HistoryFavoritesDialogFragment;
import com.landa.dialog.SearchDialogFragment;
import com.landa.features.BrowseHandler;
import com.landa.features.FavoritesHandler;
import com.landa.features.HiddenFileHandler;
import com.landa.features.HistoryHandler;
import com.landa.features.OperationsHandler;
import com.landa.preference.SettingsActivity;

public class MainActivity extends FragmentActivity  {


	private BrowseHandler browseHandler;
	private OperationsHandler oph;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

    	initialiseApplication();
    	
        if(appStartedFromShortcut())
        	browseHandler.openShortcut(
        			new File(getIntent().getStringExtra("shortcut_path")));
        else
        	browseHandler.openFile(new File(BrowseHandler.current_path));
  
    }
    
    
    private void initialiseApplication()
    {
        BrowseHandler.init(this, MainActivity.this);
        browseHandler = BrowseHandler.getInstance();
        
        OperationsHandler.init(this, MainActivity.this);
        oph = OperationsHandler.getInstance();
        
        DatabaseManager.init(this); //must be inited before the rest!
        HiddenFileHandler.assembleHiddenFilesHashmap();
        
        //PreferenceManager.setDefaultValues(context, sharedPreferencesName, sharedPreferencesMode, resId, readAgain)
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, readAgain)
    }

    private boolean appStartedFromShortcut()
    {
        Intent intent = getIntent();
        String path = intent.getStringExtra("shortcut_path");
        
        if(path != null)
        	return true;
        return false;
    }
    //..
    private boolean doubleBackToExitPressedOnce = false;
    @Override
	public void onBackPressed() {
	
    	if(oph.isSelectActive()) {
    		oph.cancelSelect();
    		Toast.makeText(this, "Select canceled", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	FragmentManager fm = getSupportFragmentManager();
    	
    	//1 can be either at the home/root folder, or after a dialog item click (History/Favorites)
    	if (fm.getBackStackEntryCount() == 1) {
        	
    		//if currently NOT at "/" or Home folder, go to parent folder
    		if(!browseHandler.atHomeOrRootFolder()) {
        		
    			browseHandler.goUpOneLevel();
    			
    		} else {
    			
    		    if (doubleBackToExitPressedOnce) {
    		    	moveTaskToBack(true);
    		    } else
    			    showBackAgainMessage();
    		}
    		
    	} else if (fm.getBackStackEntryCount() > 1) {
    		
    		browseHandler.popLastFragment();
    		
	    } else { 
		    //super.onBackPressed();
		    if (doubleBackToExitPressedOnce) {
		    	moveTaskToBack(true);
		    } else
			    showBackAgainMessage();
	    }
    	
	}
    

    //on rotate: clear fragments?
	private void showBackAgainMessage()
	{
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
		
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            	doubleBackToExitPressedOnce = false;   
            }
        }, 2000);
	}

 

    //history button and clear history button
    HistoryFavoritesDialogFragment history_dialog = new HistoryFavoritesDialogFragment();
    public void showHistory(View view) {
    	
    	Bundle bdl = new Bundle(1);
    	bdl.putString("dialog_type", "history");
    	
    	history_dialog.setArguments(bdl);
    	history_dialog.show(getSupportFragmentManager(), null);
    }
    public void clearHistory(View view) {
    	HistoryHandler hh = new HistoryHandler(this);
    	hh.clearHistory();
    	history_dialog.dismiss();
    }
    

    
    
    //"Favorites" button
    HistoryFavoritesDialogFragment favorite_dialog = new HistoryFavoritesDialogFragment();
    public void showFavorites(View view) {
    	Bundle bdl = new Bundle(1);
    	bdl.putString("dialog_type", "favorites");
    	
    	favorite_dialog.setArguments(bdl);
    	favorite_dialog.show(getSupportFragmentManager(), null);
    }
    public void clearFavorites(View view)
    {
    	FavoritesHandler fh = new FavoritesHandler(this);
    	fh.clearFavorites();
    	favorite_dialog.dismiss();
    }
    
    //"Actions" button
    public void showHideActionsButton(View view) {
    	browseHandler.executeOpenCloseActions(view);
    }
    
    
    //Button "..." after copy/cut is clicked
	public void showClipboard(View view)
    {
    	oph.openClipboardDialog();
    }
	
	//button "Paste" inside OperationsDialog
	public void pasteFile(View view)
	{
		oph.paste(OperationsHandler.getClipboard_files().get(0));
	
	}
	//button "Clear clipboard" inside OperationsDialog
	public void clearClipboard(View view)
	{
		//paste first file in clipboard
		oph.clearClipboard();
	}
	
	//button "Select" from the base_actions
	public void selectButton(View view)
	{
		if(oph.isSelectActive()) {
			oph.cancelSelect();
			return;
		}
		
		oph.beginSelect();
		
		if(oph.isCopy_cut_multiple_files_active()) {
			oph.setPasteOperationsVisibility(View.GONE);
			oph.setCopy_cut_multiple_files_active(false);
			//discard_files()
		}
	}
	
	//menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        return true;
    }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.operations:
	    	if(oph.isSelectActive()) {
	    		oph.openMultipleFilesOperationsDialog();
	    	} else {
	    		oph.openDefaultOperationsDialog();
	    	}
	        return true;
	    case R.id.menu_settings:
	    	startSettingsActivity();
	    	return true;
	    case R.id.create_new:
	    	oph.showCreateNewDialog();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void startSettingsActivity()
	{
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		handleIfSdCardStateChanged();
		
	}
	
	private void handleIfSdCardStateChanged()
	{
		//if inside sd card & sd card isnt mounted - show "sd card unmounted" view
		if(BrowseHandler.isSdCardMounted() == false 
		&& BrowseHandler.isInsideSdCard()) {
			browseHandler.displaySdCardUnmountedView();
			browseHandler.setSdCardUnmountedViewShown(true);
		} else { //only undisplay if it's already visible
			if(browseHandler.isSdCardUnmountedViewShown())
				browseHandler.undisplaySdCardUnmountedView(new File(BrowseHandler.current_path));
		}
	}
	
	
	public void upButton(View vw)
	{
		if(BrowseHandler.current_path.equals("/")) {
			onBackPressed();
		} else
			browseHandler.goUpOneLevel();
	}
	
	//copy/cut/paste/delete
	public void copyButton(View vw)
	{
		oph.copyCutSelectedFiles(PasteFile.STATUS_COPY);
		
		oph.setPasteOperationsVisibility(View.VISIBLE);
	}
	
	public void cutButton(View vw)
	{
		oph.copyCutSelectedFiles(PasteFile.STATUS_CUT);
		
		oph.setPasteOperationsVisibility(View.VISIBLE);
	}
	
	public void deleteButton(View vw)
	{
		oph.deleteSelectedFiles();
	}
	
	public void hideButton(View vw)
	{
		oph.hideSelectedFiles();
	}
	
	public void pasteButton(View vw)
	{
		boolean success;
		
		if(oph.isCopy_cut_multiple_files_active()) {
			
			//call paste on all files
			success = oph.pasteSelectedFiles();
			
			oph.setCopy_cut_multiple_files_active(false);
		} else {
			
			success = oph.paste(oph.getCopied_cut_file());
			
		}
		
		if(success)
			oph.setPasteOperationsVisibility(View.GONE);
		else
			Toast.makeText(this, "Paste didn't finish successfully.", Toast.LENGTH_LONG).show();
	}
	
	public void cancelButton(View vw)
	{
		oph.setPasteOperationsVisibility(View.GONE);
		
		if(oph.isCopy_cut_multiple_files_active()) {
			oph.setCopy_cut_multiple_files_active(false);
		}
			
	}
	
	public void searchButton(View vw)
	{
		SearchDialogFragment search_dialog = new SearchDialogFragment();
		
		search_dialog.show(getSupportFragmentManager(), null);
	}
	
	public void showAdvancedSearchButton(View vw)
	{
		
		LinearLayout advanced_search = (LinearLayout) ((View)vw.getParent()).findViewById(R.id.advanced_search);
		if(advanced_search.getVisibility() == View.GONE)
			advanced_search.setVisibility(View.VISIBLE);
		else
			advanced_search.setVisibility(View.GONE);
	}
}
