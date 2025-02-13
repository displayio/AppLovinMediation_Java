package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.brandio.ads.Controller;
import com.brandio.ads.ads.Ad;
import com.brandio.ads.ads.AdUnitType;
import com.brandio.ads.containers.BannerContainer;
import com.brandio.ads.containers.InfeedContainer;
import com.brandio.ads.containers.InlineContainer;
import com.brandio.ads.containers.InterscrollerContainer;
import com.brandio.ads.containers.MediumRectangleContainer;
import com.brandio.ads.exceptions.DIOError;
import com.brandio.ads.listeners.AdEventListener;
import com.brandio.ads.listeners.AdRequestListener;
import com.brandio.ads.listeners.SdkInitListener;
import com.brandio.ads.placements.BannerPlacement;
import com.brandio.ads.placements.InfeedPlacement;
import com.brandio.ads.placements.InlinePlacement;
import com.brandio.ads.placements.InterscrollerPlacement;
import com.brandio.ads.placements.MediumRectanglePlacement;
import com.brandio.ads.placements.Placement;
import com.brandio.ads.request.AdRequest;
import com.brandio.ads.request.AdRequestBuilder;
import com.brandio.ads.request.MediationPlatform;

public class DisplayIOMediationAdapter extends MediationAdapterBase implements MaxAdViewAdapter,
        MaxInterstitialAdapter, MaxRewardedAdapter {
    public static final String TAG = "DioAdapter";
    public static final String DIO_AD_REQUEST = "dioAdRequest";
    private Ad interstitialDIOAd;
    private Ad rewardedDIOAd;
    private Ad bannerDIOAd;
    private Ad mrectDIOAd;
    private Ad infeedDIOAd;
    private Ad intersrollerDIOAd;
    private Ad inlineAd;

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

            AppLovinSdkUtils.runOnUiThread(() -> Controller.getInstance().init(
                    activity, appID, new SdkInitListener() {
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
                    }));
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
            interstitialDIOAd = null;
        }
        if (rewardedDIOAd != null && rewardedDIOAd.isImpressed()) {
            rewardedDIOAd = null;
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
        if (inlineAd != null && inlineAd.isImpressed()) {
            inlineAd.close();
            inlineAd = null;
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
                null,
                activity
        );
    }

    //rewarded video ads
    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters,
                               Activity activity,
                               MaxRewardedAdapterListener maxRewardedAdapterListener) {
        if (!isReadyToRequestAd(activity)) {
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.UNSPECIFIED);
            return;
        }
        requestAndLoadDisplayIOAd(
                maxAdapterResponseParameters,
                null,
                null,
                maxRewardedAdapterListener,
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

                        @Override
                        public void onAdStarted(Ad ad) {
                        }

                        @Override
                        public void onSoundToggle(boolean isEnabled) {
                        }
                    }
            );

            activity.runOnUiThread(() -> interstitialDIOAd.showAd(activity));
        } else {
            maxInterstitialAdapterListener.onInterstitialAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
        }
    }

    //rewarded video ads
    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters,
                               Activity activity,
                               MaxRewardedAdapterListener maxRewardedAdapterListener) {
        if (rewardedDIOAd != null) {
            configureReward(maxAdapterResponseParameters);
            rewardedDIOAd.setEventListener(
                    new AdEventListener() {
                        @Override
                        public void onShown(Ad ad) {
                            maxRewardedAdapterListener.onRewardedAdDisplayed();
                        }

                        @Override
                        public void onFailedToShow(Ad ad) {
                            maxRewardedAdapterListener.onRewardedAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
                        }

                        @Override
                        public void onClicked(Ad ad) {
                            maxRewardedAdapterListener.onRewardedAdClicked();
                        }

                        @Override
                        public void onClosed(Ad ad) {
                            maxRewardedAdapterListener.onRewardedAdHidden();
                        }

                        @Override
                        public void onAdCompleted(Ad ad) {
                            MaxReward reward = getReward();
                            if (reward == null) {
                                reward = new MaxReward() {
                                    @Override
                                    public String getLabel() {
                                        return MaxReward.DEFAULT_LABEL;
                                    }

                                    @Override
                                    public int getAmount() {
                                        return MaxReward.DEFAULT_AMOUNT;
                                    }
                                };
                            }
                            maxRewardedAdapterListener.onUserRewarded(reward);
                        }

                        @Override
                        public void onAdStarted(Ad ad) {
                        }

                        @Override
                        public void onSoundToggle(boolean isEnabled) {
                        }
                    }
            );

            activity.runOnUiThread(() -> rewardedDIOAd.showAd(activity));
        } else {
            maxRewardedAdapterListener.onRewardedAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED);
        }
    }

    private void requestAndLoadDisplayIOAd(
            MaxAdapterResponseParameters maxAdapterResponseParameters,
            MaxAdViewAdapterListener inlineAdListener,
            MaxInterstitialAdapterListener interstitialListener,
            MaxRewardedAdapterListener rewardedListener,
            Activity activity
    ) {
        String plcID = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        if (plcID == null || plcID.isEmpty()) {
            Log.e(TAG, "Error, placement ID missed, set up placement ID on your AppLovin dashboard");
            notifyError(inlineAdListener, interstitialListener, rewardedListener, MaxAdapterError.INTERNAL_ERROR);
            return;
        }

        final Placement placement;
        try {
            placement = Controller.getInstance().getPlacement(plcID);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error, no placement with ID " + plcID);
            notifyError(inlineAdListener, interstitialListener, rewardedListener, MaxAdapterError.INTERNAL_ERROR);
            return;
        }

        AdUnitType placementType = placement.getType();
        AdRequest adRequest = (AdRequest) maxAdapterResponseParameters.getLocalExtraParameters().get(DIO_AD_REQUEST);

        boolean isUsed = false;
        if (adRequest != null) {
            try {
                isUsed = placement.getAdRequestById(adRequest.getId()) != null || adRequest.wasRequested();
            } catch (Exception ignored) {
                Log.e(TAG, "Exception ", ignored);
            }
        }

        if (adRequest == null || isUsed) {
            adRequest = placement.newAdRequestBuilder()
                    .setMediationPlatform(MediationPlatform.APPLOVIN)
                    .build();
        } else {
            adRequest = new AdRequestBuilder(adRequest)
                    .setMediationPlatform(MediationPlatform.APPLOVIN)
                    .build();
            placement.addAdRequest(adRequest);
        }

        final String adRequestId = adRequest.getId();
        adRequest.setAdRequestListener(new AdRequestListener() {
            @Override
            public void onAdReceived(Ad ad) {

                if (placementType == AdUnitType.INTERSTITIAL) {
                    if (interstitialListener != null) {
                        interstitialDIOAd = ad;
                        interstitialListener.onInterstitialAdLoaded();
                    }
                    if (rewardedListener != null) {
                        rewardedDIOAd = ad;
                        rewardedListener.onRewardedAdLoaded();
                    }
                    return;
                }

                ViewGroup adView = InlineContainer.getAdView(activity);
                switch (placementType) {
                    case BANNER:
                        bannerDIOAd = ad;
                        BannerContainer bannerContainer = ((BannerPlacement) placement)
                                .getContainer(adRequestId);
                        bannerContainer.bindTo(adView);
                        break;
                    case MEDIUMRECTANGLE:
                        mrectDIOAd = ad;
                        MediumRectangleContainer mediumRectangleContainer = ((MediumRectanglePlacement) placement)
                                .getContainer(adRequestId);
                        mediumRectangleContainer.bindTo(adView);
                        break;
                    case INFEED:
                        infeedDIOAd = ad;
                        InfeedContainer infeedContainer =
                                ((InfeedPlacement) placement).getContainer(adRequestId);
                        infeedContainer.bindTo(adView);
                        break;
                    case INTERSCROLLER:
                        intersrollerDIOAd = ad;
                        InterscrollerContainer interscrollerContainer =
                                ((InterscrollerPlacement) placement).getContainer(adRequestId);
                        interscrollerContainer.bindTo(adView);
                        break;
                    case INLINE:
                        inlineAd = ad;
                        InlineContainer inlineContainer =
                                ((InlinePlacement) placement).getContainer(adRequestId);
                        inlineContainer.bindTo(adView);
                        break;
                }

                adView.setTag(ad.getAdUnitType());
                ad.setEventListener(
                        new AdEventListener() {
                            @Override
                            public void onShown(Ad ad) {
                                inlineAdListener.onAdViewAdDisplayed();
                            }

                            @Override
                            public void onFailedToShow(Ad ad) {
                                notifyError(inlineAdListener,
                                        interstitialListener,
                                        rewardedListener,
                                        MaxAdapterError.AD_DISPLAY_FAILED);
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

                            @Override
                            public void onAdStarted(Ad ad) {
                            }

                            @Override
                            public void onSoundToggle(boolean isEnabled) {
                            }
                        }
                );
                inlineAdListener.onAdViewAdLoaded(adView);

            }

            @Override
            public void onNoAds(DIOError dioError) {
                Log.e(TAG, "No Ads for placement " + plcID);
                notifyError(inlineAdListener, interstitialListener, rewardedListener, MaxAdapterError.NO_FILL);
            }

            @Override
            public void onFailedToLoad(DIOError dioError) {
                Log.e(TAG, "Failed to load ad for placement " + plcID);
                notifyError(inlineAdListener, interstitialListener, rewardedListener, MaxAdapterError.NO_FILL);
            }
        });
        adRequest.requestAd();
    }

    private void notifyError(MaxAdViewAdapterListener inlineAdListener,
                             MaxInterstitialAdapterListener interstitialListener,
                             MaxRewardedAdapterListener rewardedListener,
                             MaxAdapterError error) {
        if (inlineAdListener != null) {
            inlineAdListener.onAdViewAdLoadFailed(error);
        }
        if (interstitialListener != null) {
            interstitialListener.onInterstitialAdLoadFailed(error);
        }
        if (rewardedListener != null) {
            rewardedListener.onRewardedAdDisplayFailed(error);
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
