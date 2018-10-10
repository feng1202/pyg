app.controller("baseController", function($scope) {
	// 分页控件配置
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();// 重新加载
		}
	};

	// 重新加载列表 数据 （刷新列表）
	$scope.reloadList = function() {
		// 切换页码
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
	}

	$scope.selectIds = [];// 选中的id集合

	// 用户勾选复选框
	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {// 如果被选中，则添加到数组
			$scope.selectIds.push(id);// push向集合添加元素
		} else {
			var index = $scope.selectIds.indexOf(id);// 查找值的位置
			$scope.selectIds.splice(index, 1);// 参数1：移除的位置 参数2：移除的个数
		}
	}

	// 提取 json 字符串数据中某个属性，返回拼接字符串 逗号分隔
	$scope.jsonToString = function(jsonString, key) {
		var json = JSON.parse(jsonString);// 将 json 字符串转换为 json 对象
		var value = "";
		for (var i = 0; i < json.length; i++) {
			if (i > 0) {
				value += ",";
			}
			value += json[i][key];
		}
          return value;
	}

});