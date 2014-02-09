<%@page import="java.sql.Connection"%><%@page import="com.common.db.PreparedExecuteQueryManager"%><%@page import="com.common.VbyP"%><%@page import="com.common.util.SLibrary"%><%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%><%
	//T_ID=0401112000&T_TIME=20110128105404&MENU_NAME=0808881130-home&ANI=07079999805&DTMF_CNT=1&DTMF_1=01012345678
	String T_ID="";
	String T_TIME="";
	String MENU_NAME="";
	String ANI="";
	int DTMF_CNT=0;
	String DTMF_1="";
	
	Connection conn = null;
	
	try {
		
		
		T_ID = SLibrary.IfNull(request.getParameter("T_ID"));
		T_TIME = SLibrary.IfNull(request.getParameter("T_TIME"));
		MENU_NAME = SLibrary.IfNull(request.getParameter("MENU_NAME"));
		ANI = SLibrary.IfNull(request.getParameter("ANI"));
		DTMF_CNT = SLibrary.intValue( SLibrary.IfNull(request.getParameter("DTMF_CNT")) );
		DTMF_1 = SLibrary.IfNull(request.getParameter("DTMF_1"));

		VbyP.accessLog("���Űźο�û : "+T_ID+","+T_TIME+","+MENU_NAME+","+ANI+","+Integer.toString( DTMF_CNT )+","+DTMF_1);
		
		if (SLibrary.isNull(T_ID) || SLibrary.isNull(MENU_NAME) || SLibrary.isNull(DTMF_1) )
			throw new Exception("�ʼ� ������ �����ϴ�.");
		
		conn = VbyP.getDB();
		
		if (conn == null) throw new Exception("DB ���ῡ �����Ͽ����ϴ�.");
		
		PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
		pq.setPrepared(conn, "insert into refuse(phone, reg_date) values(?, now())");
		pq.setString(1, DTMF_1);
		
		if ( pq.executeUpdate() <= 0) throw new Exception("DB ���忡 �����Ͽ����ϴ�.");
		
%><form name='outfrm' method='post'>
<input type='hidden' name='T_ID' value='<%=T_ID%>'>
<input type='hidden' name='T_TIME' value='<%=SLibrary.getDateTimeString("yyyyMMddHHmmss")%>'>
<input type='hidden' name='RESULT' value='0'>
<input type='hidden' name='MENU_NAME' value='<%=MENU_NAME%>'>
<input type='hidden' name='ACTION_TYPE' value='3'>
<input type='hidden' name='NEXT_MENU' value=''>
<input type='hidden' name='MENT_FLAG' value='0'>
<input type='hidden' name='MENT_CNT' value='1'>
<input type='hidden' name='MENT_1' value='F_��ϼ���'>
</form><%
	
	
	}catch(Exception e) {
		VbyP.errorLog(e.getMessage());
%><form name='outfrm' method='post'>
<input type='hidden' name='T_ID' value='<%=T_ID%>'>
<input type='hidden' name='T_TIME' value='<%=SLibrary.getDateTimeString("yyyyMMddHHmmss")%>'>
<input type='hidden' name='RESULT' value='0'>
<input type='hidden' name='MENU_NAME' value='<%=MENU_NAME%>'>
<input type='hidden' name='ACTION_TYPE' value='3'>
<input type='hidden' name='NEXT_MENU' value=''>
<input type='hidden' name='MENT_FLAG' value='0'>
<input type='hidden' name='MENT_CNT' value='1'>
<input type='hidden' name='MENT_1' value='F_��Ͻ���'>
</form><%

	}finally {
		if ( conn != null ){
			try{conn.close();}catch(Exception e){}
		}
		//SLibrary.alertScript("", "document.outfrm.submit();");
	}
 %>