package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口
 * 
 * @author Administrator
 *
 */
public interface BrandService {

	// 查询所有的品牌
	public List<TbBrand> findAll();

	/**
	 * 品牌分页
	 * @param pageNum  当前页面
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	/**
	 * 增加
	 * @param brand
	 */
	public void add(TbBrand brand);
	
	/**
	 * 根据id查询实体
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);
	
	/**
	 * 修改
	 * @param brand
	 */
    public void updata(TbBrand brand);
    
    /**
     * 批量删除
     * @param ids
     */
    public void delete(long[] ids);
    
   /**
    *  
	* 查询+品牌分页 
    * @param brand    品牌
    * @param pageNum  当前页面
    * @param pageSize 每页记录数
    * @return
    */
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);
	
	/**
	 * 品牌下拉框数据
	 * @return
	 */
	List<Map> selectOptionList();
}
