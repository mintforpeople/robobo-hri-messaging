package com.mytechia.robobo.framework.hri.messaging.twitter;

import java.util.ArrayList;

/**
 * Created by luis on 31/10/16.
 */

public interface ITwitterListener {
    public void onNewMention(ArrayList<String> mentions);
}
