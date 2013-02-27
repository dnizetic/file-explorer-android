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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.adapter.OperationsAdapter;
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
	public static final String OP_HIDE = "Hide";
	public static final String OP_COMPRESS = "Compress";
	public static final String OP_SET_AS_HOME = "Set as home";
	public static final String OP_PROPERTIES = "Properties";

	ArrayList<String> operationsInfo = new ArrayList<String>();

	private OperationsHandler opHandler;
	private File f;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		opHandler = OperationsHandler.getInstance();
		
		final String operation_type = getArguments().getString("operation_type");
		final String file_absolute_path = getArguments().getString("file_absolute_path");
		
		if(file_absolute_path != null)
			this.f = new File(file_absolute_path);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		ArrayList<String> operationsList;
		if (operation_type == "single_file") {
			setSingleFileDialogTitle(builder);
			operationsList = getSingleFileOperationsList();
		} else if (operation_type == "multiple_files") {
			setDefaultDialogTitle(builder);
			operationsList = getMultipleFilesOperationsList();
		} else {
			setDefaultDialogTitle(builder);
			operationsList = getDefaultOperationsList();
		}

		OperationsAdapter adap = new OperationsAdapter(getActivity(),
				operationsList);

		// upon operation click, execute operation
		builder.setAdapter(adap, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				String op_name = getOperationName(((AlertDialog) dialog)
						.getListView().getChildAt(whichButton));
				if (operation_type == "single_file") {
					executeSingleFileOperation(op_name);
				} else {
					executeMultipleFilesOperation(op_name);
				}
			}
		});

		return builder.create();
	}

	private String getOperationName(View lw) {
		TextView op = (TextView) lw.findViewById(R.id.operation_name);
		String op_name = op.getText().toString();

		return op_name;
	}

	private void setSingleFileDialogTitle(AlertDialog.Builder builder) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View vw = inflater.inflate(R.layout.operations_title_view, null);
		// set the file name & corresponding image
		TextView title = (TextView) vw.findViewById(R.id.operations_file_name);
		title.setText(" ".concat(f.getName()));

		ImageView img = (ImageView) vw.findViewById(R.id.operations_file_image);
		
		//BrowseHandler.executeBindFileTypeToImage(f.getAbsolutePath(), img);
		img.setImageResource(BrowseHandler.getFileIconResourceId(f.getAbsolutePath()));
		
		builder.setCustomTitle(vw);
	}

	private void setDefaultDialogTitle(AlertDialog.Builder builder) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View vw = inflater.inflate(R.layout.operations_title_view, null);

		builder.setCustomTitle(vw);
	}

	private ArrayList<String> getSingleFileOperationsList() {
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

	private ArrayList<String> getMultipleFilesOperationsList() {
		operationsInfo.add(OP_CUT);
		operationsInfo.add(OP_COPY);
		operationsInfo.add(OP_DELETE);
		operationsInfo.add(OP_SELECT_ALL);
		operationsInfo.add(OP_SELECT_INVERSE);
		operationsInfo.add(OP_FAVORITE);
		operationsInfo.add(OP_HIDE);
		operationsInfo.add(OP_COMPRESS);
		operationsInfo.add(OP_PROPERTIES);

		return operationsInfo;
	}

	private ArrayList<String> getDefaultOperationsList() {
		operationsInfo.add(OP_SELECT_ALL);
		operationsInfo.add(OP_SET_AS_HOME);

		return operationsInfo;
	}

	private void executeSingleFileOperation(String op_name) {
		if (op_name == OP_CUT) {

			opHandler.cut(f);

		} else if (op_name == OP_COPY) {

			opHandler.copy(f);

		} else if (op_name == OP_RENAME) {

			showRenameDialog();

		} else if (op_name == OP_DELETE) {

			showDeleteConfirmationDialog();

		} else if (op_name == OP_SELECT_ALL) {
			
			opHandler.selectAll();
			
		} else if (op_name == OP_CREATE_SHORTCUT) {
			
			opHandler.addShortcut(f);
			
		} else if (op_name == OP_FAVORITE) {
			
			opHandler.addFavorite(f);
			
		} else if (op_name == OP_HIDE) {
			
			opHandler.hideFile(f);
			
		} else if (op_name == OP_COMPRESS) {
			
			opHandler.compressFile(f);
			
		} else if (op_name == OP_SET_AS_HOME) {
			
			opHandler.setDirectoryAsHome(f);
			
		} else if (op_name == OP_PROPERTIES) {
			
			opHandler.showFileProperties(f);
			
		}

		opHandler.setLast_operation(OperationsHandler.OP_SINGLE);
	}

	private void executeMultipleFilesOperation(String op_name) {

		if (op_name == OP_CUT) {
			opHandler.cutSelectedFiles();

		} else if (op_name == OP_COPY) {
			opHandler.copySelectedFiles();

		} else if (op_name == OP_DELETE) {
			opHandler.deleteSelectedFiles();
			opHandler.cancelSelect();
		} else if (op_name == OP_SELECT_ALL) {
			opHandler.selectAll();
		} else if (op_name == OP_SELECT_INVERSE) {

		} else if (op_name == OP_FAVORITE) {
		} else if (op_name == OP_HIDE) {
		} else if (op_name == OP_COMPRESS) {
		} else if (op_name == OP_PROPERTIES) {
		}

		opHandler.setLast_operation(OperationsHandler.OP_MULTIPLE);
	}

	// delete prompt
	private void showDeleteConfirmationDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								opHandler.delete(f);

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

		builder.show();
	}

}