/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver;

import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.on.OnEventFinish;
import com.l2jserver.on.OnEventKill;
import com.l2jserver.on.OnEventKillTeam;
import com.l2jserver.on.OnEventRegistrationStart;
import com.l2jserver.on.OnEventStart;

/**
 * @author UnAfraid
 */
public enum EventType
{
	// Events Engine
	ON_EVENT_FINISH(OnEventFinish.class, void.class),
	ON_EVENT_KILL(OnEventKill.class, void.class),
	ON_EVENT_KILL_TEAM(OnEventKillTeam.class, void.class),
	ON_EVENT_REGISTRATION_START(OnEventRegistrationStart.class, void.class),
	ON_EVENT_START(OnEventStart.class, void.class);
	
	private final Class<? extends IBaseEvent> _eventClass;
	private final Class<?>[] _returnClass;
	
	private EventType(Class<? extends IBaseEvent> eventClass, Class<?>... returnClasss)
	{
		_eventClass = eventClass;
		_returnClass = returnClasss;
	}
	
	public Class<? extends IBaseEvent> getEventClass()
	{
		return _eventClass;
	}
	
	public Class<?>[] getReturnClasses()
	{
		return _returnClass;
	}
	
	public boolean isEventClass(Class<?> clazz)
	{
		return _eventClass == clazz;
	}
	
	public boolean isReturnClass(Class<?> clazz)
	{
		return Util.contains(_returnClass, clazz);
	}
}
