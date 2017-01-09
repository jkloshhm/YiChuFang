package com.example.guojian.weekcook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.guojian.weekcook.R;

public class SearchActivity extends Activity {
    private EditText editText;
    private String hotSreachName[] = {"土豆", "红烧肉", "韭菜", "鱼", "汤", "排骨", "早餐", "批萨"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_search_back);
        if (mBack != null) mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText = (EditText) findViewById(R.id.edit_query_view_main);
        LinearLayout mSearch = (LinearLayout) findViewById(R.id.ll_search_activity);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(SearchActivity.this, "请输入正确的菜名", Toast.LENGTH_SHORT).show();
                } else {
                    //setEditTextInhibitInputSpeChat(mSearchName);
                    Intent mIntent = new Intent(SearchActivity.this, CookListActivity.class);
                    mIntent.putExtra("CookType", "GetDataBySearchName");
                    mIntent.putExtra("name", editText.getText().toString().replace(" ", ""));
                    startActivity(mIntent);
                }
            }
        });

        GridView hotSearchGridView = (GridView) findViewById(R.id.gv_hot_search);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.hot_search_gv_adapter_item, hotSreachName);
        hotSearchGridView.setAdapter(arrayAdapter);
        hotSearchGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = hotSreachName[position];
                //setEditTextInhibitInputSpeChat(mSearchName);
                Intent mIntent = new Intent(SearchActivity.this, CookListActivity.class);
                mIntent.putExtra("CookType", "GetDataBySearchName");
                mIntent.putExtra("name", name);
                startActivity(mIntent);
            }
        });







        /*SearchView mSearchView = (SearchView) findViewById(R.id.search_view_main);
        if (null != mSearchView) {
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setSubmitButtonEnabled(true);
            //mSearchView.setFocusable(true);
            //mSearchView.setQueryHint("请输入正确的菜名");
            try {
                //--拿到字节码
                Class<?> argClass = mSearchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(mSearchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
                //ImageView iv = (ImageView) ownField.get(mSearchView);
                //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.m_search_press));
                //Class<?> argClass =mSearchView.getClass();
                //指定某个私有属性
                Field mSearchHintIconField = argClass.getDeclaredField("mSearchHintIcon");
                mSearchHintIconField.setAccessible(true);
                View mSearchHintIcon = (View)mSearchHintIconField.get(mSearchView);
                mSearchHintIcon.setVisibility(View.GONE);
                //注意mSearchPlate的背景是stateListDrawable(不同状态不同的图片)  所以不能用BitmapDrawable
                //Field ownField = argClass.getDeclaredField("mSearchPlate");
                //setAccessible 它是用来设置是否有权限访问反射类中的私有属性的，只有设置为true时才可以访问，默认为false
                ownField.setAccessible(true);
                //View mView = (View) ownField.get(mSearchView);
                mView.setBackground(getResources().getDrawable(R.drawable.m_search_press));
                //指定某个私有属性
                Field mQueryTextView = argClass.getDeclaredField("mQueryTextView");
                mQueryTextView.setAccessible(true);
                Class<?> mTextViewClass = mQueryTextView.get(mSearchView).getClass()
                        .getSuperclass().getSuperclass().getSuperclass();

                //mCursorDrawableRes光标图片Id的属性 这个属性是TextView的属性，所以要用mQueryTextView（SearchAutoComplete）
                //的父类（AutoCompleteTextView）的父  类( EditText）的父类(TextView)
                Field mCursorDrawableRes = mTextViewClass.getDeclaredField("mCursorDrawableRes");
                //setAccessible 它是用来设置是否有权限访问反射类中的私有属性的，只有设置为true时才可以访问，默认为false
                mCursorDrawableRes.setAccessible(true);
                //注意第一个参数持有这个属性(mQueryTextView)的对象(mSearchView) 光标必须是一张图片不能是颜色，因为光标有两张图片，
                //一张是第一次获得焦点的时候的闪烁的图片，一张是后边有内容时候的图片，如果用颜色填充的话，就会失去闪烁的那张图片，
                //颜色填充的会缩短文字和光标的距离（某些字母会背光标覆盖一部分）。
                mCursorDrawableRes.set(mQueryTextView.get(mSearchView), R.drawable.m_search_press);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSearchView.setFocusable(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (TextUtils.isEmpty(query.trim())) {
                        Toast.makeText(SearchActivity.this, "请输入正确的菜名", Toast.LENGTH_SHORT).show();
                    } else {
                        //setEditTextInhibitInputSpeChat(mSearchName);
                        Intent mIntent = new Intent(SearchActivity.this, CookListActivity.class);
                        mIntent.putExtra("CookType", "GetDataBySearchName");
                        mIntent.putExtra("name", query.replace(" ", ""));
                        startActivity(mIntent);
                    }
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }*/

    }
}
