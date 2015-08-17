// JavaScript Document

// 焦点图开始
 var newSlideSize = function slideSize(){
        var w = Math.ceil($(".swiper-container").width()/1.96);
        $(".swiper-container,.swiper-wrapper,.swiper-slide").height(w); 
     };
     newSlideSize();
     $(window).resize(function(){
       newSlideSize();
     });
     
     var mySwiper = new Swiper('.swiper-container',{   
    pagination: '.pagination',
    loop:true,
    autoplay:3000,	
    paginationClickable: true,
    onSlideChangeStart: function(){
     //回调函数
    }
}); 


//点击清空搜索框
$(".clear-txt").click(function(){
   $(this).siblings("input").val("");
});





 
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  