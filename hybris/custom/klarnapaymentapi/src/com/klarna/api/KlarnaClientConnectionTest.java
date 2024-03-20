/**
 * @author Siddhesh Rangankar
 */
package com.klarna.api;

import static java.lang.System.getProperty;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.klarna.api.model.ApiException;
import com.klarna.api.payments.PaymentsSessionsApi;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsOrderLine;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.model.KlarnaPayConfigModel;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * @author Siddhesh Rangankar
 *
 */
public class KlarnaClientConnectionTest implements InitializingBean
{
	private static final Logger LOG = Logger.getLogger(KlarnaClientConnectionTest.class);
	private FlexibleSearchService flexibleSearchService;

	/**
	 * @param flexibleSearchService the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}



	@Override
	public void afterPropertiesSet() throws Exception
	{

		LOG.info("KLARNA Starting Integration Connection Test");

		//String username = "PN00036_45e31ed37cf1";
		//String password = "kJRuG6KfDubVPSAD";


		final String configQuery = "select {pk} from {KlarnaPayConfig}";
		final Tenant currentTenant = Registry.getCurrentTenantNoFallback();
		try
		{
			final PaymentsSession klarnaCreditSessionData = new PaymentsSession();
			final Client client = getClient();
			if (client == null) {
				LOG.error("Error while creating Client" );
				return;
			}
			final PaymentsSessionsApi paymentsSessionsApi = client.newPaymentsSessionsApi();
			final PaymentsOrderLine orderLine1 = new PaymentsOrderLine();
			orderLine1.setType("physical");
			orderLine1.setReference("123050");
			orderLine1.setName("Tomatoes");
			orderLine1.setQuantity(10L);
			orderLine1.setQuantityUnit("kg");
			orderLine1.setUnitPrice(600L);
			orderLine1.setTaxRate(2500L);
			orderLine1.setTotalAmount(6000L);
			orderLine1.setTotalTaxAmount(1200L);

			  final PaymentsOrderLine orderLine2 = new PaymentsOrderLine();
			  orderLine2.setType("physical");
			  orderLine2.setReference("543670");
			  orderLine2.setName("Bananas");
			  orderLine2.setQuantity(1L);
			  orderLine2.setQuantityUnit("bag");
			  orderLine2.setUnitPrice(5000L);
			  orderLine2.setTaxRate(2500L);
			  orderLine2.setTotalAmount(4000L);
			  orderLine2.setTotalDiscountAmount(1000L);
			  orderLine2.setTotalTaxAmount(800L);


			final List<PaymentsOrderLine> lines = new ArrayList<PaymentsOrderLine>();
			lines.add(orderLine1);
			lines.add(orderLine2);

			final PaymentsSession sessionRequest = new PaymentsSession();
			sessionRequest.setPurchaseCountry("us");
			sessionRequest.setPurchaseCurrency("usd");
			sessionRequest.setLocale("en-us");
			sessionRequest.setOrderAmount(10000L);
			sessionRequest.setOrderTaxAmount(2000L);
			sessionRequest.setOrderLines(lines);

			final PaymentsMerchantSession session = paymentsSessionsApi.create(sessionRequest);
			System.out.println("Klarna payment session :" + session);

			LOG.info("Klarna Integration Connection Test Successful for Tenant " + currentTenant + " client tocken "
					+ session.getClientToken());
		}
		catch (final IOException e)
		{
			System.out.println("Connection problem: " + e.getMessage());
		}
		catch (final ApiException e)
		{
			//System.out.println("API issue: " + e.getMessage());
			if (e.getHttpStatus() == 401)
			{
				LOG.error("Authorization Issue - " + e.getHttpStatus() + " Please check Username or Password ");
			}
			else {
				LOG.error("Authorization Issue - " + e.getMessage());

			}

		}
		catch (final Exception e)
		{
			LOG.error("Error while conencting to klarna" + e.getMessage());
		}



	}
	private Client getClient() {
		final String fileName = "KlarnaProps.txt";
		final ClassLoader classLoader = getClass().getClassLoader();

		final File klarnaProps = new File(classLoader.getResource(fileName).getFile());
		Scanner sc = null;

		String url=null, username=null, password=null;
		try
		{
			sc = new Scanner(klarnaProps);
			while (sc.hasNextLine())
			{
				final String line1 = sc.nextLine();
				final String lineChars[]=line1.split(":", 2);
				url=lineChars[1];
				final String line2 = sc.nextLine();
				final String line2Chars[]=line2.split(":", 2);
				username=line2Chars[1];
				final String line3 = sc.nextLine();
				final String line3Chars[]=line3.split(":", 2);
				password=line3Chars[1];
				break;
			}
			return new Client(username, password, URI.create(url));
		}
		catch (final IOException exp)
		{
			LOG.error("IO Error "+exp.getMessage());
		}
		finally
		{
			if (sc != null)
			{
				sc.close();
			}
		}
		return null;
	}

}
