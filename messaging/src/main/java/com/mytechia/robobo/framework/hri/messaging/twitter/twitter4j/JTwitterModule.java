package com.mytechia.robobo.framework.hri.messaging.twitter.twitter4j;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TabWidget;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.mytechia.robobo.framework.hri.messaging.twitter.ATwitterModule;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.BasicAuthorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by luis on 28/10/16.
 */

public class JTwitterModule extends ATwitterModule {

    //region VAR
    Twitter twitter;
    String TAG = "JTwitterModule";
    Date lastDateChecked =  new Date();;
    int delay = 1000;
    Timer checker = new Timer();
    //endregion

    //region IModule Methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("messaging.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }
        String name = properties.getProperty("twitteraccount");
        String password = properties.getProperty("twitterpasswd");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthAccessToken(properties.getProperty("oauth.accessToken"));
        cb.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        cb.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"));
        cb.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"));
//        cb.setPassword(password);
//        cb.setUser(name);

        cb.setDebugEnabled(true);
        Log.d(TAG,"CB Ready");

        Configuration c  = cb.build();
        OAuthAuthorization auth = new OAuthAuthorization(c);
        twitter = new TwitterFactory().getInstance(auth);




    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }


    //endRegion

    //region ITwitterModule methods
    @Override
    public boolean updateStatus(final String message) {
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    twitter4j.Status status = twitter.updateStatus(message);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }


        }.execute();


        return true;
    }

    @Override
    public void checkMentions() {
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    ResponseList<twitter4j.Status> timeline = twitter.getMentionsTimeline();
                    ArrayList<String> mentions = new ArrayList<String>();
                    Date firstRetrievedDate = lastDateChecked;
                    boolean first = true;
                    int i = 0;

                    while (timeline.get(i).getCreatedAt().after(lastDateChecked)){
                        Log.d(TAG, "Dentro del bucle");
                        twitter4j.Status s =timeline.get(i);
                        if (first){
                            first = false;
                            firstRetrievedDate = s.getCreatedAt();
                        }
                        mentions.add(s.getText());
                        i++;
                    }

                    lastDateChecked = firstRetrievedDate;
                    if (!first) {
                        notifyMentions(mentions);
                    }


                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }


        }.execute();

    }

    @Override
    public void setAutoCheckDelay(long milliseconds) {
        checker.cancel();
        checker = new Timer();
        checker.scheduleAtFixedRate(new CheckTlTask(),0,milliseconds);
    }
    //endregion

    private class CheckTlTask extends TimerTask{

        @Override
        public void run() {
            checkMentions();
        }
    }

}
