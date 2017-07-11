package com.pug3eye.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pug3eye.liveat500px.R;
import com.pug3eye.liveat500px.dao.PhotoItemCollectionDao;
import com.pug3eye.liveat500px.dao.PhotoItemDao;
import com.pug3eye.liveat500px.manager.PhotoListManager;
import com.pug3eye.liveat500px.view.PhotoListItem;

/**
 * Created by pug3eye on 01/07/2017.
 */

public class PhotoListAdepter extends BaseAdapter {

    PhotoItemCollectionDao dao;

    int lastPosition = -1;

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {                                                                         // How many ITEM?
        if (dao == null)
            return 0;
        if (dao.getData() == null)
            return 0;
        return dao.getData().size();                            //  ex. getDao is null & getData is null
    }

    @Override
    public Object getItem(int position) {
        return dao.getData().get(position);
    }                                                                   // Provide data of a position

    @Override
    public long getItemId(int position) {
        return 0;
    }

/*    @Override
    public int getViewTypeCount() {                                                                 // for Many View
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 0 : 1;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        if (position > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
            item.startAnimation(anim);
            lastPosition = position;

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
        lastPosition += amount;
    }

}


