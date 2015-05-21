package com.landa.general;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

public class FileFinder {

	
	private String search_file;
	//add 2 Strings: file_size and file_type
	private String file_size;
	private String file_type;
	private ArrayList<File> matches = new ArrayList<File>();

	private void walk(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if(list != null && list.length > 0)
			for (File f : list) {
				if (f.isDirectory()) {
					
					walk(f.getAbsolutePath());
					
				}

				if(fileSizeMatch(f, this.file_size)
				&& fileTypeMatch(f, this.file_type)) {
					if(search_file == null)
						matches.add(f);
					else if(f.getName().toLowerCase().contains(search_file.toLowerCase()))
						matches.add(f);
				}
				
				
			}
	}

	//arg0 - file name we are searching for
	//arg1 - folder to start with
	public File[] main(String[] args) {
		if(args.length < 2) {
			return null;
		}
		
		this.search_file = args[0];
		this.file_size = args[2];
		this.file_type = args[3];
		
		//uncomment this
		//if(search_file == null && file_size == "Any" && file_type == "Any")
		//return matches.toArray();
		if(search_file == null
		&& file_size.equals("Any")
		&& file_type.equals("Any")) {
			return null;
		}

		walk(args[1]);

		return matches.toArray(new File[matches.size()]);
	}

	
	private boolean fileSizeMatch(File f, String size)
	{
		if(size.equals("Any"))
			return true;
		
		Long flength = f.length();
		
		Long MB_1 = 1024*1024L;
		
		Log.v("size", size);
		//Log.v("flength1", Long.toString(flength));
		//Log.v("flength2", Double.toString(0.5*MB_1));
		
		if(size.contains("0.5MB")) {
			Log.v("Equals", "Ok");
		}
		
		if(flength < 0.5*MB_1) {
			if(size.contains("0.5MB"))
				return true;
		} else if(flength < 1*MB_1) {
			if( size.contains("1MB"))
				return true;
		} else if(flength < 10*MB_1) {
			if(size.contains("10MB"))
				return true;
		} else if(flength < 25*MB_1) {
			if(size.contains("25MB"))
				return true;
		} else if(flength < 50*MB_1) {
			if(size.contains("50MB"))
				return true;
		} else if(flength > 50*MB_1) {
			if(size.contains(">"))
				return true;
		}
		return false;
	}
	
	private boolean fileTypeMatch(File f, String type)
	{
		if(type.equals("Any"))
			return true;
		
		String mime_type = General.getMimeType(f.getPath());

		if(mime_type == null)
			return false;
		
		Log.v("mime_type", mime_type);
		Log.v("type", type);
		
		if(mime_type.contains("audio") && type.contains("Audio")) {
			return true;
		} else if(mime_type.contains("image") && type.contains("Image")) {
			return true;
		} else if(mime_type.contains("text") && type.contains("Text")) {
			return true;
		} else if(mime_type.contains("video") && type.contains("Video")) {
			return true;
		} else if(mime_type.contains("application") && type.contains("Application")) {
			return true;
		}
		
		return false;
	}
}
