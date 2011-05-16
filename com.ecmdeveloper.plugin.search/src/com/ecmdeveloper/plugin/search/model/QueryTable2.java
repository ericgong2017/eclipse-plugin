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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class QueryTable2 implements IQueryTable {

	private ArrayList<IQueryTable> childQueryTables;
	private ArrayList<IQueryField> fields = new ArrayList<IQueryField>();
	private final String objectStoreName;
	private final String objectStoreDisplayName;
	private final String connectionName;
	private final String connectionDisplayName;
	private final String name;
	private final String displayName;
	
	public QueryTable2(String name, String displayName, String objectStoreName, String objectStoreDisplayName,
			String connectionName, String connectionDisplayName) {
		this.name = name;
		this.displayName = displayName;
		this.objectStoreName = objectStoreName;
		this.objectStoreDisplayName = objectStoreDisplayName;
		this.connectionName = connectionName;
		this.connectionDisplayName = connectionDisplayName;
		childQueryTables = new ArrayList<IQueryTable>();
	}

	public QueryTable2(ClassDescription classDescription) {
		
		ObjectStore objectStore = classDescription.getObjectStore();
		objectStoreName = objectStore.getName();
		objectStoreDisplayName = objectStore.getDisplayName();
		connectionName = objectStore.getConnection().getName();
		connectionDisplayName = objectStore.getConnection().getDisplayName();
		name = classDescription.getName();
		displayName = classDescription.getDisplayName();
		childQueryTables = new ArrayList<IQueryTable>();
		
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			IQueryField queryField = new QueryField2(propertyDescription, this );
			fields.add(queryField);
		}
		
		Collections.sort(fields, new Comparator<IQueryField>(){
			@Override
			public int compare(IQueryField arg0, IQueryField arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		fields.add(0, new ThisQueryField(this) );
	}

	@Override
	public Collection<IQueryTable> getChildQueryTables() {
		return childQueryTables;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Collection<IQueryField> getQueryFields() {
		return fields;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void addChildQueryTable(IQueryTable childTable) {
		childQueryTables.add(childTable);
		childTable.mergeQueryFields(getQueryFields());

		Collections.sort(childQueryTables, new Comparator<IQueryTable>(){

			@Override
			public int compare(IQueryTable arg0, IQueryTable arg1) {
				return arg0.getName().compareTo( arg1.getName() );
			}
		});
	}

	@Override
	public void mergeQueryFields(Collection<IQueryField> queryFields) {
		for ( IQueryField queryField : queryFields ) {
			IQueryField childQueryField = getQueryField(queryField.getName() );
			if ( childQueryField != null) {
				fields.remove(childQueryField);
			}
		}
	}

	@Override
	public IQueryField getQueryField(String name) {
		for ( IQueryField queryField : fields ) {
			if ( queryField.getName().equals(name) ) {
				return queryField;
			}
		}
		return null;
	}

	@Override
	public Collection<IQueryField> getSelectedQueryFields() {
		Collection<IQueryField> selectedQueryFields = new ArrayList<IQueryField>();
		for (IQueryField queryField : fields ) {
			if ( queryField.isSelected() ) {
				selectedQueryFields.add(queryField);
			}
		}
		
		for ( IQueryTable queryTable : childQueryTables ) {
			selectedQueryFields.addAll( queryTable.getSelectedQueryFields() );
		}
		return selectedQueryFields;
	}

	@Override
	public String getObjectStoreName() {
		return objectStoreName;
	}

	@Override
	public String getObjectStoreDisplayName() {
		return objectStoreDisplayName;
	}

	@Override
	public String getConnectionName() {
		return connectionName;
	}

	@Override
	public String getConnectionDisplayName() {
		return connectionDisplayName;
	}

	@Override
	public void addQueryField(IQueryField queryField) {
		fields.add(queryField);
	}
}