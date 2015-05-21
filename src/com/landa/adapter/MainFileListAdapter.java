package com.landa.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.landa.fileexplorermanager.R;
import com.landa.datatypes.SelectedFile;
import com.landa.features.BrowseHandler;
import com.landa.features.OperationsHandler;
import com.landa.general.ImageResizer;

public class MainFileListAdapter extends BaseAdapter {
	private final Context context;
	private SelectedFile[] data; 

	public SelectedFile[] getData() {
		return data;
	}
	public void setData(SelectedFile[] data) {
		this.data = data;
	}

	public MainFileListAdapter(Context context, SelectedFile[] values) {
		this.context = context;
		this.data = values;
	}

	private void refill(SelectedFile[] new_data) {
		
		data = new_data;
	    notifyDataSetChanged();
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.list_row, parent, false);
		
		File f = data[position].getFile();
		
		TextView textView = (TextView) rowView.findViewById(R.id.file_name);
		TextView fullPath = (TextView) rowView.findViewById(R.id.full_path);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.file_image);
		
		if(data[position].isSelected()) {
			textView.setTextColor(Color.BLUE);
		}
		
		textView.setText(f.getName());
		fullPath.setText(f.getAbsolutePath());
		
		
		int file_icon_res_id = BrowseHandler.getFileIconResourceId(f.getAbsolutePath());
		/*if(file_icon_res_id == R.drawable.image
		&& PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean("show_image_thumbnails", true)) {
			
			imageView.setImageBitmap(getImageThumbnail(f));
			
		} else */
			imageView.setImageResource(file_icon_res_id);
		

		return rowView;
	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	
    public void refillAdapterData()
    {
    	refill(getUpdatedSelectedFiles());
    }
    
    private Bitmap getImageThumbnail(File image)
    {
		byte[] thumbnail_data = ImageResizer.resizeImage(image);
		Bitmap bmp = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);
		return bmp;
    }
    
    
    private SelectedFile[] getUpdatedSelectedFiles()
    {
    	OperationsHandler oph = OperationsHandler.getInstance();
    	
		//get selected files
		ArrayList<File> selected_files = oph.getSelected_files();
		
		Log.v("selected_files length", Integer.toString(selected_files.size()));
		
		ArrayList<SelectedFile> selected_files_temp = new ArrayList<SelectedFile>();
		//assemble SelectedFileTemp
		for(int i = 0; i < data.length; ++i) {
			SelectedFile f = new SelectedFile();
			f.setFile(data[i].getFile());
			
			f.setSelected(false);
			for(int j = 0; j < selected_files.size(); ++j) {
				if(selected_files.get(j).getAbsolutePath().equals(f.getFile().getAbsolutePath())) 
					f.setSelected(true);
			}
			
			selected_files_temp.add(f);
		}
		
		return selected_files_temp.toArray(new SelectedFile[data.length]);
    }

}