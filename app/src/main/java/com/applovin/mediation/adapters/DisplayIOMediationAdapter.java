package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.brandio.ads.AdProvider;
import com.brandio.ads.AdRequest;
import com.brandio.ads.BannerPlacement;
import com.brandio.ads.Controller;
import com.brandio.ads.InfeedPlacement;
import com.brandio.ads.InterscrollerPlacement;
import com.brandio.ads.InterstitialPlacement;
import com.brandio.ads.MediumRectanglePlacement;
import com.brandio.ads.Placement;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.containers.InfeedAdContainer;
import com.brandio.ads.containers.InterscrollerContainer;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.exceptions.DioSdkException;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdLoadListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.listeners.SdkInitListener;

public class DisplayIOMediationAdapter extends MediationAdapterBase implements MaxAdViewAdapter,
        MaxInterstitialAdapter {
    public static final String TAG = "DioAdapter";
    private static final String APP_ID = "7729";
    private Ad interstitialDIOAd;

    public DisplayIOMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters,
                           Activity activity,
                           OnCompletionListener onCompletionListener) {
        Log.e(TAG,  "maxAdapterInitializationParameters.getServerParameters().getString(\"app_id\"):");
        Log.e(TAG,  maxAdapterInitializationParameters.getServerParameters().getString("app_id"));
        Log.e(TAG,  maxAdapterInitializationParameters.getServerParameters().toString());

        if (!Controller.getInstance().isInitialized()) {
            onCompletionListener.onCompletion(
                    InitializationStatus.INITIALIZING,
                    null
            );

            AppLovinSdkUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Controller.getInstance().init(activity, null, APP_ID, new SdkInitListener() {
                        @Override
                        public void onInit() {
                            onCompletionListener.onCompletion(
                                    InitializationStatus.INITIALIZED_SUCCESS,
                                    null
                            );
                            Toast.makeText(activity, "DIO Initialized!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onInitError(DIOError dioError) {
                            onCompletionListener.onCompletion(
                                    InitializationStatus.INITIALIZED_FAILURE,
                                    null
                            );
                        }
                    });
                }
            });
        } else {
            onCompletionListener.onCompletion(
                    MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS,
                    null
            );
        }

    }

    @Override
    public String getSdkVersion() {
        return Controller.getInstance().getVer();
    }

    @Override
    public String getAdapterVersion() {
        return Controller.getInstance().getVer();
    }

    @Override
    public void onDestroy() {
        Controller.getInstance().onDestroy();
    }

    //inline ads
    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters maxAdapterResponseParameters,
                             MaxAdFormat maxAdFormat,
                             Activity activity,
                             MaxAdViewAdapterListener maxAdViewAdapterListener) {

        if (!isReadyToRequestAd(activity)) {
            maxAdViewAdapterListener.onAdViewAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            return;
        }

        String plcID = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        requestAndLoadDisplayIOAd(
                plcID,
                maxAdViewAdapterListener,
                null,
                activity
        );
    }

    //interstitial ads
    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters,
                                   Activity activity,
                                   MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        if (!isReadyToRequestAd(activity)) {
            maxInterstitialAdapterListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            return;
        }

        String plcID = maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        requestAndLoadDisplayIOAd(
                plcID,
                null,
                maxInterstitialAdapterListener,
                activity
        );
    }

    //interstitial ads
    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters,
                                   Activity activity, MaxInterstitialAdapterListener
                                           maxInterstitialAdapterListener) {
        if (interstitialDIOAd != null) {
            interstitialDIOAd.setEventListener(
                    new AdEventListener() {
                        @Override
                        public void onShown(Ad ad) {
                            maxInterstitialAdapterListener.onInterstitialAdDisplayed();
                        }

                        @Override
                        public void onFailedToShow(Ad ad) {
                            maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
                        }

                        @Override
                        public void onClicked(Ad ad) {
                            maxInterstitialAdapterListener.onInterstitialAdClicked();
                        }

                        @Override
                        public void onClosed(Ad ad) {
                            maxInterstitialAdapterListener.onInterstitialAdHidden();
                        }

                        @Override
                        public void onAdCompleted(Ad ad) {

                        }
                    }
            );
            interstitialDIOAd.showAd(activity);
        }
    }

    private void requestAndLoadDisplayIOAd(
            String plcID,
            MaxAdViewAdapterListener inlineAdListener,
            MaxInterstitialAdapterListener interstitialListener,
            Activity activity
    ) {

        final Placement placement;
        try {
            placement = Controller.getInstance().getPlacement(plcID);
        } catch (DioSdkException e) {
            Log.e(TAG, "Unexpected error, no placement with ID " + plcID);
            notifyError(inlineAdListener, interstitialListener);
            return;
        }
        AdRequest adRequest = placement.newAdRequest();
        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(AdProvider adProvider) {

                adProvider.setAdLoadListener(new AdLoadListener() {
                    @Override
                    public void onLoaded(Ad ad) {
                        if (placement instanceof InterstitialPlacement) {
                            interstitialDIOAd = ad;
                            if (interstitialListener != null) {
                                interstitialListener.onInterstitialAdLoaded();
                            }
                            return;
                        }

                        View adView = null;
                        if (placement instanceof BannerPlacement) {
                            adView = ((BannerPlacement) placement).getBanner(
                                    activity,
                                    adRequest.getId()
                            );
                        } else if (placement instanceof MediumRectanglePlacement) {
                            adView = ((MediumRectanglePlacement) placement).getMediumRectangle(
                                    activity,
                                    adRequest.getId()
                            );
                        } else if (placement instanceof InfeedPlacement) {
                            adView = InfeedAdContainer.getAdView(activity);
                            InfeedAdContainer infeedContainer =
                                    ((InfeedPlacement) placement).getInfeedContainer(activity, adRequest.getId());
                            infeedContainer.bindTo((ViewGroup) adView);
                        } else if (placement instanceof InterscrollerPlacement) {
                            adView = InterscrollerContainer.getAdView(activity);
                            adView.setId(Integer.parseInt(plcID));
                            InterscrollerContainer interscrollerContainer =
                                    ((InterscrollerPlacement) placement).getContainer(activity, adRequest.getId(), null);
                            try {
                                interscrollerContainer.bindTo((ViewGroup) adView);
                            } catch (Exception e) {
                                inlineAdListener.onAdViewAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
                                e.printStackTrace();
                            }
                        }

                        if (adView != null) {
                            ad.setEventListener(
                                    new AdEventListener() {
                                        @Override
                                        public void onShown(Ad ad) {
                                            inlineAdListener.onAdViewAdDisplayed();
                                        }

                                        @Override
                                        public void onFailedToShow(Ad ad) {
                                            inlineAdListener.onAdViewAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
                                        }

                                        @Override
                                        public void onClicked(Ad ad) {
                                            inlineAdListener.onAdViewAdClicked();
                                        }

                                        @Override
                                        public void onClosed(Ad ad) {
                                            inlineAdListener.onAdViewAdHidden();
                                        }

                                        @Override
                                        public void onAdCompleted(Ad ad) {

                                        }
                                    }
                            );
                            inlineAdListener.onAdViewAdLoaded(adView);
                        } else {
                            inlineAdListener.onAdViewAdLoadFailed(MaxAdapterError.NO_FILL);
                        }

                    }

                    @Override
                    public void onFailedToLoad(DIOError dioError) {
                        Log.e(TAG, "Failed to load ad for placement " + plcID);
                        notifyError(inlineAdListener, interstitialListener);
                    }
                });
                try {
                    adProvider.loadAd();
                } catch (DioSdkException e) {
                    Log.e(TAG, "Failed to load ad for placement " + plcID);
                    notifyError(inlineAdListener, interstitialListener);
                }
            }

            @Override
            public void onNoAds(DIOError dioError) {
                Log.e(TAG, "No Ads for placement " + plcID);
                notifyError(inlineAdListener, interstitialListener);
            }
        });
        adRequest.requestAd();
    }

    private void notifyError(MaxAdViewAdapterListener inlineAdListener,
                             MaxInterstitialAdapterListener interstitialListener) {
        if (inlineAdListener != null) {
            inlineAdListener.onAdViewAdLoadFailed(MaxAdapterError.UNSPECIFIED);
        }
        if (interstitialListener != null) {
            interstitialListener.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED);
        }
    }

    private boolean isReadyToRequestAd(Activity activity) {
        if (!Controller.getInstance().isInitialized()) {
            Log.e(TAG, "DIO SDK is not initialized!");
            return false;
        }

        if (activity == null) {
            Log.e(TAG, "Activity cannot be null");
            return false;
        }
        return true;
    }
}
