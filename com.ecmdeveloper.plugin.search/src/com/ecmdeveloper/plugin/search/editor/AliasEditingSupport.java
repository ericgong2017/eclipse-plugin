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

package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;

/**
 * @author ricardo.belfor
 *
 */
public class AliasEditingSupport extends EditingSupport {

	public AliasEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite) getViewer().getControl() );
	}

	@Override
	protected Object getValue(Object element) {
		String alias = "";
		if ( element instanceof IQueryField ) {
			alias = ((IQueryField) element).getAlias();
		} else if ( element instanceof IQueryTable ) {
			alias = ((IQueryTable) element).getAlias();
		}
		return alias == null? "" : alias;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String alias = getAliasFromValue(value);
		if ( element instanceof IQueryField ) {
			((IQueryField) element).setAlias( alias );
		} else if ( element instanceof IQueryTable ) {
			((IQueryTable) element).setAlias( alias );
		}
		getViewer().refresh(element);
	}

	private String getAliasFromValue(Object value) {
		String alias = (String) value;
		if ( alias != null && alias.isEmpty() ) {
			alias = null;
		}
		return alias;
	}
}
