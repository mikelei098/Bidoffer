package Bidoffer;

/** this is a order class which will be sent by Listener (the clients) to the exchange **/


public class Order implements Comparable<Order>{

	/** client¡¯s own, private id of the order **/
	protected String clientUniqueId;  
	/** the market to which the order should be sent, e.g. ¡°IBM¡± **/
	protected String marketId;
	/** the quantity of the order **/
	protected int quantity;
	/** whether it is a buy side order **/
	protected boolean buy;
	/** This is the limit price of the order **/
	protected Double price;
	/** whether the order is cancelled **/
	protected boolean cancel;
	
	/** Constructor of the Order
	 * 
	 * @param clientUniqueId client¡¯s own, private id of the order
	 * @param marketId the market to which the order should be sent
	 * @param quantity the quantity of the order
	 * @param buy whether this is a buy order
	 * @param price the limit price of the order
	 */
	public Order(String clientUniqueId, String marketId, int quantity, boolean buy, double price) {
		this.clientUniqueId = clientUniqueId;
		this.marketId = marketId;
		this.quantity = quantity;
		this.buy = buy;
		this.price = price;
		this.cancel = false;
	}
	
	
	/** the implementment of compareTo to make the order comparable based on
	 * its price
	 * 
	 * @param o the other order which will be compared with
	 * @return the standard compareTo result
	 */
	@Override
	public int compareTo(Order o) {
		return this.price.compareTo(o.price);
	}
}
