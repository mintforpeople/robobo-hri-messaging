package com.mytechia.robobo.framework.hri.messaging.twitter;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 28/10/16.
 */

public interface ITwitterModule extends IModule{
    public void suscribe(ITwitterListener listener);
    public void unsuscribe(ITwitterListener listener);
    public boolean updateStatus(String message);
    public void checkMentions();
    public void setAutoCheckDelay(long milliseconds);
}
