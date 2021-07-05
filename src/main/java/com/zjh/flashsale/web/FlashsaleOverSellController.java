package com.zjh.flashsale.web;

import com.zjh.flashsale.services.FlashsaleActivityService;
import com.zjh.flashsale.services.FlashsaleOverSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FlashsaleOverSellController {

    @Autowired
    private FlashsaleActivityService flashsaleActivityService;

    @Autowired
    private FlashsaleOverSellService flashsaleOverSellService;

    /**
     * 处理抢购请求
     *
     * @param flashsaleActivityId
     * @return
     *
     */

//    @ResponseBody
//    @RequestMapping("/flashsale/{flashsaleActivityId}")
//    public String flashsale(@PathVariable long flashsaleActivityId) {
//        return flashsaleOverSellService.processFlashsale((flashsaleActivityId));
//    }

    /**
     * 使用 lua 脚本处理抢购请求
     * @param flashsaleActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/{flashsaleActivityId}")
    public String flashsaleCommodity(@PathVariable long flashsaleActivityId) {
        boolean stockValidateResult = flashsaleActivityService.flashsaleStockValidator(flashsaleActivityId);
        return stockValidateResult ? "恭喜你秒杀成功" : "商品已经售完，下次再来";
    }
}
