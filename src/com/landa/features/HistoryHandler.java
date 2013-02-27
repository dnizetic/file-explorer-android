package com.landa.features;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.landa.database.DatabaseHelper;
import com.landa.database.DatabaseManager;
import com.landa.model.History;

//used by BrowseHandler & MainActivity
public class HistoryHandler {

	private static DatabaseHelper helper;

	public HistoryHandler(Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	public void insertHistory(File f) {

		
		//delete duplicate histories (if exists)
		deleteDuplicateHistory(f);

		// insert the new History entity
		createHistory(f);
		
		deleteIfMoreThenTen();
	}

	public boolean createHistory(File f)
	{
		History h = new History();
		h.setFull_path(f.getPath());

		Dao<History, Integer> dao = helper.getHistoryDao();
		try {
			dao.create(h);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	
	
	public void clearHistory() {
		
		Dao<History, Integer> dao = helper.getHistoryDao();
		
		DeleteBuilder<History, Integer> db = dao.deleteBuilder();
		try {
			db.delete();
		} catch(SQLException e) {
			
		}
	}
	
	//deletes duplicates of f
	private boolean deleteDuplicateHistory(File f)
	{
		Dao<History, Integer> dao = helper.getHistoryDao();
		
		ArrayList<History> matches = new ArrayList<History>();

		//dao.queryForEq(fieldName, value)
		
		try {
			matches = (ArrayList<History>) dao.queryForEq("full_path",
					f.getPath());
			// iterate through matches, delete them
			for (History h : matches)
				dao.delete(h);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	
	//deletes entities (if more then 10 in DB)
	private boolean deleteIfMoreThenTen()
	{
		Dao<History, Integer> dao = helper.getHistoryDao();
		
		ArrayList<History> entities = new ArrayList<History>();
		
		// check # of entities: if 11, delete first one.
		try {
			Long num_entities = dao.countOf();
			if (num_entities > 10) {
				Long top_x = num_entities - 10;

				QueryBuilder<History, Integer> qb = dao.queryBuilder();
				qb.limit(top_x);

				entities = (ArrayList<History>) qb.query();

				for (History z : entities)
					dao.delete(z);
				
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	public static File[] getAllHistoriesAsFiles()
	{
		DatabaseManager mgr = DatabaseManager.getInstance();
		
		List<History> histories = mgr.getAllHistories();
		
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < histories.size(); ++i) {
			File f = new File(histories.get(i).getFull_path());
			
			if(f != null)
				files.add(f);
		}
		return files.toArray(new File[files.size()]);
	}

}
