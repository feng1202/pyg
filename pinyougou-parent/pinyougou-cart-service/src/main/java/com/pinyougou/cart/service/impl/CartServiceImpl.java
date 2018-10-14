package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

/**
 *  购物车服务实现类
 * @author Administrator
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	// 添加商品到购物车
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		// 1.根据商品SKU ID查询SKU商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			throw new RuntimeException("商品不存在");
		}
		if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态无效");
		}
		
		// 2.获取商家ID
		String sellerId = item.getSellerId();
		
		// 3.根据商家ID判断购物车列表中是否存在该商家的购物车
		Cart cart = serachCartBySellerId(cartList,sellerId);
		
		
		if (cart == null) {// 4.如果购物车列表中不存在该商家的购物车
			// 4.1 新建购物车对象
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			TbOrderItem orderItem = createOrderItem(item,num);
			List orderItemList= new ArrayList();
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			
			
			// 4.2 将新建的购物车对象添加到购物车列表
			cartList.add(cart);
			
			
		}else {// 5.如果购物车列表中存在该商家的购物车
			// 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = serachOrderItemByItemId(cart.getOrderItemList(),itemId);
			if (orderItem==null) {// 5.1. 如果不存在，新增购物车明细对象
				orderItem = createOrderItem(item,num);
				cart.getOrderItemList().add(orderItem);
				
			} else {// 5.2. 如果存在，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);//更改数量
				//更改金额
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

				//当明细数量小于等于0，移除此明细
				if (orderItem.getNum() <= 0) {
					cart.getOrderItemList().remove(orderItem);
				}
				//当购物车的明细数量为0，在购物车列表中移除此购物车
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
			
		}
		

		return cartList;
	}

	//根据商家ID查询购物车对象
	private Cart serachCartBySellerId(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	//根据商品明细ID查询
	private TbOrderItem serachOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId) {
		for (TbOrderItem orderItem : orderItemList) {
			if (orderItem.getItemId().longValue() == itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	
	//创建购物车明细对象
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		//创建新的购物车明细对象
		if (num <= 0) {
			throw new RuntimeException("数量非法");
		}
		
		TbOrderItem orderItem= new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		
		return orderItem;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	// 从redis中提取购物车数据
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车数据....."+username);
		List<Cart> cartList	=(List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if (cartList == null) {
			cartList = new ArrayList();
		}
		return cartList;
	}

	//向redis中存入购物车数据
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis中存入购物车....."+username);
		redisTemplate.boundHashOps("cartList").put(username,cartList);
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		System.out.println("合并购物车");
		for(Cart cart: cartList2){
			for(TbOrderItem orderItem :cart.getOrderItemList()){
				cartList1= addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());		
			}
		}
		return cartList1;
	}
	
}
