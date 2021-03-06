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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import com.ecmdeveloper.plugin.diagrams.model.AttributeRelationship;

/**
 * @author Ricardo.Belfor
 *
 */
public class AttributeRelationshipEditPart extends AbstractClassesConnectionEditPart {

	private static final int AGGREGATE_SIZE = 1;
	private static final String SINGLE_MULTIPLICITY = "1";

	public AttributeRelationshipEditPart(AttributeRelationship model) {
		setModel(model);
	}

	public AttributeRelationship getAttributeRelationship() {
		return (AttributeRelationship) getModel();
	}
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getAttributeRelationship().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getAttributeRelationship().removePropertyChangeListener(this);
		}
	}
	
	public void positionConnections() {
		
		AttributeRelationship attributeRelationship = getAttributeRelationship();

		if ( attributeRelationship.isLoop() ) {

			Rectangle bounds = ((AbstractGraphicalEditPart)getSource()).getFigure().getBounds();

			int outerX = bounds.width/2 + 20;
			int outerY = -(bounds.height/2 + 20);
			
			RelativeBendpoint point = new RelativeBendpoint(getConnectionFigure());
			point.setRelativeDimensions(new Dimension(outerX,0), new Dimension(outerX,0) );
			point.setWeight(0.5f);

			RelativeBendpoint point2 = new RelativeBendpoint(getConnectionFigure());
			point2.setRelativeDimensions(new Dimension(outerX,outerY), new Dimension(outerX,outerY) );
			point2.setWeight(0.5f);
			
			RelativeBendpoint point3 = new RelativeBendpoint(getConnectionFigure());
			point3.setRelativeDimensions(new Dimension(0,outerY), new Dimension(0,outerY) );
			point3.setWeight(0.5f);
			
			List<Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();
			constraint.add(point);
			constraint.add(point2);
			constraint.add(point3);
			
			this.getConnectionFigure().setRoutingConstraint(constraint);
		}
	}
	
	@Override
	protected IFigure createFigure() {
		
		PolylineConnection connection = (PolylineConnection) super.createFigure();

		AttributeRelationship attributeRelationship = getAttributeRelationship();
		addDecorations(attributeRelationship, connection);
		addMultiplicityLabels(attributeRelationship, connection);
		return connection;
	}

	@Override
	protected void refreshVisuals() {
		getFigure().setVisible( getAttributeRelationship().isVisible() );
	}
	
	private void addDecorations(AttributeRelationship attributeRelationship,
			PolylineConnection connection) {
		connection.setTargetDecoration(new PolygonDecoration() );
		
		if ( attributeRelationship.getSourceConnector().isAggregate() )  {
			addAggregate(connection);
		} else if ( attributeRelationship.getTargetConnector().getPropertyId() != null ) {
			connection.setSourceDecoration( new PolygonDecoration() );
		}
		
	}

	private void addMultiplicityLabels(AttributeRelationship attributeRelationship,
			PolylineConnection connection) {
		
		addMultiplicityLabel(attributeRelationship.getSourceConnector().getMultiplicity(),
				connection, false, false );

		addMultiplicityLabel(attributeRelationship.getTargetConnector().getMultiplicity(),
				connection, true, attributeRelationship.isLoop() );
	}

	private void addMultiplicityLabel(String multiplicity,
			PolylineConnection connection, boolean isEnd, boolean isLoop ) {
	
		if ( multiplicity == null || multiplicity.isEmpty() ) {
			multiplicity = SINGLE_MULTIPLICITY;
		}

		ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, isEnd);
		targetEndpointLocator.setVDistance(10);

// TODO add attribute labels		
//		ConnectionEndpointLocator targetEndpointLocator2 = new ConnectionEndpointLocator(connection, isEnd);
//		targetEndpointLocator2.setVDistance(-10);
//		Label bla = new Label("myAttribute" );
//		connection.add( bla, targetEndpointLocator2 );
		
		if ( isLoop ) {
			targetEndpointLocator.setUDistance(20);
		}
		Label multiplicityLabel = new Label( multiplicity );
		connection.add(multiplicityLabel, targetEndpointLocator);
	}

	private void addAggregate(PolylineConnection connection) {
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setFill(true);
		decoration.setBackgroundColor( org.eclipse.draw2d.ColorConstants.white );
		PointList decorationPointList = createDecorationPointList();
		decoration.setTemplate(decorationPointList);
		connection.setSourceDecoration(decoration);
	}

	private PointList createDecorationPointList() {
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0,0);
		decorationPointList.addPoint(-AGGREGATE_SIZE, AGGREGATE_SIZE);
		decorationPointList.addPoint(-2*AGGREGATE_SIZE,0);
		decorationPointList.addPoint(-AGGREGATE_SIZE,-AGGREGATE_SIZE);
		return decorationPointList;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		getFigure().setVisible( getAttributeRelationship().isVisible() );
	}
}