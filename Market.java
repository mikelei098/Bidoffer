package Bidoffer;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/** the is the class for the market. It will implement the sweeping function when
 *  new order comes in until the book is balanced.
 *  
 *  users should not have ability to access to the Market object, so the methods are
 *  all protected
 *  
 * @author Hanchao Lei
 *
 */

public class Market {
	/** the Id of the market, e.g. "IBM" **/
	protected String marketId;
	/** the bid book **/
	private Queue<Order> bid;
	/** the offer book **/
	private Queue<Order> offer;
	/** the exchange the market will be in **/
	protected Exchange exchange;
	
	
	
	/** constructor for Market
	 * 
	 * @param name the marketId for the the Market
	 */
	public Market(String name) {
		marketId = name;
		bid = new PriorityQueue<Order>(Collections.reverseOrder());
		offer = new PriorityQueue<Order>();
		exchange = null;
	}
	
	/** method to add an order into the book and run the sweep
	 * 
	 * @param o the order that will be added into the book
	 */
	protected void add(Order o, String exchangeProvidedId) {
		
		
		if (o.buy) {
			bid.add(o);
		}
		
		else {
			offer.add(o);
		}
		
		sweep(o.buy);
	}
	
	/** the method to sweep the book as well as sending callback to the
	 * listener each time when the order execution happens
	 * 
	 * @param buy whether the order just added is buy
	 **/
	protected void sweep(boolean buy) {
		// If one of the books is empty, return a callback saying
		//nothing is executed
		if(bid.size()==0) {
			Listener sellL = exchange.id.get(offer.peek().clientUniqueId);
			String sellexId = exchange.orderList.get(offer.peek());
			sellL.callbackList.add(new Callback(sellexId, 0, offer.peek().price, false));
			return;
		}
		
		if(offer.size() == 0) {
			Listener bidL = exchange.id.get(bid.peek().clientUniqueId);
			String bidexId = exchange.orderList.get(bid.peek());
			bidL.callbackList.add(new Callback(bidexId, 0, bid.peek().price, true));
			return;
		}
			
	while(true) {
		// If one of the books is empty, do nothing
		if (bid.size() == 0 || offer.size() ==0)
			break;
	
		//find Listeners of bid and sell side by the clientUniqueId
		Listener bidL = exchange.id.get(bid.peek().clientUniqueId);
		Listener sellL = exchange.id.get(offer.peek().clientUniqueId);
		//get exchangeProvided order for bidder and seller
		String bidexId = exchange.orderList.get(bid.peek());
		String sellexId = exchange.orderList.get(offer.peek());
		
		//If there is a gap between bid and offer, sweep nothing
		if ((bid.size() == 0 || offer.size() ==0) ||
				(bid.peek().price < offer.peek().price)) {
			if (buy) //if a bid is just added, send callback to the bidder
				bidL.callbackList.add(new Callback(bidexId, 0, bid.peek().price, true));
			else //if a offer is just added, send callback to the offer
				sellL.callbackList.add(new Callback(sellexId, 0, offer.peek().price, false));
			return;
		}
		
		//If the order is cancelled, pop the order
		if (bid.peek().cancel) {
			bid.poll();
			continue;
		}
		
		if (offer.peek().cancel) {
			offer.poll();
			continue;
		}
		
		double price = 0.0;
		if (buy)  //If a bid just added into the book, then the sweeping price will depends on offers
			price = offer.peek().price;
		else
			price = bid.peek().price;
		
		int diff = bid.peek().quantity - offer.peek().quantity;
		
		//If buy quantity equals to sell quantity, 
		//send Callback with offer quantity
		//pop them out of the queue together
		if (diff == 0) {
			//send callback to client
			bidL.callbackList.add(new Callback(bidexId, bid.peek().quantity, price, true));
			sellL.callbackList.add(new Callback(sellexId, bid.peek().quantity, price, false));
			bid.poll();
			offer.poll();

		}
		
		//If buy quantity is larger than sell quantity, only pop the sell order out of queue
		if (diff > 0) {
			//update Callback
			bidL.callbackList.add(new Callback(bidexId, offer.peek().quantity, price, true));
			sellL.callbackList.add(new Callback(sellexId, offer.peek().quantity, price, false));
			bid.peek().quantity = diff;
			offer.peek().quantity = 0;
			offer.poll();
		}
		
		//If sell quantity is larger than buy quantity, 
		//update Callback with bid quantity
		//only pop the buy order out of queue
		if (diff < 0) {
			//update Callback
			bidL.callbackList.add(new Callback(bidexId, bid.peek().quantity, price, true));
			sellL.callbackList.add(new Callback(sellexId, bid.peek().quantity, price, false));
			bid.peek().quantity = 0;
			offer.peek().quantity = -diff;
			bid.poll();
		}
	}
		
	}
	
	

}
