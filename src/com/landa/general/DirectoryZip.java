package com.landa.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
public class DirectoryZip
{
	//http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
	
    List<String> fileList;
    private static String OUTPUT_ZIP_FILE = "C:\\MyFile.zip";
	private static String SOURCE_FOLDER = "C:\\testzip";
	

    public DirectoryZip(){
    	fileList = new ArrayList<String>();
    }
 
    public static boolean main( String[] args )
    {
    	DirectoryZip appZip = new DirectoryZip();
    	appZip.generateFileList(new File(SOURCE_FOLDER));
    	return appZip.zipIt(OUTPUT_ZIP_FILE);
    }
 
    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public boolean zipIt(String zipFile){
 
     byte[] buffer = new byte[1024];
 
     try{
 
    	FileOutputStream fos = new FileOutputStream(zipFile);
    	ZipOutputStream zos = new ZipOutputStream(fos);
 
    	System.out.println("Output to Zip : " + zipFile);
 
    	for(String file : this.fileList){
 
    		System.out.println("File Added : " + file);
    		ZipEntry ze= new ZipEntry(file);
        	zos.putNextEntry(ze);
 
        	FileInputStream in = 
                       new FileInputStream(SOURCE_FOLDER + File.separator + file);
 
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
 
        	in.close();
    	}
 
    	zos.closeEntry();
    	//remember close it
    	zos.close();
 
    	System.out.println("Done");
    	
    	return true;
    }catch(IOException ex){
       ex.printStackTrace();
       
       return false;
    }
   }
 
    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    public void generateFileList(File node){
 
    	//add file only
	if(node.isFile()){
		fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
	}
 
	if(node.isDirectory()){
		String[] subNote = node.list();
		for(String filename : subNote){
			generateFileList(new File(node, filename));
		}
	}
 
    }
 
    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
    }
    
    
    
    //getters/setters
    public static String getOUTPUT_ZIP_FILE() {
		return OUTPUT_ZIP_FILE;
	}

	public static void setOUTPUT_ZIP_FILE(String oUTPUT_ZIP_FILE) {
		OUTPUT_ZIP_FILE = oUTPUT_ZIP_FILE;
	}

	public static String getSOURCE_FOLDER() {
		return SOURCE_FOLDER;
	}

	public static void setSOURCE_FOLDER(String sOURCE_FOLDER) {
		SOURCE_FOLDER = sOURCE_FOLDER;
	}
    
}