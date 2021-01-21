package Bidoffer;

import java.util.HashMap;

import java.util.Map;

/** This class simulate a function of the exchange. It can have multiple
 * markets and multiple listeners (the clients) in it. clients can send
 * order to different markets and exchange will provide clients a unique
 * exchangeProvideId for each order to the clients. Clients can use this 
 * Id to cancel the order
 * 
 * Users should not have direct access to this object, so all the methods 
 * are protected.
 * 
 * @author Hanchao Lei
 *
 */
public class Exchange {
	
	/** the set of market **/
	private Map<String, Market> market;
	/** the map to identify listener by its clientUniqueId **/
	protected Map<String, Listener> id;
	/** the map to identify exchangeProvideId by corresponding Order **/
	protected Map<Order, String> orderList;
	/** the map to identify Order by its corresponding exchangeProvideId **/
	protected Map<String, Order> exIdList;
	
	

	/** the constructor for exchange **/
	public Exchange () {
		this.market = new HashMap<String, Market>();;
		this.id = new HashMap<String, Listener>();
		orderList =  new HashMap<Order,String>();
		exIdList =  new HashMap<String, Order>();
	}
	
	/** the method to add the listener into the exchange
	 * 
	 * @param l the listener that will be added
	 * @throws Exception when the listener is already in the exchange
	 */
	protected void listenerJoin(Listener l) throws Exception{
		if (id.containsKey(l.clientUniqueId))
			throw new Exception ("client is already in exchange");
		
		this.id.put(l.clientUniqueId, l);
	}
	
	/** the method to add the listener into the exchange
	 * 
	 * @param l the listener that will be added
	 * @throws Exception when the listener is already in the exchange
	 */
	protected void marketJoin(Market m) throws Exception{
		if (market.containsKey(m.marketId))
			throw new Exception ("market is already in exchange");
		
		this.market.put(m.marketId, m);
		m.exchange = this;
	}
	
	/** add the order into the market
	 * 
	 * @param o the order that will be added
	 * @return a Callback object for the order
	 */
	protected void addOrder (Order o) throws Exception{
		if (!market.containsKey(o.marketId))
			throw new Exception (o.marketId +" market does not exist in Exchange");
		if (o.quantity <= 0)
			throw new Exception ("Order quantity has to be positive");
		if (o.price <=0.0)
			throw new Exception ("Order price has to be positive");
		
		
		Market targetMarket = market.get(o.marketId);
		
		//generate exId for the order
		long code = idGenerator(o);
		int i = 123;
		String exId = String.valueOf(code);
		
		//add i to the exId until the exId become unique
		while (exIdList.containsKey(exId)) {
			code = code +i;
			exId = String.valueOf(code);
		}
		orderList.put(o, exId);
		exIdList.put(exId, o);
		targetMarket.add(o, exId);
	}
	
	protected void cancel(String exchangedProvideId) throws Exception{
		if(!exIdList.containsKey(exchangedProvideId))
			throw new Exception ("Exchange never provided such Id before");
		
		Order o = exIdList.get(exchangedProvideId);
		o.cancel = true;
		
		//return a callback to the listener
		Listener l = id.get(o.clientUniqueId);
		Callback cb = new Callback(exchangedProvideId, o.quantity, o.price, o.buy);
		cb.cancel = true;
		l.callbackList.add(cb);
	}
	
	/** the helper method to generate exchangeProvidedId
	 *
	 *	@param o the order which needs exchangeProvidedId
	 */
	private long idGenerator (Order o) {
		long code = o.quantity + (long) (o.price*100) + o.marketId.hashCode() +o.clientUniqueId.hashCode(); 
		if (o.buy)
			code = -code;
		return code;
	}
}
