<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%@ page import="javasample12.Diary" %>

<!DOCTYPE html>
<html>
	<!-- includeディレクティブというものをつかって他のhtmlやjspファイルを呼び出すこともできる。 -->
	<!-- jspを呼び出す場合には、呼び出した先のjspに変数を渡して、そこで使っていくこともできる。 -->
	<% request.setAttribute("title", "Diary"); %>
	<%@ include file="/WEB-INF/jsp/head.jsp" %>

<body>
		<header>
			<div class="logo">Diary</div>
		</header>
		<div class="main">
			<div class="index_type_virtualcard_circle">
			</div>
			<form method="post">
				<div class="mt30">
					<label for="content">投稿内容：</label>
					<textarea id="content" class="content" name="content"></textarea>
				</div>
				<div class="button-area">
					<button type="submit" class="btn btn-success mt30 relative float-right">日記を投稿</button>
				</div>
			</form>
			<h1 class="diary-list-title">日記一覧</h1>
			<div>
			<%
			// サーブレットクラスでrequestという変数にセットした値を、getAttributeという書き方で取得することができる。
			// getAttributeだけ取得しただけではデータ型が定まっていないので、キャストしてあげる必要がある。
			// また、Diaryクラスを使う為に、「パッケージ名.クラス名」と書いてimportする。
			List<Diary> list = (List<Diary>)request.getAttribute("diaryList");

			// TimeStamp型のままだミリ秒表示になってしまうので、日付表示するためにSimpleDateFormatを使う。
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss");

			// 拡張for文でまわす
			for(Diary diary : list){
			%>
			<div class="diary-content">
				<div class="post-date">
					<%= sdf.format(diary.getCreated_at()) %>
				</div>
				<p><%= diary.getContent() %></p>
			</div>
			<%
			}
			%>
			</div>
		</div>
	</body>
</html>