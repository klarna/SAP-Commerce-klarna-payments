/**
 *
 */
package com.klarna.payment.daos.impl;

import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import com.klarna.payment.daos.KPTitleDAO;


/**
 * @author rajani.narayanan
 *
 */
public class DefaultKPTitleDAO implements KPTitleDAO
{

	private FlexibleSearchService flexibleSearchService;

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.klarna.daos.KlarnaTitleDAO#findTitleByNameAndLocale(java.lang.String)
	 */
	@Override
	public TitleModel findTitleByName(final String name)
	{

		final String queryString = "SELECT {p:PK}   FROM {Title AS p}   WHERE {p:name:o} = ?name";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("name", name);
		final List<TitleModel> titleModels = flexibleSearchService.<TitleModel> search(query).getResult();
		if (titleModels != null && !titleModels.isEmpty())
		{
			return titleModels.get(0);
		}
		else
		{
			return null;
		}
	}
}
