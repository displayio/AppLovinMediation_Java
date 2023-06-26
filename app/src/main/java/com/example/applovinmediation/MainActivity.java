package com.example.applovinmediation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private MaxAdView adView;
    private MaxInterstitialAd interstitialAd;
    private ViewGroup rootAdView;

    enum AdUnitType {
        BANNER,
        MEDIUMRECT,
        INFEED,
        INTERSTITIAL,
        INTERSCROLLER,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootAdView = findViewById(R.id.reserved_for_ad);
        initAppLovinSdk();

    }


    private void initAppLovinSdk() {
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
        AppLovinSdk.getInstance(this).initializeSdk(new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(AppLovinSdkConfiguration config) {
                Log.e(TAG, "AppLovin Initialized!");
                MainActivity.this.showToast("AppLovin Initialized!");
                setupButtons();
                interstitialAd = new MaxInterstitialAd("879e5078a8df075e", MainActivity.this);
            }
        });
    }

    private void setupButtons() {

        findViewById(R.id.button_banner).setOnClickListener(view -> {
            createAd(AdUnitType.BANNER);
        });
        findViewById(R.id.button_medium_rect).setOnClickListener(view -> {
            createAd(AdUnitType.MEDIUMRECT);
        });
        findViewById(R.id.button_infeed).setOnClickListener(view -> {
            createAd(AdUnitType.INFEED);
        });
        findViewById(R.id.button_interstitial).setOnClickListener(view -> {
            createAd(AdUnitType.INTERSTITIAL);
        });
        findViewById(R.id.button_interscroller).setOnClickListener(view -> {
            createAd(AdUnitType.INTERSCROLLER);
        });

        findViewById(R.id.button_show_interstitial).setOnClickListener(view -> {
            showInterstitial();
        });

        findViewById(R.id.button_banner).setEnabled(true);
        findViewById(R.id.button_medium_rect).setEnabled(true);
        findViewById(R.id.button_infeed).setEnabled(true);
        findViewById(R.id.button_interstitial).setEnabled(true);
        findViewById(R.id.button_interscroller).setEnabled(true);

    }

    private void showInterstitial() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
            findViewById(R.id.button_show_interstitial).setEnabled(false);
        }
    }

    private void createAd(AdUnitType adUnitType) {
        switch (adUnitType) {
            case BANNER:
                adView = new MaxAdView("47dca8ad50135955", this);
                break;
            case MEDIUMRECT:
                adView = new MaxAdView("443e36ed302312ce", this);
                break;
            case INFEED:
                adView = new MaxAdView("f850d5aa98acf91b", this);
                break;
            case INTERSTITIAL:
                loadInterstitialdAd();
                return;
            case INTERSCROLLER:
                createInterscrollerAd();
                return;

        }
        adView.setListener(maxAdViewAdListener);
        adView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800));
        adView.setGravity(Gravity.CENTER);
        adView.setBackgroundColor(Color.WHITE);
//        adView.setExtraParameter( "allow_pause_auto_refresh_immediately", "true" );
//        adView.stopAutoRefresh();
        rootAdView.removeAllViews();
        rootAdView.addView(adView);

        adView.loadAd();
    }

    private void loadInterstitialdAd() {

        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                showToast("MaxInterstitialAd LOADED!");
                findViewById(R.id.button_show_interstitial).setEnabled(true);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG,"MaxInterstitialAd onAdLoadFailed!");
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG,"MaxInterstitialAd onAdDisplayFailed!");
            }
        });
        interstitialAd.loadAd();
    }

    private void createInterscrollerAd() {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }


    static MaxAdViewAdListener maxAdViewAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdLoaded(MaxAd ad) {
            Log.e(TAG, "onAdLoaded");
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            Log.e(TAG, "onAdDisplayed");
        }

        @Override
        public void onAdHidden(MaxAd ad) {
        }

        @Override
        public void onAdClicked(MaxAd ad) {
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        }

        @Override
        public void onAdExpanded(MaxAd ad) {
        }

        @Override
        public void onAdCollapsed(MaxAd ad) {
        }
    };


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}