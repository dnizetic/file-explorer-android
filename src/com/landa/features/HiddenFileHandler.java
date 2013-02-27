package com.landa.features;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.landa.database.DatabaseHelper;
import com.landa.database.DatabaseManager;
import com.landa.model.HiddenFile;

//used by BrowseHandler & MainActivity
public class HiddenFileHandler {

	private static DatabaseHelper helper;

	public HiddenFileHandler(Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	public void insertHiddenFile(File f) {

		createHiddenFile(f);
		
	}


	private boolean createHiddenFile(File f)
	{
		HiddenFile h = new HiddenFile();
		h.setFull_path(f.getPath());

		Dao<HiddenFile, Integer> dao = helper.getHiddenFileDao();
		try {
			dao.create(h);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	
	
	public void clearHiddenFiles() {
		
		Dao<HiddenFile, Integer> dao = helper.getHiddenFileDao();
		
		DeleteBuilder<HiddenFile, Integer> db = dao.deleteBuilder();
		try {
			db.delete();
		} catch(SQLException e) {
			
		}
	}
	
	public void deleteHiddenFile(File f) {
		
		Dao<HiddenFile, Integer> dao = helper.getHiddenFileDao();
		
		List<HiddenFile> lst = new ArrayList<HiddenFile>();
		try {
			lst = dao.queryForEq("full_path", f.getAbsolutePath());
			for (HiddenFile h : lst) {
				dao.delete(h);
			}
		} catch(SQLException e) {
		}
	}
	
	private static HashMap<String, Boolean> hiddenFileHashmap;
	public static HashMap<String, Boolean> getHiddenFileHashmap() {
		return hiddenFileHashmap;
	}

	public static void setHiddenFileHashmap(
			HashMap<String, Boolean> hiddenFileHashmap) {
		HiddenFileHandler.hiddenFileHashmap = hiddenFileHashmap;
	}

	//on app initilisation, create a Hashmap
	//Hashmap<String, boolean>
	//     parent_file, has_hidden_files
	//assembled from the hidden_files table
	
	public static void assembleHiddenFilesHashmap()
	{
		setHiddenFileHashmap(new HashMap<String, Boolean>());
		
		DatabaseManager mgr = DatabaseManager.getInstance();
		List<HiddenFile> hidden_files = mgr.getAllHiddenFiles();
		
		for(int i = 0; i < hidden_files.size(); ++i) {
			
			//get parent
			File f = new File(hidden_files.get(i).getFull_path())
				.getParentFile();
			
			if(f != null && f.exists()) {
				hiddenFileHashmap.put(f.getAbsolutePath(), true);
			}
		}
	}
	
	public static void addHiddenFileHash(String key, boolean value)
	{
		getHiddenFileHashmap().put(key, value);
	}
	
	public static boolean fileContainsHiddenFiles(File f)
	{
		if(!f.isDirectory())
			return false;
		//use Boolean class because .get() can return true, false or null
		//"boolean" only supports true/false (no null values)
		//return false if null is returned
		Boolean status = hiddenFileHashmap.get(f.getAbsolutePath());
		return (status != null) ? status : false;
	}
	
	
	public static File[] getAllHiddenFilesAsFiles()
	{
		DatabaseManager mgr = DatabaseManager.getInstance();
		
		List<HiddenFile> hidden_files = mgr.getAllHiddenFiles();
		
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < hidden_files.size(); ++i) {
			File f = new File(hidden_files.get(i).getFull_path());
			
			if(f != null)
				files.add(f);
		}
		return files.toArray(new File[files.size()]);
	}
	
}
