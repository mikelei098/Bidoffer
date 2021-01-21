package Bidoffer;

/** this is the Callback class which is used to send information each time
 * an execution happends and the exchangeProvideId back to Listener (the Client)
 * 
 * Users should not have ability to access this object, so methods are protected 
 * 
 * @author Hanchao Lei
 *
 */

public class Callback {
	/** the Id provided by exchange to the listener **/
	private String exchangeProvidedId;
	/** the quantity filled or cancelled **/
	protected int quantity;
	/** the price where the order is filled **/
	protected double price;
	/** whether the order is cancelled **/
	protected boolean cancel;
	/** whether the order is buy side **/
	protected boolean buy;
	
	public Callback (String exchangeProvidedId, int quantity, double price, boolean buy) {
		this.exchangeProvidedId = exchangeProvidedId;
		this.quantity = quantity;
		this.price = price;
		cancel = false;
		this.buy = buy;
	}
	
	/** return the exchangeProvidedId
	 * 
	 * @return the exchangeProvidedId
	 */
	protected String getExId() {
		return exchangeProvidedId;
	}

	/** override toString() method for Callback
	 * 
	 * @return a string representation of Callback
	 */
	@Override
	public String toString() {
		if (cancel)
			return String.valueOf(quantity) + "shares are cancelled";  
		else if (buy)
			return "B" + String.valueOf(quantity) + "@" + String.valueOf(price);
		else
			return "S" + String.valueOf(quantity) + "@" + String.valueOf(price);
	}
}
