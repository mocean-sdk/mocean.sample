package com.mocean.sample;


import android.content.Context;

import com.mocean.IPaymentCallback;
import com.mocean.IPaymentService;
import com.mocean.IServiceCallback;
import com.mocean.PaymentServiceManager;

public class PaySession {

    public final static int CREATED = 0;
    public final static int FAIL = -1;
    public final static int UNSUPPORTED_OPERATOR = -2;

    interface IPaySessionCallback {
        void onResult(String order, int result);
    }

    private String mChargeId;
    private String mOrder;
    private IPaySessionCallback mPaySessionCb;
    private Context mContext;

    public PaySession(Context context, String chargeId, IPaySessionCallback payCb){
        mContext = context;
        mChargeId = chargeId;
        mPaySessionCb = payCb;
        mOrder = null;
    }

    public void execute(){
        PaymentServiceManager.get(mContext, new IServiceCallback<IPaymentService>(){
            @Override
            public void call(IPaymentService payService) {
                if(null == payService) {
                    mPaySessionCb.onResult(mOrder, FAIL);
                    return;
                }
                mOrder = payService.pay(mChargeId, 0, new IPaymentCallback(){

                    @Override
                    public void onResult(int status, int errCode) {
                        if(IPaymentCallback.STATUS_SUCCESS == status) {
                            mPaySessionCb.onResult(mOrder, CREATED);
                        } else if (IPaymentCallback.STATUS_ERROR == status) {
                            if(-5 == errCode) {
                                mPaySessionCb.onResult(mOrder, CREATED);
                            } else if (-4 == errCode) {
                                mPaySessionCb.onResult(mOrder, UNSUPPORTED_OPERATOR);
                            } else {
                                mPaySessionCb.onResult(mOrder, FAIL);
                            }
                        } else {
                            mPaySessionCb.onResult(mOrder, FAIL);
                        }
                    }
                });
            }
        });
    }
}
