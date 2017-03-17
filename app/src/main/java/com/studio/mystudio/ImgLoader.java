package com.studio.mystudio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yue on 2017/3/15.
 * 　　　　　　　  ┏┓　 ┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　     ┃
 * 　　　　　　　┃　　　━　    ┃ ++ + + +
 * 　　　　　　 ████━████     ┃++  ++
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃  +  +
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */

public class ImgLoader {

    private ImageView mImageView;
    private String mUrl;

    //使用LruCache来缓存图片，Key=图片的url，value=要缓存的内容
    //1.创建LurCache对象
    private LruCache<String, Bitmap> mCache;

    //存储用到的listview
    private ListView mListView;
    //管理task
    private Set<NewsAsyncTask> mTask;

    public ImgLoader(ListView listView) {

        mListView = listView;
        mTask = new HashSet<>();

        //2.在构造方法中初始化LruCache
        //2.1获取当前应用的内存大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //2.2创建缓存器的大小
        int cacheSize = maxMemory / 4;
        //2.3初始化LurCache对象
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            //2.4重写sizeOf方法，这个方法用于获取每一个存入的对象的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存时调用，把bitmap的大小传进去
                return value.getByteCount();
            }
        };
    }

    //在使用LurCache之前要执行两个方法1.将内容保存到LurCache中，2.从LurCache中得到保存的数据
    //3.把内容保存到LurCache中
    public void addBitmapToCache(String url, Bitmap bitmap) {
        //判断当前缓存是否存在
        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);
        }
    }

    //4.从LurCache中得到保存的数据
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //当image的tag和相同的时候再加载图片，可以防止图片在加载过程中由于viewHolder的存在而跳动
                    if (mImageView.getTag().equals(mUrl)) {
                        mImageView.setImageBitmap((Bitmap) msg.obj);
                    }
                    break;
            }
        }
    };

    //使用多线程的方式加载图片
    public void showImgByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromURL(url);
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = bitmap;
                handler.sendMessageDelayed(message, 1500);

            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //使用AsyncTask实现异步加载方法
    public void showImgByAsyncTask(ImageView imageView, String url) {
        //5.使用LurCache的getBitmapFromCache方法，cache中获取bitmap
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            //如果缓存中没有url(key)，只能去网络访问，否则把bitmap设置给imageView
            //new NewsAsyncTask(imageView, url).execute(url);

            //在滚动的时候不加载图片，设置为默认图片
            imageView.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    //参数1：请求网址，参数2：是否需要记录中间过程，Void是不需要记录，参数3：返回的值是bitmap图片
    public class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        //private ImageView TImageView;
        private String TUrl;

        //public NewsAsyncTask(ImageView imageView, String url) {
        //这里就不需要手动传入imageView了，直接通过tag就可以获得imageView了
        public NewsAsyncTask(String url) {
            //TImageView = imageView;
            TUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //6.把下载好的图片放入cache中
            Bitmap bitmap = getBitmapFromURL(params[0]);
            if (bitmap != null) {
                addBitmapToCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
/*            if (TImageView.getTag().equals(TUrl)) {
                TImageView.setImageBitmap(bitmap);
            }*/
            //在adapter中，url和imageView已经绑定了
            ImageView imageView = (ImageView) mListView.findViewWithTag(TUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

    //加载从Start到end之间的图片
    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            //获取Start到end之间的图片URL
            String url = NewsAdapter.URLS[i];

            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                //如果缓存中没有url(key)，只能去网络访问，否则把bitmap设置给imageView
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            } else {
                //在adapter中已经给imageView设置过Tag了，就可以直接找到了
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //停止加载图片
    public void cancelAllTask() {
        if (mTask != null) {
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

}
