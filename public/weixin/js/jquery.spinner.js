;(function ($) {
  $.fn.spinner = function (opts) {
    return this.each(function () {
      var defaults = {value:0, min:0}
      var options = $.extend(defaults, opts)
      var keyCodes = {up:38, down:40}
      var container = $('<div></div>')
      container.addClass('spinner')
      var obj = $(this);

      var textField = $(this).addClass('value').attr('maxlength', '2').attr('id','sumCount').val(options.value)
        .bind('keyup paste change', function (e) {
          var field = $(this)
          if (e.keyCode == keyCodes.up) changeValue(1)
          else if (e.keyCode == keyCodes.down) changeValue(-1)
          else if (getValue(field) != container.data('lastValidValue')) validateAndTrigger(field)
        })
      textField.wrap(container)

      var increaseButton = $('<button class="increase">+</button>').click(function () { changeValue(1) })
      var decreaseButton = $('<button class="decrease">-</button>').click(function () { changeValue(-1) })

      validate(textField)
      container.data('lastValidValue', options.value)
      textField.before(decreaseButton)
      textField.after(increaseButton)

      function changeValue(delta) {
        textField.val(getValue() + delta);

        validateAndTrigger(textField)
      }

      function validateAndTrigger(field) {
        clearTimeout(container.data('timeout'))
        var value = validate(field);
        var price = $('#price').val();
        var product_id = obj.attr("product_id");
        var product_type = obj.attr("product_type");
        var productIds = $('#productIds').val();
        var typeCount = $('#productType_'+product_type).html();
        var isHave = false;
        if(productIds != null && productIds.indexOf(",") >= 0) {
          var ids = productIds.split(",");
          for (i=0;i<ids.length ;i++ )   {
            if(ids[i] == product_id) {
              isHave = true;
            }
          }
        }
        if(value == 0) {
          var newIds = "";
          var ids = productIds.split(",");
          for (i=0;i<ids.length ;i++ )   {
            if(ids[i] != '' && ids[i] != product_id) {
              newIds += ","+ids[i];
            }
          }
          $('#productIds').val(newIds);
          $('#productType_'+product_type).html(parseInt(typeCount) - 1)
        }

        var ids_count = $('#product_id_value').val();
        var newIds_count = "";
        //  4_5,5_2,6_1
        if(ids_count != null && ids_count != '' && ids_count.indexOf(",") >= 0) {
          var isHave2 = false;
          var ids_countArray = ids_count.split(",");
          for(var k = 0 ; k < ids_countArray.length ; k++) {
            var id_count = ids_countArray[k];
            if(id_count != null && id_count != '' && id_count.indexOf("_")) {
              var pId = id_count.split("_")[0];
              if(pId == product_id) {
                if(value > 0) {
                  newIds_count += pId+"_"+value + ",";
                  isHave2 = true;
                }
              } else {
                newIds_count += id_count + ",";
              }
            }
          }
          if(!isHave2) {
            newIds_count += product_id+"_"+value;
          }
          $('#product_id_value').val(newIds_count);
        } else {
          var id_value = product_id+"_"+value+",";
          $('#product_id_value').val(id_value);
        }


        if(!isHave) {
          $('#productIds').val(productIds + "," + product_id);

          $('#productType_'+product_type).html(parseInt(typeCount) + 1)
        }
       // alert($('#product_id_value').val());
        var carts=$('#product_id_value').val();
        document.getElementById("carts").value=carts;
        $('#sumPrice').html(value * price);
        if (!isInvalid(value)) {
          textField.trigger('update', [field, value])
        }
        var sumcount=$('#sumCount').val();
        document.getElementById("number").value=sumcount;
      }

      function validate(field) {
        var value = getValue()
        if (value <= options.min) decreaseButton.attr('disabled', 'disabled')
        else decreaseButton.removeAttr('disabled')
        field.toggleClass('invalid', isInvalid(value)).toggleClass('passive', value === 0)

        if (isInvalid(value)) {
          var timeout = setTimeout(function () {
            textField.val(container.data('lastValidValue'))
            validate(field)
          }, 500)
          container.data('timeout', timeout)
        } else {
          container.data('lastValidValue', value)
        }
        return value
      }

      function isInvalid(value) { return isNaN(+value) || value < options.min; }

      function getValue(field) {
        field = field || textField;
        return parseInt(field.val() || 0, 10)
      }
    })
  }
})(jQuery)