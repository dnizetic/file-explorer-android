package com.landa.dialog;

import java.io.File;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.adapter.DialogFileListAdapter;
import com.landa.features.BrowseHandler;
import com.landa.features.FavoritesHandler;
import com.landa.features.HistoryHandler;
import com.landa.general.General;

//Favorites/History dialog
public class GeneralDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// possible values: "history", "favorites"
		final String dialog_type = getArguments().getString("dialog_type");
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View vw;
		
		// create title view
		if (dialog_type == "history")
			vw = inflater.inflate(R.layout.history_title_view, null);
		else if (dialog_type == "favorites") // favorites
			vw = inflater.inflate(R.layout.favorites_title_view, null);
		else
			vw = null;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCustomTitle(vw);

		File[] files;
		if (dialog_type == "history") {
			
			HistoryHandler hh = new HistoryHandler(getActivity());
			files = hh.getAllHistoriesAsFiles();
			
		} else if (dialog_type == "favorites") {

			FavoritesHandler fh = new FavoritesHandler(getActivity());
			files = fh.getAllFavoritesAsFiles();
			
			//sort ascending
			Arrays.sort(files, 0, files.length, General.fileSorter);

			
		} else {
			files = null;
		}

		DialogFileListAdapter adapter = new DialogFileListAdapter(getActivity(), files);

		// onClick Favorites/History list item
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				//wrong
				//File f = getFileClicked(((AlertDialog) dialog).getListView()
				//			.getChildAt(whichButton));

				//correct
				File f = (File) ((AlertDialog) dialog).getListView()
							.getAdapter().getItem(whichButton);
				
				Log.v("f", f.getAbsolutePath());
				//Log.v("f2", f2.getAbsolutePath());
				
				BrowseHandler bh = BrowseHandler.getInstance();

				if (f.isDirectory())
					bh.clearBackStack();

				if (bh.isSdCardUnmountedViewShown())
					bh.undisplaySdCardUnmountedView(f);
				else
					bh.openFile(f);
					
			}
		});

		return builder.create();
	}

}