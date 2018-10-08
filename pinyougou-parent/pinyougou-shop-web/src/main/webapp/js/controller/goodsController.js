//控制层 
app.controller('goodsController', function($scope, $controller,$location, goodsService,
		uploadService, itemCatService, typeTemplateService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		goodsService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		goodsService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 查询实体
	$scope.findOne = function() {
		var id=$location.search()['id'];//获取参数值
		
		if(id==null){
			return ;
		}
		
		goodsService.findOne(id).success(
			function(response) {
				$scope.entity = response;
				
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				
				//显示图片列表
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);	
				
				//规格				
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
			
				//SKU列表规格列转换
				for( var i=0;i<$scope.entity.itemList.length;i++ ){
					$scope.entity.itemList[i].spec = 
					JSON.parse( $scope.entity.itemList[i].spec);		
				}	
			}
		);
	}
	
	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items= $scope.entity.goodsDesc.specificationItems;
		var object= $scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}else{
	         //如果能够查询到规格选项
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}			
	}

	// 增加商品
	$scope.add = function() {
		$scope.entity.goodsDesc.introduction = editor.html();

		goodsService.add($scope.entity).success(function(response) {
			if (response.success) {
				alert("增加成功");
				$scope.entity = {};

				editor.html('');// 清空富文本编辑器
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		goodsService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.reloadList();// 刷新列表
				$scope.selectIds = [];
			}
		});
	}

	$scope.searchEntity = {};// 定义搜索对象

	// 搜索
	$scope.search = function(page, rows) {
		goodsService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}

	/**
	 * 上传图片
	 */
	$scope.uploadFile = function() {
		uploadService.uploadFile().success(function(response) {
			if (response.success) {// 如果上传成功，取出 url
				$scope.image_entity.url = response.message;// 设置文件地址
			} else {
				alert(response.message);
			}
		}).error(function() {

			alert("上传发生错误");
		});
	}

	// 定义页面实体结构
	$scope.entity = {
		goods : {},
		goodsDesc : {
			itemImages : [],
			specificationItems : []
		}
	};

	// 添加图片列表
	$scope.add_image_entity = function() {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	// 列表中移除图片
	$scope.remove_image_entity = function(index) {
		$scope.entity.goodsDesc.itemImages.splice(index, 1);
	}

	// 查询一级商品分类列表
	$scope.selectItemCat1List = function() {
		itemCatService.findByParentId(0).success(function(response) {
			$scope.itemCat1List = response;
			// 初始化选择框,防止空白行
			// $scope.entity.goods.category1Id = $scope.itemCat1List[0].id;

		});
	}

	// 查询二级商品分类列表
	$scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {
		// 根据选择的值，查询二级分类
		itemCatService.findByParentId(newValue).success(function(response) {
			$scope.itemCat2List = response;
		});
	});

	// 查询三级商品分类列表
	$scope.$watch('entity.goods.category2Id', function(newValue, oldValue) {
		// 根据选择的值，查询三级分类
		itemCatService.findByParentId(newValue).success(function(response) {
			$scope.itemCat3List = response;
		});
	});

	// 三级分类选择后 读取模板ID
	$scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {
		// 根据选择的值，查询模板ID
		itemCatService.findOne(newValue).success(function(response) {
			// 更新模板ID
			$scope.entity.goods.typetypeTemplateId = response.typeId;
		})

	});

	// 模板ID选择后 更新品牌列表
	$scope.$watch('entity.goods.typetypeTemplateId', function(newValue,oldValue){
		//读取品牌列表和扩展属性
		typeTemplateService.findOne(newValue).success(
				function(response) {
					$scope.typeTemplate = response;// 获取类型模板
					// 品牌列表类型转换
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
					
					//如果没有ID，则加载模板中的扩展数据
					if($location.search()['id']==null){
						// 扩展属性
						$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
					}
				}
		);
		
		// 查询规格列表
		typeTemplateService.findSpecList(newValue).success(function(response) {
			$scope.specList = response;
		});
	});

	// 保存选中规格选项
	$scope.updateSpecAttribute = function($event, name, value) {
		var object = $scope.searchObjectByKey(
				$scope.entity.goodsDesc.specificationItems, 'attributeName',
				name);
		if (object != null) {
			if ($event.target.checked) {
				object.attributeValue.push(value);
			} else {// 取消勾选
				object.attributeValue.splice(object.attributeValue
						.indexOf(value), 1);// 移除选项
				// 如果选项都取消了，将此条记录移除
				if (object.attributeValue.length == 0) {
					$scope.entity.goodsDesc.specificationItems.splice(
							$scope.entity.goodsDesc.specificationItems
									.indexOf(object), 1);
				}
			}
		} else {
			$scope.entity.goodsDesc.specificationItems.push({
				"attributeName" : name,
				"attributeValue" : [ value ]
			});
		}
	}

	// 创建SKU列表
	$scope.createItemList = function() {
		$scope.entity.itemList = [ {
			spec : {},
			price : 0,
			num : 99999,
			status : '0',
			isDefault : '0'
		} ];// 初始
		var items = $scope.entity.goodsDesc.specificationItems;
		for (var i = 0; i < items.length; i++) {
			$scope.entity.itemList = addColumn($scope.entity.itemList,
					items[i].attributeName, items[i].attributeValue);
		}
	}
	// 添加列值
	addColumn = function(list, columnName, conlumnValues) {
		var newList = [];// 新的集合
		for (var i = 0; i < list.length; i++) {
			var oldRow = list[i];
			for (var j = 0; j < conlumnValues.length; j++) {
				var newRow = JSON.parse(JSON.stringify(oldRow));// 深克隆
				newRow.spec[columnName] = conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}

	// 商品状态
	$scope.status = [ '未审核', '已审核', '审核未通过', '关闭' ];

	// 商品分类列表
	$scope.itemCatList = [];

	// 加载商品分类列表
	$scope.findItemCatList = function() {
		itemCatService.findAll().success(
			function(response) {			
				for(var i=0; i<response.length;i++){
					$scope.itemCatList[response[i].id] =response[i].name;
				}
			}
		);

	}

	//保存 
	$scope.save=function(){			
		//提取文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();	
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert('保存成功');					
					location.href="goods.html";//跳转到商品列表页
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	
});
