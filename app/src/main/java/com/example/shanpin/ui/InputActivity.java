package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shanpin.R;


public class InputActivity extends AppCompatActivity {
    private String title="";
    private Toolbar toolbar;
    private EditText E_input;
    private int MENU_ITEM_1=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        title=getIntent().getStringExtra("title");
        toolbar = findViewById(R.id.activity_input_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        E_input =findViewById(R.id.activity_input_edit_text);
        E_input.setText(getIntent().getStringExtra("content"));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,MENU_ITEM_1,0,"修改")
                .setShowAsAction(Menu.FLAG_ALWAYS_PERFORM_CLOSE);
        //getMenuInflater().inflate(R.menu.menu_publish,menu); // 参数1为布局文件(menu_main.xml)
        return true;
    }

    //Toolbar item 按键响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                String input = E_input.getText().toString();
                if(input.equals("")){
                    Toast.makeText(getApplicationContext(),"输入不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = getIntent();
                    intent.putExtra("input", input);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}