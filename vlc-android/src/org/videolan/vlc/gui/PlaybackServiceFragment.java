/*
 * *************************************************************************
 *  PlaybackServiceFragment.java
 * **************************************************************************
 *  Copyright © 2015 VLC authors, VideoLAN, and VideoLabs
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *  ***************************************************************************
 */

package org.videolan.vlc.gui;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;

import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.PlaybackServiceClient;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

public abstract class PlaybackServiceFragment extends Fragment implements PlaybackServiceClient.Callback {

    private PlaybackServiceActivity.Helper getHelper() {
        final Activity activity = getActivity();
        if (activity == null)
            return null;

        if ((activity instanceof AudioPlayerContainerActivity))
            return ((AudioPlayerContainerActivity) activity).getHelper();
        else if ((activity instanceof PlaybackServiceActivity))
            return ((PlaybackServiceActivity) activity).getHelper();
        else if ((activity instanceof VideoPlayerActivity))
            return ((VideoPlayerActivity) activity).getHelper();
        else
            throw new IllegalArgumentException("Fragment must be inside AudioPlayerContainerActivity or PlaybackServiceActivity");
    }


    @MainThread
    private void registerFragment(PlaybackServiceClient.Callback callback) {
        final PlaybackServiceActivity.Helper helper = getHelper();
        if (helper != null)
            helper.registerFragment(callback);
    }

    @MainThread
    private void unregisterFragment(PlaybackServiceClient.Callback callback) {
        final PlaybackServiceActivity.Helper helper = getHelper();
        if (helper != null)
            helper.unregisterFragment(callback);
    }

    protected PlaybackService mService;

    public void onStart(){
        super.onStart();
        registerFragment(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterFragment(this);
    }

    @Override
    public void onConnected(PlaybackService service) {
        mService = service;
    }

    @Override
    public void onDisconnected() {
        mService = null;
    }
}
