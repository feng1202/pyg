//控制层 
app.controller('seckillGoodsController' ,function($scope,$location,seckillGoodsService,$interval){	
	 //读取列表数据绑定到表单中  
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		seckillGoodsService.findOne($location.search()['id']).success(
				function(response){
					$scope.entity= response;
					
					//时间倒计时
					allsecond =Math.floor( (  new Date($scope.entity.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
					time= $interval(function(){ 
					  if(allsecond >0){ 
						allsecond--;
						$scope.timeString=convertTimeString(allsecond);//转换时间字符串
					  }else{
						  $interval.cancel(time); 		  
						  alert("秒杀服务已结束");
					  }
					},1000);
				}

		);
	}
	
	//转换秒为   天小时分钟秒格式  XXX天 10:22:33
	convertTimeString=function(allsecond){
		var days= Math.floor( allsecond/(60*60*24));//天数
		var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
		var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		
		//天
		var timeString="";
		if(days<10 && days>0){			
			timeString="0"+days+"天 ";
		}else{
			timeString=days+"天 ";
		}
		
		
		//时
		var timeHours="";
		if(hours<10){
			timeHours="0"+hours;
		}else{
			timeHours=hours;
		}
		
		//分
		var timeMinutes="";
		if(minutes<10){
			timeMinutes="0"+minutes;
		}else{
			timeMinutes=minutes;
		}
		
		//秒
		var timeSeconds="";
		if(seconds<10){
			timeSeconds="0"+seconds;
		}else{
			timeSeconds=seconds;
		}
		
		return timeString+"："+timeHours+":"+timeMinutes+":"+timeSeconds;
	}
	
	//提交订单
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
			function(response){
				if(response.success){//如果下单成功
					alert("抢购成功，请在5分钟内完成支付");
					location.href="pay.html";//跳转到支付页面
				}else{
					alert(response.message);
				}
			}
		);		
	}
	
});