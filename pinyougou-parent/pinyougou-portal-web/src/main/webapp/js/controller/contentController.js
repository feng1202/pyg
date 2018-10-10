//广告控制层（运营商后台）
app.controller("contentController",function($scope,contentService){
	
	$scope.contentList=[];//广告集合
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
				$scope.contentList[categoryId]=response;
			}
		);
	}
	
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
	//搜索跳转
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
<<<<<<< HEAD
=======
=======
	
>>>>>>> 8d4c79b6237d07e2cbada9d1da1f49ab4df4da24
>>>>>>> b22a8e16a57039082bbd2fe8883a488574d5cea0
});