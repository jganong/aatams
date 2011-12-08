package au.org.emii.aatams.report.filter

class ReportFilterFactoryService 
{
    static transactional = true

    ReportFilter newFilter(Class domain, Map params) 
	{
		ReportFilter filter = new ReportFilter(domain)
		
		if (!params)
		{ 
			return filter
		}
		
		if (params.max)
		{
			filter.max = params.max 
		}
		
		if (params.offset)
		{
			filter.offset = params.offset
		}
		
		removeEmptyFilterParameters(params)
		
		if (params.eq)
		{
			filter.addCriterion(new EqualsReportFilterCriterion(params.eq))
		}
		
		if (params.in)
		{
			filter.addCriterion(new InReportFilterCriterion(params.in))
		}
		
		if (params.between)
		{
			filter.addCriterion(new BetweenReportFilterCriterion(params.between))
		}
		
		return filter
    }
	
	private void removeEmptyFilterParameters(Map params)
	{
		def propertiesToRemove = []
		
		params.each
		{
			property, value ->
			
			if (AbstractReportFilterCriterion.isMap(value))
			{
				removeEmptyFilterParameters(value)
				if (!hasLeaf(value))
				{
					propertiesToRemove.add(property)
				}
			}
			else if (AbstractReportFilterCriterion.isLeaf(property, value))
			{
				// Do nothing.
			}
			else
			{
				propertiesToRemove.add(property)
			}
		}
		
		propertiesToRemove.each 
		{
			params.remove(it)
		}
	}
	
	private boolean hasLeaf(Map params)
	{
		if (params == [:])
		{
			return false
		}
		
		boolean retVal = false
		params.each 
		{ 
			property, value ->
			
			if (AbstractReportFilterCriterion.isMap(value))
			{
				retVal |= hasLeaf(value)
			}
			else if (AbstractReportFilterCriterion.isLeaf(property, value))
			{
				retVal |= true
			}
			else
			{
			
			}
		}
		
		return retVal
	}
}
