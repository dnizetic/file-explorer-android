package com.landa.adapter;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.features.BrowseHandler;

//used by FavoritesDialogFragment, HistoryDialogFragment
public class DialogFileListAdapter extends BaseAdapter {
	private final Context context;
	private final File[] data;

	public DialogFileListAdapter(Context context, File[] values) {
		// super(context, R.layout.list_row, values);
		this.context = context;
		this.data = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_dialog_list_row,
				parent, false);

		TextView textView = (TextView) rowView.findViewById(R.id.h_file_name);
		TextView fullPath = (TextView) rowView.findViewById(R.id.h_full_path);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.file_image);

		File f = data[position];
		textView.setText(f.getName());
		fullPath.setText(f.getAbsolutePath());
		imageView.setImageResource(BrowseHandler
				.getFileIconResourceId(f.getAbsolutePath()));
		
		
		return rowView;

	}

	public int getCount() {
		return data.length;
	}

	public File getItem(int position) {
		return data[position];
	}

	public long getItemId(int position) {
		return position;
	}

}