package com.landa.dialog;

import java.io.File;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.landa.fileexplorermanager.R;
import com.landa.features.BrowseHandler;

public class FilePropertiesDialogFragment extends DialogFragment {

	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		String file_absolute_path = getArguments().getString("file_absolute_path");
		File f = new File(file_absolute_path);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vw = inflater.inflate(R.layout.file_properties, null);
        
        TextView fname = (TextView) vw.findViewById(R.id.file_name);
        fname.setText(f.getName());
        
        TextView fpath = (TextView) vw.findViewById(R.id.full_path);
        fpath.setText(f.getAbsolutePath());
        
        TextView fsize = (TextView) vw.findViewById(R.id.file_size);
        fsize.setText(Long.toString(f.length()).concat(" bytes."));
        
        ImageView imageView = (ImageView) vw.findViewById(R.id.file_image);
		imageView.setImageResource(
				BrowseHandler.getFileIconResourceId(f.getAbsolutePath()));
        
        TextView fcontains = (TextView) vw.findViewById(R.id.file_contains);
        if(f.isDirectory()) {
            File[] files = f.listFiles();
            int num_folders = 0;
            int num_files = 0;
            for(int i = 0; i < files.length; ++i) {
            	if(files[i].isDirectory())
            		++num_folders;
            	else
            		++num_files;
            }
        	fcontains.setText("Files: ".concat(Integer.toString(num_files)).concat(", Folders:")
        			.concat(Integer.toString(num_folders)));
        } else {
        	fcontains.setText("n/a");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        TextView fmodified = (TextView) vw.findViewById(R.id.file_modified);
        fmodified.setText(sdf.format(f.lastModified()));
        
        
        TextView freadable = (TextView) vw.findViewById(R.id.file_readable);
        freadable.setText(f.canRead() ? "Yes" : "No");
        
        TextView fwritable = (TextView) vw.findViewById(R.id.file_writable);
        fwritable.setText(f.canWrite() ? "Yes" : "No");
        
        TextView fhidden = (TextView) vw.findViewById(R.id.file_hidden);
        fhidden.setText(f.isHidden() ? "Yes" : "No");
        
        AlertDialog dialog = builder.create();
        
        dialog.setView(vw, 0, 0, 0, 0);

        return dialog;
    }
	
}
