package com.example.applovinmediation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.brandio.ads.Controller;
import com.brandio.ads.InterscrollerPlacement;

import java.util.Objects;

public class ListActivity extends AppCompatActivity {
    static final String placementID = "7022";
    static final String DisplayIO = "DisplayIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        View adView = createAd();
        RecyclerView recyclerView = findViewById(R.id.rv);

        //necessary for displaying IS
        try {
            InterscrollerPlacement placement = (InterscrollerPlacement) Controller.getInstance().getPlacement(placementID);
            placement.setParentRecyclerView(recyclerView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType != 20) {
                    View view = new View(ListActivity.this);
                    view.setBackgroundColor(Color.BLUE);
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

    private MaxAdView createAd() {
        MaxAdView adView = new MaxAdView("a7890d10e5dc7459", this);
        adView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
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

                //necessary for displaying IS
                if (Objects.equals(ad.getNetworkName(), DisplayIO)){
                    View view = adView.findViewById(Integer.parseInt(placementID));
                    view.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
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

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        adView.loadAd();
        return adView;
    }
}

