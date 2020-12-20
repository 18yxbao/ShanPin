package com.example.shanpin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.Adapter.PinListAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.bean.TagBean;

import java.util.ArrayList;
import java.util.List;

public class Fragment_home extends Fragment {
    private List<PinBean> itemList = new ArrayList<PinBean>();
    private List<TagBean> tagList= new ArrayList<TagBean>();
    private List<Button> tagButtonList = new ArrayList<Button>();


    private RecyclerView recyclerView;
    private LinearLayout tag_layout;

    public Fragment_home(){
        initItems();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_recycler_view);
        tag_layout=view.findViewById(R.id.fragment_home_tag_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        PinListAdapter adapter = new PinListAdapter(itemList);
        recyclerView.setAdapter(adapter);

        initTag();
        return view;
    }

    private void initTag(){
        for(int i=0 ; i< tagList.size();i++) {
            Button button = new Button(getContext());
            button.setText(tagList.get(i).getName());
            button.setTag(tagList.get(i));
            button.setOnClickListener(tag_click);
            button.setBackgroundResource(R.color.color_background_grey);
            tagButtonList.add(button);
            tag_layout.addView(button);
        }
    }


    private View.OnClickListener tag_click =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i = 0; i < tagButtonList.size(); i++) {
                Button button = tagButtonList.get(i);
                button.setBackgroundResource(R.color.color_background_grey);
            }
            view.setBackgroundResource(R.color.colorPrimary);
            TagBean tag = (TagBean) view.getTag();
            Toast.makeText(getContext(), tag.getName(), Toast.LENGTH_SHORT).show();
        }
    };




    private void initItems() {
        PinBean apple = new PinBean("apple", "123","2020年12月11日");
        itemList.add(apple);
        PinBean banana = new PinBean("apple", "123","2020年12月11日");
        banana.setTitle("banana");
        itemList.add(banana);
        PinBean pear = new PinBean("apple", "123","2020年12月11日");
        pear.setTitle("pear");
        itemList.add(pear);

        TagBean tag1 = new TagBean();
        tag1.setID("0");
        tag1.setName("拼车");
        tagList.add(tag1);
        TagBean tag2 = new TagBean();
        tag2.setID("1");
        tag2.setName("拼单");
        tagList.add(tag2);
        TagBean tag3 = new TagBean();
        tag3.setID("2");
        tag3.setName("拼自习室");
        tagList.add(tag3);
        TagBean tag4 = new TagBean();
        tag4.setID("3");
        tag4.setName("拼运动");
        tagList.add(tag4);
        TagBean tag5 = new TagBean();
        tag5.setID("4");
        tag5.setName("拼旅游");
        tagList.add(tag5);
        TagBean tag6 = new TagBean();
        tag6.setID("5");
        tag6.setName("拼其他");
        tagList.add(tag6);
    }
}
