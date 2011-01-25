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

/**
 * @author ricardo.belfor
 *
 */
public class QueryOperation extends QueryPart {

	private ArrayList<QueryPart> queryParts;
	private final QueryOperationType queryOperationType;
	
	public QueryOperation(QueryOperationType queryOperationType) {
		super();
		this.queryOperationType = queryOperationType;
		queryParts = new ArrayList<QueryPart>();
	}

	public boolean add(QueryPart queryPart ) {
		return queryParts.add(queryPart);
	}
	
	public boolean remove(QueryPart queryPart ) {
		return queryParts.remove(queryPart);
	}

	public Collection<QueryPart> getQueryParts()
	{
		return queryParts;
	}

	@Override
	public String toString() {
		return queryOperationType.toString();
	}
}