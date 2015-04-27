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

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.util.Broadcast;


public final class EventManager
{
	// Logger for this class
	protected static final Logger _log = Logger.getLogger(EventManager.class.getName());
	
	private StartTask _task;
	
	/**
	 * Method to start participation
	 */
	public void startReg()
	{
		if (!EventEngine.startParticipation())
		{
			Broadcast.toAllOnlinePlayers("TvT Event: Event was cancelled.");
			_log.warning("TvTEventEngine[TvTManager.run()]: Error spawning event npc for participation.");
			
			scheduleEventStart();
		}
		else
		{
			Broadcast.toAllOnlinePlayers("TvT Event: Registration opened for " + Config.TVT_EVENT_PARTICIPATION_TIME + " minute(s).");
			
			// schedule registration end
			_task.setStartTime(System.currentTimeMillis() + (60000L * Config.TVT_EVENT_PARTICIPATION_TIME));
			ThreadPoolManager.getInstance().executeGeneral(_task);
		}
	}
	
	/**
	 * Method to start the fight
	 */
	public void startEvent()
	{
		if (!EventEngine.startFight())
		{
			Broadcast.toAllOnlinePlayers("TvT Event: Event cancelled due to lack of Participation.");
			_log.info("TvTEventEngine[TvTManager.run()]: Lack of registration, abort event.");
			
			scheduleEventStart();
		}
		else
		{
			EventEngine.sysMsgToAllParticipants("TvT Event: Teleporting participants to an arena in " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			_task.setStartTime(System.currentTimeMillis() + (60000L * Config.TVT_EVENT_RUNNING_TIME));
			ThreadPoolManager.getInstance().executeGeneral(_task);
		}
	}
	
	/**
	 * Method to end the event and reward
	 */
	public void endEvent()
	{
		Broadcast.toAllOnlinePlayers(EventEngine.calculateRewards());
		EventEngine.sysMsgToAllParticipants("TvT Event: Teleporting back to the registration npc in " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		EventEngine.stopFight();
		
		scheduleEventStart();
	}
	
	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeGeneral(_task);
		}
	}
	
	/**
	 * Class for Events cycles
	 */
	class StartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public StartTask(long startTime)
		{
			_startTime = startTime;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		
		
		@Override
		public void run()
		{
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			if (delay > 0)
			{
				announce(delay);
			}
			
			int nextMsg = 0;
			if (delay > 3600)
			{
				nextMsg = delay - 3600;
			}
			else if (delay > 1800)
			{
				nextMsg = delay - 1800;
			}
			else if (delay > 900)
			{
				nextMsg = delay - 900;
			}
			else if (delay > 600)
			{
				nextMsg = delay - 600;
			}
			else if (delay > 300)
			{
				nextMsg = delay - 300;
			}
			else if (delay > 60)
			{
				nextMsg = delay - 60;
			}
			else if (delay > 5)
			{
				nextMsg = delay - 5;
			}
			else if (delay > 0)
			{
				nextMsg = delay;
			}
			else
			{
				// start
				if (EventEngine.isInactive())
				{
					startReg();
				}
				else if (EventEngine.isParticipating())
				{
					startEvent();
				}
				else
				{
					endEvent();
				}
			}
			
			if (delay > 0)
			{
				nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			}
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (EventEngine.isParticipating())
				{
					Broadcast.toAllOnlinePlayers("TvT Event: " + (time / 60 / 60) + " hour(s) until registration is closed!");
				}
				else if (EventEngine.isStarted())
				{
					EventEngine.sysMsgToAllParticipants("TvT Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
				}
			}
			else if (time >= 60)
			{
				if (EventEngine.isParticipating())
				{
					Broadcast.toAllOnlinePlayers("TvT Event: " + (time / 60) + " minute(s) until registration is closed!");
				}
				else if (EventEngine.isStarted())
				{
					EventEngine.sysMsgToAllParticipants("TvT Event: " + (time / 60) + " minute(s) until the event is finished!");
				}
			}
			else
			{
				if (EventEngine.isParticipating())
				{
					Broadcast.toAllOnlinePlayers("TvT Event: " + time + " second(s) until registration is closed!");
				}
				else if (EventEngine.isStarted())
				{
					EventEngine.sysMsgToAllParticipants("TvT Event: " + time + " second(s) until the event is finished!");
				}
			}
		}
	}
	
	/**
	 * Initialize new/Returns the one and only instance<br>
	 * @return EventManager<br>
	 */
	public static EventManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final EventManager _instance = new EventManager();
	}
}
