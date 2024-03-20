/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Jan 25, 2023, 4:42:37 PM                    ---
 * ----------------------------------------------------------------
 */
package com.klarna.payment.constants;

/**
 * @deprecated since ages - use constants in Model classes instead
 */
@Deprecated(since = "ages", forRemoval = false)
@SuppressWarnings({"unused","cast"})
public class GeneratedKlarnapaymentConstants
{
	public static final String EXTENSIONNAME = "klarnapayment";
	public static class TC
	{
		public static final String KLARNAPAYCONFIG = "KlarnaPayConfig".intern();
		public static final String KPENDPOINTMODE = "KPEndpointMode".intern();
		public static final String KPENDPOINTTYPE = "KPEndpointType".intern();
		public static final String KPPAYMENTINFO = "KPPaymentInfo".intern();
		public static final String KPPAYMENTOPTIONS = "KPPaymentOptions".intern();
		public static final String ORDERFAILEDEMAILPROCESS = "OrderFailedEmailProcess".intern();
		public static final String PRODUCTIDENTIFIERS = "ProductIdentifiers".intern();
		public static final String RBGCOLOR = "RBGColor".intern();
	}
	public static class Attributes
	{
		public static class AbstractOrder
		{
			public static final String ISAUTOCAPTURED = "isAutoCaptured".intern();
			public static final String ISKPAUTHORISED = "isKpAuthorised".intern();
			public static final String ISKPFRAUDRISKSTOPPED = "isKpFraudRiskStopped".intern();
			public static final String ISKPPENDINGORDER = "isKpPendingOrder".intern();
			public static final String KPANONYMOUSGUID = "kpAnonymousGUID".intern();
			public static final String KPFRAUDSTATUS = "kpFraudStatus".intern();
			public static final String KPIDENTIFIER = "kpIdentifier".intern();
			public static final String KPORDERID = "kpOrderId".intern();
		}
		public static class BaseStore
		{
			public static final String KLARNAPAYCONFIG = "klarnaPayConfig".intern();
		}
	}
	public static class Enumerations
	{
		public static class KPEndpointMode
		{
			public static final String TEST = "TEST".intern();
			public static final String LIVE = "LIVE".intern();
		}
		public static class KPEndpointType
		{
			public static final String EUROPE = "EUROPE".intern();
			public static final String NORTH_AMERICA = "NORTH_AMERICA".intern();
			public static final String OCEANIA = "OCEANIA".intern();
		}
		public static class KPPaymentOptions
		{
			public static final String PAY_LATER = "pay_later".intern();
			public static final String CREDIT = "credit".intern();
		}
		public static class PaymentTransactionType
		{
			public static final String KLARNA_ORDER_PLACED = "KLARNA_ORDER_PLACED".intern();
		}
	}
	
	protected GeneratedKlarnapaymentConstants()
	{
		// private constructor
	}
	
	
}
