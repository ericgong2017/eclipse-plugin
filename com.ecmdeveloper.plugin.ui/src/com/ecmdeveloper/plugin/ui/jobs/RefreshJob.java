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

package com.ecmdeveloper.plugin.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IRefreshTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;

/**
 * @author ricardo.belfor
 *
 */
public class RefreshJob extends Job {

	private IObjectStoreItem[] objectStoreItems;

	public RefreshJob(IObjectStoreItem[] objectStoreItems) {
		super("refreshJob");
		this.objectStoreItems = objectStoreItems;
	}


	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if ( objectStoreItems.length > 0 ) {
			ITaskFactory taskFactory = objectStoreItems[0].getTaskFactory();
			IRefreshTask refreshTask = taskFactory.getRefreshTask( objectStoreItems, true );
			Activator.getDefault().getTaskManager().executeTaskASync(refreshTask);
		}
		return null;
	}
}
