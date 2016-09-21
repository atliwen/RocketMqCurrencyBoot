 


//  数据
var MQ = Datas = {

    // 表单验证   发送MQ 的   表单验证数据
    sendTopicDate: {
        sendTopic: {
            message: "Topic证失败",
            validators: {
                notEmpty: {
                    message: "Topic不能为空"
                },
                regexp: {
                    regexp: /^[a-zA-Z]+$/,
                    message: "Topic只能包含大写、小写、字母"
                }
            }
        },
        sendbody: {
            message: "body证失败",
            validators: {
                notEmpty: {
                    message: "body不能为空"
                }
            }
        }
    },
    // 表单验证  开启一个新的 消费端  的 表单验证数据
    newidsDate: {
        Topic: {
            message: "Topic证失败",
            validators: {
                notEmpty: {
                    message: "Topic不能为空"
                },
                regexp: {
                    regexp: /^[a-zA-Z]+$/,
                    message: "Topic只能包含大写、小写、字母"
                }
            }
        }
    },
    // 表单验证  删除一个 消费端 的  表单验证数据
    deleteidData: {
        Consumer: {
            message: "Consumer证失败",
            validators: {
                notEmpty: {
                    message: "Consumer不能为空"
                },
                regexp: {
                    regexp: /@/,
                    message: "Consumer必须有@符合"
                }
            }
        }
    },

    // 设置 Url
    dataUrl: {
        sendformidUrl:null,
        newidsUrl: null,
        deleteidUrl:null
    }

}

 

// 表单 验证 提交 事件
var MQ = Homejq = {

    sendformidSubmit: function (validator, form, submitButton) {
       
        $.ajax({
            type: "POST",
            url: Datas.dataUrl.sendformidUrl,
            
            data: {
                Topic: form.context.sendTopic.value,
                Tags: form.context.sendTags.value,
                body: form.context.sendbody.value
            },
            success: function (data) {
                alert("成功!");
                $("#sendtestid").addClass("in").html("SendResult : " + data);
                $(form).bootstrapValidator("disableSubmitButtons", false);
            },
            error: function (data) {
                alert("失败!");
                $("#sendtestid").addClass("in").html("发送失败 " + data);
                $(form).bootstrapValidator("disableSubmitButtons", true);
            }
        });
    },

    newidsSubmit: function (validator, form, submitButton) {
      
        $.ajax({
            type: "POST",
            url:  Datas.dataUrl.newidsUrl,
            data: {
                Topic: form.context.Topic.value,
                Tags: form.context.Tags.value
            },
            success: function () {
                alert("成功!");
                $("#promptid").addClass("in").html("启动成功了");
                $("#homeid").click();
                $("#table").bootstrapTable("refresh", true);
                $(form).bootstrapValidator("disableSubmitButtons", false);
            },
            error: function (date) {
                alert("失败!");
                $("#promptid").addClass("in").html("启动失败 " + date.responseText);
                $(form).bootstrapValidator("disableSubmitButtons", true);
            }
        });
    },

    deleteidSubmit: function (validator, form, submitButton) {
        $.ajax({
            type: "DELETE",
            url:  Datas.dataUrl.deleteidUrl+"?Consumer=" + form.context.Consumer.value,
            success: function (date) {
                alert(date);
                $("#Consumer").val("");
                $("#deletepromptid").addClass("in").html(date);
                $("#homeid").click();
                $("#table").bootstrapTable("refresh", true);
                $(form).bootstrapValidator("disableSubmitButtons", false);
            },
            error: function (date) {
                alert("失败!");
                $("#deletepromptid").addClass("in").html(date);
                $(form).bootstrapValidator("disableSubmitButtons", true);
            }
        });
    },

    bootstrapValidator: function(id, fields, submitHandler) {
        $(id).bootstrapValidator({
            message: "This value is not valid",
            feedbackIcons: {
                valid: "glyphicon glyphicon-ok",
                invalid: "glyphicon glyphicon-remove",
                validating: "glyphicon glyphicon-refresh"
            },
            fields: fields,
            submitHandler: submitHandler
        });
    }
}