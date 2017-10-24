function changeR(node) {
  // 用于点击时产生不同的验证码
  node.src = "/kaptcha-image?time=" + new Date().getTime();
}

$(function() {
  // <%if (!StringUtils.isEmpty(mobile)){%>
  // $("#submit1").trigger("click");
  // <%}%>

  function showErrorInfo(msg) {
    $("#error").html(msg).css("display", "block")
  }

  function hideErrorInfo() {
    $("#error").css("display", "none")
  }

  $("#codeBtn").click(function() {
    if ($("#username").val() == "") {
      $.toptip("请输入手机号")
      return false;
    } else if ($("#username").val().length != 11) {
      $.toptip("请输入正确的手机号")
      return false;
    }

    /*
    图形验证码存在时做判断
     */
    if (+$('.weui_vcode').attr("account") > 0 && $('#kaptcha')[0]) {
      //对象存在的处理逻辑
      if ($("#kaptcha").val() == "") {
        $.toptip("请输入图形验证码")
        return false;
      } else if ($("#kaptcha").val().length != 4) {
        $.toptip("请输入正确的图形验证码")
        return false;
      }
    }
    time();
    getVcode();

  })

  var wait = 60;

  function time() {


    if (wait == 0) {
      $("#codeBtn").removeAttr("disabled");
      $("#codeBtn").html("获取验证码")
      wait = 60;
    } else {
      $("#codeBtn").attr("disabled", "disabled")
      $("#codeBtn").html("重新发送" + wait)
      wait--;
      timeKeeping = setTimeout(function() {
          time()
        },
        1000)
    }
  }
  //获取验证码
  function getVcode() {

    var _url = '/ignore/sms/getVerificationCode/' + $("#username").val() +
      '/' + ($('#kaptcha').val() || "undefined");
    console.log(_url)
    $.ajax({

      url: _url,
      type: 'GET', //GET
      dataType: 'text', //返回的数据格式：json/xml/html/script/jsonp/text
      async: false,
      success: function(data) {
        var obj = JSON.parse(data)
        if (obj.stat == 0) {
          $.toptip(obj.data)
          return false
        } else if (obj.stat == 1) {
          $("#smscode").focus();

        }

      },
      error: function(xhr, textStatus) {
        console.log('错误')
      }

    })
  }

  $("#smscode").click(function() {})
  $("#submit").click(function() {
    if ($.trim($("#username").val()) == "") {
      $.toptip('请输入手机号')
      return false
    } else if ($.trim($("#username").val()).length != 11) {
      $.toptip('请输入正确手机号')
      return false
    }

    if ($.trim($("#smscode").val()) == "") {
      $.toptip("请输入验证码")
      return false
    } else if(+$(".weui_vcode").attr("account") > 0 && $.trim($("#kaptcha").val()) == ""){
      $.toptip("请输入图形验证码")
      return false;
    } else {
      var correctCode = validateVcode($("#username").val(), $("#vcode").val());
      console.log(correctCode)
      if (correctCode == "false") {
        $.toptip("验证码不正确")
        var account =  +$(".weui_vcode").attr("account");
        if(account === NaN){
        	account = 0;
        }
        $(".weui_vcode").attr("account", account + 1);
        changeR($("#kaptchaImg")[0]);
        return false
      }
    }

  })

  //判断验证码是否错误
  function validateVcode() {
    var vCodeFlag = false;
    var _url = '/ignore/sms/validateVcode';
    console.log(_url)
    $.ajax({

      url: _url,
      type: 'POST', //GET
      data: {
        tel: $("#username").val(),
        vcode: $("#smscode").val()
      },
      dataType: 'text', //返回的数据格式：json/xml/html/script/jsonp/text
      async: false,
      success: function(data) {
        console.log(data)
        vCodeFlag = data;
      },
      error: function(xhr, textStatus) {
        console.log('错误')
      }

    })
    return vCodeFlag;
  }


})