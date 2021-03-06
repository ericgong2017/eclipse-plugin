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

package com.ecmdeveloper.plugin.properties.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.properties.editors.input.ObjectStoreItemEditorInput;
import com.ecmdeveloper.plugin.properties.model.PropertiesObject;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	private static final String PROPERTY_NOT_FOUND_MESSAGE = "Property {0} not found in properties collection";
	private IClassDescription classDescription;
	private PropertiesObject propertiesObject;
	private TableViewer viewer;
	private ArrayList<Property> properties;
	
	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<Property> properties = getProperties();
		return properties.toArray();
	}

	private ArrayList<Property> getProperties() {

		properties = new ArrayList<Property>();
		
		for ( IPropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			properties.add( new Property( propertiesObject, propertyDescription ) );
		}
		return properties;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TableViewer) viewer;
		
		if ( propertiesObject != null ) {
			propertiesObject.removePropertyChangeListener(this);
		}
		
		if ( newInput != null ) {
			classDescription = ((ObjectStoreItemEditorInput) newInput).getClassDescription();
			propertiesObject = ((ObjectStoreItemEditorInput) newInput).getPropertiesObject();
			
			if ( propertiesObject != null ) {
				propertiesObject.addPropertyChangeListener(this);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		String propertyName = event.getPropertyName();
		Property changedProperty = findProperty( propertyName );
		viewer.update(changedProperty, null);
	}

	private Property findProperty(String propertyName) {

		for ( Property property : properties ) {
			if ( property.getName().equals( propertyName ) ) {
				return property;
			}
		}
		
		throw new IllegalArgumentException(MessageFormat.format(PROPERTY_NOT_FOUND_MESSAGE, propertyName ) );
	}
}
