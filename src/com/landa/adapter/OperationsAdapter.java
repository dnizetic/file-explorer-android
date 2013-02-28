package com.landa.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fileexplorermanager.R;
import com.landa.dialog.OperationsDialogFragment;

public class OperationsAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final ArrayList<String> data;

  public OperationsAdapter(Context context, ArrayList<String> values) {
    super(context, R.layout.operations_list_item, values);
    this.context = context;
    this.data = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    //View rowView = inflater.inflate(R.layout.operations_list, parent, false);
    View rowView = inflater.inflate(R.layout.operations_list_item, parent, false);
    
    TextView textView = (TextView) rowView.findViewById(R.id.operation_name);
   
    String temp = data.get(position);
    String op_name = temp;
    textView.setText(op_name);
    
    ImageView imageView = (ImageView) rowView.findViewById(R.id.operation_image);
	if(op_name.equals(OperationsDialogFragment.OP_CUT)) {
		imageView.setImageResource(R.drawable.op_cut);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_COPY)) {
		imageView.setImageResource(R.drawable.op_copy);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_RENAME)) {
		imageView.setImageResource(R.drawable.op_rename);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_DELETE)) {
		imageView.setImageResource(R.drawable.op_delete);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_SELECT_ALL)) {
		imageView.setImageResource(R.drawable.op_select_all);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_CREATE_SHORTCUT)) {
		imageView.setImageResource(R.drawable.op_shortcut);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_FAVORITE)) {
		imageView.setImageResource(R.drawable.favorites);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_HIDE)) {
		imageView.setImageResource(R.drawable.op_hide);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_COMPRESS)) {
		imageView.setImageResource(R.drawable.op_compress);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_SET_AS_HOME)) {
		imageView.setImageResource(R.drawable.op_home);
		
	} else if(op_name.equals(OperationsDialogFragment.OP_PROPERTIES)) {
		imageView.setImageResource(R.drawable.op_info);
		
	}
    return rowView;

  }
  public int getCount() {
      return data.size();
  }

  //public Object getItem(int position) {
  //    return position;
  //}

  //public long getItemId(int position) {
  //    return position;
  //}
  
} 