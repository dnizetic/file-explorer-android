package com.landa.dialog;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileexplorermanager.R;
import com.landa.adapter.OperationsAdapter;
import com.landa.datatypes.PasteFile;
import com.landa.features.BrowseHandler;
import com.landa.features.OperationsHandler;

public class OperationsDialogFragment extends DialogFragment {

	// operation names
	public static final String OP_CUT = "Cut";
	public static final String OP_COPY = "Copy";
	public static final String OP_RENAME = "Rename";
	public static final String OP_DELETE = "Delete";
	public static final String OP_SELECT_ALL = "Select all";
	public static final String OP_SELECT_INVERSE = "Select inverse";
	public static final String OP_CREATE_SHORTCUT = "Create shortcut";
	public static final String OP_FAVORITE = "Favorite";
	public static final String OP_HIDE = "Hide"; //8
	public static final String OP_COMPRESS = "Compress"; //9
	public static final String OP_SET_AS_HOME = "Set as home"; //10
	public static final String OP_PROPERTIES = "Properties"; //11

	ArrayList<String> operationsInfo = new ArrayList<String>();

	OperationsHandler opHandler;
	private File f;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		opHandler = OperationsHandler.getInstance();
		
		final String operation_type = getArguments().getString("operation_type");
		final String file_absolute_path = getArguments().getString("file_absolute_path");
		
		if(file_absolute_path != null)
			this.f = new File(file_absolute_path);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v;
		
		ArrayList<String> operationsList;
		if (operation_type.equals("single_file")) {
			v = getSingleFileDialogTitle();
			operationsList = getSingleFileOperationsList();
		} else if (operation_type.equals("multiple_files")) {
			v = getDefaultDialogTitle();
			operationsList = getMultipleFilesOperationsList();
		} else {
			v = getDefaultDialogTitle();
			operationsList = getDefaultOperationsList();
		}

		OperationsAdapter adap = new OperationsAdapter(getActivity(),
				operationsList);

		ListView lv = (ListView) v.findViewById(R.id.op_list_view);
		
		lv.setAdapter(adap);
		
		// upon operation click, execute operation
		/*builder.setAdapter(adap, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				//String op_name = getOperationName(((AlertDialog) dialog)
				//		.getListView()
				//		.getChildAt(whichButton));
				Log.v("whichButton", Integer.toString(whichButton));
				//Log.v("operation", op_name);
				
				if (operation_type.equals("single_file")) {
					executeSingleFileOperationTemp(whichButton);
				} else if(operation_type.equals("multiple_files")) {
					executeMultipleFilesOperation(whichButton);
				} else {
					executeDefaultFilesOperation(whichButton);
				}
			}
		});*/
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (operation_type.equals("single_file")) {
					executeSingleFileOperationTemp(position);
				} else if(operation_type.equals("multiple_files")) {
					executeMultipleFilesOperation(position);
				} else {
					executeDefaultFilesOperation(position);
				}
				
			}
		}); 
		
		AlertDialog dialog = builder.create();
		
		dialog.setView(v, 0, 0, 0, 0);
		
		return dialog;
	}

//	private void setSingleFileDialogTitle(AlertDialog.Builder builder) {
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//
//		View vw = inflater.inflate(R.layout.operations_title_view, null);
//		// set the file name & corresponding image
//		TextView title = (TextView) vw.findViewById(R.id.operations_file_name);
//		title.setText(" ".concat(f.getName()));
//
//		ImageView img = (ImageView) vw.findViewById(R.id.operations_file_image);
//		
//		//BrowseHandler.executeBindFileTypeToImage(f.getAbsolutePath(), img);
//		img.setImageResource(BrowseHandler.getFileIconResourceId(f.getAbsolutePath()));
//		
//		builder.setCustomTitle(vw);
//	}
	
	private View getSingleFileDialogTitle() {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View vw = inflater.inflate(R.layout.operations_view, null);
		// set the file name & corresponding image
		TextView title = (TextView) vw.findViewById(R.id.operations_file_name);
		title.setText(" ".concat(f.getName()));

		ImageView img = (ImageView) vw.findViewById(R.id.operations_file_image);
		
		//BrowseHandler.executeBindFileTypeToImage(f.getAbsolutePath(), img);
		img.setImageResource(BrowseHandler.getFileIconResourceId(f.getAbsolutePath()));
		
		return vw;
	}

//	private void setDefaultDialogTitle(AlertDialog.Builder builder) {
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//
//		View vw = inflater.inflate(R.layout.operations_title_view, null);
//
//		builder.setCustomTitle(vw);
//	}
	
	private View getDefaultDialogTitle() {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View vw = inflater.inflate(R.layout.operations_view, null);

		return vw;
	}
	

	private ArrayList<String> getSingleFileOperationsList() {
		//order important //why?
		operationsInfo.add(OP_CUT);
		operationsInfo.add(OP_COPY);
		operationsInfo.add(OP_RENAME);
		operationsInfo.add(OP_DELETE);
		operationsInfo.add(OP_SELECT_ALL);
		operationsInfo.add(OP_CREATE_SHORTCUT);
		operationsInfo.add(OP_FAVORITE);
		operationsInfo.add(OP_HIDE);
		operationsInfo.add(OP_COMPRESS);
		operationsInfo.add(OP_SET_AS_HOME);
		operationsInfo.add(OP_PROPERTIES);

		return operationsInfo;
	}

	private void executeSingleFileOperationTemp(int op_id) {
		
		switch(op_id + 1) {
			case 1: //cut
				opHandler.cut(f);
				break;
			case 2: //copy
				opHandler.copy(f);
				break;
			case 3: //rename
				showRenameDialog();
				break;
			case 4: //delete
				showDeleteConfirmationDialog();
				break;
			case 5: //select all
				opHandler.selectAll();
				break;
			case 6: //add shortcut
				opHandler.addShortcut(f);
				break;
			case 7: //add favorite
				opHandler.addFavorite(f);
				break;
			case 8: //hide file
				opHandler.hide(f);
				break;
			case 9: //compress file
				opHandler.compressFile(f);
				break;
			case 10: //set dir as home
				opHandler.setDirectoryAsHome(f);
				break;
			case 11: //show file properties
				opHandler.showFileProperties(f);
				break;
		}

		dismiss();
	}
	
	

	private ArrayList<String> getMultipleFilesOperationsList() {
		operationsInfo.add(OP_CUT);
		operationsInfo.add(OP_COPY);
		operationsInfo.add(OP_DELETE);
		operationsInfo.add(OP_SELECT_ALL);
		//operationsInfo.add(OP_SELECT_INVERSE);
		//operationsInfo.add(OP_FAVORITE);
		operationsInfo.add(OP_HIDE);
		//operationsInfo.add(OP_COMPRESS);
		//operationsInfo.add(OP_PROPERTIES);

		return operationsInfo;
	}
	
	
	private void executeMultipleFilesOperation(int op_id) {

		
		switch(op_id + 1) {
			case 1: //cut
				opHandler.copyCutSelectedFiles(PasteFile.STATUS_CUT);
				break;
			case 2: //copy
				opHandler.copyCutSelectedFiles(PasteFile.STATUS_COPY);
				break;
			case 3: //delete
				opHandler.deleteSelectedFiles();
				break;
			case 4: //select all
				opHandler.selectAll();
				break;
			case 5: //hide
				opHandler.hideSelectedFiles();
				break;
		}
		dismiss();
	}


	// delete prompt
	private void showDeleteConfirmationDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								
								if(!opHandler.delete(f)) {
									Toast.makeText(getActivity(), 
											"Error while deleting.", Toast.LENGTH_LONG).show();
								}
								
								// refresh view
								BrowseHandler bh = BrowseHandler.getInstance();
								bh.refreshContent();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				// Set your icon here
				.setTitle("Delete ".concat(f.getName()))
				.setIcon(R.drawable.warning);

		builder.show();

	}


	private void showRenameDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						EditText lw = (EditText) ((AlertDialog) dialog)
								.findViewById(R.id.input_1);
						opHandler.rename(f, lw.getText().toString());
						// refresh view
						BrowseHandler bh = BrowseHandler.getInstance();
						bh.refreshContent();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setTitle("Rename ".concat(f.getName()));

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View vw = inflater.inflate(R.layout.single_input_view, null);

		builder.setView(vw);

		EditText et = (EditText) vw.findViewById(R.id.input_1);
		et.setText(f.getName());
		et.selectAll();

		builder.show();
	}
	
	
	

	private ArrayList<String> getDefaultOperationsList() {
		operationsInfo.add(OP_SELECT_ALL);
		operationsInfo.add(OP_SET_AS_HOME);

		return operationsInfo;
	}
	
	private void executeDefaultFilesOperation(int op_id) {

		switch(op_id + 1) {
			case 1: //select all
				opHandler.selectAll();
				break;
			case 2: //set as home
				opHandler.setDirectoryAsHome(f);
				break;
		}
		dismiss();
	}

}