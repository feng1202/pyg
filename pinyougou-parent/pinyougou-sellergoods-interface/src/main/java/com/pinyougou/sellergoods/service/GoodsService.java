package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;
<<<<<<< HEAD
import com.pinyougou.pojo.TbItem;
=======
<<<<<<< HEAD
import com.pinyougou.pojo.TbItem;
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
import com.pinyougou.pojogroup.Goods;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum,int pageSize);
	
	/**
	 * 批量修改状态
	 * @param ids
	 * @param status
	 */
	public void updateStatus(Long []ids,String status);
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
	
	/**
	 * 根据商品ID和状态查询Item表信息  
	 * @param goodsId
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status );
<<<<<<< HEAD
=======
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
}
