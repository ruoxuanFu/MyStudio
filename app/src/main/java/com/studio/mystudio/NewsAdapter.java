package com.studio.mystudio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater inflater;

    //7.在adapter中使用LurCache
    private ImgLoader mImgLoader;

    public NewsAdapter(Context context, List<NewsBean> data) {
        this.mList = data;
        this.inflater = LayoutInflater.from(context);
        mImgLoader = new ImgLoader();
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

    class ViewHolder {
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView imgIcon;
    }
}
