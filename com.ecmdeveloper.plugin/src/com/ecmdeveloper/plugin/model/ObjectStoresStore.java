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

package com.ecmdeveloper.plugin.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginTagNames;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoresStore {

	private static final int CURRENT_FILE_VERSION = 1;

	public void load(ObjectStores objectStores, Map<String, ContentEngineConnection> connections) {

		FileReader reader = null;
		try {
			reader = new FileReader(getObjectStoresFile());
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			loadConnections(memento, connections);
			loadObjectStores(memento, objectStores, connections);
		} catch (FileNotFoundException e) {
			// Ignored... no object store items exist yet.
		} catch (Exception e) {
			// Log the exception and move on.
			PluginLog.error(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	}

	private void loadConnections(XMLMemento memento, Map<String, ContentEngineConnection> connections) {
		IMemento connectionsChild = memento.getChild(PluginTagNames.CONNECTIONS_TAG);
		if ( connectionsChild != null ) {
			for ( IMemento connectionChild : connectionsChild.getChildren(PluginTagNames.CONNECTION_TAG) ) {
				ContentEngineConnection contentEngineConnection = loadConnection(connectionChild);
				connections.put(contentEngineConnection.getName(), contentEngineConnection );
			}
		}
	}

	private void loadObjectStores(XMLMemento memento, ObjectStores objectStores,
			Map<String, ContentEngineConnection> connections) {
		IMemento objectStoresChild = memento.getChild(PluginTagNames.OBJECT_STORES_TAG);
		if ( objectStoresChild != null )
		{
			for ( IMemento objectStoreChild : objectStoresChild.getChildren( PluginTagNames.OBJECT_STORE_TAG ) )
			{
				loadObjectStore(objectStoreChild, objectStores, connections);
			}
		}
	}

	private void loadObjectStore(IMemento objectStoreChild, ObjectStores objectStores,
			Map<String, ContentEngineConnection> connections) {
		String name = objectStoreChild.getString( PluginTagNames.NAME_TAG );
		String displayName = objectStoreChild.getString( PluginTagNames.DISPLAY_NAME_TAG );
		ObjectStore objectStore = new ObjectStore( name, displayName, objectStores );
		
		String connectionName = objectStoreChild.getString( PluginTagNames.CONNECTION_NAME_TAG );
		objectStore.setConnection( connections.get( connectionName ) );
		
		objectStores.add( objectStore );
	}

	private ContentEngineConnection loadConnection(IMemento connectionChild) {
		ContentEngineConnection contentEngineConnection = new ContentEngineConnection();
		
		contentEngineConnection.setName( connectionChild.getString( PluginTagNames.NAME_TAG ) );
		contentEngineConnection.setDisplayName( connectionChild.getString( PluginTagNames.DISPLAY_NAME_TAG ) );
		contentEngineConnection.setUrl( connectionChild.getString( PluginTagNames.URL_TAG ) );
		contentEngineConnection.setUsername( connectionChild.getString( PluginTagNames.USERNAME_TAG ) );
		contentEngineConnection.setPassword( connectionChild.getString( PluginTagNames.PASSWORD_TAG ) );
		return contentEngineConnection;
	}

	public void save(ObjectStores objectStores, Map<String, ContentEngineConnection> connections)
	{
		if (connections == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.OBJECT_STORES_TAG);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );

		saveConnections(memento, connections);
		saveObjectStores(memento, objectStores);

		FileWriter writer = null;
		try {
			writer = new FileWriter(getObjectStoresFile());
			memento.save(writer);
		} catch (IOException e) {
			PluginLog.error(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	 		
	}
	
	private File getObjectStoresFile()
	{
		return Activator.getDefault().getStateLocation().append("objectstores.xml").toFile();
	}

	private void saveObjectStores(XMLMemento memento, ObjectStores objectStores) {
		IMemento objectStoresChild = memento.createChild(PluginTagNames.OBJECT_STORES_TAG); 
		
		for ( IObjectStoreItem objectStore : objectStores.getChildren()) {
			saveObjectStore(objectStoresChild, objectStore);
		}
	}

	private void saveConnections(XMLMemento memento, Map<String, ContentEngineConnection> connections) {

		IMemento connectionsChild = memento.createChild(PluginTagNames.CONNECTIONS_TAG); 
		
		for ( ContentEngineConnection contentEngineConnection : connections.values() ) {
			saveConnection(connectionsChild, contentEngineConnection);
		}
	}

	private void saveConnection(IMemento connectionsChild, ContentEngineConnection contentEngineConnection) {
		
		IMemento connectionChild = connectionsChild.createChild(PluginTagNames.CONNECTION_TAG);
		
		connectionChild.putString( PluginTagNames.NAME_TAG, contentEngineConnection.getName() );
		connectionChild.putString( PluginTagNames.DISPLAY_NAME_TAG, contentEngineConnection.getDisplayName() );
		connectionChild.putString( PluginTagNames.URL_TAG, contentEngineConnection.getUrl() );
		connectionChild.putString( PluginTagNames.USERNAME_TAG, contentEngineConnection.getUsername() );
		String password = contentEngineConnection.getPassword();
		// TODO add some basic encryption
		connectionChild.putString( PluginTagNames.PASSWORD_TAG, password );
	}

	private void saveObjectStore(IMemento objectStoresChild, IObjectStoreItem objectStore) {
		IMemento objectStoreChild = objectStoresChild.createChild(PluginTagNames.OBJECT_STORE_TAG);
		
		objectStoreChild.putString( PluginTagNames.NAME_TAG, objectStore.getName() );
		objectStoreChild.putString( PluginTagNames.DISPLAY_NAME_TAG, objectStore.getDisplayName() );
		objectStoreChild.putString( PluginTagNames.CONNECTION_NAME_TAG, ((ObjectStore)objectStore).getConnection().getName() );
	}
}