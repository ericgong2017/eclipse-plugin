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

package com.ecmdeveloper.plugin.model.tasks.security;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetMembersTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.security.Principal;
import com.ecmdeveloper.plugin.model.security.Realm;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.ecmdeveloper.plugin.util.CEIterable;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Factory;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.security.Group;
import com.filenet.api.security.User;

/**
 * @author ricardo.belfor
 *
 */
public class GetMembersTask extends BaseTask implements IGetMembersTask {

	private final Realm realm;
	private final Principal principal;
	private static final PropertyFilter MEMBERS_PROPERTY_FILTER = new PropertyFilter();
	{
		MEMBERS_PROPERTY_FILTER.addIncludeProperty(0, null, null, PropertyNames.GROUPS, null);
		MEMBERS_PROPERTY_FILTER.addIncludeProperty(0, null, null, PropertyNames.USERS, null);
	}
	private Collection<IPrincipal> members = new ArrayList<IPrincipal>();
	
	public GetMembersTask(IRealm realm, IPrincipal principal) {
		this.realm = (Realm) realm;
		this.principal = (Principal) principal;
	}

	@Override
	protected Object execute() throws Exception {
		
		com.filenet.api.security.Realm internalRealm = realm.getInternalRealm();
		Group parentGroup = Factory.Group.fetchInstance(internalRealm.getConnection(), principal.getName(),
				MEMBERS_PROPERTY_FILTER);
		
		for ( Group group : new CEIterable<Group>( parentGroup.get_Groups() ) ) {
			members.add( new Principal(group, realm) );
		}

		for ( User user : new CEIterable<User>( parentGroup.get_Users() ) ) {
			members.add( new Principal(user, realm) );
		}
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return (ContentEngineConnection) realm.getObjectStore().getConnection();
	}

	@Override
	public Collection<IPrincipal> getMembers() {
		return members;
	}
}
