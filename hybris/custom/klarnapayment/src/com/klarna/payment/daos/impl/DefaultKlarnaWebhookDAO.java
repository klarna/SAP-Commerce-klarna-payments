package com.klarna.payment.daos.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import com.klarna.model.KlarnaWebhookModel;
import com.klarna.payment.daos.KlarnaWebhookDAO;


public class DefaultKlarnaWebhookDAO implements KlarnaWebhookDAO
{

	protected static final String WEBHOOK_QUERY_STRING = "SELECT {p:" + KlarnaWebhookModel.PK + "}" + "FROM {"
			+ KlarnaWebhookModel._TYPECODE + " AS p} " + "WHERE {p:" + KlarnaWebhookModel.BASESITE + "}=?baseSite ";

	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<KlarnaWebhookModel> findWebhookByBaseSite(final BaseSiteModel baseSite)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(WEBHOOK_QUERY_STRING);
		query.addQueryParameter("baseSite", baseSite);
		return flexibleSearchService.<KlarnaWebhookModel> search(query).getResult();
	}
}
