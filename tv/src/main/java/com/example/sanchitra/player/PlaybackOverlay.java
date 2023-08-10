package com.example.sanchitra.player;

import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import android.os.Handler;
import android.view.View;

public class PlaybackOverlay extends VideoSupportFragment {

    private int mCurrentPlaybackState;
    private Handler mHandler;
    private Runnable mRunnable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    public void playbackStateChanged() {
//
//        if (mCurrentPlaybackState != PlaybackState.STATE_PLAYING) {
//            mCurrentPlaybackState = PlaybackState.STATE_PLAYING;
////            startProgressAutomation();
//            setFadingEnabled(true);
//            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PAUSE);
//            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PAUSE));
//            notifyChanged(mPlayPauseAction);
//        } else if (mCurrentPlaybackState != PlaybackState.STATE_PAUSED) {
//            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
//            stopProgressAutomation();
//            //setFadingEnabled(false); // if set to false, PlaybackcontrolsRow will always be on the screen
//            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PLAY);
//            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PLAY));
//            notifyChanged(mPlayPauseAction);
//        }
//
//        int currentTime = ((PlaybackOverlayActivity) getActivity()).getPosition();
//        mPlaybackControlsRow.setCurrentTime(currentTime);
//        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
//
//    }

//    private void togglePlayback(boolean playPause) {
//        /* Video control part */
////        ((VideoPlayer)).playPause(playPause);
//        /* UI control part */
////        playbackStateChanged();
//    }

//    private void startProgressAutomation() {
//        if (mRunnable == null) {
//            mRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    int updatePeriod = getUpdatePeriod();
//                    int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
//                    int totalTime = mPlaybackControlsRow.getTotalTime();
//                    mPlaybackControlsRow.setCurrentTime(currentTime);
//                    mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
//
//                    if (totalTime > 0 && totalTime <= currentTime) {
//                        stopProgressAutomation();
//                        next(true);
//                    } else {
//                        mHandler.postDelayed(this, updatePeriod);
//                    }
//                }
//            };
//            mHandler.postDelayed(mRunnable, getUpdatePeriod());
//        }
//    }


}