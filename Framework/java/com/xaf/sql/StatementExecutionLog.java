package com.xaf.sql;

import java.util.*;
import com.xaf.config.*;
import com.xaf.value.*;

public class StatementExecutionLog extends ArrayList
{
	public final class StatementExecutionStatistics
	{
		public int resetAfterCount;
		public int totalExecutions;
		public int totalFailed;

		public long averageTotalExecTime;
		public long maxTotalExecTime;
		public long sumTotalExecTime;

		public long averageConnectionEstablishTime;
		public long maxConnectionEstablishTime;
		public long sumConnectionEstablishTime;

		public long averageBindParamsTime;
		public long maxBindParamsTime;
		public long sumBindParamsTime;

		public long averageSqlExecTime;
		public long maxSqlExecTime;
		public long sumSqlExecTime;

		public StatementExecutionStatistics()
		{
		}
	}

	/* resetLogAfterCount
	     value -1 means unknown (find out at first call)
		 value 0 means never reset
		 value > 0 means reset after this many entries
	*/
	private int resetLogAfterCount = -1;

    public StatementExecutionLog()
    {
    }

	public int getResetLogAfterCount()
	{
		return resetLogAfterCount;
	}

	public StatementExecutionLogEntry createNewEntry(ValueContext vc, StatementInfo si)
	{
		if(resetLogAfterCount == -1)
		{
			Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
			if(appConfig == null)
				throw new RuntimeException("Unable to obtain default configuration");
			Collection logs = appConfig.getValues(vc, "com.xaf.sql.StatementManager.ExecutionLog.ResetCount." + si.getId());
			if(logs == null)
				logs = appConfig.getValues(vc, "com.xaf.sql.StatementManager.ExecutionLog.ResetCount");
			if(logs == null)
				resetLogAfterCount = 0;
			else
			{
				String envName = ConfigurationManagerFactory.getExecutionEvironmentName(vc.getServletContext());
				for(Iterator i = logs.iterator(); i.hasNext(); )
				{
					Object entry = i.next();
					if(entry instanceof Property)
					{
						Property logProperty = (Property) entry;
						String logEnvName = logProperty.getName();

						if(envName.equalsIgnoreCase(logEnvName))
						{
							String resetCountStr = appConfig.getValue(vc, logProperty, null);
							try
							{
								int resetCount = Integer.parseInt(resetCountStr);
								resetLogAfterCount = resetCount;
							}
							catch(Exception e)
							{
								resetLogAfterCount = 0;
							}
						}
					}
				}

				// if no value specified, then default to no reset
				if(resetLogAfterCount == -1)
					resetLogAfterCount = 0;
			}
		}

		if(resetLogAfterCount > 0 && size() >= resetLogAfterCount)
			clear();

		StatementExecutionLogEntry result = new StatementExecutionLogEntry(vc, si);
		add(result);
		return result;
	}

	public StatementExecutionStatistics getStatistics()
	{
		StatementExecutionStatistics stats = new StatementExecutionStatistics();

		int items = size();
		int failed = 0;
		int successful = 0;

	    for(int i = 0; i < items; i++)
		{
			StatementExecutionLogEntry entry = (StatementExecutionLogEntry) get(i);
			if(! entry.wasSuccessful())
			{
				failed++;
				continue;
			}

			long totalTime = entry.getTotalExecutionTime();
			stats.sumTotalExecTime += totalTime;
			if(totalTime > stats.maxTotalExecTime)
				stats.maxTotalExecTime = totalTime;

			long connTime = entry.getConnectionEstablishTime();
			stats.sumConnectionEstablishTime += connTime;
			if(connTime > stats.maxConnectionEstablishTime)
				stats.maxConnectionEstablishTime = connTime;

			long bindParamsTime = entry.getBindParamsBindTime();
			stats.sumBindParamsTime += bindParamsTime;
			if(bindParamsTime > stats.maxBindParamsTime)
				stats.maxBindParamsTime = bindParamsTime;

			long sqlExecTime = entry.getSqlExecTime();
			stats.sumSqlExecTime += sqlExecTime;
			if(sqlExecTime > stats.maxSqlExecTime)
				stats.maxSqlExecTime = sqlExecTime;

			successful++;
		}

		stats.resetAfterCount = resetLogAfterCount;
		stats.totalExecutions = items;
		stats.totalFailed = failed;

		if(successful == 0)
			return stats;

		stats.averageTotalExecTime = stats.sumTotalExecTime / successful;
		stats.averageConnectionEstablishTime = stats.sumConnectionEstablishTime / successful;
		stats.averageBindParamsTime = stats.sumBindParamsTime / successful;
		stats.averageSqlExecTime = stats.sumSqlExecTime / successful;

		return stats;
	}
}