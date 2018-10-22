package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	/**
	 * 刷新秒杀商品
	 */
	@Scheduled(cron="0 * * * * ?")
	public void refreshSeckillGoods(){
		System.out.println("执行了秒杀商品增量更新 任务调度"+new Date());
		
		//查询所有的秒杀商品键集合
		List ids = new ArrayList( redisTemplate.boundHashOps("seckillGoods").keys());
		System.out.println(ids);
		
		//查询正在秒杀的商品列表		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//审核通过
		criteria.andStockCountGreaterThan(0);//剩余库存大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
		criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间		
		// 排除缓存中已经有的商品id集合
		if (ids.size() > 0) {
			criteria.andIdNotIn(ids);
		}
		
		List<TbSeckillGoods> seckillGoodsList= seckillGoodsMapper.selectByExample(example);		
		//将列表数据装入缓存 
		for( TbSeckillGoods seckillGoods:seckillGoodsList ){
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
             System.out.println("增量更新秒杀商品id:" + seckillGoods.getId());
		}
		System.out.println("将"+seckillGoodsList.size()+"条商品装入缓存");		
	}
	
	
	/**
	 * 移除秒杀商品
	 */
	@Scheduled(cron="* * * * * ?")
	public void removeSeckillGoods(){
		System.out.println("移除秒杀商品任务在执行");
		//扫描缓存中秒杀商品列表，发现过期的移除
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		for( TbSeckillGoods seckillGoods:seckillGoodsList ){
			if(seckillGoods.getEndTime().getTime()< new Date().getTime()  ){//如果结束日期小于当前日期，则表示过期
				seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//向数据库保存记录
				redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());//移除缓存数据
				System.out.println("移除秒杀商品"+seckillGoods.getId());
			}			
		}
		System.out.println("移除秒杀商品任务结束");		
	}
}
