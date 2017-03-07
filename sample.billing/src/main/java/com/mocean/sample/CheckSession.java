package com.mocean.sample;


import android.content.Context;

import com.mocean.IPaymentCallback;
import com.mocean.IPaymentService;
import com.mocean.IServiceCallback;
import com.mocean.PaymentServiceManager;

public class CheckSession{

    public final static int ORDER_SUCCESS = 1;
    public final static int TIMEOUT = 0;
    public final static int ORDER_FAIL = -1;
    public final static int ERROR_NET = -2;

    interface ICheckSessionCallback {
        void onResult(String order, int result);
    }

    private String mOrder;
    private ICheckSessionCallback mCheckSessionCb;
    private Context mContext;

    public CheckSession(Context context, String order, ICheckSessionCallback checkCb){
        mContext = context;
        mCheckSessionCb = checkCb;
        mOrder = order;
    }

    public void execute(final int timeout){
        PaymentServiceManager.get(mContext, new IServiceCallback<IPaymentService>(){

            @Override
            public void call(IPaymentService payService) {
                if(null == payService) {
                    mCheckSessionCb.onResult(mOrder, TIMEOUT);
                    return;
                }
                if(null == mOrder) {
                    mCheckSessionCb.onResult(mOrder, ORDER_FAIL);
                }
                payService.check(mOrder, timeout, new IPaymentCallback(){
                    @Override
                    public void onResult(int status, int errCode) {
                        if(IPaymentCallback.STATUS_SUCCESS == status) {
                            mCheckSessionCb.onResult(mOrder, ORDER_SUCCESS);
                        } else if (IPaymentCallback.STATUS_ERROR == status) {
                            if(-5 == errCode) {
                                mCheckSessionCb.onResult(mOrder, TIMEOUT);
                            } else {
                                mCheckSessionCb.onResult(mOrder, ERROR_NET);
                            }
                        } else {
                            mCheckSessionCb.onResult(mOrder, ORDER_FAIL);
                        }
                    }
                });
            }
        });
    }
}
