package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

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
import com.brandio.ads.Controller;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.containers.BannerContainer;
import com.brandio.ads.containers.InfeedContainer;
import com.brandio.ads.containers.InterscrollerContainer;
import com.brandio.ads.containers.MediumRectangleContainer;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.exceptions.DioSdkException;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.listeners.SdkInitListener;
import com.brandio.ads.placements.BannerPlacement;
import com.brandio.ads.placements.InfeedPlacement;
import com.brandio.ads.placements.InterscrollerPlacement;
import com.brandio.ads.placements.InterstitialPlacement;
import com.brandio.ads.placements.MediumRectanglePlacement;
import com.brandio.ads.placements.Placement;
import com.brandio.ads.request.AdRequest;
import com.brandio.ads.request.AdRequestBuilder;
import com.brandio.ads.request.MediationPlatform;

public class DisplayIOMediationAdapter extends MediationAdapterBase implements MaxAdViewAdapter,
        MaxInterstitialAdapter {
    public static final String TAG = "DioAdapter";
    public static final String DIO_AD_REQUEST = "dioAdRequest";
    private Ad interstitialDIOAd;
    private Ad bannerDIOAd;
    private Ad mrectDIOAd;
    private Ad infeedDIOAd;
    private Ad intersrollerDIOAd;

    public DisplayIOMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters,
                           Activity activity,
                           OnCompletionListener onCompletionListener) {
        String appID = maxAdapterInitializationParameters.getServerParameters().getString("app_id");

        if (appID == null) {
            onCompletionListener.onCompletion(
                    InitializationStatus.INITIALIZED_FAILURE,
                    null
            );
            return;
        }
        if (!Controller.getInstance().isInitialized()) {
            onCompletionListener.onCompletion(
                    InitializationStatus.INITIALIZING,
                    null
            );

            AppLovinSdkUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Controller.getInstance().init(activity, appID, new SdkInitListener() {
                        @Override
                        public void onInit() {
                            onCompletionListener.onCompletion(
                                    InitializationStatus.INITIALIZED_SUCCESS,
                                    null
                            );
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
        if (interstitialDIOAd != null && interstitialDIOAd.isImpressed()) {
            interstitialDIOAd = null; // interstitial ads closed automatically
        }
        if (bannerDIOAd != null && bannerDIOAd.isImpressed()) {
            bannerDIOAd.close();
            bannerDIOAd = null;
        }
        if (mrectDIOAd != null && mrectDIOAd.isImpressed()) {
            mrectDIOAd.close();
            mrectDIOAd = null;
        }
        if (infeedDIOAd != null && infeedDIOAd.isImpressed()) {
            infeedDIOAd.close();
            infeedDIOAd = null;
        }
        if (intersrollerDIOAd != null && intersrollerDIOAd.isImpressed()) {
            intersrollerDIOAd.close();
            intersrollerDIOAd = null;
        }
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

        requestAndLoadDisplayIOAd(
                maxAdapterResponseParameters,
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
        requestAndLoadDisplayIOAd(
                maxAdapterResponseParameters,
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
        } else {
            maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
        }
    }

    private void requestAndLoadDisplayIOAd(
            MaxAdapterResponseParameters maxAdapterResponseParameters,
            MaxAdViewAdapterListener inlineAdListener,
            MaxInterstitialAdapterListener interstitialListener,
            Activity activity
    ) {
        String plcID = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        if (plcID == null || plcID.isEmpty()) {
            Log.e(TAG, "Error, placement ID missed, set up placement ID on your AppLovin dashboard");
            notifyError(inlineAdListener, interstitialListener, MaxAdapterError.INTERNAL_ERROR);
            return;
        }

        final Placement placement;
        try {
            placement = Controller.getInstance().getPlacement(plcID);
        } catch (DioSdkException e) {
            Log.e(TAG, "Unexpected error, no placement with ID " + plcID);
            notifyError(inlineAdListener, interstitialListener, MaxAdapterError.INTERNAL_ERROR);
            return;
        }

        AdRequest adRequest = null;
        boolean isUsed = false;

        try {
            adRequest = (AdRequest) maxAdapterResponseParameters.getLocalExtraParameters().get(DIO_AD_REQUEST);
            isUsed = placement.getAdRequestById(adRequest.getId()) != null;
        } catch (Exception ignored) {
        }

        if (adRequest != null && !isUsed) {
            adRequest = new AdRequestBuilder(adRequest)
                    .setMediationPlatform(MediationPlatform.APPLOVIN).build();
            placement.addAdRequest(adRequest);
        } else {
            adRequest = placement.newAdRequestBuilder()
                    .setMediationPlatform(MediationPlatform.APPLOVIN).build();
        }
        final String adRequestId = adRequest.getId();
        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(Ad ad) {

                if (placement instanceof InterstitialPlacement) {
                    interstitialDIOAd = ad;
                    if (interstitialListener != null) {
                        interstitialListener.onInterstitialAdLoaded();
                    }
                    return;
                }

                ViewGroup adView = null;
                if (placement instanceof BannerPlacement) {
                    bannerDIOAd = ad;
                    BannerContainer bannerContainer = ((BannerPlacement) placement)
                            .getContainer(activity, adRequestId);
                    adView = BannerContainer.getAdView(activity);
                    bannerContainer.bindTo(adView);
                } else if (placement instanceof MediumRectanglePlacement) {
                    mrectDIOAd = ad;
                    MediumRectangleContainer mediumRectangleContainer = ((MediumRectanglePlacement) placement)
                            .getContainer(activity, adRequestId);
                    adView = BannerContainer.getAdView(activity);
                    mediumRectangleContainer.bindTo(adView);
                } else if (placement instanceof InfeedPlacement) {
                    infeedDIOAd = ad;
                    adView = InfeedContainer.getAdView(activity);
                    InfeedContainer infeedContainer =
                            ((InfeedPlacement) placement).getContainer(activity, adRequestId);
                    infeedContainer.bindTo((ViewGroup) adView);
                } else if (placement instanceof InterscrollerPlacement) {
                    intersrollerDIOAd = ad;
                    adView = InterscrollerContainer.getAdView(activity);
                    InterscrollerContainer interscrollerContainer =
                            ((InterscrollerPlacement) placement).getContainer(activity, adRequestId);
                    try {
                        interscrollerContainer.bindTo((ViewGroup) adView);
                    } catch (Exception e) {
                        notifyError(inlineAdListener, interstitialListener, MaxAdapterError.INTERNAL_ERROR);
                        e.printStackTrace();
                    }
                }

                if (adView != null) {
                    adView.setId(Integer.parseInt(plcID));
                    ad.setEventListener(
                            new AdEventListener() {
                                @Override
                                public void onShown(Ad ad) {
                                    inlineAdListener.onAdViewAdDisplayed();
                                }

                                @Override
                                public void onFailedToShow(Ad ad) {
                                    notifyError(inlineAdListener, interstitialListener, MaxAdapterError.AD_DISPLAY_FAILED);
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
                    notifyError(inlineAdListener, interstitialListener, MaxAdapterError.NO_FILL);
                }
            }

            @Override
            public void onNoAds(DIOError dioError) {
                Log.e(TAG, "No Ads for placement " + plcID);
                notifyError(inlineAdListener, interstitialListener, MaxAdapterError.NO_FILL);
            }

            @Override
            public void onFailedToLoad(DIOError dioError) {
                Log.e(TAG, "Failed to load ad for placement " + plcID);
                notifyError(inlineAdListener, interstitialListener, MaxAdapterError.NO_FILL);
            }
        });
        adRequest.requestAd();
    }

    private void notifyError(MaxAdViewAdapterListener inlineAdListener,
                             MaxInterstitialAdapterListener interstitialListener,
                             MaxAdapterError error) {
        if (inlineAdListener != null) {
            inlineAdListener.onAdViewAdLoadFailed(error);
        }
        if (interstitialListener != null) {
            interstitialListener.onInterstitialAdLoadFailed(error);
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
