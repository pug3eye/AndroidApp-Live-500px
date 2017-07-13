package com.pug3eye.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pug3eye.liveat500px.R;
import com.pug3eye.liveat500px.dao.PhotoItemCollectionDao;
import com.pug3eye.liveat500px.dao.PhotoItemDao;
import com.pug3eye.liveat500px.datatype.MutableInteger;
import com.pug3eye.liveat500px.manager.PhotoListManager;
import com.pug3eye.liveat500px.view.PhotoListItem;

/**
 * Created by pug3eye on 01/07/2017.
 */

public class PhotoListAdepter extends BaseAdapter {

    PhotoItemCollectionDao dao;
    MutableInteger lastPositionInteger;

    public PhotoListAdepter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {                                                                         // How many ITEM?
        if (dao == null)
            return 1;
        if (dao.getData() == null)
            return 1;
        return dao.getData().size() + 1;                            //  ex. getDao is null & getData is null
    }

    @Override
    public Object getItem(int position) {
        return dao.getData().get(position);
    }                                                                   // Provide data of a position

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {                                                                 // for Many View
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            // progress Bar
            ProgressBar item;
            if (convertView != null)
                item = (ProgressBar) convertView;
            else
                item = new ProgressBar(parent.getContext());
            return item;
        }
//        if (getItemViewType(position) == 0) {
        PhotoListItem item;
        if (convertView != null)
            item = (PhotoListItem) convertView;                                                     // PhotoListItem is CustomViewGroup
        else
            item = new PhotoListItem(parent.getContext());

        PhotoItemDao dao = (PhotoItemDao) getItem(position);         // must don't null
        item.setNameText(dao.getCaption());                                                         // *** Set from dao into item in PhotoListItem
        item.setDescriptionText(dao.getUserName() + " by " + dao.getCamera());
        item.setImageUrl(dao.getImageUrl());

        if (position > lastPositionInteger.getValue()) {
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
            item.startAnimation(anim);
            lastPositionInteger.setValue(position);

        }

        return item;
       /* } else {
            TextView item;
            if (convertView != null)
                item = (TextView) convertView;
            else
                item = new TextView(parent.getContext());
            item.setText("Position" + position);
            return item;

        }
    */
    }

    public void increaseLastPosition(int amount) {
        lastPositionInteger.setValue(lastPositionInteger.getValue() + amount);
    }

}


