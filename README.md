# bid-Offer Book for Stock exchanges

Hanchao Lei

hl2714

Date: 11/05/2020

All the .java files are contained in Bidoffer package.

The main idea of my program is to divide client from access market, exchange , or so on, so less errors may be produced. As a client, he/she can send or cancel order using his/her own object, Listener. And within the Listener, he/she can access the callback list to view all the execution happened in the past and the exchangeProvideId. In each methods in Listener Class, I add Exception throwing to prevent users from causing errors to the exchange. The PriorityQueue is used when constructing the bid and offer book. HashMap is mainly used to track the ClientUniqueId, exchangeProvideId, marketId paired with their corresponding objects.


To run my code, simply run Test_Bidoffer.java, which will run test cases separately testing if the the main functions, like sending orders, sweeping book, returning callback, and canceling orders, give desired results. All tests should pass successfully.