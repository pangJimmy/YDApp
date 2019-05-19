package com.pl.ydapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.pl.ydapp.base.BaseActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends BaseActivity implements AdapterView.OnItemClickListener{

    //定义图标数组
    private int[] imageRes = new int[]{
            R.drawable.ic_in_warehouse,
            R.drawable.ic_out_warehouse ,
            R.drawable.ic_print_qr
            } ;
    ////定义图标下方的名称数组
    private String[] name ;
    private GridView grid ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        //设置标题
        setToolbarTitle(R.string.app_name);
        //主操作名
        name = getResources().getStringArray(R.array.main_menu) ;
        initView();
    }


    private void initView(){

        grid = (GridView) findViewById(R.id.gridview_main) ;
        int length = imageRes.length;

        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", imageRes[i]);//添加图像资源的ID
            map.put("ItemText", name[i]);//按序号做ItemText
            lstImageItem.add(map);
        }
        //生成适配器的ImageItem 与动态数组的元素相对应
        SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,//数据来源
                R.layout.item_gridview,//item的XML实现

                //动态数组与ImageItem对应的子项
                new String[]{"ItemImage", "ItemText"},

                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.img_main, R.id.txt_main});
        //添加并且显示
        grid.setAdapter(saImageItems);

        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent ;
        switch (position){
            case 0://入仓记录
                intent = new Intent(this, PartInActivity.class) ;
                startActivity(intent);
                break;
            case 1://出仓核对
                intent = new Intent(this, CompareIDActivity.class) ;
                startActivity(intent);
                break;
            case 2://打印二维码
                intent = new Intent(this, PrintActivity.class) ;
                startActivity(intent);
                break;
        }
    }
}
