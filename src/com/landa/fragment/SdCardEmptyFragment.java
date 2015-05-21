package com.landa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.landa.fileexplorermanager.R;

public class SdCardEmptyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
    	View view = inflater.inflate(R.layout.sdcardunmounted, container, false);
        return view;
    }
}