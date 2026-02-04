package com.klarna.payment.cronjob;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.payment.facades.KlarnaWebhookFacade;
import com.klarna.payment.model.KlarnaWebhookCronJobModel;


/**
 * Cron Job to create/edit Klarna Webhook for a Base Site
 */
public class KlarnaWebhookJob extends AbstractJobPerformable<KlarnaWebhookCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(KlarnaWebhookJob.class);

	private static final String CREATE_WEBHOOK_ACTION = "CREATE";
	private static final String UPDATE_WEBHOOK_ACTION = "UPDATE";
	private static final String DELETE_WEBHOOK_ACTION = "DELETE";
	private static final String SIMULATE_WEBHOOK_ACTION = "SIMULATE";

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private KlarnaWebhookFacade klarnaWebhookFacade;

	@Override
	public PerformResult perform(final KlarnaWebhookCronJobModel cronJob)
	{
		try
		{
			final BaseSiteModel baseSite = cronJob.getBaseSite();
			if (baseSite == null)
			{
				LOG.error("CronJob failure! BaseSite is not available for the CronJob: " + cronJob.getCode());
				return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
			}

			final String action = cronJob.getAction();
			if (StringUtils.isEmpty(action))
			{
				LOG.error("\"CronJob failure! Webhook action is empty for the Cronjob: " + cronJob.getCode());
				return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
			}
			boolean isSuccess = false;
			if (StringUtils.equalsIgnoreCase(CREATE_WEBHOOK_ACTION, action))
			{
				isSuccess = klarnaWebhookFacade.createWebhook(baseSite);
			}
			if (StringUtils.equalsIgnoreCase(DELETE_WEBHOOK_ACTION, action))
			{
				isSuccess = klarnaWebhookFacade.deleteWebhook(baseSite);
			}
			if (isSuccess)
			{
				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}

			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);

		}
		catch (final Exception e)
		{
			LOG.error("CronJob failure! Exception occurred during Klarna Webhhok Cronjob", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}
}
