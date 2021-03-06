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

package com.ecmdeveloper.plugin.content.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.content.jobs.CheckinJob;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinWizard extends Wizard {

	private static final String WINDOW_TITLE_MESSAGE = "Checkin Document {0}";

	private ConfigureCheckinWizardPage configureCheckinPage;
	private ContentSelectionWizardPage contentSelectionPage;
	private final IDocument document;
	private IFile initialContent;
	private String initialMimeType;
	private final boolean isTrackedDocument;
	
	public CheckinWizard(IDocument document, boolean isTrackedDocument ) {
		this.document = document;
		String title = MessageFormat.format( WINDOW_TITLE_MESSAGE, document.getName() );
		setWindowTitle(title);
		this.isTrackedDocument = isTrackedDocument;
	}

	public void addPages() {
		configureCheckinPage = new ConfigureCheckinWizardPage(isTrackedDocument);
		addPage(configureCheckinPage);
		contentSelectionPage = new ContentSelectionWizardPage( document.getName() );
		addPage(contentSelectionPage);
	}
	
	@Override
	public boolean canFinish() {
		
		if ( getContainer().getCurrentPage().equals( configureCheckinPage ) ) {
			return ! configureCheckinPage.isSelectContent();
		}
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		if ( page instanceof ConfigureCheckinWizardPage ) {
			if ( ((ConfigureCheckinWizardPage)page).isSelectContent() ) {
				addInitialContent();
				return contentSelectionPage;
			}
		}
		return null;
	}

	private void addInitialContent() {
		if ( initialContent != null ) {
			ArrayList<Object> content = new ArrayList<Object>();
			content.add(initialContent);
			contentSelectionPage.setContent(content, initialMimeType);
		}
	}

	@Override
	public boolean performFinish() {

		Job checkinJob = createCheckinJob();
		checkinJob.setUser(true);
		checkinJob.schedule();
		
		return true;
	}

	private Job createCheckinJob() {
		Collection<Object> content = null;
		String mimeType = null;

		if ( configureCheckinPage.isUseTrackedContent() && contentSelectionPage.getContent().size() == 0 ) {
			addInitialContent();
		}
		
		if ( configureCheckinPage.isSelectContent() || configureCheckinPage.isUseTrackedContent() ) {
			content = contentSelectionPage.getContent();
			mimeType = contentSelectionPage.getMimeType();
		}
		
		boolean majorVersion = configureCheckinPage.isCheckinMajor();
		
		Job checkinJob = new CheckinJob( document, getShell(), content, mimeType, majorVersion   );
		return checkinJob;
	}

	public void setInitialContent(IFile initialContent, String mimeType ) {
		this.initialContent = initialContent;
		this.initialMimeType = mimeType;
	}
}
