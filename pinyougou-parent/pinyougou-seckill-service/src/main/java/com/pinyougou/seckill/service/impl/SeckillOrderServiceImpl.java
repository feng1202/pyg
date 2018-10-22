package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Autowired
		private RedisTemplate redisTemplate;
		
		@Autowired
		private TbSeckillGoodsMapper seckillGoodsMapper;
		
		@Autowired
		private IdWorker idWorker;
		
		//秒杀下单
		@Override
		public void submitOrder(Long seckillId, String userId) {
			//1.从缓存中查询秒杀商品	
			TbSeckillGoods  seckillGoods=(TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
			if (seckillGoods == null) {
				throw new RuntimeException("商品不存在");
			}
			if (seckillGoods.getStockCount()<=0) {
				throw new RuntimeException("商品已抢购一空");
			}
			
			//2.减少（redis）库存
			seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
			redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);//放回缓存
			if(seckillGoods.getStockCount()==0){//如果已经被秒光
				seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//同步到数据库	
				redisTemplate.boundHashOps("seckillGoods").delete(seckillId);		
				
				System.out.println("商品同步到数据库。。。");
			}
			
			
			//3.存储秒杀订单(不向数据库存，只向缓存中存储)	
			long orderId = idWorker.nextId();
			TbSeckillOrder seckillOrder=new TbSeckillOrder();
			seckillOrder.setId(orderId);
			seckillOrder.setCreateTime(new Date());
			seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
			seckillOrder.setSeckillId(seckillId);
			seckillOrder.setSellerId(seckillGoods.getSellerId());
			seckillOrder.setUserId(userId);//设置用户ID
			seckillOrder.setStatus("0");//状态
			
			
			redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
			System.out.println("保存订单成功(redis)");
		}

		//从缓存中提取订单
		@Override
		public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
				
			return 	(TbSeckillOrder)redisTemplate.boundHashOps("seckillOrder").get(userId);

		}

		@Override
		public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
			System.out.println("saveOrderFromRedisToDb:"+userId);
			//1.从缓存中提取订单数据
			TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
			if(seckillOrder==null){
				throw new RuntimeException("订单不存在");
			}
			//如果与传递过来的订单号不符
			if(seckillOrder.getId().longValue()!=orderId.longValue()){
				throw new RuntimeException("订单不相符");
			}	
			
			//2.修改订单实体属性
			seckillOrder.setTransactionId(transactionId);//交易流水号
			seckillOrder.setPayTime(new Date());//支付时间
			seckillOrder.setStatus("1");//支付状态
			
			//3.将订单存入数据库
			seckillOrderMapper.insert(seckillOrder);//保存到数据库
			
			//4.清除缓存中的订单
			redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
		}

		@Override
		public void deleteOrderFromRedis(String userId, Long orderId) {
			
			//1.获取redis中的订单
			TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
			if (seckillOrder != null  && seckillOrder.getId().equals(orderId) ){
				
				//2.删除缓存中的订单
				redisTemplate.boundHashOps("seckillOrder").delete(userId);
				
				//3.库存回退
				//3.1从缓存成获取秒杀商品 
				TbSeckillGoods seckillGoods=(TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
				if (seckillGoods != null) {
					//增加库存
					seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
					//放入缓存中
					redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
				}else {
					seckillGoods = new TbSeckillGoods();
					seckillGoods.setId(seckillOrder.getSeckillId());
					// 设置redis中秒杀商品的属性
					seckillGoods.setStockCount(1);// 数量为1
					redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
				}
				System.out.println("用户未支付,订单已取消");
			}
			
		}
			
	
	
}
