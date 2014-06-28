<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.bt.service.Service" %>
<%@ page import="java.util.Locale" %>
<html>
<head>
<meta charset="utf-8">
<link href="css/xistyle.css" rel="stylesheet" type="text/css" media="all" />
<title>用户反馈</title>
<script type="text/javascript">
function validate(){
	var uid = document.getElementById("feedback");
	if(uid.value.length == 0){
		alert('详细描述不能为空');
		return false;
	}
	
	return true;
}
</script>
</head>

<body onsubmit="return validate();">
<%
String game = "english";
Service service = new Service(game, Locale.CHINA);
%>
<form action="ok.jsp" method="post">
         <div class="xiluruk">
              <dl>
                  <dt><%=service.getMessage("title.game")%>问题建议提交</dt>
                  <dd>
                      <span>&nbsp;</span>
                      <select name="category" class="xiwenbenk">
                        <option value="suggest"><%=service.getMessage("select.suggest")%></option>
                        <option value="other"><%=service.getMessage("select.other")%></option>
                      </select>
                  </dd>
                  <dd>
                      <span>联系邮箱</span>
                      <input name="email" type="email" class="xiwenbenk" maxlength="20" placeholder="sample@email.com">
                  </dd>
                  <dd>
                      <span>详细描述</span>
                      <textarea id="feedback" name="feedback" cols="" rows="" class="xiwenbenqy" maxlength="1000" placeholder="请输入详细描述。（１０００字以内）"></textarea>
                  </dd>
                  <dd>
                      <input type="submit" value="提交"  class="querenk">
                      <input type="hidden" name="game" value='<%=game%>'/>
                  </dd>
              </dl>
         </div>
</form>

</body>
</html>
