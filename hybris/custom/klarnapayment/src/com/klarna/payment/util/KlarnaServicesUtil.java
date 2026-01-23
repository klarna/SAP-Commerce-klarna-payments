package com.klarna.payment.util;

import de.hybris.platform.util.Config;

import java.util.Arrays;

import com.klarna.integration.dto.KlarnaClientSystemDTO;
import com.klarna.integration.dto.KlarnaIntegrationMetaDataDTO;

public final class KlarnaServicesUtil
{
	public KlarnaIntegrationMetaDataDTO getKlarnaMetaData()
	{
		final KlarnaIntegrationMetaDataDTO metaData = new KlarnaIntegrationMetaDataDTO();

		final KlarnaClientSystemDTO integrator = new KlarnaClientSystemDTO();
		integrator.setName(Config.getParameter("klarna.integrator.name"));
		integrator.setModuleName(Config.getParameter("klarna.integrator.module.name"));
		integrator.setModuleVersion(Config.getParameter("klarna.integrator.module.version"));
		metaData.setIntegrator(integrator);

		final KlarnaClientSystemDTO originator = new KlarnaClientSystemDTO();
		originator.setName(Config.getParameter("klarna.originator.name"));
		originator.setModuleName(Config.getParameter("klarna.originator.module.name"));
		originator.setModuleVersion(Config.getParameter("klarna.originator.module.version"));
		metaData.setOriginators(Arrays.asList(originator));

		return metaData;
	}
}
