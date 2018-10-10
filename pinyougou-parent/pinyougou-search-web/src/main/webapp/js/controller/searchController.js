<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
app.controller("searchController",function($scope,$location,searchService){
	
	//定义搜索对象的结构 category:商品分类
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};//搜索对象
	
	//搜索
	$scope.search=function(){
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;//搜索返回的结果
					buildPageLabel();//调用
<<<<<<< HEAD
=======
=======
app.controller("searchController",function($scope,searchService){
	
	//定义搜索对象的结构 category:商品分类
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};//搜索对象
	
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;//搜索返回的结果
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
				}
		);
	}
	
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
	//构建分页标签(totalPages为总页数)
	buildPageLabel=function(){
		$scope.pageLabel=[];//新增分页栏属性		
		var maxPageNo= $scope.resultMap.totalPages;//得到最后页码
		var firstPage=1;//开始页码
		var lastPage=maxPageNo;//截止页码
		
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后边有点
		
		if($scope.resultMap.totalPages> 5){  //如果总页数大于5页,显示部分页码		
			if($scope.searchMap.pageNo<=3){//如果当前页小于等于3,显示前5页
				lastPage=5; //前5页
				$scope.firstDot=false;//前面没点
				
			}else if( $scope.searchMap.pageNo>=lastPage-2  ){//如果当前页大于等于最大页码-2
				firstPage= maxPageNo-4;		 //后5页	
				$scope.lastDot=false;//后边没点
				
			}else{ //显示当前页为中心的5页
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;	
				
			}
		}else{
			$scope.firstDot=false;//前面无点
			$scope.lastDot=false;//后边无点
		}
		
		//循环产生页码标签				
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);				
		}		
	}
	
	
	//添加复合搜索条件  改变searchMap
	$scope.addSearchItem=function(key,value){
		if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
<<<<<<< HEAD
=======
=======
	
	//添加复合搜索条件  改变searchMap
	$scope.addSearchItem=function(key,value){
		if(key=='category' || key=='brand'){//如果点击的是分类或者是品牌
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
			$scope.searchMap[key]=value;
		}else{//否则是规格
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();//执行搜索 
	}
	
	//移除复合搜索条件
	$scope.removeSearchItem=function(key){
<<<<<<< HEAD
		if(key=="category" ||  key=="brand" || key=='price'){//如果是分类或品牌
=======
<<<<<<< HEAD
		if(key=="category" ||  key=="brand" || key=='price'){//如果是分类或品牌
=======
		if(key=="category" ||  key=="brand"){//如果是分类或品牌
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
			$scope.searchMap[key]="";		
		}else{//否则是规格
			delete $scope.searchMap.spec[key];//移除此属性
		}
		$scope.search();//执行搜索 
	}
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
	
	//根据页码查询(分页查询)
	$scope.queryByPage=function(pageNo){
		//页码验证
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return;
		}		
		$scope.searchMap.pageNo=pageNo;			
		$scope.search();//查询
	}
	
	//判断当前页码是否为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	
	//判断当前页是否为最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	//排序查询
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		
			
		$scope.search();//查询		
	}
	
	//判断关键字是否是品牌
	$scope.keywordsIsBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
				return true;
			}			
		}		
		return false;
	}
	
	//加载查询字符串
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=  $location.search()['keywords'];
		$scope.search();
	}
<<<<<<< HEAD
=======
=======
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
});