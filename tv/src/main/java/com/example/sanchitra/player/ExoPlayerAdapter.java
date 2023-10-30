package com.example.sanchitra.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import androidx.leanback.media.PlaybackGlueHost;
import androidx.leanback.media.PlayerAdapter;
import androidx.leanback.media.SurfaceHolderGlueHost;
import android.view.SurfaceHolder;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

public class ExoPlayerAdapter extends PlayerAdapter{

    Context mContext;
    SimpleExoPlayer mPlayer;
    SurfaceHolderGlueHost mSurfaceHolderGlueHost;
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getCallback().onCurrentPositionChanged(ExoPlayerAdapter.this);
            getCallback().onBufferedPositionChanged(ExoPlayerAdapter.this);
            mHandler.postDelayed(this, getUpdatePeriod());
        }
    };
    final Handler mHandler = new Handler();
    boolean mInitialized = false;
    Uri mMediaSourceUri = null;
    boolean mHasDisplay;
    boolean mBufferingStart;
    @C.StreamType int mAudioStreamType;

    public ExoPlayerAdapter(Context context) {
        mContext = context;
        int flags = DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
                | DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
        DefaultHlsExtractorFactory extractorFactory = new DefaultHlsExtractorFactory(flags, true);

//        New Implementation
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        dataSourceFactory.setAllowCrossProtocolRedirects(true);
        dataSourceFactory.setUserAgent("curl/7.85.0");
        dataSourceFactory.setConnectTimeoutMs(10000);
        HlsMediaSource.Factory hlsMediaSource =
                new HlsMediaSource.Factory(dataSourceFactory);
//                        .setExtractorFactory(extractorFactory);
        mPlayer = new SimpleExoPlayer.Builder(context)
                .setMediaSourceFactory(hlsMediaSource)
                .build();
    }

//    public ExoPlayerAdapter(Context context) {
//        mContext = context;
//        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext,
//                new DefaultTrackSelector(),
//                new DefaultLoadControl());
//        mPlayer.addListener(this);
//    }

    @Override
    public void onAttachedToHost(PlaybackGlueHost host) {
        if (host instanceof SurfaceHolderGlueHost) {
            mSurfaceHolderGlueHost = ((SurfaceHolderGlueHost) host);
            mSurfaceHolderGlueHost.setSurfaceHolderCallback(new VideoPlayerSurfaceHolderCallback());
        }
    }

    /**
     * Will reset the {@link ExoPlayer} and the glue such that a new file can be played. You are
     * not required to call this method before playing the first file. However you have to call it
     * before playing a second one.
     */
    public void reset() {
        changeToUninitialized();
        mPlayer.stop();
    }

    void changeToUninitialized() {
        if (mInitialized) {
            mInitialized = false;
            notifyBufferingStartEnd();
            if (mHasDisplay) {
                getCallback().onPreparedStateChanged(ExoPlayerAdapter.this);
            }
        }
    }

    /**
     * Notify the state of buffering. For example, an app may enable/disable a loading figure
     * according to the state of buffering.
     */
    void notifyBufferingStartEnd() {
        getCallback().onBufferingStateChanged(ExoPlayerAdapter.this,
                mBufferingStart || !mInitialized);
    }

    /**
     * Release internal {@link SimpleExoPlayer}. Should not use the object after call release().
     */
    public void release() {
        changeToUninitialized();
        mHasDisplay = false;
        mPlayer.release();
    }

    @Override
    public void onDetachedFromHost() {
        if (mSurfaceHolderGlueHost != null) {
            mSurfaceHolderGlueHost.setSurfaceHolderCallback(null);
            mSurfaceHolderGlueHost = null;
        }
        reset();
        release();
    }

    void setDisplay(SurfaceHolder surfaceHolder) {
        boolean hadDisplay = mHasDisplay;
        mHasDisplay = surfaceHolder != null;
        if (hadDisplay == mHasDisplay) {
            return;
        }

        mPlayer.setVideoSurfaceHolder(surfaceHolder);
        if (mInitialized) {
            getCallback().onPreparedStateChanged(ExoPlayerAdapter.this);
        }
    }

    @Override
    public void setProgressUpdatingEnabled(final boolean enabled) {
        mHandler.removeCallbacks(mRunnable);
        if (!enabled) {
            return;
        }
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    int getUpdatePeriod() {
        return 16;
    }

    @Override
    public boolean isPlaying() {
        boolean exoPlayerIsPlaying = mPlayer.getPlaybackState() == ExoPlayer.STATE_READY
                && mPlayer.getPlayWhenReady();
        return mInitialized && exoPlayerIsPlaying;
    }

    @Override
    public long getDuration() {
        return mInitialized ? mPlayer.getDuration() : -1;
    }

    @Override
    public long getCurrentPosition() {
        return mInitialized ? mPlayer.getCurrentPosition() : -1;
    }


    @Override
    public void play() {
        if (!mInitialized || isPlaying()) {
            return;
        }

        mPlayer.setPlayWhenReady(true);
        getCallback().onPlayStateChanged(ExoPlayerAdapter.this);
        getCallback().onCurrentPositionChanged(ExoPlayerAdapter.this);
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            mPlayer.setPlayWhenReady(false);
            getCallback().onPlayStateChanged(ExoPlayerAdapter.this);
        }
    }

    @Override
    public void seekTo(long newPosition) {
        if (!mInitialized) {
            return;
        }
        mPlayer.seekTo(newPosition);
    }

    @Override
    public long getBufferedPosition() {
        return mPlayer.getBufferedPosition();
    }

    public Context getContext() {
        return mContext;
    }

    public boolean setDataSource(Uri uri) {
        if (Objects.equals(mMediaSourceUri, uri)) {
            return false;
        }
        mMediaSourceUri = uri;
        prepareMediaForPlaying();
        return true;
    }

    public int getAudioStreamType() {
        return mAudioStreamType;
    }

    public void setAudioStreamType(@C.StreamType int audioStreamType) {
        mAudioStreamType = audioStreamType;
    }

    public MediaSource onCreateMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(mContext, "ExoPlayerAdapter");
        int flags = DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
                | DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
        DefaultHlsExtractorFactory extractorFactory = new DefaultHlsExtractorFactory(flags, true);

//        New Implementation
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        dataSourceFactory.setAllowCrossProtocolRedirects(true);
        dataSourceFactory.setUserAgent("curl/7.85.0");
        dataSourceFactory.setConnectTimeoutMs(10000);


        // Create a player instance.
        return new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

    private void prepareMediaForPlaying() {
        reset();
        if (mMediaSourceUri != null) {
//            MediaSource mediaSource = onCreateMediaSource(mMediaSourceUri);
//            mPlayer.prepare(mediaSource);

            // Set the media source to be played.
            // Prepare the player.
            mPlayer.setMediaItem(MediaItem.fromUri(mMediaSourceUri));
            mPlayer.prepare();

        } else {
            return;
        }
        notifyBufferingStartEnd();
        getCallback().onPlayStateChanged(ExoPlayerAdapter.this);
    }

    /**
     * @return True if ExoPlayer is ready and got a SurfaceHolder if
     * {@link PlaybackGlueHost} provides SurfaceHolder.
     */
    @Override
    public boolean isPrepared() {
        return mInitialized && (mSurfaceHolderGlueHost == null || mHasDisplay);
    }

    class VideoPlayerSurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            setDisplay(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            setDisplay(null);
        }
    }

    // ExoPlayer Event Listeners

//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        mBufferingStart = false;
//        if (playbackState == ExoPlayer.STATE_READY && !mInitialized) {
//            mInitialized = true;
//            if (mSurfaceHolderGlueHost == null || mHasDisplay) {
//                getCallback().onPreparedStateChanged(ExoPlayerAdapter.this);
//            }
//        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
//            mBufferingStart = true;
//        } else if (playbackState == ExoPlayer.STATE_ENDED) {
//            getCallback().onPlayStateChanged(ExoPlayerAdapter.this);
//            getCallback().onPlayCompleted(ExoPlayerAdapter.this);
//        }
//        notifyBufferingStartEnd();
//    }


}
