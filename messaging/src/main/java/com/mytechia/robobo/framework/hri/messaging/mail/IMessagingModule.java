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
package com.mytechia.robobo.framework.hri.messaging.mail;

import android.graphics.Bitmap;

import com.mytechia.robobo.framework.IModule;

/**
 *Interface of the email messaging module
 */
public interface IMessagingModule extends IModule {


    /**
     * Sends a text via email
     * @param text The message
     * @param addresee The destinatary (<someone>@<something>.<domain>)
     */
    void  sendMessage(String text,String addresee);

    /**
     * Sends a text via email with an attached image
     * @param text The message
     * @param addresee The destinatary (<someone>@<something>.<domain>)
     * @param photoAtachment The image to be attached to the message
     */
    void  sendMessage(String text,String addresee, Bitmap photoAtachment);
}
