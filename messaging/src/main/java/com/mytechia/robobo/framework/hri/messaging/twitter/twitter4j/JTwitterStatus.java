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

import com.mytechia.robobo.framework.hri.messaging.twitter.IStatus;

import java.util.Date;

import twitter4j.Status;

/**
 * Created by luis on 2/11/16.
 */

/**
 * Class representing the twitter status
 */
public class JTwitterStatus implements IStatus {
    private String author;
    private String message;
    private Date date;
    private String authorName;
    private Status jTwitterSt;

    public JTwitterStatus(Status status){
        this.author = status.getUser().getScreenName();
        this.authorName = status.getUser().getName();
        this.date = status.getCreatedAt();
        this.message = status.getText();
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getAuthorName() {
        return this.author;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Date getDate() {
        return this.date;
    }


}
