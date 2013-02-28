package com.landa.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.adapter.FileListAdapter;
import com.landa.database.DatabaseManager;
import com.landa.features.BrowseHandler;
import com.landa.features.HiddenFileHandler;
import com.landa.features.OperationsHandler;
import com.landa.model.HiddenFile;


public class ContentFragment extends ListFragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
    	
    	final String pth =  getArguments().getString("file_absolute_path");
    	File f = new File(pth);
    	
		View view = inflater.inflate(R.layout.content_view, container, false);
		ListView list = (ListView) view.findViewById(android.R.id.list);
		
		File[] files = f.listFiles();
		
		//filter out hidden files
		if(HiddenFileHandler.fileContainsHiddenFiles(f)) {
			//n^2 here: remove hidden files from the list
			files = filterHiddenFiles(files);
		}
		
		if (files != null && files.length > 0) {
			
			FileListAdapter adapter = new FileListAdapter(getActivity(), files);
			list.setAdapter(adapter);
			hideEmptyFolderMessage();
			
		} else { 
			
			list.setAdapter(null);
			showEmptyFolderMessage();
		}
    	
        return view;
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
        
        //this was changed to final: 27.2.
        ListView list = (ListView) view.findViewById(android.R.id.list);
        
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				OperationsHandler oph = OperationsHandler.getInstance();
				if(oph.isSelectActive()) {
					oph.selectFile(new File(getFileFullPath(view)), view);
					
					Log.v("TAG", "Getting FileListAdapter");
					FileListAdapter ad = (FileListAdapter) parent.getAdapter();
					View v = ad.getView(position, view, parent);
					
					TextView fileName = (TextView) v.findViewById(R.id.file_name);
					Log.v("file_name", fileName.getText().toString());
					fileName.setTextColor(Color.BLUE);
					
					/*View v = parent.getChildAt(position);
					TextView fileName = (TextView) v.findViewById(R.id.file_name);
					Log.v("file_name", fileName.getText().toString());
					fileName.setTextColor(Color.BLUE);*/
					
					
					//.setBackgroundColor(Color.GREEN);
					//ad.notifyDataSetChanged();
					
				} else {
					BrowseHandler bh = BrowseHandler.getInstance();
					bh.openFile(new File(getFileFullPath(view)));
				}
			}
		}); 
		
		
		//set onItemLongClickListener
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	        
	        	openOperationsDialog(view);
	            return false;
	        }
	    });
        
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
    
    
}

