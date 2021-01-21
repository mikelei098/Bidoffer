package Bidoffer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import junit.framework.Assert;

class Test_BidOffer {

	@Test
	/** one simple test **/
	void Test_Simple() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		
		
		//jack sell 200 ibm at 100
		jack.sell("IBM", 200, 100.0);
		//jack should receive a callback saying the order is not executed e.g. quantity = 0;
		assertEquals("S0@100.0 ", jack.callbackString());

		//then peter bid 100, so both of them should receive a callback saying 100 quantity executed
		peter.bid("IBM", 100, 100.0);
		assertEquals("B100@100.0 ", peter.callbackString());
		assertEquals("S0@100.0 S100@100.0 ", jack.callbackString());
	}
	
	@Test
	/** the the priorityQueue inside the market with multiple orders **/
	void Test_queue1() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		jack.sell("IBM", 100, 200);
		jack.sell("IBM", 100, 300);
		jack.sell("IBM", 100, 400);
		
		peter.bid("IBM", 100, 50);
		
		//nothing should happen e.g. quantity = 0 for all
		assertEquals("B0@50.0 ", peter.callbackString());
		assertEquals("S0@200.0 S0@200.0 S0@200.0 ", jack.callbackString());
		
		peter.bid("IBM", 300, 500);
		
		//since bid price larger than all offer prices, so order should be 
		//executed in ascending order of offer prices so should be:
		//S100@200, S100@300, S100@400
		assertEquals("S0@200.0 S0@200.0 S0@200.0 S100@200.0 S100@300.0 S100@400.0 ",
						jack.callbackString());
		
		assertEquals("B0@50.0 B100@200.0 B100@300.0 B100@400.0 ", peter.callbackString());
	}
	
	@Test
	/** test the priorityQueue inside the market with multiple orders (reverse the previous test) **/
	void Test_queue2() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		jack.bid("IBM", 100, 200);
		jack.bid("IBM", 100, 300);
		jack.bid("IBM", 100, 400);
		
		peter.sell("IBM", 100, 500);
		
		//nothing should happen e.g. quantity = 0 for all
		assertEquals("S0@500.0 ", peter.callbackString());
		assertEquals("B0@200.0 B0@300.0 B0@400.0 ", jack.callbackString());
		
		peter.sell("IBM", 300, 200);
		
		//since sell price smaller than all bid prices, so order should be 
		//executed in descending order of bid prices. so should be:
		//S100@200, S100@300, S100@400
		assertEquals("B0@200.0 B0@300.0 B0@400.0 B100@400.0 B100@300.0 B100@200.0 ",
						jack.callbackString());
		
		assertEquals("S0@500.0 S100@400.0 S100@300.0 S100@200.0 ", peter.callbackString());
	}
	
	
	@Test
	/** test the case when bid price lower than offer price. Nothing should happen **/
	void Test_unequal_price1() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		//jack sell 200 ibm at 200
		jack.sell("IBM", 200, 200.0);
		//then peter bid 100 at 100,
		peter.bid("IBM", 100, 100.0);
		
		//they should receive a callback saying the order is not executed e.g. quantity = 0;
		assertEquals("S0@200.0 ", jack.callbackString());
		assertEquals("B0@100.0 ", peter.callbackString());
	}
	
	@Test
	/** test the case when new bid price is higher than existed offer price 
	 * the order should be sweep at the existed offer price
	 */
	void Test_unequal_price2() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		//jack sell 200 ibm at 100
		jack.sell("IBM", 200, 100.0);
		//then peter bid 100 at 200,
		peter.bid("IBM", 100, 200.0);
		
		//the order should executed @100.0
		assertEquals("S0@100.0 S100@100.0 ", jack.callbackString());
		assertEquals("B100@100.0 ", peter.callbackString());
		
		//if peter bid another 100 at 200, same thing should happen
		peter.bid("IBM", 100, 200.0);
		assertEquals("S0@100.0 S100@100.0 S100@100.0 ", jack.callbackString());
		assertEquals("B100@100.0 B100@100.0 ", peter.callbackString());
		
	}
	
	@Test
	/** test the case when new offer price is lower than existed bid price
	 * the order should be sweep at the existed bid price
	 */
	void Test_unequal_price3() throws Exception {
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		//then peter bid 200 at 200,
		peter.bid("IBM", 200, 200.0);
		//jack sell 100 ibm at 100
		jack.sell("IBM", 100, 100.0);

		
		//the order should executed @200.0
		assertEquals("S100@200.0 ", jack.callbackString());
		assertEquals("B0@200.0 B100@200.0 ", peter.callbackString());
		
		//if jack sell another 100 at 200, same thing should happen
		jack.sell("IBM", 100, 200.0);
		assertEquals("S100@200.0 S100@200.0 ", jack.callbackString());
		assertEquals("B0@200.0 B100@200.0 B100@200.0 ", peter.callbackString());
	}
	
	@Test
	/** test the cancel function of Listener **/
	void Test_cancel() throws Exception{
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		Listener peter = new Listener("peter", nyse);
		nyse.marketJoin(ibm);
		
		//peter bid 200 at 100,
		peter.bid("IBM", 200, 100.0);
		//jack sell 100 ibm at 100
		jack.sell("IBM", 100, 100.0);
		
		//then peter cancel the order, so 100 shares should be cancelled
		String exId = peter.getExId(1);
		peter.cancel(exId);
		assertEquals("B0@100.0 B100@100.0 100shares are cancelled ", peter.callbackString());
		//jack sell another 100 at 100, nothing would happened
		jack.sell("IBM", 100, 100.0);
		assertEquals("B0@100.0 B100@100.0 100shares are cancelled ", peter.callbackString());
		assertEquals("S100@100.0 ", jack.callbackString()); //only 100 shares before peter'cancellation was sold

	}
	
	@Test
	/** Test error throw when client enter a non-positive quantity **/
	void Test_quantity() throws Exception{
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		nyse.marketJoin(ibm);
		
		boolean error = false;
		try {
		jack.sell("IBM", 0, 100.0);
		} catch (Exception e) {
			assertEquals("Order quantity has to be positive", e.getMessage());
			error = true;
		}
		
		assertTrue(error);
		//so jack will not get a callback since the order is invalid
		assertTrue(jack.callbackList.isEmpty());
	}
	
	@Test 
	/**Test error throw when client enter a non-exist market **/
	void Test_market() throws Exception{
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		nyse.marketJoin(ibm);
		
		boolean error = false;
		try {
		jack.sell("APPL", 100, 100.0);
		} catch (Exception e) {
			assertEquals("APPL market does not exist in Exchange", e.getMessage());
			error = true;
		}
		
		assertTrue(error);
		//so jack will not get a callback since the order is invalid
		assertTrue(jack.callbackList.isEmpty());
	}
	
	@Test
	/**Test error throw when client enter a non-positive price **/
	void Test_price() throws Exception{
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		nyse.marketJoin(ibm);
		
		boolean error = false;
		try {
		jack.sell("IBM", 100, -100.0);
		} catch (Exception e) {
			assertEquals("Order price has to be positive", e.getMessage());
			error = true;
		}
		
		assertTrue(error);
		//so jack will not get a callback since the order is invalid
		assertTrue(jack.callbackList.isEmpty());
	}
	
	@Test
	/** Test error throw two same client try to enter the exchange **/
	void Test_sameClient() {

		Exchange nyse = new Exchange();
		
		boolean error = false;
		try {
			Listener jack = new Listener("jack", nyse);
			Listener jack1 = new Listener("jack", nyse);
		} catch (Exception e) {
			assertEquals("client is already in exchange", e.getMessage());
			error = true;
		}
		
		assertTrue(error);

	}

	@Test
	/** Test error throw if listener uses a wrong exchangeProvideId **/
	void Test_wrongExId() throws Exception{
		Market ibm = new Market("IBM");
		Exchange nyse = new Exchange();
		Listener jack = new Listener("jack", nyse);
		nyse.marketJoin(ibm);
		
		boolean error = false;
		try {
			jack.cancel("SSS");
		} catch (Exception e) {
			assertEquals("Exchange never provided such Id before", e.getMessage());
			error = true;
		}
		
		assertTrue(error);
	}
}
