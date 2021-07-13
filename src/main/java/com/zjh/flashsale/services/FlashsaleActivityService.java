package com.zjh.flashsale.services;

import com.alibaba.fastjson.JSON;
import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.dao.FlashsaleCommodityDao;
import com.zjh.flashsale.db.dao.OrderDao;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.po.FlashsaleCommodity;
import com.zjh.flashsale.db.po.Order;
import com.zjh.flashsale.mq.RocketMQService;
import com.zjh.flashsale.util.RedisService;
import com.zjh.flashsale.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class FlashsaleActivityService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleCommodityDao flashsaleCommodityDao;

    @Autowired
    private RocketMQService rocketMQService;

    @Autowired
    private OrderDao orderDao;

    /**
     * datacenterId;  数据中心
     * machineId;     机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */
    private SnowFlake snowFlake = new SnowFlake(1, 1);

    /**
     * 创建订单
     *
     * @param flashsaleActivityId
     * @param userId
     * @return
     * @throws Exception
     */
    public Order createOrder(long flashsaleActivityId, long userId) throws Exception {
        /*
         * 1.创建订单
         */
        FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
        Order order = new Order();
        //采用雪花算法生成订单ID
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setFlashsaleActivityId(flashsaleActivity.getId());
        order.setUserId(userId);
        order.setOrderAmount(flashsaleActivity.getFlashsalePrice().longValue());
        /*
         *2.发送创建订单消息
         */
        rocketMQService.sendMessage("flashsale_order", JSON.toJSONString(order));

        /*
         * 3.发送订单付款状态校验消息
         * 开源RocketMQ支持延迟消息，但是不支持秒级精度。默认支持18个level的延迟消息，这是通过broker端的messageDelayLevel配置项确定的，如下：
         * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         */
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 3);

        return order;
    }

    /**
     * 判断商品是否还有库存
     *
     * @param activityId 商品ID
     * @return
     */
    public boolean flashsaleStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return redisService.stockDeductValidator(key);
    }

    /**
     * 将秒杀详情相关信息倒入redis
     *
     * @param flashsaleActivityId
     */
    public void pushFlashsaleInfoToRedis(long flashsaleActivityId) {
        FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
        redisService.setValue("flashsaleActivity:" + flashsaleActivityId, JSON.toJSONString(flashsaleActivity));

        FlashsaleCommodity flashsaleCommodity = flashsaleCommodityDao.queryFlashsaleCommodityById(flashsaleActivity.getCommodityId());
        redisService.setValue("flashsaleCommodity:" + flashsaleActivity.getCommodityId(), JSON.toJSONString(flashsaleCommodity));
    }

    /**
     * 订单支付完成处理
     * @param orderNo
     */
    public void payOrderProcess(String orderNo) throws Exception{
        log.info("完成支付订单 订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        /*
         * 1.判断订单是否存在
         * 2.判断订单状态是否为未支付状态
         */
        if (order == null) {
            log.error("订单号对应订单不存在：" + orderNo);
            return;
        } else if(order.getOrderStatus() != 1 ) {
            log.error("订单状态无效：" + orderNo);
            return;
        }
        /*
         * 2.订单支付完成
         */
        order.setPayTime(new Date());
        //订单状态 0:没有可用库存，无效订单 1:已创建等待付款 ,2:支付完成
        order.setOrderStatus(2);
        orderDao.updateOrder(order);
        /*
         *3.发送订单付款成功消息
         */
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
//        boolean deductStockResult = flashsaleActivityDao.deductStock(order.getFlashsaleActivityId());
//        if (deductStockResult) {
//            order.setPayTime(new Date());
//            // 订单状态 0、没有可用库存，无效订单  1、已创建等待支付  2、完成支付
//            order.setOrderStatus(2);
//            orderDao.updateOrder(order);
//        }
    }
}