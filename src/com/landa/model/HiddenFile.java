package com.landa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "hidden_files")
public class HiddenFile {
    
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String full_path;
    
    public HiddenFile() {
        // ORMLite needs a no-arg constructor 
    }
    public HiddenFile(Integer id, String full_path) {
        this.id = id;
        this.full_path = full_path;
    }
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getFull_path() {
		return full_path;
	}
	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}


}