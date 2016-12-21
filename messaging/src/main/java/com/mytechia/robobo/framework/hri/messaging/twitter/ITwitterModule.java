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
package com.mytechia.robobo.framework.hri.messaging.twitter;

import com.mytechia.robobo.framework.IModule;

/**
 * Interface of the ROBOBO Twitter module
 */

public interface ITwitterModule extends IModule{
    /**
     * Suscribes a listener to the mention notifications
     * @param listener The listener to be added
     */
    public void suscribe(ITwitterListener listener);

    /**
     * Unsuscribes a listener from the mention notifications
     * @param listener The listener to be removed
     */
    public void unsuscribe(ITwitterListener listener);

    /**
     * Posts a new status to twitter
     * @param message The message
     * @return Boolean representing the success of the operation
     */
    public boolean updateStatus(String message);

    /**
     * Checks for new mentions
     */
    public void checkMentions();

    /**
     * Sets the delay between auto mention chacks
     * @param milliseconds The time in milliseconds
     */
    public void setAutoCheckDelay(long milliseconds);

    /**
     * Sets the twitter module to streaming mode
     */
    public void setStreaming();
}
