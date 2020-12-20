package com.example.shanpin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.shanpin.R;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.PictureUtil;

public class Fragment_me extends Fragment {
    private LinearLayout mysetting;
    private LinearLayout outload;
    private TextView account_text;
    private TextView name_text;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        mysetting=(LinearLayout) view.findViewById(R.id.fragment_me_mysetting);
        outload=(LinearLayout) view.findViewById(R.id.fragment_me_outload);

        account_text=(TextView) view.findViewById(R.id.fragment_me_account);
        name_text=(TextView) view.findViewById(R.id.fragment_me_name);
        imageView=(ImageView) view.findViewById(R.id.fragment_me_icon);

        UserBean userBean = AccountUtil.getUserInfo(getContext());
        if((!PictureUtil.readIconFromFile(view.getContext(), imageView)) && userBean.getIcon()!=null && (!userBean.getIcon().isEmpty())){
            PictureUtil.downloadImage(userBean.getIcon(),PictureUtil.getIconPath(getActivity()),getActivity(),imageView);
        }

        account_text.setText(userBean.getEmail());
        name_text.setText(userBean.getName());

        mysetting.setOnClickListener(click);
        outload.setOnClickListener(click);


        return view;
    }



    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            Intent intent;
            switch(view.getId()){
                case R.id.fragment_me_mysetting:
                    intent=new Intent(view.getContext(), SetUserInfoActivity.class);
                    intent.putExtra("userNum",AccountUtil.getAccount(getContext()));
                    view.getContext().startActivity(intent);
                    break;
                case R.id.fragment_me_outload:

                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences("logindate", view.getContext().MODE_PRIVATE).edit();
                    editor.putBoolean("islogin", false);
                    editor.apply();
                    intent=new Intent(view.getContext(), LoginActivity.class);
                    view.getContext().startActivity(intent);
                    getActivity().finish();
                    break;

            }
        }
    };
}
