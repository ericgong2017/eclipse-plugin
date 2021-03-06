/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.core.model.tasks.IBaseTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.admin.CodeModule;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.events.CmChangePreprocessorAction;

/**
 * @author ricardo.belfor
 *
 */
public class CreateChangePreprocessorTask extends CodeModuleTask implements IBaseTask {

	private final String codeModuleId;
	private final String name;
	private final String className;
	private final boolean enabled;
	private final ObjectStore objectStore;

	public CreateChangePreprocessorTask(String codeModuleId, String name, String className,
			boolean enabled, ObjectStore objectStore) {
		this.codeModuleId = codeModuleId;
		this.name = name;
		this.className = className;
		this.enabled = enabled;
		this.objectStore = objectStore;
	}

	@Override
	protected Object execute() throws Exception {

		VersionSeries versionSeries = getCodeModuleVersionSeries(objectStore, codeModuleId );
		CodeModule document = (CodeModule) versionSeries.get_CurrentVersion();
		
		com.filenet.api.core.ObjectStore objectStoreObject = (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
		CmChangePreprocessorAction action = Factory.CmChangePreprocessorAction.createInstance(objectStoreObject, "CmChangePreprocessorAction");
		
		action.set_CodeModule( document);
		action.set_DisplayName(name);
		action.set_IsEnabled( enabled );
		action.set_ProgId(className);
		action.save(RefreshMode.NO_REFRESH);

		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}

}
