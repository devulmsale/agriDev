// JavaScript Document


//tab切换
function cTab(tab_controler,tab_con){
	this.tab_controler = tab_controler;
	this.tab_con = tab_con;
	var tabs = $(tab_controler).find("a");
	var panels = $(tab_con).children("div");
	$(tab_con).children("div").css("display","none");
	$(tab_con).children("div:first").css("display","block");
	$(tabs).bind("click", function(){
		var index = $.inArray(this,tabs);
		tabs.removeClass("tab-cur")
		.eq(index).addClass("tab-cur");
		panels.css("display","none")
		.eq(index).css("display","block");
		return false;
	});
};

//点击清空搜索框
$(".clear-txt").click(function(){
   $(this).siblings("input").val("");
});

//弹出切换class
$(".apply-pup").click(function(){
	$(this).toggleClass("apply-pup-hover");
	$("#apply-pup-icon").slideToggle(350);	
	return false;
});

//返回顶部
//页面滚动显示下载按钮
$(window).scroll(function(){
	var scroolh = $(window).scrollTop();	
	if(scroolh >= 400){
	     $(".return-top").addClass("return-topTo");
	}
	else{		
	     $(".return-top").removeClass("return-topTo");
	};	
	if(scroolh > 300){
		$(".float-download").fadeIn(150);
	}
	else{
		$(".float-download").fadeOut(150);
	};
});

$(".return-top").click(function(){
	  $('body,html').animate({scrollTop:0},220); 	 
	  return false;
});

//下拉刷新上拉

for(var i=0;i<document.querySelectorAll("#wrapper ul li").length;i++){
document.querySelectorAll("#wrapper ul li")[i].colorfulBg(); }
refresher.init({
	id:"wrapper",//<------------------------------------------------------------------------------------┐
	pullDownAction:Refresh,                                                            
	pullUpAction:Load 																			
	});																						
var generatedCount = 0;																			
function Refresh() {																
	setTimeout(function () {	// <-- Simulate network congestion, remove setTimeout from production!
		var el, li, i;																		
		el =document.querySelector("#wrapper ul");									
		el.innerHTML='';																
		for (i=0; i<11; i++) {																		 
			li = document.createElement('li');													
			li.appendChild(document.createTextNode('async row ' + (++generatedCount)));				
		el.insertBefore(li, el.childNodes[0]);														
		}																							 
		wrapper.refresh();/****remember to refresh after  action completed！ ---yourId.refresh(); ----| ****/
			for(var i=0;i<document.querySelectorAll("#wrapper ul li").length;i++){
		document.querySelectorAll("#wrapper ul li")[i].colorfulBg(); }
	}, 1000);

}

function Load() {
	setTimeout(function () {// <-- Simulate network congestion, remove setTimeout from production!
		var el, li, i;
		el =document.querySelector("#wrapper ul");
		for (i=0; i<2; i++) {
			li = document.createElement('li');
			li.appendChild(document.createTextNode('async row ' + (++generatedCount)));
			el.appendChild(li, el.childNodes[0]);
		}
		wrapper.refresh();/****remember to refresh after action completed！！！   ---id.refresh(); --- ****/
		for(var i=0;i<document.querySelectorAll("#wrapper ul li").length;i++){
		document.querySelectorAll("#wrapper ul li")[i].colorfulBg(); }
	}, 1000);	
}


 
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  