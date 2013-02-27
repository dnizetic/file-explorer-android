package com.landa.datatypes;

import java.io.File;

public class ClipboardFile {
	
	public static final boolean STATUS_CUT = true;
	public static final boolean STATUS_COPY = false;
	
	
	File file;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	
	//copy - 0, cut - 1
	boolean status;
	
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public ClipboardFile(File f, boolean status) {
		this.file = f;
		this.status = status;
	}
	


}
