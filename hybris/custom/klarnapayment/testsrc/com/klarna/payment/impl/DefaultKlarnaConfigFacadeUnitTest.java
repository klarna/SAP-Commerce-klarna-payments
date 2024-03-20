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

import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.facades.impl.DefaultKPConfigFacade;
import com.klarna.payment.model.KlarnaPayConfigModel;



@UnitTest
public class DefaultKlarnaConfigFacadeUnitTest
{
	private DefaultKPConfigFacade defaultKPConfigFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private Converter klarnaPaymentConfigConverter;

	@Mock
	private KlarnaPayConfigModel KlarnaPayConfigModel;

	@Mock
	BaseStoreModel baseStoreModel;

	KlarnaConfigData config = new KlarnaConfigData();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		config.setActive(true);
		config.setCode("KLARNA_UK");
		config.setMerchantEmail("rrr@ttt.com");
		config.setMerchantID("KK11");
		config.setSharedSecret("Test");
	}


	@Test
	public void testGetKlarnaConfigNull()
	{
		defaultKPConfigFacade = Mockito.spy(new DefaultKPConfigFacade());
		Mockito.doReturn(baseStoreService).when(defaultKPConfigFacade).getBaseStoreService();
		Mockito.doReturn(klarnaPaymentConfigConverter).when(defaultKPConfigFacade).getKlarnaPaymentConfigConverter();
		Mockito.doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		Mockito.when(baseStoreModel.getKlarnaPayConfig()).thenReturn(null);


		Assert.assertNull(defaultKPConfigFacade.getKlarnaConfig());
	}


	@Test
	public void testGetKlarnaConfig()
	{
		defaultKPConfigFacade = Mockito.spy(new DefaultKPConfigFacade());
		Mockito.doReturn(baseStoreService).when(defaultKPConfigFacade).getBaseStoreService();
		Mockito.doReturn(klarnaPaymentConfigConverter).when(defaultKPConfigFacade).getKlarnaPaymentConfigConverter();
		Mockito.doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		Mockito.when(baseStoreModel.getKlarnaPayConfig()).thenReturn(KlarnaPayConfigModel);

		Mockito.doReturn(config).when(klarnaPaymentConfigConverter).convert(Mockito.any(KlarnaPayConfigModel.class));
		defaultKPConfigFacade.getKlarnaConfig();
		Assert.assertNotNull(config);
	}


}
