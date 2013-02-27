package com.landa.features;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileexplorermanager.R;
import com.landa.datatypes.ClipboardFile;
import com.landa.datatypes.SelectedFile;
import com.landa.dialog.ClipboardDialogFragment;
import com.landa.dialog.CreateNewDialogFragment;
import com.landa.dialog.OperationsDialogFragment;
import com.landa.fileexplorermanager.MainActivity;
import com.landa.general.DirectoryZip;
import com.landa.general.FileZip;
import com.landa.general.General;


public class OperationsHandler {
	
	
    static private OperationsHandler instance;

    static public void init(Context ctx, FragmentActivity ac) {
        //if (null==instance) {
            instance = new OperationsHandler(ctx, ac);
        //}
    }
    static public OperationsHandler getInstance() {
        return instance;
    }
	
	private Context ctx;
	private FragmentActivity ac;
	
	public OperationsHandler(Context ctx, FragmentActivity ac)
	{
		this.ctx = ctx;
		this.ac = ac;
	}
	
	
	public void openOperationsDialog(File f) 
	{
		OperationsDialogFragment opDialog = new OperationsDialogFragment();
		
    	Bundle bdl = new Bundle(1);
    	bdl.putString("operation_type", "single_file");
    	bdl.putString("file_absolute_path", f.getAbsolutePath());
    	opDialog.setArguments(bdl);
		
		opDialog.show(ac.getSupportFragmentManager(), null);
	}
	
	public void openMultipleFilesOperationsDialog() 
	{
		OperationsDialogFragment opDialog = new OperationsDialogFragment();
		
    	Bundle bdl = new Bundle(1);
    	bdl.putString("operation_type", "multiple_files");
    	opDialog.setArguments(bdl);
		
    	opDialog.show(ac.getSupportFragmentManager(), null);
	}
	
	public void openDefaultOperationsDialog() 
	{
		OperationsDialogFragment opDialog = new OperationsDialogFragment();
		
    	Bundle bdl = new Bundle(1);
    	bdl.putString("operation_type", "default");
    	opDialog.setArguments(bdl);
		
    	opDialog.show(ac.getSupportFragmentManager(), null);
	}
	
	//getter/setter
	private static ArrayList<ClipboardFile> clipboard_files = new ArrayList<ClipboardFile>();
	
	public static ArrayList<ClipboardFile> getClipboard_files() {
		return clipboard_files;
	}
	
	public static ArrayList<File> getFilesFromClipboard()
	{
		ArrayList<File> l = new ArrayList<File>();
		
		for(int i = 0; i < clipboard_files.size(); ++i)
			l.add(clipboard_files.get(i).getFile());
		
		return l;
	}


	ClipboardDialogFragment cdf;
	public void openClipboardDialog()
	{
		cdf = new ClipboardDialogFragment();
		cdf.show(ac.getSupportFragmentManager(), null);
	}
	
	
	private void showOperationsPickButton()
	{
    	//if invisible, show button at bottom
		Button ops = (Button) ac.findViewById(R.id.operation_pick);
		
		if(ops.getVisibility() == View.GONE) {
			ops.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideOperationsPickButton()
	{
    	//if invisible, show button at bottom
		Button ops = (Button) ac.findViewById(R.id.operation_pick);
		
		if(ops.getVisibility() != View.GONE) {
			ops.setVisibility(View.GONE);
		}
	}
	
	public void cut(File f) 
	{
		showOperationsPickButton();
		
		int index;
		if((index = existsInClipboard(f)) != -1) {
			clipboard_files.get(index).setStatus(ClipboardFile.STATUS_CUT);
		} else { 
			clipboard_files.add(0, new ClipboardFile(f, ClipboardFile.STATUS_CUT));
		}	
	}
	
	public void copy(File f) 
	{
		showOperationsPickButton();
		
		int index;
		if((index = existsInClipboard(f)) != -1) {
			clipboard_files.get(index).setStatus(ClipboardFile.STATUS_COPY);
		} else {
			clipboard_files.add(0, new ClipboardFile(f, ClipboardFile.STATUS_COPY));
		}
		
	}
	
	public static final boolean OP_SINGLE = false;
	public static final boolean OP_MULTIPLE = true;
	public boolean last_operation = OP_SINGLE;
	public boolean getLast_operation() {
		return last_operation;
	}
	public void setLast_operation(boolean last_operation) {
		this.last_operation = last_operation;
	}
	
	public void paste(ClipboardFile source) 
	{

		if(source == null) { //shouldn't happen
			displayOperationMessage("Error: clipboard empty.");
			return;
		}

		File target = new File(BrowseHandler.current_path.concat("/").concat(source.getFile().getName()));
		
		Log.v("Source path:", source.getFile().getAbsolutePath().toString());
		Log.v("Dest path:", target.getAbsolutePath().toString());
		
		if(source.getFile().getAbsolutePath().toString().compareTo(target.getAbsolutePath().toString()) == 0) {
			displayOperationMessage("Invalid path (same).");
			return;
		}

		
		//http://stackoverflow.com/questions/5715104/copy-files-from-a-folder-of-sd-card-into-another-folder-of-sd-card
		if(source.getFile().isDirectory()) {
			try {
				General.copyDirectory(source.getFile(), target);
			} catch(Exception e) {
				displayOperationMessage("Error: copy failed: ".concat(e.getMessage()));
				return;
			}
		} else { 
			try {
				General.copyFile(source.getFile(), target);
			} catch(Exception e) {
				displayOperationMessage("Error: copy failed: ".concat(e.getMessage()));
				return;
			}
		}
		
		clipboard_files.remove(0);

		
		//use the delete() method
		if(source.getStatus() == ClipboardFile.STATUS_CUT)
			delete(source.getFile());

		if(clipboard_files.size() == 0) {
			hideOperationsPickButton();
		}
		
		displayOperationMessage("Paste successfull.");
		cdf.dismiss();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void pasteAll()
	{
		
	}
	
	public void clearClipboard()
	{
		clipboard_files.clear();
		cdf.dismiss();
		hideOperationsPickButton();
	}
	
	
	private int existsInClipboard(File f)
	{
		for(int i = 0; i < clipboard_files.size(); ++i) {    
		    if(f.getAbsolutePath().toString()
		    		.compareTo(clipboard_files.get(i).getFile().getAbsolutePath().toString()) == 0) {
		    	return i;
		    }
		}
		return -1;
	}
	
	private void displayOperationMessage(String message)
	{
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}
	
	//rename
	public void rename(File f, String new_name) 
	{
		if(!f.renameTo(new File(f.getParentFile().getAbsolutePath().concat("/".concat(new_name))))) {
			displayOperationMessage("Rename failed");
			return;
		}
		displayOperationMessage("Rename success.");
	}

	
	//put it outside due to recursive delete
	boolean deleteErrors;
	public void delete(File f)
	{
		deleteRecursive(f);
		
		if(deleteErrors) {
			displayOperationMessage("Errors while deleting.");
		} else {
			displayOperationMessage("Delete success.");
		}
	}
	
	private void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    if(!fileOrDirectory.delete())
	    	deleteErrors = true;
	}

	
	
	//select
	private boolean selectActive = false;
	public boolean isSelectActive() {
		return selectActive;
	}
	public void setSelectActive(boolean selectActive) {
		this.selectActive = selectActive;
	}
	
	private ArrayList<SelectedFile> selected_files = new ArrayList<SelectedFile>();
	public void beginSelect()
	{
		//set background
		if(!isSelectActive()) {
			setSelectButtonBackground();
			setSelectActive(true);
			
			displayOperationMessage("Select ready.");
		}
	}
	
	public void cancelSelect() 
	{
		unsetSelectButtonBackground();
		setSelectActive(false);
		
		unhighlightAllSelectedFiles();
		
		displayOperationMessage("Select canceled.");
	}
	
	private void setSelectButtonBackground()
	{
		Button btn = (Button) ac.findViewById(R.id.select_button);
		btn.setBackgroundResource(R.layout.gradient_bg);
	}
	private void unsetSelectButtonBackground()
	{
		Button btn = (Button) ac.findViewById(R.id.select_button);
		btn.setBackgroundColor(Color.WHITE);
		
	}
	
	private boolean fileAlreadySelected(File f)
	{
		for(int i = 0; i < selected_files.size(); ++i)
			if(f.getAbsolutePath().toString()
					.compareTo(selected_files.get(i).getFile().getAbsolutePath().toString()) == 0)
				return true;
		return false;
	}
	
	
	
	public void selectFile(File f, View vw)
	{
		if(fileAlreadySelected(f)) {
			deselectFile(vw);
		} else {
			selected_files.add(new SelectedFile(f, (TextView) vw.findViewById(R.id.file_name)));
			highlightFileName(vw);
		}
	}
	
	
	private void deselectFile(View vw)
	{
		TextView fileName = (TextView) vw.findViewById(R.id.file_name);
		
		fileName.setTextColor(Color.BLACK);
		removeFromSelectedFiles(new File(fileName.getText().toString()));
	}
	
	private void highlightFileName(View vw)
	{
		TextView fileName = (TextView) vw.findViewById(R.id.file_name);
		fileName.setTextColor(Color.BLUE);
	}


	private void removeFromSelectedFiles(File f)
	{
		for(int i = 0; i < selected_files.size(); ++i) {
			if(f.getName().toString().compareTo(selected_files.get(i).getFile().getName().toString()) == 0) {
				selected_files.remove(i);
				return;
			}
		}
	}
	
	private void unhighlightAllSelectedFiles()
	{
		for(int i = 0; i < selected_files.size(); ++i)
			selected_files.get(i).getTv().setTextColor(Color.BLACK);
		
		selected_files.clear();
	}
	
	public void cutSelectedFiles()
	{
		for(int i = 0; i < selected_files.size(); ++i)
			cut(selected_files.get(i).getFile());
		
		cancelSelect();
	}
	
	public void copySelectedFiles()
	{
		for(int i = 0; i < selected_files.size(); ++i)
			copy(selected_files.get(i).getFile());
		
		cancelSelect();
	}
	
	public void deleteSelectedFiles()
	{
		for(int i = 0; i < selected_files.size(); ++i)
			delete(selected_files.get(i).getFile());
		
		cancelSelect();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	

	public void selectAll()
	{
		beginSelect();
		
		ListView lv = (ListView) ac.findViewById(android.R.id.list);
		//lv.setCacheColorHint(Color.BLUE);
		
		for(int i = 0; i < lv.getChildCount(); ++i) {
			View vw = lv.getChildAt(i);
			
			TextView full_path = (TextView) vw.findViewById(R.id.full_path);
			String f_path = full_path.getText().toString();
			
			TextView tv = (TextView) vw.findViewById(R.id.file_name);
			
			File f = new File(f_path);
			if(!fileAlreadySelected(f))
				selectFile(f, tv);
		}
	}
	
	public void addShortcut(File f) {
	    //Adding shortcut for MainActivity 
	    //on Home screen
	    Intent shortcutIntent = new Intent(ctx,
	            MainActivity.class);
	    
	    
	    shortcutIntent.setAction(Intent.ACTION_MAIN);
	    shortcutIntent.putExtra("shortcut_path", f.getAbsolutePath());
	 
	    Intent addIntent = new Intent();
	    addIntent
	            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, f.getName());
	    
	    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
	            Intent.ShortcutIconResource.fromContext(ctx,
	            		BrowseHandler.getFileIconResourceId(f.getAbsolutePath())));
	 
	    addIntent
	            .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
	    
		ctx.sendBroadcast(addIntent);
		
		displayOperationMessage("Shortcut created successfully.");
	}
	
	public void addFavorite(File f)
	{
		FavoritesHandler fh = new FavoritesHandler(ctx);
		
		displayOperationMessage(fh.insertFavorite(f));
	}
	
	public void hideFile(File f)
	{
		HiddenFileHandler hfh = new HiddenFileHandler(ctx);
		
		hfh.insertHiddenFile(f);
		
		//update the assembled list
		HiddenFileHandler.addHiddenFileHash(new File(f.getParent()).getAbsolutePath(), true);
		
		//refresh current content
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void showCreateNewDialog()
	{
		CreateNewDialogFragment df = new CreateNewDialogFragment();
		
		df.show(ac.getSupportFragmentManager(), null);
	}
	
	public void createNew(String type, String file_name)
	{
		String file_path = BrowseHandler.current_path;
		if(General.lastCharOfString(file_path) != '/')
			 file_path = BrowseHandler.current_path.concat("/");
		
		file_path = file_path.concat(file_name);
		File new_file = new File(file_path);
		
		if(!new_file.exists()) {
			if(type == "Folder") {
				boolean result = new_file.mkdir();
				if(result) {
					displayOperationMessage("Folder created successfully.");
				} else {
					displayOperationMessage("Folder creation failed.");
				}
			} else {
				try {
					new_file.createNewFile();
					displayOperationMessage("File created successfully.");
				} catch(Exception e) {
					displayOperationMessage("File creation failed.");
				}
			}
		} else {
			displayOperationMessage("File already exists, please specify another name.");
		}

		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void compressFile(File f)
	{
		if(f.isDirectory()) {
			DirectoryZip.setSOURCE_FOLDER(f.getAbsolutePath());
			DirectoryZip.setOUTPUT_ZIP_FILE(f.getAbsolutePath().concat(".zip"));
			DirectoryZip.main(null);

		} else {
			FileZip.setSOURCE_FILE(f.getAbsolutePath());
			FileZip.setOUTPUT_ZIP_FILE(f.getAbsolutePath().concat(".zip"));
			FileZip.main(null);
		}
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void setDirectoryAsHome(File f)
	{
		if(f.isDirectory()) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			
			Editor editor = prefs.edit();
			editor.putString("home_dir", f.getAbsolutePath());
			editor.commit();
			
			displayOperationMessage("Home altered.");
		} else {
			displayOperationMessage("Must be a directory.");
		}
	}
	
	public void showFileProperties(File f)
	{
		Dialog dialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		
		dialog.show();
	}
	
}