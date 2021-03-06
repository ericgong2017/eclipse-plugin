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

package com.ecmdeveloper.plugin.diagrams.properties;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElementWithResize;
import com.ecmdeveloper.plugin.diagrams.model.validators.PositiveIntegerValidator;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class ClassDiagramElementWithResizeProperties extends ClassDiagramElementProperties {

	private static final String HEIGHT_PROP = "ClassDiagramElement.Height";
	private static final String WIDTH_PROP = "ClassDiagramElement.Width";

	protected static final TextPropertyDescriptor WIDTH_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(WIDTH_PROP, "Width");
	protected static final TextPropertyDescriptor HEIGHT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(HEIGHT_PROP, "Height");
	
	static {

		WIDTH_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.SIZE );
		WIDTH_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );

		HEIGHT_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.SIZE );
		HEIGHT_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );
	}

	private ClassDiagramElementWithResize classDiagramElementWithResize;
	
	public ClassDiagramElementWithResizeProperties(ClassDiagramElementWithResize classDiagramElementWithResize) {
		super(classDiagramElementWithResize);
		this.classDiagramElementWithResize = classDiagramElementWithResize;
	}

	public Object getPropertyValue(Object propertyId) {

		if (HEIGHT_PROP.equals(propertyId)) {
			return Integer.toString(classDiagramElementWithResize.getSize().height);
		}
		if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(classDiagramElementWithResize.getSize().width);
		}
		return super.getPropertyValue(propertyId);
	}

	public void setPropertyValue(Object propertyId, Object value) {
		if (HEIGHT_PROP.equals(propertyId)) {
			int height = Integer.parseInt((String) value);
			classDiagramElementWithResize.setSize(new Dimension(classDiagramElementWithResize
					.getSize().width, height));
		} else if (WIDTH_PROP.equals(propertyId)) {
			int width = Integer.parseInt((String) value);
			classDiagramElementWithResize.setSize(new Dimension(width,
					classDiagramElementWithResize.getSize().height));
		}

		super.setPropertyValue(propertyId, value);
	}
}
