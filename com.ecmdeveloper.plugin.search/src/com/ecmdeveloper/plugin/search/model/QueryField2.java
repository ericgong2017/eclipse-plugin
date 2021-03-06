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

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;

/**
 * @author ricardo.belfor
 *
 */
public class QueryField2 implements IQueryField {

	public static final String PROPERTY_VALUE = "property_value";
	
	private final String name;
	private final String displayName;
	private final QueryFieldType queryFieldType;
	private final IQueryTable queryTable;
	private final boolean orderable;
	private final boolean containable;
	private final boolean cbrEnabled;
	private final boolean searchable;
	private final boolean selectable;
	
	private SortType sortType = SortType.NONE;
	private int sortOrder = 0;
	private boolean selected;
	private String alias;

	public QueryField2(IPropertyDescription propertyDescription, IQueryTable queryTable) {
		this.queryTable = queryTable;
		this.name = propertyDescription.getName();
		this.displayName = propertyDescription.getDisplayName();
		this.queryFieldType = getQueryFieldType(propertyDescription);
		this.orderable = propertyDescription.isOrderable();
		this.containable = propertyDescription.isContainable();
		this.cbrEnabled = propertyDescription.isCBREnabled();
		this.searchable = propertyDescription.isSearchable();
		this.selectable = propertyDescription.isSelectable();
	}
	
	public QueryField2(String name, String displayName, QueryFieldType queryFieldType,
			boolean orderable, boolean containable, boolean cbrEnabled, boolean searchable, boolean selectable, IQueryTable queryTable) {
		this.name = name;
		this.displayName = displayName;
		this.queryFieldType = queryFieldType;
		this.orderable = orderable;
		this.containable = containable;
		this.cbrEnabled = cbrEnabled;
		this.selectable = selectable;
		this.queryTable = queryTable;
		this.searchable = searchable;
	}

	private QueryFieldType getQueryFieldType(IPropertyDescription propertyDescription) {
		switch ( propertyDescription.getPropertyType() ) {
		case BINARY: return QueryFieldType.BINARY;
		case BOOLEAN: return propertyDescription.isMultivalue() ? QueryFieldType.BOOLEAN_MV : QueryFieldType.BOOLEAN;
		case DATE: return propertyDescription.isMultivalue() ? QueryFieldType.DATE_MV : QueryFieldType.DATE;
		case DOUBLE: return propertyDescription.isMultivalue() ? QueryFieldType.DOUBLE_MV : QueryFieldType.DOUBLE;
		case GUID: return propertyDescription.isMultivalue() ? QueryFieldType.GUID_MV : QueryFieldType.GUID;
		case LONG: return propertyDescription.isMultivalue() ? QueryFieldType.LONG_MV : QueryFieldType.LONG ;
		case OBJECT: return QueryFieldType.OBJECT;
		case STRING: return propertyDescription.isMultivalue() ? QueryFieldType.STRING_MV : QueryFieldType.STRING;
		case UNKNOWN: return QueryFieldType.NONE;
		}
		return null;
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
	public QueryFieldType getType() {
		return queryFieldType;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public SortType getSortType() {
		return sortType;
	}

	@Override
	public void setSortType(SortType sortType) {
		SortType oldValue = this.sortType;
		this.sortType = sortType;
		queryTable.notifyQueryFieldChanged(PROPERTY_VALUE, oldValue,  sortType);
	}

	@Override
	public int getSortOrder() {
		return sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		int oldValue = this.sortOrder;
		this.sortOrder = sortOrder;
		queryTable.notifyQueryFieldChanged(PROPERTY_VALUE, oldValue,  sortOrder);
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		boolean oldValue = this.selected;
		this.selected = selected;
		queryTable.notifyQueryFieldChanged(PROPERTY_VALUE, oldValue,  selected);
	}

	public IQueryTable getQueryTable() {
		return queryTable;
	}

	@Override
	public boolean isOrderable() {
		return orderable;
	}

	@Override
	public boolean isSupportsWildcards() {
		return searchable && QueryFieldType.STRING.equals( queryFieldType );
	}

	@Override
	public boolean isContainable() {
		return containable;
	}

	@Override
	public boolean isSearchable() {
		return searchable;
	}

	@Override
	public boolean isCBREnabled() {
		return cbrEnabled;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		String oldValue = this.alias;
		this.alias = alias;
		queryTable.notifyQueryFieldChanged(PROPERTY_VALUE, oldValue,  alias);
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}
}
