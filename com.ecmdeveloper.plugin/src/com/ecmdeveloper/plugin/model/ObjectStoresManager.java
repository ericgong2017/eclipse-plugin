/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.model.tasks.MoveTask;
import com.ecmdeveloper.plugin.model.tasks.RefreshTask;
import com.ecmdeveloper.plugin.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.model.tasks.TaskListener;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginTagNames;

/**
 * This class manages the connections to the Object Stores. It is also
 * responsible for executing the different tasks on the object store items.
 * 
 * @author Ricardo Belfor
 * 
 */
public class ObjectStoresManager implements IObjectStoresManager, TaskListener
{
	private static final int CURRENT_FILE_VERSION = 1;

	private static final String CONNECT_MESSAGE = "Connecting to \"{0}\"";

	private static ObjectStoresManager objectStoresManager;
	
	protected Map<String,ContentEngineConnection> connections;
	protected ObjectStores objectStores;

	private List<ObjectStoresManagerListener> listeners = new ArrayList<ObjectStoresManagerListener>();
	
	private ExecutorService executorService;

	private ObjectStoresManager() {
		executorService = Executors.newSingleThreadExecutor();
	}
	
	public static ObjectStoresManager getManager()
	{
		if ( objectStoresManager == null )
		{
			objectStoresManager = new ObjectStoresManager();
		}
		return objectStoresManager;
	}

	public Collection<ContentEngineConnection> getConnections() {

		if ( connections == null) {
			loadObjectStores();
		}
		
		return connections.values();
	}
	
	public String createConnection(String url, String username, String password, IProgressMonitor monitor ) throws ExecutionException 
	{
		try {
			
			if ( monitor != null ) {
	    		monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, url ), IProgressMonitor.UNKNOWN);
			}
			
			if ( connections == null) {
				loadObjectStores();
			}

			CreateConnectionTask createConnection = new CreateConnectionTask(url, username, password );
			
			try {
				return executorService.submit(createConnection).get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	public void connectConnection( ContentEngineConnection connection,IProgressMonitor monitor ) throws ExecutionException {

		try {
			
			String connectionName = connection.getName();
			if ( monitor != null ) {
				monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, connection.getDisplayName() ), IProgressMonitor.UNKNOWN);
			}
	
			if ( connections.containsKey( connectionName ) ) {
	
				ConnectConnectionTask callable = new ConnectConnectionTask(connectionName);
				try {
					executorService.submit(callable).get();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				
			} else {
				throw new UnsupportedOperationException( "Invalid connection name '" + connectionName + "'" );
			}
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	public void addObjectStore( final ObjectStore objectStore )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		Callable<Object> callable = new Callable<Object>(){

			@Override
			public Object call() throws Exception {
				objectStore.connect();
				objectStores.add(objectStore);
				fireObjectStoreItemsChanged( new IObjectStoreItem[] { objectStore }, null, null );
				return null;
			}
		};
		
		executorService.submit(callable);
	}

	/**
	 * Removes the Object Store from the list of configured Object Stores. If
	 * there are no more Object Stores using the same connection then the
	 * connection is also removed.
	 * 
	 * @param objectStore the object store
	 */
	public void removeObjectStore(ObjectStore objectStore) 
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		objectStores.remove( objectStore );
		
		String connectionName = objectStore.getConnection().getName();
		boolean found = false;
		for (IObjectStoreItem objectStoreItem : objectStores.getChildren() ) {
			String objectStoreConnectionName = ((ObjectStore)objectStoreItem).getConnection().getName();
			if ( objectStoreConnectionName.equals(connectionName ) ) {
				found = true;
				break;
			}
		}
			
		if ( ! found ) {
			connections.remove( connectionName );
		}

		fireObjectStoreItemsChanged( null, new IObjectStoreItem[] { objectStore }, null );
	}

	public void connectObjectStore(ObjectStore objectStore, IProgressMonitor monitor ) throws ExecutionException
	{
		if ( objectStore == null) {
			return;
		}
		
		if ( objectStore.isConnected() ) {
			return;
		}
		
		connectConnection(objectStore.getConnection(), monitor );
	}

	public ObjectStores getObjectStores()
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		return objectStores;
	}

	public ObjectStore[] getNewObjectstores( String connectionName ) {

		ObjectStore[] objectStores2 = getObjectStores(connectionName);
		ArrayList<ObjectStore> newObjectStores = new ArrayList<ObjectStore>();

		for (ObjectStore objectStore2 : objectStores2 ) {
		
			boolean found = false;

			for ( IObjectStoreItem objectStore : objectStores.getChildren() ) {
			
				if (objectStore.getName().equals( objectStore2.getName() )
						&& ((ObjectStore) objectStore).getConnection()
								.getName().equals(connectionName)) {
					found = true;
					break;
				}
			}
			
			if ( ! found ) {
				newObjectStores.add( objectStore2 );
			}
		}
		
		return newObjectStores.toArray( new ObjectStore[0] );
	}
	
	public ObjectStore[] getObjectStores(String connectionName )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		if ( ! connections.containsKey( connectionName ) )
		{
			return new ObjectStore[0];
		}
		
		final ContentEngineConnection connection = connections.get( connectionName );

		Callable<ObjectStore[]> a = new Callable<ObjectStore[]>(){

			@Override
			public ObjectStore[] call() throws Exception {
				return connection.getObjectStores(objectStores);			}
		};
		
		try {
			return executorService.submit( a ).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectStore getObjectStore(String connectionName, String objectStoreName) {

		if ( objectStores == null) {
			loadObjectStores();
		}
		
		for (IObjectStoreItem objectStoreItem : objectStores.getChildren() )
		{
			ObjectStore objectStore = (ObjectStore) objectStoreItem;
			if ( objectStore.getName().equals(objectStoreName) &&
					objectStore.getConnection().getName().equals(connectionName ) )
			{
				return objectStore;
			}
		}
		return null;
	}

	public Object executeTaskSync( Callable<Object> task ) throws ExecutionException
	{
		try {
			if ( task instanceof BaseTask ) {
				((BaseTask)task).addTaskListener(this);
			}
			return executorService.submit(task).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} 
	}

	public void executeTaskASync( Callable<Object> task )
	{
		if ( task instanceof BaseTask ) {
			((BaseTask)task).addTaskListener(this);
		}
		executorService.submit(task);
	}
	
	public void moveObjectStoreItems( IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination )
	{
		fireObjectStoreItemsChanged(null, objectStoreItems, null );

		Set<IObjectStoreItem> updateSet = new HashSet<IObjectStoreItem>();
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			if ( objectStoreItem.getParent() != null ) {
				objectStoreItem.getParent().refresh();
				updateSet.add( objectStoreItem.getParent() );
			}
		}

		destination.refresh();
		updateSet.add( destination );
		
		fireObjectStoreItemsChanged(null, null, updateSet.toArray( new IObjectStoreItem[0]) );
	}
	
	private void saveObjectStores(XMLMemento memento) 
	{
		// First save the connection to the object store
		
		IMemento connectionsChild = memento.createChild(PluginTagNames.CONNECTIONS_TAG); 
		
		for ( ContentEngineConnection contentEngineConnection : connections.values() ) {
			
			IMemento connectionChild = connectionsChild.createChild(PluginTagNames.CONNECTION_TAG);
			
			connectionChild.putString( PluginTagNames.NAME_TAG, contentEngineConnection.getName() );
			connectionChild.putString( PluginTagNames.DISPLAY_NAME_TAG, contentEngineConnection.getDisplayName() );
			connectionChild.putString( PluginTagNames.URL_TAG, contentEngineConnection.getUrl() );
			connectionChild.putString( PluginTagNames.USERNAME_TAG, contentEngineConnection.getUsername() );
			String password = contentEngineConnection.getPassword();
			// TODO add some basic encryption
			connectionChild.putString( PluginTagNames.PASSWORD_TAG, password );
			
		}
		
		// Next save the object stores

		IMemento objectStoresChild = memento.createChild(PluginTagNames.OBJECT_STORES_TAG); 
		
		for ( IObjectStoreItem objectStore : objectStores.getChildren()) {
			
			IMemento objectStoreChild = objectStoresChild.createChild(PluginTagNames.OBJECT_STORE_TAG);
			
			objectStoreChild.putString( PluginTagNames.NAME_TAG, objectStore.getName() );
			objectStoreChild.putString( PluginTagNames.DISPLAY_NAME_TAG, objectStore.getDisplayName() );
			objectStoreChild.putString( PluginTagNames.CONNECTION_NAME_TAG, ((ObjectStore)objectStore).getConnection().getName() );
		}
	}
	
	private void loadObjectStores(XMLMemento memento)
	{
		IMemento connectionsChild = memento.getChild(PluginTagNames.CONNECTIONS_TAG);
		if ( connectionsChild != null ) {
			for ( IMemento connectionChild : connectionsChild.getChildren(PluginTagNames.CONNECTION_TAG) ) {
				
				ContentEngineConnection contentEngineConnection = new ContentEngineConnection();
				
				contentEngineConnection.setName( connectionChild.getString( PluginTagNames.NAME_TAG ) );
				contentEngineConnection.setDisplayName( connectionChild.getString( PluginTagNames.DISPLAY_NAME_TAG ) );
				contentEngineConnection.setUrl( connectionChild.getString( PluginTagNames.URL_TAG ) );
				contentEngineConnection.setUsername( connectionChild.getString( PluginTagNames.USERNAME_TAG ) );
				contentEngineConnection.setPassword( connectionChild.getString( PluginTagNames.PASSWORD_TAG ) );
				
				connections.put(contentEngineConnection.getName(), contentEngineConnection );
			}
		}

		IMemento objectStoresChild = memento.getChild(PluginTagNames.OBJECT_STORES_TAG);
		if ( objectStoresChild != null )
		{
			for ( IMemento objectStoreChild : objectStoresChild.getChildren( PluginTagNames.OBJECT_STORE_TAG ) )
			{
				String name = objectStoreChild.getString( PluginTagNames.NAME_TAG );
				String displayName = objectStoreChild.getString( PluginTagNames.DISPLAY_NAME_TAG );
				ObjectStore objectStore = new ObjectStore( name, displayName, objectStores );
				
				String connectionName = objectStoreChild.getString( PluginTagNames.CONNECTION_NAME_TAG );
				objectStore.setConnection( connections.get( connectionName ) );
				
				objectStores.add( objectStore );
			}
		}
	}

	private void loadObjectStores() {

		connections = new HashMap<String, ContentEngineConnection>();
		objectStores = new ObjectStores();
		
		FileReader reader = null;
		try {
			reader = new FileReader(getObjectStoresFile());
			loadObjectStores(XMLMemento.createReadRoot(reader));
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
	
	public void saveObjectStores()
	{
		if (connections == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.OBJECT_STORES_TAG);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );

		saveObjectStores(memento);
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

	public void addObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		listeners.remove(listener);
	}

	private void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (ObjectStoresManagerListener listener : listeners) {
			listener.objectStoreItemsChanged(event);
		}
	}
	
	class ConnectConnectionTask implements Callable<Object> {
		private Object connectionName;

		public ConnectConnectionTask(Object connectionName) {
			super();
			this.connectionName = connectionName;
		}

		@Override
		public Object call() throws Exception {
			connections.get( connectionName ).connect();
			ArrayList<IObjectStoreItem> connectionObjectStores = new ArrayList<IObjectStoreItem>();
			
			for ( IObjectStoreItem objectStoreItem : objectStores.getChildren() ) {
				
				ObjectStore objectStore = (ObjectStore) objectStoreItem;
				if ( objectStore.getConnection().getName().equals(connectionName)) {
					objectStore.connect();
					connectionObjectStores.add( objectStore );
				}
			}

			fireObjectStoreItemsChanged(null, null, connectionObjectStores.toArray( new IObjectStoreItem[0] ) );
			return null;
		}
	}
	
	class CreateConnectionTask implements Callable<String> {

		private String url;
		private String username;
		private String password;

		public CreateConnectionTask(String url, String username, String password) {
			super();
			this.url = url;
			this.username = username;
			this.password = password;
		}

		@Override
		public String call() throws Exception {

			ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
			
			objectStoreConnection.setUrl(url);
			objectStoreConnection.setUsername(username);
			objectStoreConnection.setPassword(password);
			objectStoreConnection.connect();
	
			connections.put( objectStoreConnection.getName(), objectStoreConnection);
			
			return objectStoreConnection.getName();
		}
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		handleTaskCompleteEvent(taskCompleteEvent);
	}

	private void handleTaskCompleteEvent(TaskCompleteEvent taskCompleteEvent) {
		if ( isTaskSourceInstanceOf(taskCompleteEvent, DeleteTask.class) ) {
			handleDeleteTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, LoadChildrenTask.class) ) {
			handleLoadChildrenTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, RefreshTask.class) ) {
			handleRefreshTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, MoveTask.class) ) {
			handleMoveTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, UpdateTask.class) ) {
			handleUpdateTaskCompleted(taskCompleteEvent);
//		} if ( isTaskSourceInstanceOf(taskCompleteEvent, CheckoutTask.class) ||
//				isTaskSourceInstanceOf(taskCompleteEvent, CancelCheckoutTask.class) ||
//				isTaskSourceInstanceOf(taskCompleteEvent, CheckinTask.class) ) {
//			handleDocumentTaskCompleted(taskCompleteEvent);
//		}
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, DocumentTask.class) ) {
			handleDocumentTaskCompleted(taskCompleteEvent);
		}
	}

	private boolean isTaskSourceInstanceOf(TaskCompleteEvent taskCompleteEvent, Class<?> taskClass) {

		Class<? extends Object> eventClass = taskCompleteEvent.getSource().getClass();
		
		do {
			if ( eventClass.equals( taskClass ) ) {
				return true;
			}
			eventClass = eventClass.getSuperclass();
		} while ( ! eventClass.equals( Object.class ) );
		
		return false;
	}

	private void handleUpdateTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		UpdateTask updateTask = (UpdateTask) taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = updateTask.getObjectStoreItems();
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
	}

	private void handleDeleteTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		DeleteTask deleteTask = (DeleteTask) taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = deleteTask.getObjectStoreItems();
		fireObjectStoreItemsChanged(null, objectStoreItems, null );
	}

	private void handleLoadChildrenTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		LoadChildrenTask loadChildrenTask = (LoadChildrenTask) taskCompleteEvent.getSource();
		ObjectStoreItem objectStoreItem = loadChildrenTask.getObjectStoreItem();
		fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem } );
	}

	private void handleRefreshTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		RefreshTask refreshTask = (RefreshTask)taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = refreshTask.getObjectStoreItems();
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
	}

	private void handleMoveTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		MoveTask moveTask = (MoveTask) taskCompleteEvent.getSource();
		fireObjectStoreItemsChanged(null, moveTask.getObjectStoreItems(), moveTask.getUpdatedObjectStoreItems() );
	}

	private void handleDocumentTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		DocumentTask documentTask = (DocumentTask) taskCompleteEvent.getSource();
		ObjectStoreItem objectStoreItem = documentTask.getDocument();
		fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem } );
	}
}