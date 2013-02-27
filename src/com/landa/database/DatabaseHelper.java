package com.landa.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.landa.model.Favorite;
import com.landa.model.HiddenFile;
import com.landa.model.History;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application
    private static final String DATABASE_NAME = "fileExplorer.sqlite";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<History, Integer> historyDao = null;
    private Dao<Favorite, Integer> favoritesDao = null;
    private Dao<HiddenFile, Integer> hiddenDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,ConnectionSource connectionSource) {
        try {
        	
            TableUtils.createTable(connectionSource, History.class);
            TableUtils.createTable(connectionSource, Favorite.class);
            TableUtils.createTable(connectionSource, HiddenFile.class);
            
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>(); 
            switch(oldVersion) 
            {
              case 1: 
                  //allSql.add("alter table AdData add column `new_col` VARCHAR");
                  //allSql.add("alter table AdData add column `new_col2` VARCHAR");
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
        
    }

    public Dao<History, Integer> getHistoryDao() {
        if (null == historyDao) {
            try {
            	historyDao = getDao(History.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return historyDao;
    }
    
    public Dao<Favorite, Integer> getFavoritesDao() {
        if (null == favoritesDao) {
            try {
            	favoritesDao = getDao(Favorite.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return favoritesDao;
    }
    
    public Dao<HiddenFile, Integer> getHiddenFileDao() {
        if (null == hiddenDao) {
            try {
            	hiddenDao = getDao(HiddenFile.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return hiddenDao;
    }

}