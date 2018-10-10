package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	// 查询所有品牌
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		// 分页
		PageHelper.startPage(pageNum, pageSize);

		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

		return new PageResult(page.getTotal(), page.getResult());
	}

	// 新增
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}

	// 根据id查询
	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	// 修改
	@Override
	public void updata(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}

	// 批量删除
	@Override
	public void delete(long[] ids) {
		for (long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}

	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		// 分页
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		// 判断品牌是否为空
		if (brand != null) {
			// 判断查找条件 品牌是否为空
			if (brand.getName() != null && brand.getName().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			// 判断查找条件 首字母是否为空
			if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
				criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
			}
		}

		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		
		return brandMapper.selectOptionList();
	}

}
