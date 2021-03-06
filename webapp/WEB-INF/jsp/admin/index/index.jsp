<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/jsp/common/taglibs.jspf"%>
<es:contentHeader title="Easy-Scaffold" index="true"/>

    <ul id="tabs-menu" class="dropdown-menu">
        <li><a class="close-current" href="#">关闭</a></li>
        <li><a class="close-others" href="#">关闭其他</a></li>
        <li><a class="close-all" href="#">关闭所有</a></li>
        <li class="divider"></li>
        <li><a class="close-left-all" href="#">关闭当前左边的所有</a></li>
        <li><a class="close-right-all" href="#">关闭当前右边的所有</a></li>
    </ul>

<div class="index-panel">

    <div class="tabs-bar tabs-fix-top">
        <span class="icon-chevron-left" style="display: none;"></span>

        <div class="ul-wrapper">
            <ul>
                <li>
                    <a href="#tabs-0">欢迎使用</a>
                    <span class='menu' role='presentation' style="display:inline-block;width: 14px;height: 14px"></span>
                    <br/>
                    <span class='menu icon-refresh' role='presentation' title='刷新'></span>
                </li>
            </ul>
        </div>
        <span class="icon-chevron-right" style="display: none;"></span>

        <div id="tabs-0" data-index="0" data-url="${ctx}/admin/welcome"></div>

    </div>

 <iframe id="iframe-tabs-0" tabs="true" width="100%" 
  class="ui-layout-center" frameborder="0" scrolling="auto"
  src="${ctx}/admin/welcome" onload='reSizeHeight("iframe-tabs-0");reSizeWidth("iframe-tabs-0");'></iframe>

    <%@include file="userinfo.jsp"%>

    <div class="ui-layout-north index-header">
        <%@include file="header.jsp"%>
    </div>

    <div class="ui-layout-south">
        <%@include file="footer.jsp"%>
    </div>
    <div class="ui-layout-west menu">
        <%@include file="menu.jsp"%>
    </div>
</div>
<es:contentFooter/>
<script type="text/javascript">
    $(function() {
        $.app.initIndex();
    });
    
    function reSizeHeight(iframename) {
        var pTar = document.getElementById(iframename);
        if (pTar) {
            if (pTar.contentDocument && pTar.contentDocument.body.offsetHeight) {
                pTar.height = pTar.contentDocument.body.offsetHeight;
            } //ie
            else if (pTar.Document && pTar.Document.body.scrollHeight) {
                pTar.height = pTar.Document.body.scrollHeight;
            }
        }
    }
    //iframe宽度自适应
    function reSizeWidth(iframename) {
        var pTar = document.getElementById(iframename);
        if (pTar) {
            if (pTar.contentDocument && pTar.contentDocument.body.offsetWidth) {
                pTar.width = pTar.contentDocument.body.offsetWidth;
            } //ie
            else if (pTar.Document && pTar.Document.body.scrollWidth) {
                pTar.width = pTar.Document.body.scrollWidth;
            }
        }
    }
</script>