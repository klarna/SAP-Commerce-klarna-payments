package com.klarna.payment.daos;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import java.util.List;

import com.klarna.model.KlarnaWebhookModel;


public interface KlarnaWebhookDAO
{
	List<KlarnaWebhookModel> findWebhookByBaseSite(final BaseSiteModel baseSite);

}