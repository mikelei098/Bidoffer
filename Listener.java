package Bidoffer;

import java.util.ArrayList;
import java.util.List;

/** this is the class for the client. A listener object can send bid or sell order  
 * to certain market and cancel order using exchangeProvidedId. It has a CallbackList
 * which will keep all the callback it received from the exchange
 * 
 * @author Hanchao Lei
 */


public class Listener {
	
	/** the client¡¯s own, private id of the order **/
	protected String clientUniqueId;
	/** the exchange the client will join to buy and sell **/
	private Exchange exchange;
	/** the callBack that will received from exchange **/
	protected List<Callback> callbackList;
	
	
	/** the constructor for Listener. It initialized the field for Listener and 
	 * make it join the desired exchange
	 * 
	 * @param clientUniqueId the client¡¯s own, private id of the order
	 * @param exchange the exchange the client will join to buy and sell
	 * @param callback the callBack that will received from exchange
	 * @throws when the listener is already in the Exchange
	 */
	public Listener (String clientUniqueId, Exchange exchange) throws Exception{
		this.clientUniqueId = clientUniqueId;
		this.exchange = exchange;
		this.callbackList = new ArrayList<Callback>() ;
		exchange.listenerJoin(this);
	}
	
	
	/** the method for Listener to send bid order to the exchange
	 * 
	 * @param marketId the market Id of the order
	 * @param quantity the quantity of the order
	 * @param price the price of the order
	 * 
	 */
	public void bid(String marketId, int quantity, double price) throws Exception{
		Order order = new Order(clientUniqueId, marketId, quantity, true, price);
		exchange.addOrder(order);
	}
	
	/**the method for Listener to send sell order to the exchange
	 * 
	 * @param marketId the market Id of the order
	 * @param quantity the quantity of the order
	 * @param price the price of the order
	 * 
	 */
	public void sell(String marketId, int quantity, double price) throws Exception{
		Order order = new Order(clientUniqueId, marketId, quantity, false, price);
		exchange.addOrder(order);
	}
	
	/** the method for Listener to cancel the order by the exchangeProvideId
	 * 
	 * @param exId the id provided by exchange
	 */
	public void cancel(String exId) throws Exception{
		exchange.cancel(exId);
	}
	
	/** a helper method to get the exchangeProvideId from the list of
	 * Callbacks that Listener has already received
	 * 
	 * @param i the index of the Callback in the list
	 * @return the exchangeProvideId
	 */
	public String getExId(int i) {
		return callbackList.get(i).getExId();
	}
	
	/** return a String to contain the Callback received so far
	 * @return a String to contain the Callback received so far
	 */
	public String callbackString() {
		String s = "";
		for (int i = 0; i < callbackList.size(); i++)
			s = s + callbackList.get(i).toString() + " ";
		
		return s;
	}
	
	
	

}
