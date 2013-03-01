package com.landa.features;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.landa.database.DatabaseHelper;
import com.landa.database.DatabaseManager;
import com.landa.model.Favorite;

//used by BrowseHandler & MainActivity
public class FavoritesHandler {

	private static DatabaseHelper helper;

	public FavoritesHandler(Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	public String insertFavorite(File f) {

		
		if(favoriteAlreadyExists(f)) {
			return "File already a favorite.";
		} else {
			if(createFavorite(f)) {
				return "File added to favorites";
			} else 
				return "Error: cannot add to favorites.";
		}
	}

	private boolean createFavorite(File f)
	{
		Favorite fv = new Favorite();
		fv.setFull_path(f.getAbsolutePath());

		Dao<Favorite, Integer> dao = helper.getFavoritesDao();
		try {
			dao.create(fv);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	private boolean favoriteAlreadyExists(File f)
	{
		Dao<Favorite, Integer> dao = helper.getFavoritesDao();
		
		ArrayList<Favorite> matches = new ArrayList<Favorite>();
		try {
			matches = (ArrayList<Favorite>) dao.queryForEq("full_path",
					f.getAbsolutePath());
			
			if(matches.size() > 0)
				return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	
	public void clearFavorites() {
		
		Dao<Favorite, Integer> dao = helper.getFavoritesDao();
		
		DeleteBuilder<Favorite, Integer> db = dao.deleteBuilder();
		try {
			db.delete();
		} catch(SQLException e) {
			Log.v("clearFavorites()", "failed");
		}
	}
	
	public static File[] getAllFavoritesAsFiles()
	{
		DatabaseManager mgr = DatabaseManager.getInstance();
		
		List<Favorite> favorites = mgr.getAllFavorites();
		
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < favorites.size(); ++i) {
			File f = new File(favorites.get(i).getFull_path());
			
			if(f != null)
				files.add(f);
		}
		return files.toArray(new File[files.size()]);
	}
	
	public static void deleteFavorite(File f) {
		
		Dao<Favorite, Integer> dao = helper.getFavoritesDao();
		
		List<Favorite> lst = new ArrayList<Favorite>();
		try {
			lst = dao.queryForEq("full_path", f.getAbsolutePath());
			for (Favorite h : lst) {
				dao.delete(h);
			}
		} catch(SQLException e) {
		}
	}
	

}
