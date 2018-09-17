package com.example.wangjie.imagetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BitmapManager {

    private Context context;
    private int loadingImage;

    public BitmapManager(Context context,int loadingImage) {
        this.context = context;
        this.loadingImage=loadingImage;
    }

    private Map<String,Bitmap> map=new HashMap<>();//一级缓存的容器

    public void loadImage(String imagePath, ImageView imageView) {
        imageView.setTag(imagePath);
        Bitmap bitmap=getFromFirstCache(imagePath);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        bitmap=getFromSecondCache(imagePath);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            map.put(imagePath,bitmap);
            return;
        }


        loadFromThridCache(imagePath,imageView);


    }

    private void loadFromThridCache(final String imagePath, final ImageView imageView) {
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected void onPreExecute() {
                imageView.setImageResource(loadingImage);
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap=null;
                try {
                    String newImagePath=(String)imageView.getTag();
                    if(newImagePath!=imagePath){
                        return null;
                    }

                    URL url=new URL(imagePath);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setConnectTimeout(7000);
                    connection.setReadTimeout(7000);
                    connection.connect();;
                    int code= connection.getResponseCode();
                    if(code==200){
                        InputStream is=connection.getInputStream();
                        bitmap =BitmapFactory.decodeStream(is);
                        is.close();

                        if(bitmap!=null){
                            map.put(imagePath,bitmap);
                            String files=context.getExternalFilesDir(null).getAbsolutePath();
                            String name=imagePath.substring(imagePath.lastIndexOf("/")+1);
                            String filePath=files+"/"+name;
                            FileOutputStream fos=new FileOutputStream(filePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,50,fos);
                        }
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                String newImagePath=(String)imageView.getTag();
                if(newImagePath!=imagePath){
                    return;
                }
                if(bitmap==null){
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                }else{
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();

    }

    private Bitmap getFromSecondCache(String imagePath) {
        String files=context.getExternalFilesDir(null).getAbsolutePath();
        String name=imagePath.substring(imagePath.lastIndexOf("/")+1);
        String filePath=files+"/"+name;
        return BitmapFactory.decodeFile(filePath);

    }

    private Bitmap getFromFirstCache(String imagePath) {
        return map.get(imagePath);
    }
}
