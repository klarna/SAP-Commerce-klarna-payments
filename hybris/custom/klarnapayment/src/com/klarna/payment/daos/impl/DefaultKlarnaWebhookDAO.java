package com.klarna.payment.daos.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import javax.annotation.Resource;

import com.klarna.model.KlarnaWebhookModel;
import com.klarna.model.KlarnaWebhookNotificationModel;
import com.klarna.payment.daos.KlarnaWebhookDAO;


public class DefaultKlarnaWebhookDAO implements KlarnaWebhookDAO
{

	protected static final String WEBHOOK_QUERY_STRING = "SELECT {p:" + KlarnaWebhookModel.PK + "}" + "FROM {"
			+ KlarnaWebhookModel._TYPECODE + " AS p} " + "WHERE {p:" + KlarnaWebhookModel.BASESITE + "}=?baseSite ";

	protected static final String WEBHOOK_NOTIFICATION_QUERY_STRING = "SELECT {p:" + KlarnaWebhookNotificationModel.PK + "}"
			+ "FROM {" + KlarnaWebhookNotificationModel._TYPECODE + " AS p} " + "WHERE {p:"
			+ KlarnaWebhookNotificationModel.ID + "}=?id ";

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<KlarnaWebhookModel> findWebhookByBaseSite(final BaseSiteModel baseSite)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(WEBHOOK_QUERY_STRING);
		query.addQueryParameter("baseSite", baseSite);
		return flexibleSearchService.<KlarnaWebhookModel> search(query).getResult();
	}

	@Override
	public List<KlarnaWebhookNotificationModel> findWebhookNotificationById(final String id)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(WEBHOOK_NOTIFICATION_QUERY_STRING);
		query.addQueryParameter("id", id);
		return flexibleSearchService.<KlarnaWebhookNotificationModel> search(query).getResult();
	}
}
