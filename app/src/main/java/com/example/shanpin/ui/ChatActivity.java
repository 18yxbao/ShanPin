package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shanpin.Adapter.MsgAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private List<MsgContentBean> msgList = new ArrayList<MsgContentBean>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initMsgs();
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    MsgContentBean msg = new MsgContentBean(content,MsgContentBean.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }
            }
        });
    }

    private void initMsgs() {
        MsgContentBean msg1 = new MsgContentBean("Hello",MsgContentBean.TYPE_RECEIVED);
        msgList.add(msg1);
        MsgContentBean msg2 = new MsgContentBean("I'm John",MsgContentBean.TYPE_RECEIVED);
        msgList.add(msg2);
        MsgContentBean msg3 = new MsgContentBean("Hello",MsgContentBean.TYPE_SENT);
        msgList.add(msg3);
    }
}