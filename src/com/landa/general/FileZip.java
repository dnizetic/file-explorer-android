package com.landa.general;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
public class FileZip 
{	
	
    private static String OUTPUT_ZIP_FILE = "C:\\MyFile.zip";
    private static String SOURCE_FILE = "C:\\testzip";


	public static boolean main( String[] args )
    {
    	byte[] buffer = new byte[1024];
 
    	try{
 
    		FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);
    		ZipOutputStream zos = new ZipOutputStream(fos);
    		ZipEntry ze= new ZipEntry("spy.log");
    		zos.putNextEntry(ze);
    		FileInputStream in = new FileInputStream(SOURCE_FILE);
 
    		int len;
    		while ((len = in.read(buffer)) > 0) {
    			zos.write(buffer, 0, len);
    		}
 
    		in.close();
    		zos.closeEntry();
 
    		//remember close it
    		zos.close();
 
    		System.out.println("Done");
    		return true;
 
    	} catch(IOException ex){
    	   ex.printStackTrace();
    	   
    	   return false;
    	}
    }
    
    //getters/setters
    
    
	public static String getOUTPUT_ZIP_FILE() {
		return OUTPUT_ZIP_FILE;
	}


	public static void setOUTPUT_ZIP_FILE(String oUTPUT_ZIP_FILE) {
		OUTPUT_ZIP_FILE = oUTPUT_ZIP_FILE;
	}
    public static String getSOURCE_FILE() {
		return SOURCE_FILE;
	}

	public static void setSOURCE_FILE(String sOURCE_FILE) {
		SOURCE_FILE = sOURCE_FILE;
	}


}