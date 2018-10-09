package com.pinyougou.manager.controller;
<<<<<<< HEAD
import java.util.Arrays;
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
<<<<<<< HEAD
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.search.service.ItemSearchService;
=======
import com.pinyougou.pojogroup.Goods;
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
<<<<<<< HEAD
			
			//从索引库中删除
			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
<<<<<<< HEAD
	
	@Reference(timeout=100000)
	private ItemSearchService itemSearchService;
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){		
		try {
			goodsService.updateStatus(ids, status);
<<<<<<< HEAD
			
			//按照SPU ID查询 SKU列表(状态为1)		
			if("1".equals(status)){//如果是审核通过
				//得到导入的 SKU列表
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);						
				//调用搜索接口实现数据批量导入
				if(itemList.size()>0){				
					itemSearchService.importList(itemList);
				}else{
					System.out.println("没有明细数据");
				}
			}
			
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
}
