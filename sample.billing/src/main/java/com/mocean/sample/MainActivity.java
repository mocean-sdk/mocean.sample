package com.mocean.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

    // generate this key in our backend;
    static final String CHARGE_KEY = "first";

    String orderNum;
    IPaymentService paymentService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView order = (TextView)findViewById(R.id.tv_order);

        final String paymentKey = getAppMeta(this, "mocean.key", null);
        TextView info = (TextView)findViewById(R.id.tv_info);
        info.setText("Payment KEY : " + paymentKey + "\nCharge ID : " + CHARGE_KEY);

        Button btnPay = (Button)findViewById(R.id.bt_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    if(null == paymentService){
                        return;
                    }
                    orderNum = paymentService.pay(CHARGE_KEY, 10, new IPaymentCallback() {
                        @Override
                        public void onResult(int status, int errCode) {
                            if (status == IPaymentCallback.STATUS_SUCCESS){
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            } else if (status == IPaymentCallback.STATUS_FAIL){
                                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_LONG).show();
                            } else {
//                                ERROR CODE :
//                                ERR_FAILS_TO_CONNECT -1
//                                ERR_WRONG_RESPONSE   -2
//                                ERR_CONFIGURE        -4
//                                ERR_TIMEOUT          -5
//                                ERR_FAILS_TO_CHARGE  -100
                                Toast.makeText(MainActivity.this, "Error :" + errCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                    order.setText("Order ID : " + orderNum);
                }
            }
        });

        Button btnCheck = (Button)findViewById(R.id.bt_check_pay);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    if(null == orderNum || null == paymentService){
                        return;
                    }
                    paymentService.check(orderNum, 10, new IPaymentCallback() {
                        @Override
                        public void onResult(int status, int errCode) {
                            if (status == IPaymentCallback.STATUS_SUCCESS){
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            } else if (status == IPaymentCallback.STATUS_FAIL){
                                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_LONG).show();
                            } else {
//                                ERROR CODE :
//                                ERR_FAILS_TO_CONNECT -1
//                                ERR_WRONG_RESPONSE   -2
//                                ERR_CONFIGURE        -4
//                                ERR_TIMEOUT          -5
//                                ERR_FAILS_TO_CHARGE  -100
                                Toast.makeText(MainActivity.this, "Error :" + errCode, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        PaymentServiceManager.get(this, new IServiceCallback<IPaymentService>(){
            @Override
            public void call(IPaymentService service) {
                paymentService = service;
            }
        });
        orderNum = null;
    }

    private boolean checkPermission(){
        List<String> pList = new ArrayList<>();
        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)){
            pList.add(Manifest.permission.SEND_SMS);
        }
        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            pList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(pList.size() > 0) {
            ActivityCompat.requestPermissions(this, pList.toArray(new String[pList.size()]), 1);
            return false;
        }
        return true;
    }

   private String getAppMeta(Context context, String key, String def){
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.containsKey(key) ? bundle.getString(key) : def;
        } catch (Exception e) {
            return def;
        }
    }

}
