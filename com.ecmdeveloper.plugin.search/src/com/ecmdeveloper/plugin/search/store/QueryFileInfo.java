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

package com.ecmdeveloper.plugin.search.store;

import java.util.Date;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFileInfo {

	private final String name;
	private final String sql;
	private final Date lastModified;
	
	public QueryFileInfo(String name, String sql, Date lastModified) {
		this.name = name;
		this.sql = sql;
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public String getSql() {
		return sql;
	}

	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public String toString() {
		return getName();
	}
}
