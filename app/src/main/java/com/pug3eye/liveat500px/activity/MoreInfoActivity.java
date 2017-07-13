package com.pug3eye.liveat500px.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pug3eye.liveat500px.R;
import com.pug3eye.liveat500px.fragment.MoreInfoFragment;

public class MoreInfoActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        initInstances();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainerMoreInfo, MoreInfoFragment.newInstance())          // put contentContainer into mainfragment
                    .commit();
        }
    }

    private void initInstances() {
        /*toolbar = (Toolbar) findViewById(R.id.toolbar_more_info);
        setSupportActionBar(toolbar);*/
    }
}
