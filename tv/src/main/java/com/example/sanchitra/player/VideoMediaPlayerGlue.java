package com.example.sanchitra.player;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.media.PlayerAdapter;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

public class VideoMediaPlayerGlue <T extends PlayerAdapter> extends PlaybackTransportControlGlue<T> {

    private PlaybackControlsRow.RepeatAction mRepeatAction;
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private PlaybackControlsRow.PictureInPictureAction mPipAction;
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    /**
     * Constructor for the glue.
     *
     * @param context
     * @param impl    Implementation to underlying media player.
     */
    public VideoMediaPlayerGlue(Context context, T impl) {
        super(context, impl);
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(context);
        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(context);
        mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsUpAction.OUTLINE);
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(context);
        mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsDownAction.OUTLINE);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(context);
        mPipAction = new PlaybackControlsRow.PictureInPictureAction(context);
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        adapter.add(mThumbsUpAction);
        adapter.add(mThumbsDownAction);
        adapter.add(mPipAction);
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        super.onCreatePrimaryActions(adapter);
        adapter.add(mRepeatAction);
        adapter.add(mClosedCaptioningAction);
    }

    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action);
            return;
        }
        super.onActionClicked(action);
    }

    private boolean shouldDispatchAction(Action action) {
        return action == mRepeatAction || action == mThumbsUpAction || action == mThumbsDownAction
                || action == mPipAction || action == mClosedCaptioningAction;
    }

    private void dispatchAction(Action action) {
        if (action == mPipAction) {
            ((Activity) getContext()).enterPictureInPictureMode();
        } else {
//            Toast.makeText(getContext(), action.toString(), Toast.LENGTH_SHORT).show();
            PlaybackControlsRow.MultiAction multiAction = (PlaybackControlsRow.MultiAction) action;
            multiAction.nextIndex();
            notifyActionChanged(multiAction);
        }
    }

    private void notifyActionChanged(PlaybackControlsRow.MultiAction action) {
        int index = -1;
        if (getPrimaryActionsAdapter() != null) {
            index = getPrimaryActionsAdapter().indexOf(action);
        }
        if (index >= 0) {
            getPrimaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
        } else {
            if (getSecondaryActionsAdapter() != null) {
                index = getSecondaryActionsAdapter().indexOf(action);
                if (index >= 0) {
                    getSecondaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
                }
            }
        }
    }
    private ArrayObjectAdapter getPrimaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
    }

    private ArrayObjectAdapter getSecondaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter();
    }

    Handler mHandler = new Handler();

    @Override
    protected void onPlayCompleted() {
        super.onPlayCompleted();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRepeatAction.getIndex() != PlaybackControlsRow.RepeatAction.NONE) {
                    play();
                }
            }
        });
    }

    public void setMode(int mode) {
        mRepeatAction.setIndex(mode);
        if (getPrimaryActionsAdapter() == null) {
            return;
        }
        notifyActionChanged(mRepeatAction);
    }
}
