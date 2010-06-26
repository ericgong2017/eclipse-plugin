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
import java.util.ArrayList;

/**
 * @author Ricardo.Belfor
 *
 */
public class TrackedFile {

	private String id;
	private String name;
	private String objectStore;
	private String filename;
	private String connectionName;
	private String connectionDisplayName;
	private String objectStoreName;
	private String objectStoreDisplayName;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getObjectStore() {
		return objectStore;
	}
	public void setObjectStore(String objectStore) {
		this.objectStore = objectStore;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getConnectionDisplayName() {
		return connectionDisplayName;
	}
	public void setConnectionDisplayName(String connectionDisplayName) {
		this.connectionDisplayName = connectionDisplayName;
	}
	public String getObjectStoreName() {
		return objectStoreName;
	}
	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}
	public String getObjectStoreDisplayName() {
		return objectStoreDisplayName;
	}
	public void setObjectStoreDisplayName(String objectStoreDisplayName) {
		this.objectStoreDisplayName = objectStoreDisplayName;
	}
}