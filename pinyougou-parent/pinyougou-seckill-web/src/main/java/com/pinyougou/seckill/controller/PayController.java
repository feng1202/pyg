package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;
import util.IdWorker;

/**
 * 支付的控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private SeckillOrderService seckillOrderService;
	
	//生成二维码
	@RequestMapping("/createNative")
	public Map createNative() {
		
		//获取当前用户
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	     
	    //到redis查询支付日志
	    TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);
	    
	    
	    //判断支付日志存在
	    if (seckillOrder != null) {
	    	
			return weixinPayService.createNative(seckillOrder.getId()+"", (long)(seckillOrder.getMoney().doubleValue()*100)+"");
		
	    }else {
			
			return new HashMap();
		}
		
		
	}

	//查询支付状态
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
	
		//获取当前用户		
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		
		Result result= null;
		
		int x=0;
		
		while(true) {
			//调用查询接口
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if (map == null) {//出错
				result=new Result(false, "支付发送错误");
				break;
			}
			if(map.get("trade_state").equals("SUCCESS")) {//支付成功
				result= new Result(true, "支付成功");
			
				//保存订单
				seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));				
				break;
			}
		
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			
			//为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
			x++;
			if (x >= 100) {
				result=new Result(false, "二维码超时");
				
				//1.关闭支付
				Map payresult = weixinPayService.closePay(out_trade_no);				
				if( "FAIL".equals(payresult.get("result_code")) ){//如果返回结果是正常关闭
					if("ORDERPAID".equals(payresult.get("err_code"))){
						result=new Result(true, "支付成功");	
						seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
					}					
				}
				
				//删除订单
				if(result.isSuccess()==false){
					System.out.println("超时，取消订单");				
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
				}
				break;
			}
		}
		
		return result;
	}
}
