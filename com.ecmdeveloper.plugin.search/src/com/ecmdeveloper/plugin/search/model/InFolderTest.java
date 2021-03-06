/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.model;

import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class InFolderTest extends QueryComponent {

	private static final long serialVersionUID = 1L;
	
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			InFolderTest.class, "In Folder Test", "Query Field In Folder Test",
			QueryIcons.INFOLDER_TEST_ICON, QueryIcons.INFOLDER_TEST_ICON_LARGE){

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField.getQueryTable().isContentEngineTable() && queryField.isContainable();
				}};	

	private String folder;
	
	public InFolderTest(Query query) {
		super(query);
	}
	
	public static ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(QueryIcons.INFOLDER_TEST_ICON);
	}

	public static ImageDescriptor getLargeIcon() {
		return Activator.getImageDescriptor(QueryIcons.INFOLDER_TEST_ICON_LARGE);
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		String oldValue = this.folder;
		this.folder = folder;
		firePropertyChange(FIELD_CHANGED, oldValue, folder);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}

	private String toString(boolean strict) {
		if ( getField() != null && folder != null ) { 
			StringBuffer result = new StringBuffer();
			appendField(result, strict);
			result.append( " INFOLDER('");
			result.append(folder);
			result.append( "')");
			return result.toString();
		} else {
			return "";
		}
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.IN_FOLDER_TEST;
	}
}
