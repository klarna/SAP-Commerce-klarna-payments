package com.klarna.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.enums.KlarnaEnv;
import com.klarna.payment.facades.impl.DefaultKPConfigFacade;



@UnitTest
public class DefaultKlarnaConfigFacadeUnitTest
{
	private DefaultKPConfigFacade defaultKPConfigFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private Converter klarnaConfigConverter;

	@Mock
	private KlarnaConfigModel KlarnaPayConfigModel;

	@Mock
	BaseStoreModel baseStoreModel;

	KlarnaConfigData config = new KlarnaConfigData();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		config.setActive(true);
		config.setCode("KLARNA_UK");
		config.getCredential().setApiUserName(("ABCD"));
		config.getCredential().setApiPassword(("ABCD"));
		config.setEnvironment(KlarnaEnv.PRODUCTION.getCode());
		config.getCredential().setMarketRegion(KlarnapaymentConstants.KLARNA_MARKET_REGION_EUROPE);
		config.getKpConfig().setMerchantReference2("guid");
	}


	@Test
	public void testGetKlarnaConfigNull()
	{
		defaultKPConfigFacade = Mockito.spy(new DefaultKPConfigFacade());
		Mockito.doReturn(baseStoreService).when(defaultKPConfigFacade).getBaseStoreService();
		Mockito.doReturn(klarnaConfigConverter).when(defaultKPConfigFacade).getKlarnaConfigConverter();
		Mockito.doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		Mockito.when(baseStoreModel.getConfig()).thenReturn(null);


		Assert.assertNull(defaultKPConfigFacade.getKlarnaConfig());
	}


	@Test
	public void testGetKlarnaConfig()
	{
		defaultKPConfigFacade = Mockito.spy(new DefaultKPConfigFacade());
		Mockito.doReturn(baseStoreService).when(defaultKPConfigFacade).getBaseStoreService();
		Mockito.doReturn(klarnaConfigConverter).when(defaultKPConfigFacade).getKlarnaConfigConverter();
		Mockito.doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		Mockito.when(baseStoreModel.getConfig()).thenReturn(KlarnaPayConfigModel);

		Mockito.doReturn(config).when(klarnaConfigConverter).convert(Mockito.any(KlarnaConfigModel.class));
		defaultKPConfigFacade.getKlarnaConfig();
		Assert.assertNotNull(config);
	}


}
