package com.klarna.payment.daos;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import java.util.List;

import com.klarna.model.KlarnaWebhookModel;
import com.klarna.model.KlarnaWebhookNotificationModel;


public interface KlarnaWebhookDAO
{
	List<KlarnaWebhookModel> findWebhookByBaseSite(final BaseSiteModel baseSite);

	List<KlarnaWebhookNotificationModel> findWebhookNotificationById(final String id);

}