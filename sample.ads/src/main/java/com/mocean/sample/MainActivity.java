package com.mocean.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mocean.ActionMonitor;
import com.mocean.AdServiceManager;
import com.mocean.IAd;
import com.mocean.IAdItem;
import com.mocean.IAdService;
import com.mocean.ICallback;
import com.mocean.IInterstitialAd;
import com.mocean.INativeAd;
import com.mocean.IServiceCallback;

public class MainActivity extends Activity {

	IAdService mAdService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ActionMonitor().onReceive(this, new Intent(Intent.ACTION_BOOT_COMPLETED));

		setContentView(R.layout.activity_main);

		Button btnInterstitialSample = (Button)findViewById(R.id.interstitialSample);
		btnInterstitialSample.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null == mAdService){
					return;
				}
				IInterstitialAd ad = mAdService.getInterstitialAd("interstitial.default");
				ad.popup();
			}
		});

		Button btnNativeSample = (Button)findViewById(R.id.nativeSample);
		btnNativeSample.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null == mAdService){
					return;
				}
				INativeAd ad = mAdService.getNativeAd("native.ad1", 50, 50, 1, null);
				final View container = findViewById(R.id.container);
				IAdItem item = ad.getAdItem(0);
				item.bind(container,
						new String[]{IAdItem.ICON, IAdItem.TITLE, IAdItem.CALL_TO_ACTION},
						new int[]{R.id.ivNative, R.id.tvTitle, R.id.btnCta});
				container.setVisibility(View.GONE);
				ad.setOnLoadLisenter(new ICallback(){

					@Override
					public void call(int resultCode) {
						if (resultCode == IAd.OK){
							container.setVisibility(View.VISIBLE);
						}
					}
				});
				ad.load();
			}
		});

		AdServiceManager.get(MainActivity.this, new IServiceCallback<IAdService>() {
			@Override
			public void call(IAdService iAdService) {
				mAdService = iAdService;
			}
		});
	}

}
