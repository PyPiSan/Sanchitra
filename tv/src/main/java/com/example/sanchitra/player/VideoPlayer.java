package com.example.sanchitra.player;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.sanchitra.R;

import java.util.HashMap;

public class VideoPlayer extends Activity {

    private VideoView mVideoView;
    //    private LeanbackPlaybackState mPlaybackState;
    private MediaSession mSession;
    private String uriPath = "https://www005.vipanicdn.net/streamhls/027e9529af2b06fe7b4f47e507a787eb/ep.1.1677593055.1080.m3u8";

    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private int mPosition = 0;
    private long mStartTimeMillis;
    private long mDuration = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
        loadViews();

    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);
        setVideoPath(uriPath);
    }

    public void setVideoPath(String videoUrl) {
        setPosition(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;
        mDuration = getDuration(videoUrl);
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, IDLE
    }

    private void setPosition(int position) {
        if (position > mDuration) {
            mPosition = (int) mDuration;
        } else if (position < 0) {
            mPosition = 0;
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPosition = position;
        }
        mStartTimeMillis = System.currentTimeMillis();
        Log.d("video", "position set to " + mPosition);
    }

    private long getDuration(String videoUrl) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(videoUrl, new HashMap<String, String>());
        } else {
            mmr.setDataSource(videoUrl);
        }
        return Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }

    }

    public void playPause(boolean doPlay) {
        if (mPlaybackState == LeanbackPlaybackState.IDLE) {
            /* Callbacks for mVideoView */
            setupCallbacks();
        }
        if (doPlay && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            if (mPosition > 0) {
                mVideoView.seekTo(mPosition);
            }
            mVideoView.start();
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
            setPosition(mPosition + timeElapsedSinceStart);
            mVideoView.pause();
        }
    }

    private void setupCallbacks() {

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    mVideoView.start();
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlaybackState = LeanbackPlaybackState.IDLE;
            }
        });
    }
}