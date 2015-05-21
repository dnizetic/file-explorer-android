package com.landa.fragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.landa.fileexplorermanager.R;
import com.landa.adapter.MainFileListAdapter;
import com.landa.database.DatabaseManager;
import com.landa.datatypes.SelectedFile;
import com.landa.features.BrowseHandler;
import com.landa.features.HiddenFileHandler;
import com.landa.features.OperationsHandler;
import com.landa.general.General;
import com.landa.model.HiddenFile;


public class ContentFragment extends ListFragment {
	
	
	private File[] files;
	private MainFileListAdapter contentAdapter;
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
		
    	
    	final String pth =  getArguments().getString("file_absolute_path");
    	File f = new File(pth);
		
		View view = inflater.inflate(R.layout.content_view, container, false);
		ListView list = (ListView) view.findViewById(android.R.id.list);

	
		if(!prefsShowHiddenFiles()) {
			files = f.listFiles(new FilenameFilter() {
			    public boolean accept(File directory, String fileName) {
			    	return !fileName.startsWith(".");
			    }
			});
		} else {
			files = f.listFiles();
		}
		
		if (files != null && files.length > 0) {
			
			files = orderFoldersFirstFilesAfter();
			
			//filter out hidden files
			if(HiddenFileHandler.fileContainsHiddenFiles(f)) {
				//n^2 here: remove hidden files from the list
				files = filterHiddenFiles(files);
			}
			
			
			contentAdapter = new MainFileListAdapter(getActivity(), 
					convertFilesToSelectedFiles());	
		
			list.setAdapter(contentAdapter);
			hideEmptyFolderMessage();
			
		} else { 
			
			list.setAdapter(null);
			showEmptyFolderMessage();
		}
    	
		//view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT - 100));
		
        return view;
    }
    
	
	private File[] orderFoldersFirstFilesAfter()
	{
		ArrayList<File> folders_only = new ArrayList<File>();
		ArrayList<File> files_only = new ArrayList<File>();
		
		for(int i = 0; i < files.length; ++i) {
			
			if(files[i].isDirectory())
				folders_only.add(files[i]);
			else
				files_only.add(files[i]);
		}
		
		File folders_[] = folders_only.toArray(new File[folders_only.size()]);
		File files_[] = files_only.toArray(new File[files_only.size()]);
		
		Arrays.sort(folders_, 0, folders_.length, General.fileSorter);
		Arrays.sort(files_, 0, files_.length, General.fileSorter);
		
		ArrayList<File> merged = new ArrayList<File>();
		for(int i = 0; i < folders_.length; ++i)
			merged.add(folders_[i]);
		for(int i = 0; i < files_.length; ++i)
			merged.add(files_[i]);
		
		
		File[] fin = merged.toArray(new File[merged.size()]);
		
		return fin;
	}
	
	private boolean prefsShowHiddenFiles()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		return prefs.getBoolean("show_hidden_files", false);
	}
	
    private void hideEmptyFolderMessage()
    {
    	TextView emptyFolder = (TextView) getActivity().findViewById(R.id.message);
    	
    	emptyFolder.setVisibility(View.GONE);
    }
    
    private void showEmptyFolderMessage()
    {
    	TextView emptyFolder = (TextView) getActivity().findViewById(R.id.message);
    	
    	emptyFolder.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated");
        
        final ListView list = (ListView) view.findViewById(android.R.id.list);
        
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				OperationsHandler oph = OperationsHandler.getInstance();
				if(oph.isSelectActive()) {
					oph.selectFile(new File(getFileFullPath(view)));
					
					contentAdapter.refillAdapterData();
					
				} else {
					BrowseHandler bh = BrowseHandler.getInstance();
					bh.openFile(new File(getFileFullPath(view)));
				}
			}
		}); 
		
		
		//set onItemLongClickListener
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	        
	    		if(!OperationsHandler.getInstance().isSelectActive())
	    			openOperationsDialog(view);
	        	
	            return false;
	        }
	    });
		
		//view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT - 100));
    }

    
    private String getFileFullPath(View view)
    {
		TextView fullPath = (TextView) view.findViewById(R.id.full_path);
		String f_path = fullPath.getText().toString();
		return f_path;
    }
    
    private void openOperationsDialog(View view)
    {
    	OperationsHandler oph = OperationsHandler.getInstance();
    	TextView clickedFileName = (TextView) view.findViewById(R.id.full_path);
    	oph.openOperationsDialog(new File(clickedFileName.getText().toString()));
    }
    
    private File[] filterHiddenFiles(File[] output_files)
    {
    	DatabaseManager mgr = DatabaseManager.getInstance();
    	
    	List<HiddenFile> hidden_files = mgr.getAllHiddenFiles();
    	ArrayList<File> filtered_files = new ArrayList<File>();
    	
    	for(int i = 0; i < output_files.length; ++i) {
    		
    		File f1 = output_files[i];
    		boolean match = false;
    		
    		for(int j = 0; j < hidden_files.size(); ++j) {
    			
    			File f2 = new File(hidden_files.get(j).getFull_path());
    			
    			Log.v("File1", f1.getAbsolutePath());
    			Log.v("File2", f2.getAbsolutePath());
    			
				if(f1.getAbsolutePath().compareTo(f2.getAbsolutePath()) == 0) {
					match = true;
					break;
				}
    		}
    		if(match == false)
    			filtered_files.add(f1);
    	}
    	
    	return filtered_files.toArray(new File[filtered_files.size()]);
    }
    
    
    private SelectedFile[] convertFilesToSelectedFiles()
    {
    	SelectedFile[] data = new SelectedFile[files.length];
    	
    	for(int i = 0; i < files.length; ++i) {
    		SelectedFile f = new SelectedFile();
    		
    		f.setFile(files[i]);
    		f.setSelected(false);
    		
    		data[i] = f;
    	}
    	
    	return data;
    }
    
}

