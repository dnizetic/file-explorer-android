package com.landa.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

import android.util.Log;
import android.webkit.MimeTypeMap;

public class General {

	// url = file path or whatever suitable URL you want.
	public static String getMimeType(String url) {
		String type = null;

		String exten = General.getFileExtension(url);

		if (exten != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(exten);
		}
		if (type == null)
			Log.v("return mime type:", "null");
		else
			Log.v("return mime type:", type);
		return type;
	}

	public static String getFileExtension(String file) {

		int dotposition = file.lastIndexOf(".");
		if (dotposition != -1) {
			String ext = file.substring(dotposition + 1, file.length());
			return ext;
		}
		return null;
	}

	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if(targetLocation.getAbsolutePath().contains(sourceLocation.getAbsolutePath()))
			throw new IOException("Invalid path (cannot paste a folder into itself)");
		
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new IOException("Cannot create directory "
						+ targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			// make sure the directory we plan to store the recording in exists
			File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create directory "
						+ directory.getAbsolutePath());
			}

			// call self

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static Comparator<File> fileSorter = new Comparator<File>() {
		public int compare(File f1, File f2) {

			String fName1 = f1.getName().toUpperCase();
			String fName2 = f2.getName().toUpperCase();

			return fName1.compareTo(fName2);
			//descending: full_path2.compareTo(full_path1);
		}
	};
	

	public static char lastCharOfString(String s)
	{
		return s.substring(s.length() - 1).toCharArray()[0];
	}
}
