package com.example.guojian.weekcook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guojian.weekcook.R;
import com.example.guojian.weekcook.bean.CookBean;
import com.example.guojian.weekcook.bean.MaterialBean;
import com.example.guojian.weekcook.utils.ImageLoaderUtil;

import java.util.List;

/**
 * Created by guojian on 11/15/16.
 */
public class CookListAdapter extends BaseAdapter {

    private Context context;
    private List<CookBean> cookBeanList;

    public CookListAdapter(Context context, List<CookBean> cookBeanList) {
        this.context = context;
        this.cookBeanList = cookBeanList;
    }

    @Override
    public int getCount() {
        return cookBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return cookBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.cook_list_item, null);
            holder.mCookName = (TextView) convertView.findViewById(R.id.tv_item_cook_name);
            holder.mCookMaterial = (TextView) convertView.findViewById(R.id.tv_item_cook_material);
            holder.mCookingTime = (TextView) convertView.findViewById(R.id.tv_item_cook_time);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_cook_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CookBean cookBean = cookBeanList.get(position);
        Log.i("guojian", "CookListAdapter-->> cookBean.toString();====" + cookBean.toString());
        holder.mCookName.setText(cookBean.getName_cook());
        List<MaterialBean> materialBeanList = cookBean.getMaterialBeen();
        StringBuilder material = new StringBuilder("");
        for (int i = materialBeanList.size() - 1; i >= 0; i--) {
            MaterialBean materialBean = materialBeanList.get(i);
            String materialString = materialBean.getMname() + ", ";
            material.append(materialString);
        }
        holder.mCookMaterial.setText(material);
        String mCookingTimeString = "烹饪时间: " + cookBean.getCookingtime();
        holder.mCookingTime.setText(mCookingTimeString);
        String image_url = cookBean.getPic();

        ImageLoaderUtil.setRoundedBitmap(holder.imageView, image_url);
        return convertView;
    }

    class ViewHolder {
        private TextView mCookName;
        private TextView mCookMaterial;
        private TextView mCookingTime;
        private ImageView imageView;
    }
}
