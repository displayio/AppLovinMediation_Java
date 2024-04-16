package com.example.applovinmediation;

import static com.example.applovinmediation.MainActivity.AD_UNIT_ID;
import static com.example.applovinmediation.MainActivity.AD_UNIT_TYPE;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

import java.util.Objects;

public class ListActivity extends AppCompatActivity {
    //    static final String placementID = "7022";
    static final String DisplayIO = "DisplayIO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        View adView = createAd(getIntent().getStringExtra(AD_UNIT_ID));
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType != 20) {
                    View view = new View(ListActivity.this);
                    view.setBackgroundColor(Color.LTGRAY);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                    lp.setMargins(10, 10, 10, 10);
                    view.setLayoutParams(lp);
                    return new RecyclerView.ViewHolder(view) {
                    };
                } else {
                    return new RecyclerView.ViewHolder(adView) {
                    };
                }
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemCount() {
                return 40;
            }
        });
    }

    private MaxAdView createAd(String adUnitId) {
        MaxAdView adView = new MaxAdView(adUnitId, this);
//        adView.setLayoutParams(new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                800));
        adView.setGravity(Gravity.CENTER);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {

                MainActivity.AdUnitType adUnitType = MainActivity.AdUnitType.valueOf(getIntent().getStringExtra(AD_UNIT_TYPE));
                //necessary for displaying IS
                if (adUnitType == MainActivity.AdUnitType.INTERSCROLLER
                        && Objects.equals(ad.getNetworkName(), DisplayIO)) {
                    adView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    String placementID = ad.getNetworkPlacement();
                    View view = adView.findViewById(Integer.parseInt(placementID));
                    view.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                }
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
                Log.e(MainActivity.TAG, "onAdLoadFailed  " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        MainActivity.addCustomAdRequestData(null, adView);
        adView.loadAd();
        return adView;
    }
}

