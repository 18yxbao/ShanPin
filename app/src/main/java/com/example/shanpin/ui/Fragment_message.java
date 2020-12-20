package com.example.shanpin.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shanpin.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Fragment_message extends Fragment {


    //定义一个帖子列表作为数据源
    private List<Text> texts= new ArrayList<Text>();

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_message,container,false);

        return view;
    }




}
