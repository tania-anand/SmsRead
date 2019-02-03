package readsms.com.readsms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import readsms.com.readsms.R;
import readsms.com.readsms.adapter.SmsAdapter;
import readsms.com.readsms.listener.MySmsListener;
import readsms.com.readsms.model.Sms;
import readsms.com.readsms.utils.Helper;
import readsms.com.readsms.utils.MySmsService;

public class MainActivity extends AppCompatActivity  implements MySmsListener{

    private final static String TAG = MainActivity.class.getSimpleName();
    ArrayList<Sms> arrayList = new ArrayList<>();
    ArrayList<Sms> mTotalSmsArrayList = new ArrayList<>();
    RecyclerView mRecycelerView;
    SmsAdapter mSmsAdapter;
    private int limit = 5;
    private int offset = 0;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout mEmptyView;
    private final int MY_PERMISSIONS_REQUEST_SMS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview();
        requestPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"on resume");
        if(getIntent().hasExtra("mySmsObject")){
            Log.d(TAG,"has mySmsObject");
            Sms object = (Sms)getIntent().getSerializableExtra("mySmsObject");
            addSmsToList(object);
        }
    }


    private void initview(){
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mRecycelerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecycelerView.setLayoutManager(layoutManager);
        mRecycelerView.setItemAnimator(new DefaultItemAnimator());
        mEmptyView = findViewById(R.id.emptyView);
        mSmsAdapter = new SmsAdapter(getApplicationContext(),arrayList);
        mRecycelerView.setAdapter(mSmsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(arrayList.size()<mTotalSmsArrayList.size()){
                    // increase the offset and get data from it
                    offset = arrayList.size() + limit;
                    if(offset >= mTotalSmsArrayList.size()){
                        offset = mTotalSmsArrayList.size();
                    }
                    arrayList.clear();
                    arrayList.addAll(0,mTotalSmsArrayList.subList(0,offset));
                    mSmsAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(),"No More Sms",Toast.LENGTH_LONG).show();
                }

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }



    private  ArrayList<Sms> readSms(){
        ArrayList<Sms> lArrayList = new ArrayList<>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, null ,null,null);
        startManagingCursor(c);
        // Read the sms data
        assert c != null;
        if(c.moveToFirst()) {
            do{

                String mobile = c.getString(c.getColumnIndexOrThrow("address"));
                String message = c.getString(c.getColumnIndexOrThrow("body"));
                String date =  c.getString(c.getColumnIndex("date"));
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();

                // difference between current time and and sms date in millisecs
               long differenceInMillsecs = new Date().getTime() -  finaldate.getTime();

               long differenceInSecs = differenceInMillsecs /1000;


                Log.d(TAG,"difference in mill secs "+differenceInMillsecs+" difference in Secs "+differenceInSecs);
                Sms model = new Sms();

                model.setGroup(Helper.getGroupName(differenceInSecs));

                if(!model.getGroup().equals("")) {
                    model.setMessage(message);
                    model.setDate(String.valueOf(timestamp));
                    model.setMobile(mobile);
                    lArrayList.add(model);
                }

            }while(c.moveToNext());
        }

        Log.d(TAG,"array list "+lArrayList.toString());

        return  lArrayList;


    }


    private void requestPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED ){

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECEIVE_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_SMS);

                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            // Permission has already been granted
           afterGrantPermission();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    afterGrantPermission();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    requestPermission();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void afterGrantPermission(){
        MySmsService.bindListener(this);
        mTotalSmsArrayList.clear();
        mTotalSmsArrayList.addAll(readSms());

        Log.d(TAG,"mTotal Array List "+arrayList.toString());

        if(mTotalSmsArrayList.size()>0) {
            int tempOffset = limit;
            if (mTotalSmsArrayList.size() < limit) {
                tempOffset = mTotalSmsArrayList.size();
            }

            arrayList.clear();
            arrayList.addAll(0,mTotalSmsArrayList.subList(0,tempOffset));

            Log.d(TAG,"arrayList is "+arrayList.toString());

            mRecycelerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

        }else{
            mRecycelerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        mSmsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageReceived(Sms object) {
        // add in arrayList
        // sort after this according to timestamp
        Log.d(TAG,"object "+object.toString());
        addSmsToList(object);
    }


    private  void addSmsToList(Sms object){

        Log.d(TAG,"object "+object.toString());
        if(!arrayList.contains(object)) {
            mTotalSmsArrayList.add(object);

            if(mTotalSmsArrayList.size()>0) {
                arrayList.add(object);
                Collections.sort(arrayList, Sms.MyTimestampComparator);
                Collections.sort(mTotalSmsArrayList,Sms.MyTimestampComparator);
                mSmsAdapter.notifyDataSetChanged();

                mRecycelerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);

            }else{
                mRecycelerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);

            }
        }
    }


    public static boolean active = false;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}
