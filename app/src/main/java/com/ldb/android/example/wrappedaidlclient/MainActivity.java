package com.ldb.android.example.wrappedaidlclient;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ldb.android.example.wrappedaidl.ApiCallback;
import com.ldb.android.example.wrappedaidl.ApiWrapper;
import com.ldb.android.example.wrappedaidl.ApiWrapperByMessenger;
import com.ldb.android.example.wrappedaidl.aidl.CustomData;
import com.ldb.android.example.wrappedaidl.aidl.ResultCount;

import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements ApiCallback {

    private static final String TAG = "MainActivity";

    private ApiWrapper mApiWrapper;
    private ApiWrapperByMessenger mApiWrapperByMessenger;
    private EditText mNumber;
    private Button mPrime;
    private Button mStore;
    private Button mGet;
    private Button mStoreByMessenger;
    private Button mGetByMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumber = (EditText) findViewById(R.id.number_input);
        mPrime = (Button) findViewById(R.id.prime);
        mStore = (Button) findViewById(R.id.store);
        mGet = (Button) findViewById(R.id.get);
        mStoreByMessenger = (Button) findViewById(R.id.store_messenger);
        mGetByMessenger = (Button) findViewById(R.id.get_messenger);

        mApiWrapper = new ApiWrapper(this, this);
        mApiWrapperByMessenger = new ApiWrapperByMessenger(this, this);

        mPrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long number = Long.valueOf(mNumber.getText().toString());
                boolean isPrime = false;
                isPrime = mApiWrapper.isPrime(number);
                String message = isPrime ? "number_is_prime" : "number_not_prime";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        mStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomData customData = new CustomData();
                String name = mNumber.getText().toString();
                customData.setName(name);
                customData.getReference().add(name + "1");
                customData.getReference().add(name + "2");
                customData.getReference().add(name + "3");
//                customData.setCreated(new GregorianCalendar(2016, 9, 1, 9, 0 ).getTime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mApiWrapper.storeData(customData);
                        Log.d(TAG, "mService.storeData1");
                    }
                }).start();

                Log.d(TAG, "mService.storeData2");
            }
        });
        mGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomData[] result = new CustomData[10];
                Date since = new GregorianCalendar(2016, 8, 1, 8, 0 ).getTime();
                mApiWrapper.getAllDataSince(since.getTime(), result);
                Log.d(TAG, "Result: " + result.length);
                for (int i = 0; i < result.length; i++) {
                    CustomData customData = result[i];
                    if (customData != null) {
                        Log.d(TAG, result[i].getName() + result[i].getCreated().toString());
                        for (String s : result[i].getReference()) {
                            Log.d(TAG, "  -- " + s);
                        }
                    }
                }
            }
        });

        mStoreByMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomData customData = new CustomData();
                String name = mNumber.getText().toString();
                customData.setName(name);
                customData.getReference().add(name + "1");
                customData.getReference().add(name + "2");
                customData.getReference().add(name + "3");
//                customData.setCreated(new GregorianCalendar(2016, 9, 1, 9, 0 ).getTime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mApiWrapperByMessenger.storeData(customData);
                        Log.d(TAG, "mApiWrapperByMessenger.storeData1");
                    }
                }).start();

                Log.d(TAG, "mApiWrapperByMessenger.storeData2");
            }
        });
        mGetByMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date since = new GregorianCalendar(2016, 8, 1, 8, 0 ).getTime();
                mApiWrapperByMessenger.getAllDataSince(since.getTime(), 10);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mApiWrapper.release();
        mApiWrapper = null;
        mApiWrapperByMessenger.release();
        mApiWrapperByMessenger = null;
    }

    @Override
    public void onApiReady(ApiWrapper apiWrapper) {
        Log.d(TAG, "ApiWrapper onApiReady");
    }

    @Override
    public void onApiLost() {
        Log.d(TAG, "onApiLost");
    }

    @Override
    public void onDataUpdated(CustomData[] customData) {
        for(int i = 0; i < customData.length; i++) {
            Log.d(TAG, "onDataUpdated" + customData[i].getName() + " -- " + customData[i].getCreated());
            for(String s : customData[i].getReference()){
                Log.d(TAG, "  -- " + s);
            }
        }
    }

    @Override
    public void onApiReady(ApiWrapperByMessenger apiWrapperByMessenger) {
        Log.d(TAG, "ApiWrapperByMessenger onApiReady");
    }

    @Override
    public void onDataUpdated(int result, String info) {
        Log.d(TAG, "onDataUpdated: " + result + " -- " + info);
    }

    @Override
    public void onGetData(CustomData[] result, int count) {
        Log.d(TAG, "Result: " + result.length + " -- " + count);
        for (int i = 0; i < result.length; i++) {
            CustomData customData = result[i];
            if (customData != null) {
                Log.d(TAG, result[i].getName() + result[i].getCreated().toString());
                for (String s : result[i].getReference()) {
                    Log.d(TAG, "  -- " + s);
                }
            }
        }
    }

    @Override
    public void onGetData(CustomData[] result, ResultCount resultCount) {
        Log.d(TAG, "Result: " + result.length + " -- ResultCount: " + resultCount.getCount());
        for (int i = 0; i < result.length; i++) {
            CustomData customData = result[i];
            if (customData != null) {
                Log.d(TAG, result[i].getName() + result[i].getCreated().toString());
                for (String s : result[i].getReference()) {
                    Log.d(TAG, "  -- " + s);
                }
            }
        }
    }
}
