package com.mocean.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mocean.IPaymentCallback;
import com.mocean.IPaymentService;
import com.mocean.IServiceCallback;
import com.mocean.PaymentServiceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    static final String TAG_LOG = "MO_PAYMENT";
    // generate this key in our backend;
    static final String CHARGE_KEY = "first";

    String mOrderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tvOrder = (TextView)findViewById(R.id.tv_order);

        TextView info = (TextView)findViewById(R.id.tv_info);
        info.setText("Charge ID : " + CHARGE_KEY);

        Button btnPay = (Button)findViewById(R.id.bt_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    new PaySession(getApplicationContext(), CHARGE_KEY, new PaySession.IPaySessionCallback() {
                        @Override
                        public void onResult(String order, int result) {
                            if(PaySession.CREATED == result){
                                // save order for check billing result
                                mOrderNum = order;
                                Log.d(TAG_LOG, "order = " + order);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvOrder.setText("Order ID : " + mOrderNum);
                                    }
                                });
                                showToast("Pay action finished, checking billing result");
                                // start check billing result
                                checkBillingResult();
                            } else if (PaySession.UNSUPPORTED_OPERATOR == result) {
                                showToast("Unsupported Operator");
                            } else if(PaySession.FAIL == result) {
                                showToast("Pay fail");
                            }

                        }
                    }).execute();
                }
            }
        });

        Button btnCheck = (Button)findViewById(R.id.bt_check_pay);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    checkBillingResult();
                }
            }
        });

        mOrderNum = null;
    }

    private void checkBillingResult(){
        new CheckSession(getApplicationContext(), mOrderNum, new CheckSession.ICheckSessionCallback(){
            @Override
            public void onResult(String order, int result) {
                if(CheckSession.ERROR_NET == result) {
                    showToast("network error");
                } else if (CheckSession.ORDER_FAIL == result) {
                    showToast("billing fail");
                } else if (CheckSession.ORDER_SUCCESS == result) {
                    showToast("billing success");
                } else if(CheckSession.TIMEOUT == result) {
                    showToast("check timeout, need check again later");
                }
            }
        }).execute(60);
    }

    private void showToast(final String str) {
        Log.i(TAG_LOG, str);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkPermission(){
        List<String> pList = new ArrayList<>();
        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)){
            pList.add(Manifest.permission.SEND_SMS);
        }
//        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            pList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
        if(pList.size() > 0) {
            ActivityCompat.requestPermissions(this, pList.toArray(new String[pList.size()]), 1);
            Log.e(TAG_LOG, "permission deny");
            return false;
        }
        return true;
    }

}
