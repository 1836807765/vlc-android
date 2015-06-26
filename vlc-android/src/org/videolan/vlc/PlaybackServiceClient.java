/*****************************************************************************
 * PlaybackServiceClient.java
 *****************************************************************************
 * Copyright © 2011-2015 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.MainThread;
import android.util.Log;

public class PlaybackServiceClient {
    public static final String TAG = "PlaybackServiceClient";

    @MainThread
    public interface Callback {
        void onConnected(PlaybackService service);
        void onDisconnected();
    }

    private boolean mBound = false;
    private PlaybackService mService = null;
    private final Callback mCallback;
    private final Context mContext;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(TAG, "Service Connected");
            if (!mBound)
                return;

            mService = PlaybackService.getService(iBinder);
            if (mService != null && mCallback != null)
                mCallback.onConnected(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service Disconnected");
            mService = null;
            if (mCallback != null)
                mCallback.onDisconnected();
        }
    };

    private static Intent getServiceIntent(Context context) {
        return new Intent(context, PlaybackService.class);
    }

    private static void startService(Context context) {
        context.startService(getServiceIntent(context));
    }

    private static void stopService(Context context) {
        context.stopService(getServiceIntent(context));
    }

    public PlaybackServiceClient(Context context, Callback callback) {
        if (context == null)
            throw new IllegalArgumentException("Context can't be null");
        mContext = context;
        mCallback = callback;
    }

    @MainThread
    public void connect() {
        if (mBound)
            throw new IllegalStateException("already connected");
        startService(mContext);
        mBound = mContext.bindService(getServiceIntent(mContext), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @MainThread
    public void disconnect() {
        if (mBound) {
            mService = null;
            mBound = false;
            mContext.unbindService(mServiceConnection);
        }
    }

    @MainThread
    public void restartService() {
        disconnect();
        stopService(mContext);
        startService(mContext);
        connect();
    }

    public static void restartService(Context context) {
        stopService(context);
        startService(context);
    }

    @MainThread
    public PlaybackService getService() {
        return mService;
    }
}
