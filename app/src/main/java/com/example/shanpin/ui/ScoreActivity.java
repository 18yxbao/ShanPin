package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.shanpin.Adapter.ScoreAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private List<UserBean> userBeanList=new ArrayList<UserBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.activity_score_recyclerview);
        recyclerView.setLayoutManager(layoutManager);

        UserBean userBean = new UserBean();
        userBean.setName("1");
        userBeanList.add(userBean);
        userBean = new UserBean();
        userBean.setName("2");
        userBeanList.add(userBean);
        adapter=new ScoreAdapter(userBeanList,this);
        recyclerView.setAdapter(adapter);

    }
}