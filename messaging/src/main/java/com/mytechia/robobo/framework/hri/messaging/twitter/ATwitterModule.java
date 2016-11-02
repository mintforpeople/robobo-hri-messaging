package com.mytechia.robobo.framework.hri.messaging.twitter;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by luis on 28/10/16.
 */

public abstract class ATwitterModule implements ITwitterModule{
    private HashSet<ITwitterListener> listeners;

    public ATwitterModule(){
        listeners = new HashSet<ITwitterListener>();
    }

    @Override
    public void suscribe(ITwitterListener listener) {
        Log.d("FD_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(ITwitterListener listener) {
        listeners.remove(listener);
    }

    public void notifyMentions(ArrayList<String> mentions){
        for (ITwitterListener listener:listeners){
            listener.onNewMention(mentions);
        }
    }
}
