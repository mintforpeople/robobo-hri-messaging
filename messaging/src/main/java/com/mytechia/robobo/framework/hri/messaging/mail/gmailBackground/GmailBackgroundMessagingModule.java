/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo HRI Modules.
 *
 *   Robobo HRI Modules is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo HRI Modules is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo HRI Modules.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package com.mytechia.robobo.framework.hri.messaging.mail.gmailBackground;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.messaging.mail.AMessagingModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * Implementation of the mail messaging module using the GmailBackground Library
 */
public class GmailBackgroundMessagingModule extends AMessagingModule {

    //region VAR
    private Context context;
    private String TAG = "GmailMessagingModule";
    private String mailDirection;
    private String mailPassword;

    private RoboboManager m;
    //endregion
    //region IMessagingModule Methods
    @Override
    public void sendMessage(String text, String addresee) {
        m.log(TAG,"SENDMESSAGE");
        BackgroundMail.newBuilder(context)
                .withUsername(mailDirection)
                .withPassword(mailPassword)
                .withMailto(addresee)
                .withSubject("ROBOBO NOTIFICATION, ID:"+ new RandomString(10).nextString())
                .withBody(text)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Send Success");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Log.d(TAG,"Send Fail");
                    }
                })
                .send();
    }

    @Override
    public void sendMessage(String text, String addresee, Bitmap photoAtachment) {
        String imagepath = saveToExternalStorage(photoAtachment);
        m.log(TAG,"SENDMESSAGE");
        BackgroundMail.newBuilder(context)
                .withUsername(mailDirection)
                .withPassword(mailPassword)
                .withMailto(addresee)
                .withSubject("ROBOBO NOTIFICATION, ID:"+ new RandomString(10).nextString())
                .withBody(text)
                .withAttachments(imagepath)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Send Success");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Log.e(TAG,"Send Fail");
                    }
                })
                .send();

    }


    private String saveToExternalStorage(Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

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
        mailDirection = properties.getProperty("emailaccount");
        mailPassword = properties.getProperty("emailpasswd");


        context = manager.getApplicationContext();
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "GmailBackground Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.3.0";
    }
    //endregion
    private class RandomString {

        private  final char[] symbols;

         {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }


}

