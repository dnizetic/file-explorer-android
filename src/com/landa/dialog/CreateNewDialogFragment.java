package com.landa.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.landa.fileexplorermanager.R;
import com.landa.features.OperationsHandler;

public class CreateNewDialogFragment extends DialogFragment {

	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View v = inflater.inflate(R.layout.create_new_view, null);
        
		ListView lv = (ListView) v.findViewById(R.id.list);
		
        String[] values = new String[] { "File", "Folder" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
          android.R.layout.simple_list_item_1, values);
		
		lv.setAdapter(adapter);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String str = (String) parent.getItemAtPosition(position);
				showCreateNewInputDialog(str);
			}
		}); 
		
 
		AlertDialog dialog = builder.create();
		
		dialog.setView(v, 0, 0, 0, 0);
		
        return dialog;
    }

	
	private void showCreateNewInputDialog(final String type) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						EditText lw = (EditText) ((AlertDialog) dialog)
								.findViewById(R.id.input_1);
						
						String file_name = lw.getText().toString();
						
						OperationsHandler oph = OperationsHandler.getInstance();
						oph.createNew(type, file_name);
						
						closeDialog();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
				}).setTitle("Create new ".concat(type));

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View vw = inflater.inflate(R.layout.single_input_view, null);

		builder.setView(vw);

		builder.show();
	} 
	
	private void closeDialog()
	{
		dismiss();
	}
}