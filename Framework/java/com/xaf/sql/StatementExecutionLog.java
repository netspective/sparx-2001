package com.xaf.sql;

import java.util.*;
import com.xaf.value.*;

public class StatementExecutionLog extends ArrayList
{
	public final class StatementExecutionStatistics
	{
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

    public StatementExecutionLog()
    {
    }

	public StatementExecutionLogEntry createNewEntry(ValueContext vc, StatementInfo si)
	{
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