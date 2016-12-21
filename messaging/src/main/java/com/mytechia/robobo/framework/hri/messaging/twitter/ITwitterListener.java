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

import java.util.ArrayList;

/**
 * Interface for the mention listener
 */

public interface ITwitterListener {
    /**
     * Called when a new mention is received
     * @param mentions The mention
     */
    public void onNewMention(IStatus mentions);

    /**
     * Called when multiple mentions are received
     * @param mentions A list with the mentions
     */
    public void onMultipleMentions(ArrayList<IStatus> mentions);
}
