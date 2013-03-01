package com.landa.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.fileexplorermanager.R;
import com.landa.adapter.ClipboardFileListAdapter;
import com.landa.features.OperationsHandler;

//used by OperationsHandler
public class ClipboardDialogFragment extends DialogFragment {

	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vw = inflater.inflate(R.layout.clipboard_title_view, null);
        builder.setCustomTitle(vw);

        
        Log.v("ClipboardDialogFragment clipboard_files #", 
        		Integer.toString(OperationsHandler.getClipboard_files().size()));
        
        
        ClipboardFileListAdapter adapter = new ClipboardFileListAdapter(getActivity(), 
        		OperationsHandler.getFilesFromClipboard()
        			.toArray(new File[OperationsHandler.getClipboard_files().size()]));
        
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	//onclick clipboard item 
            }
        });
 
        
        return builder.create();
    }
	

	
	
}