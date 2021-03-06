/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class BaseInputRenderer {

	protected String tooltipText;
	protected String labelText;

	public BaseInputRenderer(String labelText, String tooltipText) {
		this.tooltipText = tooltipText;
		this.labelText = labelText;
	}

	protected void renderLabel(Composite container ) {
		Label label = new Label( container, SWT.NONE );
		GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText( labelText );
		label.setToolTipText(tooltipText);
	}

	protected Control addText(Composite container, int numColumns ) {
		Text textField = new Text(container, SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return textField;
	}
}
