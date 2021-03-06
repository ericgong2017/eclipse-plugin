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
import java.util.concurrent.ExecutionException;

import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.IUpdateTask;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditor;

/**
 * @author Ricardo.Belfor
 *
 */
public class SaveEditorPropertiesJob extends AbstractEditorJob {

	private static final String JOB_NAME = "Save Editor Properties";
	private static final String MONITOR_MESSAGE = "Saving Editor Properties";
	private static final String FAILED_MESSAGE = "Saving Editor Properties for \"{0}\" failed. '{0}'";

	public SaveEditorPropertiesJob(ObjectStoreItemEditor editor,IWorkbenchWindow window) {
		super(editor, window, JOB_NAME);
	}

	@Override
	protected String getFailedMessage() {
		return MessageFormat.format( FAILED_MESSAGE, objectStoreItem.getName() );		
	}

	@Override
	protected String getMonitorMessage() {
		return MONITOR_MESSAGE;
	}

	@Override
	protected void runEditorJob() throws Exception {
		update();
		scheduleRefresh();
	}

	private void scheduleRefresh() {
		RefreshEditorPropertiesJob refreshJob = new RefreshEditorPropertiesJob((ObjectStoreItemEditor) editor, window );
		refreshJob.setUser(true);
		refreshJob.setRule( new EditorSchedulingRule(2) );
		refreshJob.schedule();
	}

	private void update() throws ExecutionException {
		ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
		IUpdateTask task = taskFactory.getUpdateTask(objectStoreItem);
		Activator.getDefault().getTaskManager().executeTaskSync(task);

		window.getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				editor.saved();
			}} 
		);
	}
}
