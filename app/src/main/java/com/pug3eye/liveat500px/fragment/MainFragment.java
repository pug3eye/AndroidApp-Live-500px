package com.pug3eye.liveat500px.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.pug3eye.liveat500px.R;
import com.pug3eye.liveat500px.activity.MoreInfoActivity;
import com.pug3eye.liveat500px.adapter.PhotoListAdepter;
import com.pug3eye.liveat500px.dao.PhotoItemCollectionDao;
import com.pug3eye.liveat500px.datatype.MutableInteger;
import com.pug3eye.liveat500px.manager.HttpManager;
import com.pug3eye.liveat500px.manager.PhotoListManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    /*************
     * Variables *
     ************/

    ListView listView;
    PhotoListAdepter listAdepter;
    Button btnNewPhotos;

    SwipeRefreshLayout swipeRefreshLayout;

    PhotoListManager photoListManager;
    MutableInteger lastPositionInteger;


    /*************
     * Functions *
     *************/

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
             // Restore Instance State

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);     // combine fragment_main into Main fragment
        initInstances(rootView ,savedInstanceState);
        return rootView;
    }

    private void  init(Bundle savedInstanceState) {
        // Initialize Fragment level's variables
        photoListManager = new PhotoListManager();
        lastPositionInteger = new MutableInteger(-1);


    }

    private void initInstances(View rootView, Bundle saveInstanceState) {

        btnNewPhotos = (Button) rootView.findViewById(R.id.btnNewPhotos);
        btnNewPhotos.setOnClickListener(buttonClickListener);

        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdepter = new PhotoListAdepter(lastPositionInteger);
        listAdepter.setDao(photoListManager.getDao());
        listView.setAdapter(listAdepter);

        listView.setOnItemClickListener(listViewItemClickListener);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);
        listView.setOnScrollListener(listViewScrollListener);

        if (saveInstanceState == null)
        refreshData();
    }

    private void refreshData() {
        if (photoListManager.getCount() == 0)
            reloadData();
        else
            reloadDataNewer();

    }


    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        /*call.execute()*/   // don't use because NetworkOnMainThreadException
        // work , it is Asynchronous
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }

    boolean isLoadingMore = false;

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        int minId = photoListManager.getMinimumId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance()
                .getService()
                .loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE)
        );
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putBundle("photoListManger",
                photoListManager.onSaveInstanceState());
        outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState());

    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore instance state here
        photoListManager.onRestoreInstanceState(
                savedInstanceState.getBundle("photoListManger"));
        lastPositionInteger.onRestoreInstanceState(
                savedInstanceState.getBundle("lastPositionInteger"));
    }


    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showButtonNewPhotos() {                                     // Show Button when have New Photo
        btnNewPhotos.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_in
        );
        btnNewPhotos.startAnimation(anim);

    }

    private void hideButtonNewPhotos() {
        btnNewPhotos.setVisibility(View.GONE);                              // Hide Button when don't have New Photo
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_out
        );
        btnNewPhotos.startAnimation(anim);

    }

    private  void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    /*****************
     * Listener Zone *
     *****************/
    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnNewPhotos) {
                listView.smoothScrollToPosition(0);
                hideButtonNewPhotos();
            }
        }
    };

    final SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    final AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {
            if (view == listView) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);   // will scroll when position is 0.
                //ตำแหน่งแรกที่เห็น + จน.ที่เห็นอยู่ตอนนั้น >= จำนนวนทั้งหมดที่มีในลิสต์วิว  Load More
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        // Load More
                        loadMoreData();
                    }
                }
            }
        }
    };

    final AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(),
                    MoreInfoActivity.class);
            startActivity(intent);
        }
    };

    /***************
     * Inner class *
     ***************/

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        int mode;

        public PhotoListLoadCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);                                    // stop swipeRefresh when load data success

            if (response.isSuccessful()) {
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();          //what is the first visible item position?
                View c = listView.getChildAt(0);                                  // First view in screen
                int top = c == null ? 0 : c.getTop();

                    /*PhotoListManager.getInstance().setDao(dao); */                    // save into singleton
                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtBottomPosition(dao);
                } else {
                    photoListManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                listAdepter.setDao(photoListManager.getDao());                        // over
                listAdepter.notifyDataSetChanged();                                 // refresh Data when Change

                if (mode == MODE_RELOAD_NEWER) {
                    // Maintain Scroll Position
                    int additionalSize =
                            (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdepter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize,
                            top);         // Scroll to LastPosition
                    if (additionalSize > 0)
                        showButtonNewPhotos();
                } else {

                }

                showToast("Load Completed");
            } else {
                // Handle ,it onResponse but not success
                clearLoadingMoreFlagIfCapable(mode);
                try {
                    showToast(response.errorBody().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
            // Handle
            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);                                                 // stop swipeRefresh when load data non-success
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }
}

