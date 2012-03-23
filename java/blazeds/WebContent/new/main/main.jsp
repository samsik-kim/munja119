<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.m.home.Home"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.m.member.SessionManagement"%>
<%@page import="com.m.member.UserInformationVO"%>
<%@page import="com.common.VbyP"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.common.util.SLibrary"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

	String user_id = SLibrary.IfNull((String)session.getAttribute("user_id"));
	Connection conn = null;
	UserInformationVO vo = null;
	SessionManagement ses = null;
	Home home = null;
	String[] arrEmt = null;
	String[] arrCate = null;
	String gubun = SLibrary.IfNull( VbyP.getGET(request.getParameter("gubun")) );
	String cate = SLibrary.IfNull( VbyP.getGET(request.getParameter("cate")) );
	ArrayList<HashMap<String, String>> notihm = null;
	
	try {
		conn = VbyP.getDB();
		
		if ( SLibrary.isNull(gubun) ) gubun = "업종별문자";
		home = Home.getInstance();
		arrEmt = home.getMainEmt(conn, gubun, "%"+cate+"%", 0, 15);
		arrCate = home.getMainCate(conn, gubun);
		
		
		notihm = home.getNotices(conn);
		
		ses = new SessionManagement();
		if ( !SLibrary.IfNull( (String)session.getAttribute("user_id") ).equals("") )
			vo = ses.getUserInformation(conn, SLibrary.IfNull( (String)session.getAttribute("user_id") ));
	}catch (Exception e) {}
	finally {
		
		try {
			if ( conn != null )	conn.close();
		}catch(SQLException e) {
			VbyP.errorLog("getUserInformation >> conn.close() Exception!"); 
		}
		conn = null;
	}
	
%>
<script type="text/javascript">

	function logincheck() {
		var f = document.loginForm;
		if (!f.user_id.value) {
			alert("아이디를 입력하세요.");
			return;
		}else if (!f.user_pw.value) {
			alert("비밀번호를 입력하세요.");
			return;
		}else {
			f.submit();
		}
	}

</script>
<div id="main"><!--main Start-->
        <ul class="introduce"><!--소개-->
            <li class="intro1 ti">업계최저가격 10.7</li>
            <li class="intro2 ti">최대50만건 일괄발송</li>
            <li class="intro3 ti">장문문자발송가능</li>
            <li class="intro4 ti">이젠 스마트폰이다</li>
        </ul>
        
		<div id="flashContent" style="display:none;border:1px solid red;"></div>
		<script type="text/javascript" src="flexlib/swfobject.js"></script>
		<script type="text/javascript" src="main/main.js"></script>

        <fieldset id="emoticon">
            <ul class="title">
                <li class="<%=(gubun.equals("업종별문자"))?"businessover":"business" %>" onclick="window.location.href='?gubun=업종별문자'">업종별문자</li>
                <li class="<%=(gubun.equals("테마문자"))?"themaover":"thema" %>" onclick="window.location.href='?gubun=테마문자'">테마별문자</li>
<!--                 <li class="popular" onclick="window.location.href='?gubun=업종별문자'">인기문자</li> -->
<!--                 <li class="poto">포토문자</li> -->
                <li class="more" onclick="window.location.href='?content=normal'">더보기</li>
            </ul>
            <div class="middle">
                <div class="subTitle"><%
                
            	if (arrCate != null) {
            		int catCnt = arrCate.length;
            		for (int c = 0; c < catCnt; c++) {
            	%>
                	<a href="?gubun=<%=gubun %>&cate=<%=arrCate[c] %>" class="<%=(arrCate[c].equals(cate))?"de":""%>"><%=arrCate[c] %></a>&nbsp;&nbsp;&nbsp;&nbsp;
                <%
                	}
                }
                %>
                </div>
                <div class="emtibox">
                    <textarea class="emti" readonly ><%=arrEmt[0] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[1] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[2] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[3] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[4] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[5] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[6] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[7] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[8] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[9] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[10] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[11] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[12] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[13] %></textarea>
                    <textarea class="emti" readonly><%=arrEmt[14] %></textarea>
                </div>
            </div>
        </fieldset>

        <a href="javascript:return false;" class="potomore" onclick="window.location.href='?content=photo'">더보기</a>
        <fieldset id="poto">
            <legend>인기포토문자</legend>
            <div class="potoBox">
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" />
                <img src="images/pro_ex.jpg" class="potoimg" alt="인기" style="margin-right:0px;" />
            </div>
            
        </fieldset>


        <fieldset id="noti">
            <legend>공지사항</legend>
            <a href="" class="more">more</a>
            <%
            	if (notihm != null) {
            		int size = notihm.size();
            		HashMap<String, String> hm = null;
            		for (int i = 0; i < size; i++) {
            			hm = notihm.get(i);
            			%>
            			<div class="content"><a href="?content=notic" class="title"><%=SLibrary.IfNull(hm, "title") %></a><span class="notiDate"></span></div>
            			<%
            		}
            	}
            %>
        </fieldset>

        <a href="" class="bank">입금계좌</a>
        <a href="" class="product">상품소개</a>
        <div id="etc">
            <a href="" class="tax">세금계산서신청</a>
            <a href="" class="card">신용카드영수증출력</a>
            <a href="" class="faq">자주하는 질문</a>
            <a href="" class="mantoman">일대일문의</a>
        </div>
    </div><!--main End-->
