/**
 * Copyright 2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.ui.decorators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerRefreshEvent;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class ObjectStoreItemDecorator implements ILightweightLabelDecorator,
		ITaskManagerListener {

	private final ITaskManager manager = Activator.getDefault().getTaskManager();
	private final List<ILabelProviderListener> listenerList = new ArrayList<ILabelProviderListener>();

	public ObjectStoreItemDecorator() {
		super();
		manager.addTaskManagerListener(this);
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener the listener
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	/**
	 * Dispose.
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		manager.removeTaskManagerListener(this);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener the listener
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * Notification that the Object Store items is changed. This will change the 
	 * items registered as listeners accordingly.
	 * 
	 * @param event the event
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoresManagerListener#objectStoreItemsChanged(com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent)
	 */
	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		
		if (event.getItemsUpdated() != null) {
			for (IObjectStoreItem objectStoreItem : event.getItemsUpdated()) {
				if (isDecoratedType(objectStoreItem)) {
					notifyListeners(objectStoreItem);
				}
			}
		}
	}

	private void notifyListeners(IObjectStoreItem objectStoreItem) {
		LabelProviderChangedEvent labelEvent =
		        new LabelProviderChangedEvent(this, objectStoreItem );

		  Iterator<ILabelProviderListener> iter = listenerList.iterator();
		  while (iter.hasNext())
		     iter.next().labelProviderChanged(labelEvent);
	}

	protected abstract boolean isDecoratedType(IObjectStoreItem objectStoreItem);

	@Override
	public void objectStoreItemsRefreshed(ObjectStoresManagerRefreshEvent event) {
	}
}
