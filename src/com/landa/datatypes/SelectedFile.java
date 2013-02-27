package com.landa.datatypes;

import java.io.File;

import android.widget.TextView;

public class SelectedFile {
	

	File file;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	
	TextView tv;
	public TextView getTv() {
		return tv;
	}
	public void setTv(TextView tv) {
		this.tv = tv;
	}
	
	public SelectedFile(File f, TextView tv) {
		this.file = f;
		this.tv = tv;
	}

}
