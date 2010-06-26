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

package com.ecmdeveloper.plugin.tracker.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.tracker.Activator;
import com.ecmdeveloper.plugin.tracker.util.PluginLog;


/**
 * @author Ricardo.Belfor
 *
 */
public class FilesTracker {

	private static final String FILES_TRACKER_FILENAME = "files_tracker.xml";
	private static final int CURRENT_FILE_VERSION = 1;
	private static FilesTracker filesTracker;

	Map<String,TrackedFile> trackedFilesMap;
	
	public static FilesTracker getInstance()
	{
		if ( filesTracker == null )
		{
			filesTracker = new FilesTracker();
		}
		return filesTracker;
	}
	
	public void addTrackedFile(String filename, String id, String name, String connectionName,
			String connectionDisplayName, String objectStoreName, String objectStoreDisplayName)
	{
		initializeTrackedFiles();
		
		TrackedFile trackedFile = new TrackedFile();
		
		trackedFile.setFilename(filename);
		trackedFile.setId(id);
		trackedFile.setName(name);
		trackedFile.setConnectionName(connectionName);
		trackedFile.setConnectionDisplayName(connectionDisplayName);
		trackedFile.setObjectStoreName(objectStoreName);
		trackedFile.setObjectStoreDisplayName(objectStoreDisplayName);
		
		trackedFilesMap.put(filename, trackedFile);
		
		saveTrackedFiles();
	}
	
	public boolean isFileTracked(String filename) {
		initializeTrackedFiles();
		return trackedFilesMap.containsKey(filename);
	}
	
	public TrackedFile getTrackedFile(String filename ) {
		initializeTrackedFiles();
		if ( trackedFilesMap.containsKey(filename) ) {
			return trackedFilesMap.get(filename);
		}
		return null;
	}
	
	private void initializeTrackedFiles() {
		if ( trackedFilesMap == null) {
			trackedFilesMap = new HashMap<String, TrackedFile>();
			loadTrackedFiles();
		}
	}
	
	private void loadTrackedFiles() {
	
		FileReader reader = getFilesTrackerFileReader();
		if ( reader == null ) {
			return;
		}
		
		try {
			XMLMemento memento = XMLMemento.createReadRoot( reader );
			IMemento trackedFilesChild = memento.getChild( FilesTrackerTagNames.TRACKED_FILES );
			for ( IMemento trackedFileChild : trackedFilesChild.getChildren( FilesTrackerTagNames.TRACKED_FILE ) ) {
				TrackedFile trackedFile = loadTrackedFile(trackedFileChild);
				trackedFilesMap.put(trackedFile.getFilename(), trackedFile );
			}
		} catch (WorkbenchException e) {
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

	private TrackedFile loadTrackedFile(IMemento trackedFileChild) {
		TrackedFile trackedFile = new TrackedFile();
		
		trackedFile.setFilename( trackedFileChild.getString( FilesTrackerTagNames.FILENAME) );
		trackedFile.setId(trackedFileChild.getString( FilesTrackerTagNames.ID) );
		trackedFile.setName(trackedFileChild.getString( FilesTrackerTagNames.NAME) );
		trackedFile.setConnectionName(trackedFileChild.getString( FilesTrackerTagNames.CONNECTION_NAME ) );
		trackedFile.setConnectionDisplayName(trackedFileChild.getString( FilesTrackerTagNames.CONNECTION_DISPLAY_NAME ) );
		trackedFile.setObjectStoreName(trackedFileChild.getString( FilesTrackerTagNames.OBJECT_STORE_NAME ) );
		trackedFile.setObjectStoreDisplayName(trackedFileChild.getString( FilesTrackerTagNames.OBJECT_STORE_DISPLAY_NAME ) );
		
		return trackedFile;
	}
	
	private void saveTrackedFiles() {

		initializeTrackedFiles();
		
		XMLMemento memento = XMLMemento.createWriteRoot(FilesTrackerTagNames.TRACKED_FILES);
		memento.putInteger(FilesTrackerTagNames.VERSION, CURRENT_FILE_VERSION );
	
		for ( TrackedFile trackedFile : trackedFilesMap.values() ) {
			saveTrackedFile(memento, trackedFile);
		}
		
		saveFilesTrackerFile( memento);
	}

	private void saveTrackedFile(XMLMemento memento, TrackedFile trackedFile) {

		IMemento trackedFileChild = memento.createChild(FilesTrackerTagNames.TRACKED_FILE );

		trackedFileChild.putString(FilesTrackerTagNames.FILENAME, trackedFile.getFilename() );
		trackedFileChild.putString(FilesTrackerTagNames.ID, trackedFile.getId() );
		trackedFileChild.putString(FilesTrackerTagNames.NAME, trackedFile.getName() );
		trackedFileChild.putString(FilesTrackerTagNames.CONNECTION_NAME, trackedFile.getConnectionName() );
		trackedFileChild.putString(FilesTrackerTagNames.CONNECTION_DISPLAY_NAME, trackedFile.getConnectionDisplayName() );
		trackedFileChild.putString(FilesTrackerTagNames.OBJECT_STORE_NAME, trackedFile.getObjectStoreName() );
		trackedFileChild.putString(FilesTrackerTagNames.OBJECT_STORE_DISPLAY_NAME, trackedFile.getObjectStoreDisplayName() );
	}

	private File getFilesTrackerFile()
	{
		return Activator.getDefault().getStateLocation().append(FILES_TRACKER_FILENAME).toFile();
	}

	private void saveFilesTrackerFile(XMLMemento memento) {

		FileWriter writer = null;
		try {
			writer = new FileWriter( getFilesTrackerFile() );
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
	
	private FileReader getFilesTrackerFileReader() {
		FileReader reader = null;
		try {
			reader = new FileReader(getFilesTrackerFile());
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			PluginLog.error(e);
		}	
		return reader;
	}
}
