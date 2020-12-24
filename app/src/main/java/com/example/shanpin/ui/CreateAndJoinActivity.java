package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.shanpin.Adapter.CreateAndJoinAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 在设置界面看自己创建或者加入的pinlist的activity
 */
public class CreateAndJoinActivity extends AppCompatActivity {

    private static final String TAG ="CreateAndJoinActivity";
    private Toolbar toolbar;

    private List<MessageBean> messageBeanList= new ArrayList<MessageBean>();

    private RecyclerView recyclerView;
    private CreateAndJoinAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String mode;    //"0"是创建的拼，"1"是加入的拼
    private String suffix;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_join);
        mode=getIntent().getStringExtra("mode");
        toolbar = findViewById(R.id.activity_createandjion_toolbar);

        if(mode.equals("0")){
            toolbar.setTitle("我创建的拼");
            suffix="GetCreatePinList";

        }else {
            toolbar.setTitle("我加入的拼");
            suffix="GetJoinPinList";

        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView=findViewById(R.id.activity_createandjion__recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CreateAndJoinActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter =new CreateAndJoinAdapter(messageBeanList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout=findViewById(R.id.activity_createandjion_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList(suffix);
            }
        });
        getList(suffix);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE,  Menu.FIRST+1 , 0, "管理").setShowAsAction(1);
        menu.add(Menu.NONE,  Menu.FIRST+2 , 0, "删除").setVisible(false).setShowAsAction(1);
        menu.add(Menu.NONE,  Menu.FIRST+3 , 0, "完成").setVisible(false).setShowAsAction(1);
        return true;
    }

    //Toolbar item 按键响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case Menu.FIRST+1 :
                adapter.setManage(1);
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(true);
                break;
            case Menu.FIRST+2 :
                String userID=AccountUtil.getAccount(getApplicationContext());
                List<MessageBean> manageList=adapter.getManageList();
                for(int i=0;i<manageList.size();i++) {
                    if (mode.equals("0"))
                        deletePin(manageList.get(i).getPinID());
                    else
                        quitPin(userID, manageList.get(i).getPinID());

                    messageBeanList.remove(manageList.get(i));
                }
                manageList.clear();
                adapter.notifyDataSetChanged();
                break;

            case Menu.FIRST+3 :
                adapter.setManage(0);
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                break;
            default:
                break;
        }
        return true;
    }

    private void getList(String suffix){
        RequestBody requestBody=new FormBody.Builder()
                .add("userID",AccountUtil.getAccount(CreateAndJoinActivity.this))
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateAndJoinActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("getTalkList", "onResponse: "+"请检查你的网络");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Fragment_message", "getTalkList:onResponse: "+responseText);
                swipeRefreshLayout.setRefreshing(false);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    messageBeanList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());

//                    List<MessageBean> tempList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());
//                    messageBeanList.addAll(tempList);
                    Log.d("Fragment_message", "getTalkList:messageBeanList: "+messageBeanList.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            messageListAdapter.notifyDataSetChanged();
                            adapter.setMessageList(messageBeanList);
                        }
                    });

                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/"+suffix;
        Okhttp.sentPost(url,requestBody,callback);
    }

    private void quitPin(String userID, String pinID){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .add("userID",userID)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"退出成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(getApplicationContext(),responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/QuitPin";
        Okhttp.sentPost(url,requestBody,callback);
    }


    private void deletePin(String pinID){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(getApplicationContext(),responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/DeletePin";
        Okhttp.sentPost(url,requestBody,callback);
    }
}