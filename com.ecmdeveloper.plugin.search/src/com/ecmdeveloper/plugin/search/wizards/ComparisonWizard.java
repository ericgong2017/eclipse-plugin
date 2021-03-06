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

package com.ecmdeveloper.plugin.search.wizards;

import java.util.HashMap;

import org.eclipse.jface.wizard.IWizardPage;

import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;

/**
 * @author ricardo.belfor
 * 
 */
public class ComparisonWizard extends QueryComponentWizard {

	private static final String TITLE = "Query Condition Wizard";

	private ComparisonOperationWizardPage comparisonOperationWizardPage;
	private ValueWizardPage valueWizardPage;
	private ComparisonOperation comparisonOperation = ComparisonOperation.EQUAL;
	private HashMap<QueryFieldType, ValueWizardPage> typePages;
	private Object value;
	
	public ComparisonWizard(Query query) {
		super(query);
		setWindowTitle(TITLE);
		typePages = new HashMap<QueryFieldType, ValueWizardPage>();
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return Comparison.DESCRIPTION.isValidFor(queryField);
			}
		};
	}

	public void addPages() {

		super.addPages();

		comparisonOperationWizardPage = new ComparisonOperationWizardPage();
		comparisonOperationWizardPage.setComparisonOperation(comparisonOperation);
		comparisonOperationWizardPage.setField( getSelection() );
		addPage(comparisonOperationWizardPage);
	}

	@Override
	public IWizardPage getStartingPage() {
		if ( isSkipFieldSelection() ) {
			return comparisonOperationWizardPage;
		}
		return super.getStartingPage();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
//		System.out.println( getPageCount() );
//		for (IWizardPage p: getPages() ) {
//			System.out.println( p.getName() );
//		}
		
		if ( page instanceof ComparisonOperationWizardPage ) {
			IQueryField field = getField();
			valueWizardPage = getValueWizardPage(field);
			if ( valueWizardPage != null ) {
				valueWizardPage.setValue(value);
				return valueWizardPage;
			}
		} else if ( page instanceof SelectFieldWizardPage ) {
			comparisonOperationWizardPage.setField( getField() );
		} else if ( page instanceof ValueWizardPage ) {
			return null;
		}
		
		return super.getNextPage(page);
	}

	private ValueWizardPage getValueWizardPage(IQueryField field) {
		if ( field != null ) {
			
			if ( typePages.containsKey(field.getType() ) ) {
				return typePages.get(field.getType());
			}
			
			ValueWizardPage page = createValueWizardPage(field);
			addPage(page);
			typePages.put(field.getType(), page);
			return page;
		}
		return null;
	}

	private ValueWizardPage createValueWizardPage(IQueryField field) {
		ValueWizardPage page;
		
		switch (field.getType() ) {
		case STRING:
		case STRING_MV:
			page = new StringValueWizardPage();
			break;
		case LONG:
			page = new IntegerValueWizardPage();
			break;
		case DOUBLE:
			page = new DoubleValueWizardPage();
			break;
		case GUID:
			page = new IdValueWizardPage();
			break;
		case BOOLEAN:
		case BOOLEAN_MV:
			page = new BooleanValueWizardPage();
			break;
		case DATE:
		case DATE_MV:
			page = new DateValueWizardPage();
			break;
		case OBJECT:
			page = new ObjectValueWizardPage();
			break;
		default:
			throw new IllegalArgumentException();
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		return getField() != null && getComparisonOperation() != null && getValue() != null;
	}

	public Object getValue() {
		if ( valueWizardPage == null) {
			return null;
		}
		return valueWizardPage.getValue();
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ComparisonOperation getComparisonOperation() {
		return comparisonOperationWizardPage.getComparisonOperation();
	}

	public void setComparisonOperation(ComparisonOperation comparisonOperation) {
		this.comparisonOperation = comparisonOperation;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
