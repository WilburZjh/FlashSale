# FlashSale
Stimulate the online flash sale services. This could be used in any online shopping website such as Shopify, Amazon, Loblaws, etc.

In order to handle the high volume concurrency problem. SnowFlake algorithm is utilized to generated continuous ID.

MyBatis is used to generated related ORM.

Redis, RocketMQ has been utilized.
Get data from the MQ based on the number of requests that each machine can handle. Even if there are high volume of requests per second, it just puts the requests in the MQ, and the message of the message queue is controlled by the system itself, so that the entire system will not be collapsed.

JMeter is used to test the high volume of requests.

This project is developed based on Spring Boot.
