package com.landa.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.dialog.HistoryFavoritesDialogFragment;
import com.landa.features.BrowseHandler;
import com.landa.features.FavoritesHandler;

//used by FavoritesDialogFragment, HistoryDialogFragment
public class DialogFileListAdapter extends BaseAdapter {
	private final Context context;
	private File[] data;
	private final String dialog_type;
	HistoryFavoritesDialogFragment d;

	public DialogFileListAdapter(Context context, File[] values, 
			String dialog_type, HistoryFavoritesDialogFragment d) {
		// super(context, R.layout.list_row, values);
		this.context = context;
		this.data = values;
		this.dialog_type = dialog_type;
		this.d = d;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.history_dialog_list_row_new,
				parent, false);

		
		TextView textView = (TextView) rowView.findViewById(R.id.h_file_name);
		TextView fullPath = (TextView) rowView.findViewById(R.id.h_full_path);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.file_image);

		final File f = data[position];
		textView.setText(f.getName());
		fullPath.setText(f.getAbsolutePath());
		imageView.setImageResource(BrowseHandler
				.getFileIconResourceId(f.getAbsolutePath()));
		
		rowView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				d.dismiss();
				
				File f = getItem(position);
				BrowseHandler bh = BrowseHandler.getInstance();
	
				if (f.isDirectory())
					bh.clearBackStack();
	
				if (bh.isSdCardUnmountedViewShown())
					bh.undisplaySdCardUnmountedView(f);
				else
					bh.openFile(f);
			}
		});
		
		if(dialog_type == "favorites") {
			rowView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					
					showDeleteConfirmationDialog(f);
					return false;
				}
	        });
		}

		
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
	
	
	
	private void showDeleteConfirmationDialog(final File f) {

		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Delete favorite?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								FavoritesHandler.deleteFavorite(f);
								
								ArrayList<File> files = new ArrayList<File>(Arrays.asList(data));
								files.remove(f);
								data = files.toArray(new File[files.size()]);
								
								notifyDataSetChanged();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				// Set your icon here
				.setTitle("Delete favorite?")
				.setIcon(R.drawable.warning);

		builder.show();

	}
	

}