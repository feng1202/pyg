app.service("loginService",function($http){//登陆服务层
	// 读取登录人名称
	this.loginName=function(){
		return $http.get("../login/name.do");
	}
});