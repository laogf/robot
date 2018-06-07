
var prefix = "/app/faq"

function seachQuestion() {
	var question=$("#question").val();
	if(question=="" || question==null){
		layer.msg("问题查询不能为空！");
		document.getElementById("answer").innerHTML = "请输入问题";
		return;
	}
	$.ajax({
		type:"post",
		url:prefix+"/searchAnswer",
		data:{content:question,robotNo:"123456"},
		success:function(result){
			 document.getElementById("answer").innerHTML = result.data.answer;
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			layer.msg("查询失败");
		}
		
	})
}

document.onkeyup = function (e) {//按键信息对象以函数参数的形式传递进来了，就是那个e  
	if (window.event)//如果window.event对象存在，就以此事件对象为准  
        e = window.event;  
    var code = e.charCode || e.keyCode;  
    if (code == 13) {  
        //此处编写用户敲回车后的代码  
    	var question=$("#question").val();
    	if(question=="" || question==null){
    		layer.msg("问题查询不能为空！");
    		document.getElementById("answer").innerHTML = "请输入问题";
    		return;
    	}
		$.ajax({
			type:"post",
			url:prefix+"/searchAnswer",
			data:{content:question},
			success:function(result){
				 document.getElementById("answer").innerHTML = result.data.answer;
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				layer.msg("查询失败");
			}
			
		})
    }
}  