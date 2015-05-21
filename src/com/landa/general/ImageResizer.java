package com.landa.general;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageResizer {


	public static byte[] resizeImage(File f)
	{
		byte[] imageData = null;
		
        try {

            final int THUMBNAIL_SIZE = 64;

            String fileName = f.getAbsolutePath();
            
            FileInputStream fis = new FileInputStream(fileName);
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();

            return imageData;
            
        }
        catch(Exception ex) {

        }
        return null;
	}
	
}
