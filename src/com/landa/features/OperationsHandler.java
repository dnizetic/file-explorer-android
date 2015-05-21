package com.landa.features;

import java.io.File;
import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.landa.fileexplorermanager.R;
import com.landa.adapter.MainFileListAdapter;
import com.landa.datatypes.PasteFile;
import com.landa.datatypes.SelectedFile;
import com.landa.dialog.ClipboardDialogFragment;
import com.landa.dialog.CreateNewDialogFragment;
import com.landa.dialog.FilePropertiesDialogFragment;
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
    	bdl.putString("file_absolute_path", BrowseHandler.current_path);
    	opDialog.setArguments(bdl);
		
    	opDialog.show(ac.getSupportFragmentManager(), null);
	}
	
	//getter/setter
	private static ArrayList<PasteFile> clipboard_files = new ArrayList<PasteFile>();
	
	public static ArrayList<PasteFile> getClipboard_files() {
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
	
	
	public void addUniqueToClipboard(File f, boolean status)
	{
		int index;
		if((index = existsInClipboard(f)) != -1) {
			clipboard_files.get(index).setStatus(status);
		} else { 
			clipboard_files.add(0, new PasteFile(f, status));
		}	
	}
	
	
	private PasteFile copied_cut_file;
	
	public PasteFile getCopied_cut_file() {
		return copied_cut_file;
	}
	public void setCopied_cut_file(PasteFile copied_cut_file) {
		this.copied_cut_file = copied_cut_file;
	}
	
	public void cut(File f) 
	{
		setCopy_cut_multiple_files_active(false);
		
		setCopied_cut_file(new PasteFile(f, PasteFile.STATUS_CUT));
		
		//showOperationsPickButton();
		setPasteOperationsVisibility(View.VISIBLE);
		
		addUniqueToClipboard(f, PasteFile.STATUS_CUT);
	}
	
	public void copy(File f) 
	{
		setCopy_cut_multiple_files_active(false);
		
		setCopied_cut_file(new PasteFile(f, PasteFile.STATUS_COPY));
		
		//showOperationsPickButton();
		setPasteOperationsVisibility(View.VISIBLE);
		
		addUniqueToClipboard(f, PasteFile.STATUS_COPY);
	}
	

	//ClipboardFile arg: used to differentiate cut from copy
	public boolean paste(PasteFile source) 
	{
		if(source == null) {
			return false;
		}

		File target = new File(BrowseHandler.
				current_path.concat("/").concat(source.getFile().getName()));
		
		Log.v("Source path:", source.getFile().getAbsolutePath().toString());
		Log.v("Dest path:", target.getAbsolutePath().toString());
		
		if(source.getFile().getAbsolutePath()
				.equals(target.getAbsolutePath())) {
			displayOperationMessage("Invalid path (same).");
			return false;
		}

		
		//http://stackoverflow.com/questions/5715104/copy-files-from-a-folder-of-sd-card-into-another-folder-of-sd-card
		if(source.getFile().isDirectory()) {
			try {
				General.copyDirectory(source.getFile(), target);
			} catch(Exception e) {
				displayOperationMessage("Error: copy failed: ".concat(e.getMessage()));
				return false;
			}
		} else { 
			try {
				General.copyFile(source.getFile(), target);
			} catch(Exception e) {
				displayOperationMessage("Error: copy failed: ".concat(e.getMessage()));
				return false;
			}
		}
		
		clipboard_files.remove(0);

		
		//use the delete() method
		if(source.getStatus() == PasteFile.STATUS_CUT)
			delete(source.getFile());

		if(clipboard_files.size() == 0) {
			hideOperationsPickButton();
		}
		
		displayOperationMessage("Paste successfull.");
		//cdf.dismiss();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
		
		return true;
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
		//rename: what if parentFile() is "/"?
		//parent can't be "/", because we don't have permissions to create files/folders at "/"
		if(!f.renameTo(new File(f.getParentFile().getAbsolutePath().concat("/".concat(new_name))))) {
			displayOperationMessage("Rename failed");
			return;
		}
		displayOperationMessage("Rename success.");
	}

	
	//you don't output errors in delete().
	//because e.g. deleteAll might use it: it'll spam then
	boolean deleteErrors;
	public boolean delete(File f)
	{
		deleteRecursive(f);
		
		return deleteErrors ? false : true;
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
	
	private ArrayList<File> selected_files = new ArrayList<File>();
	public ArrayList<File> getSelected_files() {
		return selected_files;
	}
	public void setSelected_files(ArrayList<File> selected_files) {
		this.selected_files = selected_files;
	}
	public void beginSelect()
	{
		//set background
		if(!isSelectActive()) {
			setSelectButtonBackground();
			setSelectActive(true);
			
			//turn on multi-select operations
			//MS operations:
			//- copy, cut, delete, hide, cancel
			setSelectOperationsVisibility(View.VISIBLE);
			
			displayOperationMessage("Select ready.");
		}
	}
	
	public void setSelectOperationsVisibility(int visibility)
	{
		LinearLayout select_actions = (LinearLayout) ac.findViewById(R.id.show_select_ops);
		select_actions.setVisibility(visibility);
	}
	
	public void setPasteOperationsVisibility(int visibility)
	{
		LinearLayout paste_actions = (LinearLayout) ac.findViewById(R.id.show_paste_ops);
		paste_actions.setVisibility(visibility);
	}

	
	public void cancelSelect() 
	{
		unsetSelectButtonBackground();
		setSelectActive(false);
		
		selected_files.clear();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.markSelectedFiles();
		
		setSelectOperationsVisibility(View.GONE);
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
			if(f.getAbsolutePath()
					.equals(selected_files.get(i).getAbsolutePath()))
				return true;
		return false;
	}
	
	
	
	public void selectFile(File f)
	{
		if(fileAlreadySelected(f)) {
			deselectFile(f);
			//selected_files.remove(f);
		} else {
			selected_files.add(f);
		}
	}

	private void deselectFile(File f)
	{
		removeFromSelectedFiles(f);
	}
	
	private void removeFromSelectedFiles(File f)
	{
		for(int i = 0; i < selected_files.size(); ++i) {
			if(f.getName().equals(selected_files.get(i).getName())) {
				selected_files.remove(i);
				return;
			}
		}
	}
	
	
	//sp: File, Copy/Cut
	private ArrayList<PasteFile> multiple_op_files = new ArrayList<PasteFile>();
	
	
	//operation: cut or copy
	//copySelected
	//cutSelected
	public void copyCutSelectedFiles(boolean operation)
	{
		if(selected_files.size() == 0) {
			Toast.makeText(ctx, 
					"Select files first.", Toast.LENGTH_LONG).show();
			return;
		}

		setPasteOperationsVisibility(View.VISIBLE);
		
		multiple_op_files.clear();
		
		for(int i = 0; i < selected_files.size(); ++i) {
			addUniqueToClipboard(selected_files.get(i), operation);
			
			multiple_op_files.add(
					new PasteFile(selected_files.get(i), operation));
		}
		
		cancelSelect();
		
		setCopy_cut_multiple_files_active(true);
	}
	
	
	public boolean pasteSelectedFiles()
	{
		boolean success = true;
		
		for(int i = 0; i < multiple_op_files.size(); ++i) {
			if(!paste(multiple_op_files.get(i)))
				success = false;
		}
		
		return success;
	}
	
	
	
	public void deleteSelectedFiles()
	{
		
		if(selected_files.size() == 0) {
			Toast.makeText(ctx, 
					"Select files first.", Toast.LENGTH_LONG).show();
			return;
		}
		
		boolean errors = false;
		for(int i = 0; i < selected_files.size(); ++i)
			if(!delete(selected_files.get(i)))
				errors = true;
		
		cancelSelect();
		
		if(errors)
			Toast.makeText(ctx, 
					"Error while deleting.", Toast.LENGTH_LONG).show();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void hideSelectedFiles()
	{
		if(selected_files.size() == 0) {
			Toast.makeText(ctx, 
					"Select files first.", Toast.LENGTH_LONG).show();
			return;
		}
		
		for(int i = 0; i < selected_files.size(); ++i)
			hide(selected_files.get(i));
		

		displayOperationMessage("Files hidden");
		
		cancelSelect();
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	

	public void selectAll()
	{
		beginSelect();
		
		ListView lv = (ListView) ac.findViewById(android.R.id.list);
		MainFileListAdapter adapter = (MainFileListAdapter) lv.getAdapter();
		SelectedFile[] files = adapter.getData();
		
		for(int i = 0; i < files.length; ++i) {
			File f = files[i].getFile();
			
			if(!fileAlreadySelected(f))
				selectFile(f);
		}
		
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.markSelectedFiles();
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
	    
	    addIntent.putExtra("shortcut_path", f.getAbsolutePath());
	    
		ctx.sendBroadcast(addIntent);
		
		displayOperationMessage("Shortcut created successfully.");
	}
	
	public void addFavorite(File f)
	{
		FavoritesHandler fh = new FavoritesHandler(ctx);
		
		displayOperationMessage(fh.insertFavorite(f));
	}
	
	public void hide(File f)
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
		
		executeCreateNew(type, new_file);

		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void executeCreateNew(String type, File new_file)
	{
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
	}
	
	public boolean executeCreateNewWithoutMessages(String type, File new_file)
	{
		if(!new_file.exists()) {
			if(type == "Folder") {
				boolean result = new_file.mkdir();
				if(result) {
					return true;
				} else {
					return false;
				}
			} else {
				try {
					new_file.createNewFile();
					return true;
				} catch(Exception e) {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	public void compressFile(File f)
	{
		boolean status; 
		if(f.isDirectory()) {
			DirectoryZip.setSOURCE_FOLDER(f.getAbsolutePath());
			DirectoryZip.setOUTPUT_ZIP_FILE(f.getAbsolutePath().concat(".zip"));
			status = DirectoryZip.main(null);

		} else {
			FileZip.setSOURCE_FILE(f.getAbsolutePath());
			FileZip.setOUTPUT_ZIP_FILE(f.getAbsolutePath().concat(".zip"));
			status = FileZip.main(null);
		}
		if(status) {
			displayOperationMessage("File compressed.");
		} else {
			displayOperationMessage("Error while compressing file.");
		}
		
		BrowseHandler bh = BrowseHandler.getInstance();
		bh.refreshContent();
	}
	
	public void setDirectoryAsHome(File f)
	{
		if(f.isDirectory()) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			
			Editor editor = prefs.edit();
			editor.putString("home_directory", f.getAbsolutePath());
			editor.commit();
			
			displayOperationMessage("Home altered.");
		} else {
			displayOperationMessage("Must be a directory.");
		}
	}
	
	public void showFileProperties(File f)
	{
		FilePropertiesDialogFragment d = new FilePropertiesDialogFragment();
		
		Bundle bdl = new Bundle(1);
		bdl.putString("file_absolute_path", f.getAbsolutePath());
		d.setArguments(bdl);
		
		d.show(ac.getSupportFragmentManager(), null);
	}
	
	
	private boolean copy_cut_multiple_files_active = false;

	public boolean isCopy_cut_multiple_files_active() {
		return copy_cut_multiple_files_active;
	}
	public void setCopy_cut_multiple_files_active(
			boolean copy_cut_multiple_files_active) {
		this.copy_cut_multiple_files_active = copy_cut_multiple_files_active;
	}

	
	
	
}
