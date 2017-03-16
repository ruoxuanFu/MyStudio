package com.studio.mystudio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

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

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<NewsBean> mList;
    private LayoutInflater inflater;

    //7.在adapter中使用LurCache
    private ImgLoader mImgLoader;

    //记录滚动的位置
    private int mStart, mEnd;
    public static String[] URLS;

    //判断当前是否是第一次启动listView
    private boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listView) {
        this.mList = data;
        this.inflater = LayoutInflater.from(context);
        mImgLoader = new ImgLoader(listView);

        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).icon;
        }
        //注册滑动事件监听
        listView.setOnScrollListener(this);

        mFirstIn = true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_layout, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.news_content);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.news_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgIcon.setImageResource(R.mipmap.ic_launcher);

        //把imageView和对应的URL绑定
        viewHolder.imgIcon.setTag(mList.get(position).getIcon());

        //使用多线程的方式加载图片
        //new ImgLoader().showImgByThread(viewHolder.imgIcon, mList.get(position).getIcon());
        //使用AsyncTask的方式加载图片
        //new ImgLoader().showImgByAsyncTask(viewHolder.imgIcon, mList.get(position).getIcon());

        mImgLoader.showImgByAsyncTask(viewHolder.imgIcon, mList.get(position).getIcon());

        viewHolder.tvTitle.setText(mList.get(position).getTitle());
        viewHolder.tvContent.setText(mList.get(position).getContent());

        return convertView;
    }

    //实现listView在滑动的时候不加载数据，在停止滑动的时候再加载数据，优化listView，让他不卡顿
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //不滚动的时候,加载
            mImgLoader.loadImages(mStart, mEnd);
        } else {
            //滚动，不加载
            mImgLoader.cancelAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //首次进入程序加载listView
        if (mFirstIn && visibleItemCount > 0) {
            mImgLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder {
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView imgIcon;
    }
}
