$().ready(function() {
	validateRule();
});

$.validator.setDefaults({
	submitHandler : function() {
		save();
	}
});
function save() {
	var keywordId=$("#keywordId").val();
	var excludeId= $("#excludeId").val();
	
	if(keywordId==excludeId){
		parent.layer.msg('排除不可相同');
		return ;
	}
	
	$.ajax({
		cache : true,
		type : "POST",
		url : "/robot/tKeywordExclude/save",
		data : $('#signupForm').serialize(),// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				parent.reLoadExclude();
				var index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
				parent.layer.close(index);

			} else {
				parent.layer.alert(data.msg)
			}

		}
	});

}
function validateRule() {
	var icon = "<i class='fa fa-times-circle'></i> ";
	$("#signupForm").validate({
		rules : {
			excludeName : {
				required : true
			},
			keyWordName : {
				required : true
			}
		},
		messages : {
			excludeName : {
				required : icon + "请输入排除的词语"
			},
			keyWordName : {
				required : icon + "请选择关键字"
			}
		}
	})
}
var keywordId=[];
function selectKeyWordPage() {
	layer.open({
		type : 2,
		title : '选择关键字',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '350px', '70%' ],
		content : '/robot/tKeyword/selectKeyWordPage/1', // iframe的url
		btn: ['确定','关闭'],
		yes: function(index){
				 keywordId.splice(0,keywordId.length);//清空数组 

				 var keyWordName="";
                 //当点击‘确定’按钮的时候，获取弹出层返回的值
                 var res = window["layui-layer-iframe" + index].add();
                 if(res==""){
                	 return ;
                 }
                 var json = JSON.parse(res);
                 
                 
                 //添加类型id数组以及类型显示
                 for(var i=0,l=json.length;i<l;i++){
                	 keywordId.push(json[i].keywordId);
                	 if(i==0){
                		 keyWordName+=json[i].name;
                	 }else{
                		 keyWordName+=","+json[i].name;
                	 }
            	 }
                 
                 //菜单类型显示
                 $("#keyWordName").val(keyWordName);
                 $("#keywordId").val(keywordId);
                 
//                 console.log(keyWordIds);
                 //最后关闭弹出层
                 layer.close(index);
       },
	});
}

var excludeId=[];
function selectExcludePage() {
	layer.open({
		type : 2,
		title : '选择关键字',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '350px', '70%' ],
		content : '/robot/tKeyword/selectKeyWordPage/1', // iframe的url
		btn: ['确定','关闭'],
		yes: function(index){
				 excludeId.splice(0,excludeId.length);//清空数组 

				 var excludeName="";
                 //当点击‘确定’按钮的时候，获取弹出层返回的值
                 var res = window["layui-layer-iframe" + index].add();
                 if(res==""){
                	 return ;
                 }
                 var json = JSON.parse(res);
                 
                 
                 //添加类型id数组以及类型显示
                 for(var i=0,l=json.length;i<l;i++){
                	 excludeId.push(json[i].keywordId);
                	 if(i==0){
                		 excludeName+=json[i].name;
                	 }else{
                		 excludeName+=","+json[i].name;
                	 }
            	 }
                 
                 //菜单类型显示
                 $("#excludeName").val(excludeName);
                 $("#excludeId").val(excludeId);
                 
//                 console.log(keyWordIds);
                 //最后关闭弹出层
                 layer.close(index);
       },
	});
}