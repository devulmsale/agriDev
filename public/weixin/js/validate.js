function required(field) {
	if (field == null || field == "") {
		return false;
	} else {
		return true;
	}
}
function isInteger(field) {
    var reg = /^\d+$/;
    return reg.test(field);
}
function isEmail(field) {
	var atpos = field.indexOf("@");
	var dtpos = field.lastIndexOf(".");
	if (atpos < 1 || dtpos - atpos < 2) {
		return false;
	} else {
		return true;
	}
}

function isMobile(field) {
	 var length = field.length;     
     var re = /^1[3|5|7|8][0-9]\d{4,8}$/;     
     return (length == 11 && re.exec(field)) ? true : false;
}

function isNumber(field) {
	var re = /^\d+$/;
	return re.test(field);
}

function isMoney(field) {
    var reg = /^(([1-9]+)|([0-9]+\.[0-9]{1,2}))$/;
    return reg.test(field);
}

function maxLength(field, len) {
	if (field.length > len){
		return false;
	} else {
		return true;
	}
}

function minLength(field, len) {
	if (field.length < len){
		return false;
	} else {
		return true;
	}
}

function isLength(field, len) {
	if (field.length < len || field.length > len){
		return false;
	} else {
		return true;
	}
}