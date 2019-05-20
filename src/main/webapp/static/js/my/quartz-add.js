$(document).ready(function () {
    $("#quartzCron").cronGen({
        direction : 'left'
    });
    reset();

});

var reset = function(){
    $.ajax({
        type: 'POST',
        async: false,
        url: 'quartz/getQuartz.shtml',
        data: {
            quartzId : quartzId
        },
        success: function (data) {
            var quartz = data.data;
            if(quartz==null){
                return;
            }
            $("#quartzDescription").val(quartz.quartzDescription);
            $("#cQuarz").val(quartz.quartzCron);
            $("#quartzId").val(quartz.quartzId);
        },
        error: function () {
            alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}
$().ready(function () {
    var icon = "<i class='fa fa-times-circle'></i> ";
    $("#quartzForm").validate({
        rules: {
            quartzCron: {
                required: true,
            },
            quartzDescription: {
                required: true,
                maxlength: 50
            }
        },
        messages: {
            quartzCron: {
                required: icon + "请选择cron",
            },
            quartzDescription: {
                required: icon + "请输入策略描述",
                maxlength: icon + "策略描述不能超过30个字符"
            }
        },
        submitHandler:function(form){
            $.post("quartz/save.shtml", decodeURIComponent($(form).serialize(),true), function(data){
                var result = JSON.parse(data);
                if(result.status === "success"){
                    layer.msg('添加成功',{
                        time: 2000,
                        icon: 6
                    });
                    setTimeout(function(){
                        location.href = "view/quartz/execute/listUI.shtml";
                    },2000);
                }else {
                    layer.msg(result.message, {icon: 2});
                }
            });
        }
    });
});

var cancel = function(){
    location.href = "view/quartz/execute/listUI.shtml";
};