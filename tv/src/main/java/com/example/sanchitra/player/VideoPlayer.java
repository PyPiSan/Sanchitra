package com.example.sanchitra.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.widget.VideoView;

import com.example.sanchitra.R;
import com.google.firebase.installations.Utils;

public class VideoPlayer extends Activity {

    private static final String TAG = VideoPlayer.class.getSimpleName();
    private VideoView mVideoView;
    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private int mPosition = 0;
    private long mStartTimeMillis;
    private long mDuration = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        loadViews();
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, IDLE
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);
        setVideoPath("");
    }

    public void setVideoPath(String videoUrl) {
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;;
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mVideoView.suspend();
        mVideoView.setVideoURI(null);
    }
}