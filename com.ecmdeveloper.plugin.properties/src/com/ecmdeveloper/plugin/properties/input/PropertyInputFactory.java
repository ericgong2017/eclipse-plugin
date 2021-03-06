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

package com.ecmdeveloper.plugin.properties.input;

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyInputFactory {

	public static IPropertyInput getPropertyInput( IPropertyDescription propertyDescription, boolean readOnly ) {
			return new ReadOnlyPropertyInput(propertyDescription);

//		if ( propertyDescription.get_ChoiceList() == null) {
//			
//		} else {
//			// TODO: choice list renderer
//		}
		
//		switch ( internalPropertyDescription.get_Cardinality().getValue() )
//		{
//		case Cardinality.SINGLE_AS_INT:
//			
//			if ( internalPropertyDescription instanceof PropertyDescriptionString ) {
//				return new StringPropertyInput((PropertyDescriptionString) internalPropertyDescription);
//			} else if ( propertyDescription.getPropertyType().equals( PropertyType.BOOLEAN ) ) {
//				return new BooleanPropertyInput(propertyDescription);
//			} else if ( propertyDescription.getPropertyType().equals( PropertyType.LONG ) ) {
//				return new IntegerPropertyInput( propertyDescription);
//			}
//			System.out.println( internalPropertyDescription.get_DescriptiveText() );
//			break;
//		case Cardinality.ENUM_AS_INT:
//			
//			break;
//		case Cardinality.LIST_AS_INT:
//		
//			break;
//		}
//
//		return new ReadOnlyPropertyInput(internalPropertyDescription);
//		
////		throw new UnsupportedOperationException( "Input for " + propertyDescription.getName() + " is not yet supported" );
//	}
//
//	private static boolean isReadOnlyOnEdit(
//			com.filenet.api.meta.PropertyDescription internalPropertyDescription) {
//		return ! ( PropertySettability.READ_WRITE.equals( internalPropertyDescription.get_Settability() ) );
//	}
	}
}
