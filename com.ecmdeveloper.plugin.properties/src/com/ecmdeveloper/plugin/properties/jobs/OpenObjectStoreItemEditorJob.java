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

package com.ecmdeveloper.plugin.properties.jobs;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchPropertiesTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.editors.input.ObjectStoreItemEditorInput;

/**
 * @author Ricardo.Belfor
 *
 */
public class OpenObjectStoreItemEditorJob extends Job {

	private static final String FETCHING_PROPERTY_VALUES_TASK_MESSAGE = "Fetching property values";
	private static final String FETCHING_CLASS_DESCRIPTIONS_TASK_MESSAGE = "Fetching class descriptions";
	private static final String JOB_NAME = "Open Editor";
	private static final String MONITOR_MESSAGE = "Opening Editor";
	private static final String FAILED_MESSAGE = "Opening Editor for \"{0}\" failed";
	
	private IObjectStoreItem objectStoreItem;
	private IWorkbenchWindow window;
	private String editorId;
	
	public OpenObjectStoreItemEditorJob(IObjectStoreItem objectStoreItem, String editorId, IWorkbenchWindow window) {
		super(JOB_NAME);
		this.objectStoreItem = objectStoreItem;
		this.window = window;
		this.editorId = editorId;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(MONITOR_MESSAGE, 2);
			openNewEditor(monitor);
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
		}
		
		return Status.OK_STATUS;
	}

	private void openNewEditor(IProgressMonitor monitor) throws Exception {
		
		monitor.setTaskName( FETCHING_CLASS_DESCRIPTIONS_TASK_MESSAGE );
		IClassDescription classDescription = getClassDescription();
		monitor.worked(1);
		
		monitor.setTaskName(FETCHING_PROPERTY_VALUES_TASK_MESSAGE );
		fetchProperties(classDescription);
		monitor.worked(1);

		IEditorInput input = new ObjectStoreItemEditorInput( objectStoreItem, classDescription );
		openEditorWindow(input, editorId);
	}

	private void fetchProperties(IClassDescription classDescription) throws Exception {
		String[] propertyNames = getPropertyNames(classDescription);
		
		if ( propertyNames.length != 0 ) {
			ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
			IFetchPropertiesTask task = taskFactory.getFetchPropertiesTask(objectStoreItem, propertyNames);
			Activator.getDefault().getTaskManager().executeTaskSync(task);
		}
	}

	private String[] getPropertyNames(IClassDescription classDescription) {

		Set<String> propertyNames = new HashSet<String>();
		for (IPropertyDescription propertyDescription : classDescription.getPropertyDescriptions()) {
			propertyNames.add(propertyDescription.getName());
		}

		return propertyNames.toArray(new String[0]);
	}

	private IClassDescription getClassDescription() throws Exception {

		ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
		IGetClassDescriptionTask task = taskFactory.getGetClassDescriptionTask(objectStoreItem.getClassName(),
				objectStoreItem.getObjectStore());
		IClassDescription classDescription = (IClassDescription) Activator.getDefault().getTaskManager()
				.executeTaskSync(task);
		return classDescription;
	}

	private void openEditorWindow(final IEditorInput input, final String editorId) {
		
		window.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditor( window.getActivePage(), input, editorId);
				} catch (PartInitException e) {
					PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
				}
			}
		} );
	}
}
