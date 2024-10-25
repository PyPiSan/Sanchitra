package com.example.sanchitra.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.OnboardingSupportFragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.sanchitra.R;
import com.example.sanchitra.api.UserInit;
import com.example.sanchitra.model.UserModel;
import com.example.sanchitra.utils.Constant;
import com.example.sanchitra.utils.RequestModule;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnboardingFragment extends OnboardingSupportFragment {
    private static final long SPLASH_DURATION_MS = 4000;
//    private ImageView contentView;
    @Override
    protected int getPageCount() {
        return 1;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        return null;
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
//        contentView = new ImageView(getContext());
//        contentView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        contentView.setImageResource(R.mipmap.ic_banner);
//        contentView.setPadding(0, 32, 0, 32);
//        return contentView;
        return null;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }
//    @Override
//    public Animator onCreateLogoAnimation() {
//        return AnimatorInflater.loadAnimator(getContext(),
//                R.animator.onboarding_logo_screen_animation);
//    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
//        if (view != null) {
//            view.setBackgroundColor(Color.RED);
//        }
//        setLogoResourceId(R.raw.sanchitra_raw);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLogoResourceId(R.raw.ic_banner_foreground);
        @SuppressLint("HardwareIds") String deviceUser = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String myVersion = "Android "+ Build.VERSION.RELEASE;
        Constant.uid = deviceUser;
        String origin = getContext().getResources().getConfiguration().locale.getCountry();
        try {
            PackageInfo pInfo = getContext().getPackageManager().
                    getPackageInfo(getContext().getPackageName(), 0);
            Constant.versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RequestModule getID = retrofit.create(RequestModule.class);
        Call<UserModel> call = getID.getUser(new UserInit(deviceUser, origin, myVersion,
                Constant.versionName, "tv"));
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                boolean flag = false;
                UserModel resource = response.body();
                Log.d("test", String.valueOf(response.code()));
                if (response.code() == 200) {
                    flag = resource.getUserStatus();
                    Constant.key=resource.getApikey();
                }
                if (flag) {
                    if (resource.getLogged()){
                        Constant.loggedInStatus = true;
                        Constant.logo =resource.getIcon();
                        Constant.userName = resource.getUserData();
//                        Constant.message = resource.getNotification();
                        if (resource.getAds() !=null){Constant.isFree = resource.getAds();}

                    }else{
                        Constant.loggedInStatus = false;
                    }
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                                    onFinishFragment();
                            };
                        handler.postDelayed(runnable, SPLASH_DURATION_MS);
                        }
                 }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed, Try Again "+t, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(() -> onFinishFragment(), 5000);
                }

        });

    }

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        // User has seen OnboardingSupportFragment, so mark our SharedPreferences
        // flag as completed so that we don't show our OnboardingSupportFragment
        // the next time the user launches the app
//        SharedPreferences.Editor sharedPreferencesEditor =
//                PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
//        sharedPreferencesEditor.putBoolean(
//                COMPLETED_ONBOARDING_PREF_NAME, true);
//        sharedPreferencesEditor.apply();
        getActivity().finish();
    }
}
