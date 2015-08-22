jQuery(document).ready(function($){
	//open popup
	$('.cd-popup-trigger').on('click', function(event){
		event.preventDefault();
		$('.cd-popup').addClass('is-visible');
	});
	
	//close popup
	$('.cd-popup').on('click', function(event){
		if( $(event.target).is('#popup-close') || $(event.target).is('.cd-popup') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-popup').removeClass('is-visible');
	    }
    });
});


function showAlart(content) {
	$('#alertModalContent').html(content);
	$('.cd-popup').addClass('is-visible');
}

/*function showAlart(content , isConfim) {
	$('#alertModalContent').html(content);
	if(isConfim) {
		$('issssss').html('<ul class="cd-buttons"> <li><a href="javascript:ok_Btn()">是</a></li> <li><a href="javascirpt:cancel_Btn()" id="popup-close">否</a></li> </ul>');
	}
	$('.cd-popup').addClass('is-visible');
}*/

