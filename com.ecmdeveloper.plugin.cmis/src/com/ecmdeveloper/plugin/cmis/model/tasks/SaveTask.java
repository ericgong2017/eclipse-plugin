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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.tasks.ISaveTask;

/**
 * @author Ricardo.Belfor
 * @param <reservation>
 *
 */
public class SaveTask extends DocumentTask implements ISaveTask {

	private Collection<Object> contents;
	private String mimeType;
	private Document reservationDocument;

	public SaveTask(Document document, Collection<Object> contents, String mimeType ) {
		super(document);
		this.contents = contents;
		this.mimeType = mimeType;
	}

	@Override
	public Object call() throws Exception {

		if ( getDocument().isSaved() ) {
			setSavedContent();
		} else {
			setUnsavedContent();
		}

		return null;
	}

	private void setSavedContent() throws Exception {
		org.apache.chemistry.opencmis.client.api.Document reservation = getReservation();
		Session session = getDocument().getObjectStore().getSession();
		ContentStream content = ContentStreamFactory.createContent(contents, mimeType, session );

		if ( content != null ) {
			reservation.setContentStream( content, true );
		}
		reservationDocument = ObjectStoreItemFactory.createDocument( reservation, getDocument().getParent(), getDocument().getObjectStore() );
	}

	private void setUnsavedContent() throws Exception {
		Session session = getDocument().getObjectStore().getSession();
		ContentStream content = ContentStreamFactory.createContent(contents, mimeType, session );

		if ( content != null ) {
			getDocument().setContentStream( content );
		}
		reservationDocument = getDocument();
	}
	
	public Document getReservationDocument() {
		return reservationDocument;
	}
}
