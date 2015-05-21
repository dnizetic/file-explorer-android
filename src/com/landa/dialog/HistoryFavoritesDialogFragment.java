package com.landa.dialog;

import java.io.File;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.landa.fileexplorermanager.R;
import com.landa.adapter.DialogFileListAdapter;
import com.landa.features.FavoritesHandler;
import com.landa.features.HistoryHandler;
import com.landa.general.General;

//Favorites/History dialog
public class HistoryFavoritesDialogFragment extends DialogFragment {

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// possible values: "history", "favorites"
		final String dialog_type = getArguments().getString("dialog_type");
		
		LayoutInflater inflater = getActivity().getLayoutInflater();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		File[] files;
		if (dialog_type == "history") {
			
			files = HistoryHandler.getAllHistoriesAsFiles();
			
		} else if (dialog_type == "favorites") {

			FavoritesHandler fh = new FavoritesHandler(getActivity());
			files = fh.getAllFavoritesAsFiles();
			
			//sort ascending
			Arrays.sort(files, 0, files.length, General.fileSorter);

		} else {
			files = null;
		}

		
		View v =  inflater.inflate((dialog_type == "history") ? 
				R.layout.history_view : R.layout.favorites_view, null);
		
		if(files != null && files.length > 0) {
			
			ListView lv = (ListView) v.findViewById(R.id.file_list_view);
			
			DialogFileListAdapter adapter = new DialogFileListAdapter(getActivity(), 
					files, dialog_type, this);
			
			lv.setAdapter(adapter);
			
		} else {
			
			TextView tv = (TextView) v.findViewById(R.id.msg);
			tv.setVisibility(View.VISIBLE);
			
		}
		
		AlertDialog dialog = builder.create();
		
		//T-Mobile MOVE had black gaps: following line needed
		//http://stackoverflow.com/questions/10433764/alertdialog-how-to-remove-black-borders-above-and-below-view
		dialog.setView(v, 0, 0, 0, 0);
		
		return dialog;
	}
	
}