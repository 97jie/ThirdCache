package com.example.wangjie.imagetest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView lv_main;
    private LinearLayout ll_main_loading;
    private List<ShopInfo> data=new ArrayList<>();
    private MyAdapter adapter;
    private static  final int WHAT_SUCCESS=1;
    private static  final int WHAT_FAIL=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_SUCCESS :
                    ll_main_loading.setVisibility(View.GONE);
                    lv_main.setAdapter(adapter);
                break;
                case WHAT_FAIL:
                    ll_main_loading.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "没有数据！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_main=findViewById(R.id.lv_main);
        ll_main_loading=findViewById(R.id.ll_main_loading);
        adapter=new MyAdapter();

        ll_main_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonString=getJsonString();
                    data=new Gson().fromJson(jsonString,new TypeToken<List<ShopInfo>>(){}.getType());

                    handler.sendEmptyMessage(WHAT_SUCCESS);

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_FAIL);
                }
            }
        }).start();


    }

    private String getJsonString() throws Exception {
        String path="http://192.168.1.7:8080/Wwb_android/ShopInfoServlet";
        String result=null;
        URL url=new URL(path);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setReadTimeout(7000);
        connection.setConnectTimeout(8000);
        connection.connect();
        int code=connection.getResponseCode();
        if(code==200) {
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            result = baos.toString();
            baos.close();
            is.close();
        }
        connection.disconnect();

        return result;

    }

    class MyAdapter extends BaseAdapter{

        private BitmapManager manager;
        public MyAdapter(){
            manager=new BitmapManager(MainActivity.this,R.drawable.logo);
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                view=View.inflate(MainActivity.this,R.layout.item_main,null);
            }
            ShopInfo info=data.get(i);
            TextView name=view.findViewById(R.id.tv_item_name);
            TextView price=view.findViewById(R.id.tv_item_price);
            ImageView imageView=view.findViewById(R.id.iv_item_icon);

            name.setText(info.getName());
            price.setText(info.getPrice()+"元");
            String imagePath=info.getImagePath();
            //更具图片路径动态请求服务器加载图片，启动分线程
            manager.loadImage(imagePath,imageView);

            return view;
        }
    }
}
