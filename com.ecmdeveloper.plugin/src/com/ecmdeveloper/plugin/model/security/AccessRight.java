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

package com.ecmdeveloper.plugin.model.security;

import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;

/**
 * @author ricardo.belfor
 *
 */
public class AccessRight implements IAccessRight {

	private final String name;
	private final Integer mask;
	private final AccessControlEntry parent;

	private boolean granted;
	
	public AccessRight(AccessControlEntry parent, String name, Integer mask, boolean granted) {
		this.parent = parent;
		this.name = name;
		this.mask = mask;
		this.granted = granted;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isGranted() {
		return granted;
	}

	public int getMask() {
		return mask;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void setGranted(boolean granted) {
		this.granted = granted;
		parent.onAccessRightChanged(this);
//		listeners.firePropertyChange(name, granted, !granted );
	}

	@Override
	public void setGranted(IAccessLevel accessLevel) {
		granted = (((AccessLevel)accessLevel).getAccessMask() & mask) != 0; 
	}
}
