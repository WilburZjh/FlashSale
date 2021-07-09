# FlashSale
This project is developed based on `Spring Boot` to stimulate the online flash sale services. This can be used in any online shopping website such as Shopify, Amazon, Loblaws, etc.

In order to handle the high volume concurrency problem. SnowFlake algorithm is utilized to generated continuous ID.

`MyBatis` is used to generated related ORM.

`Redis` and `MessageQueue` has been utilized.

`Redis Lua` is utilized to avoid the oversell problem and protect the DB(MySQL in this case). `RocketMQ` is utilized to protect the server(Assume 1000 request per second, my server can only take 200 requests per second).

Get data from the MessageQueue based on the number of requests that each machine can handle. Even if there are high volume of requests per second, it just puts the requests in the MQ, and the message of the message queue is controlled by the system itself, so that the entire system will not be collapsed.

In order to avoid using select function to retrieve data from millions of data stored in the DB, the `delay message` from `RocketMQ` is utilized to shutdown the overtime order. 

Implement the limited purchase function by `Redis` instead of DB querying.

`JMeter` is used to test the high volume of requests.

# Optimization
1. `Content Delivery Network(CDN)`: reduce the rendering of web page.
2. Utilize `Thymeleaf` to transfer the dynamic webpage into static webpage to improve the user experience when sending request. (developing)
