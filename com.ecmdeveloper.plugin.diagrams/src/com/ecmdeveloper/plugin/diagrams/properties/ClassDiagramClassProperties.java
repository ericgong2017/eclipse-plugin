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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramClassProperties extends ClassDiagramElementProperties {

	private static final String VISIBLE_ATTRIBUTES_PROPERTY = "ClassDiagramClass.visibleAttributes";
	private static final String VISIBLE_RELATIONS_PROPERTY = "ClassDiagramClass.visibleRelations";
	private static final String CONNECTION_NAME_PROPERTY = "ClassDiagramClass.connectionName";
	private static final String OBJECT_STORE_NAME_PROPERTY = "ClassDiagramClass.objectStoreName";

	protected static final PropertyDescriptor VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR = new PropertyDescriptor(
			VISIBLE_ATTRIBUTES_PROPERTY, "Visible Attributes");

	protected static final PropertyDescriptor VISIBLE_RELATIONS_PROPERTY_DESCRIPTOR = new PropertyDescriptor(
			VISIBLE_RELATIONS_PROPERTY, "Visible Relations");
	
	protected static final PropertyDescriptor CONNECTION_NAME_PROPERTY_DESCRIPTOR = new PropertyDescriptor(
			CONNECTION_NAME_PROPERTY, "Connection Name");

	protected static final PropertyDescriptor OBJECT_STORE_NAME_PROPERTY_DESCRIPTOR = new PropertyDescriptor(
			OBJECT_STORE_NAME_PROPERTY, "Object Store Name");

	private static IPropertyDescriptor[] classDescriptors = { 
		XPOS_PROPERTY_DESCRIPTOR,
		YPOS_PROPERTY_DESCRIPTOR,
		VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR,
		VISIBLE_RELATIONS_PROPERTY_DESCRIPTOR,
		CONNECTION_NAME_PROPERTY_DESCRIPTOR,
		OBJECT_STORE_NAME_PROPERTY_DESCRIPTOR };

	static {
		VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.APPERANCE );
		VISIBLE_RELATIONS_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.APPERANCE );
		CONNECTION_NAME_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.CONTENT_ENGINE );
		OBJECT_STORE_NAME_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.CONTENT_ENGINE );
	}
	
	private ClassDiagramClass classDiagramClass;
	
	public ClassDiagramClassProperties(ClassDiagramClass classDiagramClass) {
		super(classDiagramClass);
		this.classDiagramClass = classDiagramClass;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return classDescriptors;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {

		if ( VISIBLE_ATTRIBUTES_PROPERTY.equals( propertyId ) ) {
			return new VisibleAttributesProperties(classDiagramClass);
		}

		if ( VISIBLE_RELATIONS_PROPERTY.equals( propertyId ) ) {
			return new VisibleRelationsProperties(classDiagramClass);
		}

		if ( CONNECTION_NAME_PROPERTY.equals( propertyId ) ) {
			return classDiagramClass.getConnectionDisplayName();
		}
		
		if ( OBJECT_STORE_NAME_PROPERTY.equals( propertyId ) ) {
			return classDiagramClass.getObjectStoreDisplayName();
		}

		return super.getPropertyValue(propertyId);
	}
	
	
}
