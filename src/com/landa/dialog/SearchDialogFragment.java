package com.landa.dialog;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.landa.fileexplorermanager.R;
import com.landa.features.BrowseHandler;
import com.landa.fragment.ContentFragmentNew;
import com.landa.general.FileFinder;
import com.landa.general.General;

public class SearchDialogFragment extends DialogFragment {

	private File[] search_results;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vw = inflater.inflate(R.layout.search_view, null);
        
        
        builder
	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {
						
						//add this
						if(BrowseHandler.search_displayed)
							getActivity().onBackPressed();
						
						String file_search_name = ((EditText) ((AlertDialog)dialog).findViewById(R.id.file_name))
								.getText().toString();
						
						String search_in = ((Spinner) ((AlertDialog)dialog).findViewById(R.id.search_in_spinner))
								.getSelectedItem().toString();
						
						String file_size = ((Spinner) ((AlertDialog)dialog).findViewById(R.id.file_size_spinner))
								.getSelectedItem().toString();
						
						String file_type = ((Spinner) ((AlertDialog)dialog).findViewById(R.id.file_type_spinner))
								.getSelectedItem().toString();
						
						new SearchTask(getActivity())
								.execute(
									file_search_name,
									search_in,
									file_size,
									file_type
								);
						
					}
				})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
				}
			});
        
        populateSearchInSpinner(vw);
        
        AlertDialog dialog = builder.create();
        
        dialog.setView(vw, 0, 0, 0, 0);
        
        return dialog;
    }

	
	
	private void populateSearchInSpinner(View vw)
	{
		
        File f = new File(BrowseHandler.current_path);
        Spinner sp = (Spinner) vw.findViewById(R.id.search_in_spinner);
        
        File[] files = f.listFiles(new FileFilter() {
		    public boolean accept(File f) {
		    	return f.isDirectory() && !f.getName().startsWith(".");
		    }
		});
        
        String[] fnames = new String[files.length + 1];
        fnames[0] = "Current path";
        for(int i = 0; i < files.length; ++i)
        	fnames[i + 1] = files[i].getName();
        
        Arrays.sort(fnames, 1, fnames.length, General.stringSorter);
        
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, fnames);
        
        sp.setAdapter(spinnerArrayAdapter);
	}


	
	
	private AlertDialog showSearchDisplayConfirmationDialog(final Context ctx, final FragmentActivity fac) {
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		
		builder.setMessage("Search finished. Display results?")
				.setCancelable(true)
				.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							displaySearchResults(fac);
						}
					})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setTitle("Search")
				.setIcon(R.drawable.warning);

		return builder.create();

	}
	
	private ContentFragmentNew createContentFragmentNewWithArguments()
	{
		
		ContentFragmentNew cf = new ContentFragmentNew();
		
		String fnames[] = new String[search_results.length];
		for(int i = 0; i < fnames.length; ++i)
			fnames[i] = search_results[i].getAbsolutePath();
		
		Bundle bdl = new Bundle(1);
		bdl.putStringArray("files", fnames);
	
		cf.setArguments(bdl);
		
		return cf;
	}
	
	
	private void displaySearchResults(FragmentActivity fac)
	{
		ContentFragmentNew cf = createContentFragmentNewWithArguments();

		FragmentTransaction transaction = fac.getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.contentFragment, cf);
		transaction.addToBackStack(null);
		
		//in case user clicks a folder (so back doesnt go back to search results)
		BrowseHandler.getInstance()
			.clear_back_stack_before_rendering = true;
		
		transaction.commit();
		
		BrowseHandler.search_displayed = true;
		
	}
	
	
	private class SearchTask extends AsyncTask<String, Void, Bitmap> {
		
		private Context ctx;
		private FragmentActivity fac;
		private AlertDialog dialog;
		
	    public SearchTask(Context ctx) {
	        this.ctx = ctx;
	        this.fac = (FragmentActivity) ctx;
	        dialog = showSearchDisplayConfirmationDialog(this.ctx, this.fac);
	    }
		
		
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected Bitmap doInBackground(String... args) {
	        
	    	String file_to_search = args[0];
	    	String search_in = args[1];
	    	String file_size = args[2];
	    	String file_type = args[3];
	    	
			search_results = getSearchResults(
					file_to_search, 
					search_in,
					file_size,
					file_type
			);
	    	
	    	return null;
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Bitmap result) {
	    	
			if(search_results != null && search_results.length > 0) {
				
				dialog.show();
				
			} else {
				
				Toast.makeText(this.ctx, "No results to show.",
						Toast.LENGTH_LONG).show();
				
			}
	    }
	}
	
	
	
	private File[] getSearchResults(String search_file_name, String search_in,
			String file_size, String file_type)
	{
		
		String search_file = search_file_name;
		
		//add following line
		String start_path = search_in.equals("Current path") ? BrowseHandler.current_path : 
			BrowseHandler.current_path.concat("/".concat(search_in));
		
		
		//add two args: file_size, file_type
		String[] args = { search_file, start_path, file_size, file_type };
		
		FileFinder ff = new FileFinder();
		File[] matches = ff.main(args);
		
		for(int i = 0; i < matches.length; ++i)
			Log.v("search_results", matches[i].getAbsolutePath());
		
		return matches;
	}
	
	
}
