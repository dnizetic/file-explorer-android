package com.landa.dialog;

import java.io.File;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

				
				File f = getFileClicked(((AlertDialog) dialog).getListView()
						.getChildAt(whichButton));

				
				BrowseHandler bh = BrowseHandler.getInstance();

				// clear back stack if folder - don't want to return (go up, not
				// back)
				// why not clear directly here?
				//if (f.isDirectory())
				//	bh.clear_back_stack_before_rendering = true;
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

	private File getFileClicked(View lw) {
		TextView fullPath = (TextView) lw.findViewById(R.id.h_full_path);
		String f_path = fullPath.getText().toString();

		File f = new File(f_path);

		return f;
	}



}