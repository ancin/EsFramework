<%@ page contentType="text/html;charset=GBK" language="java" %>
<%@include file="/WEB-INF/jsp/common/taglibs.jspf"%>
<es:contentHeader/>
<%@include file="/WEB-INF/jsp/common/import-calendar-css.jspf"%>
<style>

    legend {
        cursor: pointer;
    }
    .fc-button-add {
        margin-right: 10px!important;
    }

    #loading {
        position: absolute;
        top: 5px;
        right: 5px;
    }

    .ui-dialog {
        overflow: visible!important;
    }
    .ui-dialog-content {
        overflow: visible!important;
    }

    #calendar {
        width: 750px;
        margin: 0 auto;
    }
</style>
<div  style="margin: 5px;margin-top: 10px;">
    <div class="row-fluid tool ui-toolbar">
        <div style="padding-left: 10px;">
            <a class="btn btn-link btn-view-info" data-toggle="tooltip" data-placement="bottom" title="����鿴��������/�޸�����">
                <sys:showLoginUsername/>����ӭ����
            </a>
            <span class="muted">|</span>
            &nbsp;
            <span class="muted">
                ����
                <a class="btn btn-link btn-view-message no-padding" data-toggle="tooltip" data-placement="bottom" title="����鿴δ����Ϣ">
                    <span class="badge badge-important">${messageUnreadCount}��</span>
                </a>
                δ����Ϣ
            </span>
        </div>
    </div>
    <br/>

    <fieldset>
        <legend>
            �ҵ�����
     (<span class="badge badge-important" data-toggle="tooltip" data-placement="bottom" title="������죬����${calendarCount}��������������">${calendarCount}��</span>)
            <i class="icon-double-angle-down"></i>
        </legend>

        <div id='calendar'></div>

    </fieldset>

    <br/>
    <br/>
    <br/>

</div>
<es:contentFooter/>
<%@include file="/WEB-INF/jsp/common/import-calendar-js.jspf"%>
<script>
    $(function() {
        $.app.initCommonBtn();
        $("legend").click(function() {
            var next = $(this).next();
            if(next.is(":hidden")) {
                $(this).find("i").removeClass("icon-double-angle-up");
                $(this).find("i").addClass("icon-double-angle-down");
                next.slideDown(300);
            } else {
                next.slideUp(300);
                $(this).find("i").removeClass("icon-double-angle-down");
                $(this).find("i").addClass("icon-double-angle-up");
            }
        });
        $.app.initCalendar();

    })
</script>
