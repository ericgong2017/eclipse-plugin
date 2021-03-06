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

package com.ecmdeveloper.plugin.content.jobs;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.tracker.model.FilesTracker;
import com.ecmdeveloper.plugin.content.wizard.DownloadDocumentWizard;

/**
 * @author Ricardo.Belfor
 *
 */
public class DownloadDocumentJob  extends AbstractDocumentContentJob {

	private static final String TASK_MESSAGE = "Downloading document \"{0}\"";
	private static final String HANDLER_NAME = "Download Document";
	private static final String FAILED_MESSAGE = "Downloading \"{0}\" failed";
	
	private boolean openEditor;
	private boolean trackFile;

	public DownloadDocumentJob(IDocument document, IWorkbenchWindow window ) {
		this(document, window, false, false);
	}
	
	public DownloadDocumentJob(IDocument document, IWorkbenchWindow window, boolean openEditor, boolean trackFile ) {
		super(HANDLER_NAME, document, window);
		this.openEditor = openEditor;
		this.trackFile = trackFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
		monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
		Collection<Integer> contentElements = getContentElements(monitor);
		
		if ( contentElements.size() == 0 ) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}

		for ( Integer contentElement : contentElements ) {
			try {
				downloadContentElement(contentElement,monitor);
			} catch (Exception e) {
				showError( MessageFormat.format(FAILED_MESSAGE, document.getName() ), e);
			}
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	private void downloadContentElement(Integer contentElement, IProgressMonitor monitor) throws Exception {

		InputStream contentStream = getContentStream(contentElement, monitor );
		String filename = getContentElementFilename(contentElement);
		
		DownloadDocumentWizard wizard = new DownloadDocumentWizard(filename, contentStream, openEditor );
		wizard.init( window.getWorkbench(), null );
		final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				dialog.create();
				dialog.open();
			}
		} );
		
		if ( trackFile ) {
			String newFilename = wizard.getNewFile().getFullPath().toString();
			addFileToTracker(newFilename);
		}
	}

	private void addFileToTracker(String newFilename) {
		String id = document.getId();
		String name = document.getName();
		String className = document.getClassName();
		String mimeType = document.getMimeType();

		IObjectStore objectStore = document.getObjectStore();
		String objectStoreName = objectStore.getName();
		String objectStoreDisplayName = objectStore.getDisplayName();

		IConnection connection = objectStore.getConnection();
		String connectionName = connection.getName();
		String connectionDisplayName = connection.getDisplayName();
		
		FilesTracker.getInstance().addTrackedFile(newFilename, id, name, className,
				document.getVersionSeriesId(), connectionName, connectionDisplayName,
				objectStoreName, objectStoreDisplayName, mimeType);
	}

}
