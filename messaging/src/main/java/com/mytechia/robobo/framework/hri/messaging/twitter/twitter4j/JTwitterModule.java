/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo Twitter Module.
 *
 *   Robobo Twitter Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Twitter Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Twitter Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.hri.messaging.twitter.twitter4j;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.TabWidget;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.mytechia.robobo.framework.hri.messaging.twitter.ATwitterModule;
import com.mytechia.robobo.framework.hri.messaging.twitter.IStatus;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.BasicAuthorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Implementation of the Robobo Twitter module
 */
public class JTwitterModule extends ATwitterModule implements UserStreamListener {

    //region VAR
    Twitter twitter;
    String TAG = "JTwitterModule";
    Date lastDateChecked =  new Date();;
    int delay = 1000*60;
    Timer checker = new Timer();
    TwitterStream twitterStream;
    String name;
    int mode = 0; //0 streaming API 1 repeating request
    //endregion

    //region IModule Methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        m = manager;
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("messaging.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }
        this.name = properties.getProperty("twitteraccount");
        String password = properties.getProperty("twitterpasswd");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthAccessToken(properties.getProperty("oauth.accessToken"));
        cb.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        cb.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"));
        cb.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"));
//        cb.setPassword(password);
//        cb.setUser(name);

        cb.setDebugEnabled(true);
        m.log(TAG,"CB Ready");

        Configuration c  = cb.build();
        OAuthAuthorization auth = new OAuthAuthorization(c);
        twitter = new TwitterFactory().getInstance(auth);
        twitterStream = new TwitterStreamFactory().getInstance(auth);





    }

    @Override
    public void shutdown() throws InternalErrorException {
        twitterStream.shutdown();
    }

    @Override
    public String getModuleInfo() {
        return "JTwitter Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.3.0";
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
        m.log(TAG,"Checking Mentions");
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    Paging paging = new Paging(1, 10);
                    ResponseList<twitter4j.Status> timeline = twitter.getMentionsTimeline(paging);
                    ArrayList<IStatus> mentions = new ArrayList<IStatus>();
                    Date firstRetrievedDate = lastDateChecked;
                    boolean first = true;
                    int i = 0;

                    while (timeline.get(i).getCreatedAt().after(lastDateChecked)){

                        twitter4j.Status s =timeline.get(i);
                        if (first){
                            first = false;
                            firstRetrievedDate = s.getCreatedAt();
                        }
                        mentions.add(new JTwitterStatus(s));
                        i++;
                    }

                    lastDateChecked = firstRetrievedDate;
                    if (!first) {
                        notifyMultipleMentions(mentions);
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

    @Override
    public void setStreaming() {

        twitterStream.addListener(this);
        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.user();
    }
    //endregion

    //region UserStream Listeners
    @Override
    public void onStatus(Status status) {
        m.log(LogLvl.TRACE, TAG, "onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
        if (status.getText().contains("@"+name)){
            notifyMention(new JTwitterStatus(status));
        };
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        m.log(LogLvl.TRACE, TAG, "Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {
        m.log(LogLvl.TRACE, TAG, "Got a direct message deletion notice id:" + directMessageId);
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        m.log(LogLvl.TRACE, TAG, "Got a track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        m.log(LogLvl.TRACE, TAG, "Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        m.log(LogLvl.TRACE, TAG, "Got stall warning:" + warning);
    }

    @Override
    public void onFriendList(long[] friendIds) {
        m.log(LogLvl.TRACE, TAG, "onFriendList");
        for (long friendId : friendIds) {
            m.log(LogLvl.TRACE, TAG, " " + friendId);
        }
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        m.log(LogLvl.TRACE, TAG, "onFavorite source:@"
                + source.getScreenName() + " target:@"
                + target.getScreenName() + " @"
                + favoritedStatus.getUser().getScreenName() + " - "
                + favoritedStatus.getText());
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        m.log(LogLvl.TRACE, TAG, "onUnFavorite source:@"
                + source.getScreenName() + " target:@"
                + target.getScreenName() + " @"
                + unfavoritedStatus.getUser().getScreenName()
                + " - " + unfavoritedStatus.getText());
    }

    @Override
    public void onFollow(User source, User followedUser) {
        m.log(LogLvl.TRACE, TAG, "onFollow source:@"
                + source.getScreenName() + " target:@"
                + followedUser.getScreenName());
    }

    @Override
    public void onUnfollow(User source, User followedUser) {
        m.log(LogLvl.TRACE, TAG, "onFollow source:@"
                + source.getScreenName() + " target:@"
                + followedUser.getScreenName());
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        m.log(LogLvl.TRACE, TAG, "onDirectMessage text:"
                + directMessage.getText());
    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListMemberAddition added member:@"
                + addedMember.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListMemberDeleted deleted member:@"
                + deletedMember.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListSubscribed subscriber:@"
                + subscriber.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListUnsubscribed subscriber:@"
                + subscriber.getScreenName()
                + " listOwner:@" + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListCreated  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListUpdated  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {
        m.log(LogLvl.TRACE, TAG, "onUserListDestroyed  listOwner:@"
                + listOwner.getScreenName()
                + " list:" + list.getName());
    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {
        m.log(LogLvl.TRACE, TAG, "onUserProfileUpdated user:@" + updatedUser.getScreenName());
    }

    @Override
    public void onUserDeletion(long deletedUser) {
        m.log(LogLvl.TRACE, TAG, "onUserDeletion user:@" + deletedUser);
    }

    @Override
    public void onUserSuspension(long suspendedUser) {
        m.log(LogLvl.TRACE, TAG, "onUserSuspension user:@" + suspendedUser);
    }

    @Override
    public void onBlock(User source, User blockedUser) {
        m.log(LogLvl.TRACE, TAG, "onBlock source:@" + source.getScreenName()
                + " target:@" + blockedUser.getScreenName());
    }

    @Override
    public void onUnblock(User source, User unblockedUser) {
        m.log(LogLvl.TRACE, TAG, "onUnblock source:@" + source.getScreenName()
                + " target:@" + unblockedUser.getScreenName());
    }

    @Override
    public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
        m.log(LogLvl.TRACE, TAG, "onRetweetedRetweet source:@" + source.getScreenName()
                + " target:@" + target.getScreenName()
                + retweetedStatus.getUser().getScreenName()
                + " - " + retweetedStatus.getText());
    }

    @Override
    public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
        m.log(LogLvl.TRACE, TAG, "onFavroitedRetweet source:@" + source.getScreenName()
                + " target:@" + target.getScreenName()
                + favoritedRetweet.getUser().getScreenName()
                + " - " + favoritedRetweet.getText());
    }

    @Override
    public void onQuotedTweet(User source, User target, Status quotingTweet) {
        m.log(LogLvl.TRACE, TAG, "onQuotedTweet" + source.getScreenName()
                + " target:@" + target.getScreenName()
                + quotingTweet.getUser().getScreenName()
                + " - " + quotingTweet.getText());
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
        m.log(LogLvl.TRACE, TAG, "onException:" + ex.getMessage());
    }

    //endregion

    //region StatusListener methods
//    @Override
//    public void onStatus(Status status) {
//        notifyMention(new JTwitterStatus(status));
//    }
//
//    @Override
//    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//
//    }
//
//    @Override
//    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//
//    }
//
//    @Override
//    public void onScrubGeo(long userId, long upToStatusId) {
//
//    }
//
//    @Override
//    public void onStallWarning(StallWarning warning) {
//
//    }
//
//    @Override
//    public void onException(Exception ex) {
//
//    }
    //endregion

    private class CheckTlTask extends TimerTask{

        @Override
        public void run() {
            checkMentions();
        }
    }

}