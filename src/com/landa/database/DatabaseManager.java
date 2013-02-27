package com.landa.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.landa.model.Favorite;
import com.landa.model.HiddenFile;
import com.landa.model.History;

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;
    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    
    //History
    public List<History> getAllHistories() {
        List<History> data = new ArrayList<History>();
        try {
            data = getHelper().getHistoryDao().queryForAll();
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public void addORMHistory(History h) {
        try {
        	getHelper().getHistoryDao().create(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteORMHistory(History h) {
        try {
        	getHelper().getHistoryDao().delete(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateORMHistory(History h) {
        try {
            getHelper().getHistoryDao().update(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    //Favorites
    public List<Favorite> getAllFavorites() {
        List<Favorite> data = new ArrayList<Favorite>();
        try {
            data = getHelper().getFavoritesDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        return data;
    }
    
    public void addFavorite(Favorite h) {
        try {
        	getHelper().getFavoritesDao().create(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteFavorite(Favorite h) {
        try {
        	getHelper().getFavoritesDao().delete(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateFavorite(Favorite h) {
        try {
            getHelper().getFavoritesDao().update(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    //HiddenFile
    public List<HiddenFile> getAllHiddenFiles() {
        List<HiddenFile> data = new ArrayList<HiddenFile>();
        try {
            data = getHelper().getHiddenFileDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        return data;
    }
    
    public void addHidenFile(HiddenFile h) {
        try {
        	getHelper().getHiddenFileDao().create(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteHiddenFile(HiddenFile h) {
        try {
        	getHelper().getHiddenFileDao().delete(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateHiddenFile(HiddenFile h) {
        try {
            getHelper().getHiddenFileDao().update(h);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
    
}