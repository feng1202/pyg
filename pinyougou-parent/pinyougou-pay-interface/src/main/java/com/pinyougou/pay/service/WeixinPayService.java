package com.pinyougou.pay.service;
/**
 * 微信支付接口
 * @author Administrator
 *
 */

import java.util.Map;

public interface WeixinPayService {
	
	/**
	 * 生成微信支付二维码
	 * @param out_trade_no
	 * @param total_fee
	 * @return
	 */
	public Map createNative(String out_trade_no,String total_fee);

	
	/**
	 * 查询支付状态
	 * @param out_trade_no
	 */
	public Map queryPayStatus(String out_trade_no);
}
