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

import android.util.Log;

import com.mytechia.robobo.framework.RoboboManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract class that manages the listeners
 */

public abstract class ATwitterModule implements ITwitterModule{
    private HashSet<ITwitterListener> listeners;

    protected RoboboManager m;
    public ATwitterModule(){
        listeners = new HashSet<ITwitterListener>();
    }

    @Override
    public void suscribe(ITwitterListener listener) {
        Log.d("Twitter_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(ITwitterListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the listeners of multiple mentions
     * @param mentions List of the mentions
     */
    public void notifyMultipleMentions(ArrayList<IStatus> mentions){
        for (ITwitterListener listener:listeners){
            listener.onMultipleMentions(mentions);
        }
    }

    /**
     * Notifies the listeners of a single mention
     * @param mention The mention
     */
    public void notifyMention(IStatus mention){
        for (ITwitterListener listener:listeners){
            listener.onNewMention(mention);
        }
    }
}
