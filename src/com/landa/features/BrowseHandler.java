package com.landa.features;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileexplorermanager.BuildConfig;
import com.example.fileexplorermanager.R;
import com.landa.adapter.MainFileListAdapter;
import com.landa.fragment.ContentFragment;
import com.landa.fragment.SdCardEmptyFragment;
import com.landa.general.General;

public class BrowseHandler {
	
    static private BrowseHandler instance;

    static public void init(Context ctx, FragmentActivity ac) {
        //if (null==instance) {
            instance = new BrowseHandler(ctx, ac);
        //}
    }
    static public BrowseHandler getInstance() {
        return instance;
    }
	
	public Context ctx;
	public FragmentActivity ac;
	

	//current active file/folder
	public static String current_path;
	
	
	//private boolean sdCardMounted;
	public BrowseHandler(Context ctx, FragmentActivity ac) {
		this.ctx = ctx;
		this.ac = ac;
		
		ac.setContentView(R.layout.main_view);
		
		current_path = getInitialPath();
		
		//screen rotation leaves a fragment behind
		clearBackStack();
	}
	
	private File getHomeDirectory()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String home_dir = prefs.getString("home_directory", 
				Environment.getExternalStorageDirectory().toString());
		
		return new File(home_dir);
	}
	
	//test: sdcard unmounted
	private String getInitialPath()
	{
		File home_dir = getHomeDirectory();
		
		String initial_path = home_dir.getAbsolutePath();
		
		if(!isSdCardMounted() && fileIsInsideSdCard(home_dir)) {
			Toast.makeText(ctx, "SD Card unmounted, switching to '/'", Toast.LENGTH_SHORT).show();
			initial_path = "/";
		}
		return initial_path;
	}
	
	public void openShortcut(File f)
	{
		if(f != null) {
			clearBackStack();
			openFile(f);
		} else {
			Toast.makeText(ctx, "File not found.", Toast.LENGTH_SHORT).show();
			openFile(new File(BrowseHandler.current_path));
		}
	}
	

	//turn this on if backstack should be cleared before rendering content
	public boolean clear_back_stack_before_rendering = false;
	
	//used by MainActivity, GeneralDialogFragment and ContentFragment
	public void openFile(File f)
	{
		if(!f.exists()) {
			Toast.makeText(ctx, "Invalid path.", Toast.LENGTH_LONG).show();
			return;
		} else {
			if(!isSdCardMounted() && fileIsSdCardRoot(f)){
				Toast.makeText(ctx, "SD Card unmounted.", Toast.LENGTH_LONG).show();
				return;
			}
		}
		
		if(clear_back_stack_before_rendering) {
			clearBackStack();
			clear_back_stack_before_rendering = false;
		}

			
        //get current file/folder being viewed
        if(f.isDirectory()) { 
        	
        	OperationsHandler oph = OperationsHandler.getInstance();
        	if(oph.isSelectActive()) //cancels select when we click a favorites/history item or go up one level
        		oph.cancelSelect();
        	

    		if(search_displayed == true) {
    			search_displayed = false;
    		}
        	
        	//render file list of the folder
        	populateContent(f);
        	
        } else { 
        	//open OpenWith dialog
        	executeOpenWith(f);
        }
        
        executeSaveHistory(f);
	}
	
	private boolean fileIsSdCardRoot(File f)
	{
		return f.getAbsolutePath().toString()
				.compareTo(Environment.getExternalStorageDirectory().toString()) == 0;
	}
	

	//used by History/Favorites onClick() - class "GeneralDialogFragment"
	//used when we shift from current path to a different path by using a dialog (Favorites or History)
	public void clearBackStack()
	{
		FragmentManager fm = ac.getSupportFragmentManager();
		
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    fm.popBackStack();
		}
		
		
	}
	
	
	public void populateContent(File f)
	{
		FragmentTransaction transaction = ac.getSupportFragmentManager().beginTransaction();

		ContentFragment cf = createContentFragmentWithArguments(f);
		
		transaction.replace(R.id.contentFragment, cf);
		transaction.addToBackStack(null);
		
		updateShownPath(f.getPath());
		transaction.commit();
		
	}
	
	
	private ContentFragment createContentFragmentWithArguments(File f)
	{
		
		ContentFragment cf = new ContentFragment();
		
		Bundle bdl = new Bundle(1);
		bdl.putString("file_absolute_path", f.getAbsolutePath());
	
		cf.setArguments(bdl);
		
		return cf;
	}

	
	
	public void markSelectedFiles()
	{
		ListView lv = (ListView) ac.findViewById(android.R.id.list);
		
		//adapters for "Empty folder" views?
		MainFileListAdapter adapter = (MainFileListAdapter) lv.getAdapter();
		
		if(adapter != null)
			adapter.refillAdapterData();
		
	}
	
	public void refreshContent()
	{
		FragmentManager fm = ac.getSupportFragmentManager();
		fm.popBackStack();
		
		populateContent(new File(current_path));
	}
	
	//used by BrowseHandler and MainActivity
	private void updateShownPath(String new_path)
	{
		TextView shownPath = (TextView) ac.findViewById(R.id.shown_path);
		shownPath.setText(new_path);
		
		current_path = new_path;
	}
	
	
	private void executeOpenWith(File f) {
		Intent intnt = new Intent(Intent.ACTION_VIEW);

		Uri uri = Uri.fromFile(f);

		String mime = General.getMimeType(f.getAbsolutePath());

		if (BuildConfig.DEBUG) {
			//intnt.setDataAndType(uri, "*/*");
			intnt.setDataAndType(uri, mime);
		} else {
			intnt.setDataAndType(uri, mime);
		}
		
		intnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		try {
			// startActivity(intnt);
			ctx.startActivity(intnt);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(ctx, "No Application Available to view this file.",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	
	//used by ContentFragment and MainActivity
	public void executeSaveHistory(File f) {
		HistoryHandler hist = new HistoryHandler(ctx);
		hist.insertHistory(f);
	}



	//used by MainActivity
	public void executeOpenCloseActions(View vw) 
	{
		
		HorizontalScrollView hz = (HorizontalScrollView) ac.findViewById(R.id.horizontalScrollView1);
		
		if(hz.getVisibility() == View.GONE) {
			hz.setVisibility(View.VISIBLE);
		} else {
			hz.setVisibility(View.GONE);
		}
	}
	
	
	
	
	public static int getFileIconResourceId(String full_path) {

		File f = new File(full_path);
		
		if (f.isDirectory()) {
			return R.drawable.folder;
		} else {
			
			String mime_type = General.getMimeType(f.getPath());
			
			//global mime types: application, audio, image, text, video, x-world
			//full list: http://reference.sitepoint.com/html/mime-types-full

			if(mime_type != null) {
				if(mime_type == "application/zip" 
				|| mime_type == "application/rar"
				|| mime_type == "application/x-tar" 
				|| mime_type == "application/x-rar-compressed" 
				|| mime_type == "application/octet-stream"
				|| mime_type == "application/x-compressed"
				|| mime_type == "application/x-zip-compressed"
				|| mime_type == "multipart/x-zip") {
					return R.drawable.zip;
				} else if(mime_type == "text/html") {
					return R.drawable.html;
				} else if(mime_type == "application/mspowerpoint"
					   || mime_type == "application/powerpoint"
					   || mime_type == "application/vnd.ms-powerpoint"
					   || mime_type == "application/x-mspowerpoint") {
					return R.drawable.ppt;
				} else if(mime_type == "application/msword") {
					return R.drawable.word;
				} else if(mime_type == "application/x-shockwave-flash") {
					return R.drawable.afp;
				} else if(mime_type == "application/pdf") {
					return R.drawable.pdf;
				} else if(mime_type == "application/excel" 
					   || mime_type == "application/x-excel"
					   || mime_type == "application/x-msexcel") {
					return R.drawable.excel;
				}
		
				//global mime types
				else if(mime_type.contains("audio")) {
					return R.drawable.music;
				} else if(mime_type.contains("image")) {
					return R.drawable.image;
				} else if(mime_type.contains("text")) {
					return R.drawable.txt;
				} else if(mime_type.contains("video")) {
					return R.drawable.video;
				} else {
					//generic
					return R.drawable.gen;
				}
			} else {
				//generic
				return R.drawable.gen;
			}
		}
	} 
	
	
	public static boolean search_displayed = false;
	public void popLastFragment()
	{
		FragmentManager fm = ac.getSupportFragmentManager();
		
        TextView shownPath = (TextView) ac.findViewById(R.id.shown_path);
        String sPath = shownPath.getText().toString();
        
	    fm.popBackStack();
		
	    if(search_displayed) {
	    	search_displayed = false;
	    } else
	    	updateShownPath(new File(sPath).getParent());
	}
	
	public void goUpOneLevel()
	{
		
		File parent = new File(new File(current_path).getParent());
		
		clearBackStack();
		openFile(parent);
		
		Log.v("populateParentFolder()", "here");
		
	}
	
	public boolean atHomeOrRootFolder()
	{
		if(!current_path.equals(getHomeDirectory().getAbsolutePath())
		&& !current_path.equals("/")) {
			return false;
		}
		
		return true;
	}


	//sd card
	public static boolean isSdCardMounted()
	{
		return Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
	}
	
	public static boolean isInsideSdCard()
	{
		return current_path.contains(Environment.getExternalStorageDirectory().toString());
	}
	
	public static boolean fileIsInsideSdCard(File f)
	{
		return f.getAbsolutePath().contains(Environment.getExternalStorageDirectory().toString());
	}
	
	private boolean sdCardUnmountedViewShown = false;
	public boolean isSdCardUnmountedViewShown() {
		return sdCardUnmountedViewShown;
	}
	public void setSdCardUnmountedViewShown(boolean sdCardUnmountedViewShown) {
		this.sdCardUnmountedViewShown = sdCardUnmountedViewShown;
	}
	public void displaySdCardUnmountedView()
	{
		
		clearBackStack();
		setSdCardEmptyFragment();
		
	}
	
	private void setSdCardEmptyFragment()
	{
		FragmentTransaction transaction = ac.getSupportFragmentManager().beginTransaction();
		SdCardEmptyFragment sf = new SdCardEmptyFragment();
		
		transaction.replace(R.id.contentFragment, sf);
		transaction.commit();
	}


	public void undisplaySdCardUnmountedView(File f)
	{
		setSdCardUnmountedViewShown(false);
		openFile(f);
	}


}
