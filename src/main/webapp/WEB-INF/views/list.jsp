<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %> <!-- 引入标签库 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>员工列表</title>
<%
	pageContext.setAttribute("APP_PATH",request.getContextPath());
%>
<link  rel="stylesheet" href="${APP_PATH}/static/bootstrap/css/bootstrap.css">
<script type="text/javascript" src="${APP_PATH}/static/js/jquery-2.0.0.min.js"></script>
</head>
<body>
<!--开始的相对路径，赵资源，以当前资源的路径为基准，经常容易处问题 
以/开始的相对路径，以服务器的路径为标准 
	http://localhost:3306/crud
-->
 <div class="containner">
 <!--标题-->
 <div class="row"></div>
 <div class="col-md-12">
 	<h1>SSM_CRUD</h1>
 </div>
 <!--按钮-->
 <div class="row">
 	<div class="col-md-4 col-md-offset-8">
 		<button class="btn btn-primary">新增</button>
 		<button class="btn btn-danger">删除</button>
 	</div>  	
 </div>
 <!--表格数据-->
 <div class="row">
 	<div class="col-md-12">
 		<table class="table table-hover">
 			<tr>
 				<th>#</th>
 				<th>empname</th>
 				<th>gender</th>
 				<th>email</th>
 				<th>deptName</th>
 				<th>操作</th>
 			</tr>
 			<c:forEach items="${PageInfo.list}" var="emp">
 			<tr>
 				
 				<th>${emp.empId}</th>
 				<th>${emp.empName}</th>
 				<th>${emp.gender=="M"?"男":"女"}</th> <!-- M则显示男，否则显示女 -->
 				<th>${emp.email}</th>
 				<th>${emp.department.deptName}</th>
 				<th>
 				<button class="btn btn-primary btn-sm">
				<span class="glyphicon glyph icon-pencil"></span>
 				编辑</button>
 				<button class="btn btn-danger btn-sm">
	             <span class="glyphicon glyphicon-trash"></span>
 				删除</button>
 				</th>
 				</tr>
		   	</c:forEach>
 		</table>
 	</div>
 </div>
		  <!--分页信息-->
		  <div class="row">
		 	<!--分页文字信息-->
		 	<div class="col-md-6">
		 		
		 	当前第${PageInfo.pageNum}页,总${PageInfo.pages}页,总${PageInfo.total }条记录数
		 	</div>
		 	<!--分页条信息-->
		 	<div class="col-md-6">
		 		<ul class="pagination">
		 		<li><a href="${APP_PATH}/emps?pn=1">首页</a></li>
		 		<c:if test="${PageInfo.hasPreviousPage}">
		 		<li><a href="${APP_PATH}/emps?pn=${PageInfo.pageNum-1}">&laquo;</a></li>
		 		</c:if>
		  		 
		  		<c:forEach items="${PageInfo.navigatepageNums}" var="page_Num">
		  		 <c:if test="${page_Num == PageInfo.pageNum }">  <!-- 页码如果等于当前页码则高亮显示 -->
		  		  <li class="active"><a href="#">${page_Num}</a></li>
		  		 </c:if>	    
			   <c:if test ="${page_Num !=PageInfo.pageNum}">
			   <li><a href="${APP_PATH}/emps?pn=${page_Num}">${page_Num}</a></li>  <!-- 显示下一页 -->
			   </c:if>
				 </c:forEach>
				 <c:if test="${PageInfo.hasNextPage}">		 
			    <li><a href="${APP_PATH}/emps?pn=${PageInfo.pageNum+1}">&raquo;</a></li>
			    </c:if>
			    <li><a href="${APP_PATH}/emps?pn=${PageInfo.pages}">末页</a></li>    
				</ul>
		 	</div> 		
		 </div>			 
		 </div> 
<script type="text/javascript" src="${APP_PATH}/static/bootstrap/js/bootstrap.js"></script> 
</body>
</html>