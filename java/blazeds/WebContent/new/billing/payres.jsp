<%@ page contentType="text/html; charset=EUC-KR" %>
<%@page import="com.m.billing.Billing"%>
<%@page import="com.m.common.BooleanAndDescriptionVO"%>
<%@page import="com.common.VbyP"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.m.member.UserInformationVO"%>
<%@page import="com.m.member.SessionManagement"%>
<%@page import="com.m.billing.BillingVO"%>
<%@page import="com.common.util.SLibrary"%>
<%@ page import="lgdacom.XPayClient.XPayClient;"%>

<%
    /*
     * [����������û ������(STEP2-2)]
     *
     * LG�ڷ������� ���� �������� LGD_PAYKEY(����Key)�� ������ ���� ������û.(�Ķ���� ���޽� POST�� ����ϼ���)
     */

    String configPath = "/home/sms/webapps/WebContent/lgdacom";  //LG�ڷ��޿��� ������ ȯ������("/conf/lgdacom.conf,/conf/mall.conf") ��ġ ����.
    
    /*
     *************************************************
     * 1.�������� ��û - BEGIN
     *  (��, ���� �ݾ�üũ�� ���Ͻô� ��� �ݾ�üũ �κ� �ּ��� ���� �Ͻø� �˴ϴ�.)
     *************************************************
     */
    
    String CST_PLATFORM                 = request.getParameter("CST_PLATFORM");
    String CST_MID                      = request.getParameter("CST_MID");
    String LGD_MID                      = ("test".equals(CST_PLATFORM.trim())?"t":"")+CST_MID;
    String LGD_PAYKEY                   = request.getParameter("LGD_PAYKEY");

    //�ش� API�� ����ϱ� ���� WEB-INF/lib/XPayClient.jar �� Classpath �� ����ϼž� �մϴ�. 
    XPayClient xpay = new XPayClient();
   	boolean isInitOK = xpay.Init(configPath, CST_PLATFORM);   	

   	if( !isInitOK ) {
    	//API �ʱ�ȭ ���� ȭ��ó��
        out.println( "������û�� �ʱ�ȭ �ϴµ� �����Ͽ����ϴ�.<br>");
        out.println( "LG�ڷ��޿��� ������ ȯ�������� ���������� ��ġ �Ǿ����� Ȯ���Ͻñ� �ٶ��ϴ�.<br>");        
        out.println( "mall.conf���� Mert ID = Mert Key �� �ݵ�� ��ϵǾ� �־�� �մϴ�.<br><br>");
        out.println( "������ȭ LG�ڷ��� 1544-7772<br>");
        SLibrary.alertScript("������û�� �ʱ�ȭ �ϴµ� �����Ͽ����ϴ�.","");
        return;
   	
   	}else{      
   		try{
   			/*
   	   	     *************************************************
   	   	     * 1.�������� ��û(�������� ������) - END
   	   	     *************************************************
   	   	     */
	    	xpay.Init_TX(LGD_MID);
	    	xpay.Set("LGD_TXNAME", "PaymentByKey");
	    	xpay.Set("LGD_PAYKEY", LGD_PAYKEY);
	    
	    	//�ݾ��� üũ�Ͻñ� ���ϴ� ��� �Ʒ� �ּ��� Ǯ� �̿��Ͻʽÿ�.
	    	//String DB_AMOUNT = "DB�� ���ǿ��� ������ �ݾ�"; //�ݵ�� �������� �Ұ����� ��(DB�� ����)���� �ݾ��� �������ʽÿ�.
	    	//xpay.Set("LGD_AMOUNTCHECKYN", "Y");
	    	//xpay.Set("LGD_AMOUNT", DB_AMOUNT);
	    	
    	}catch(Exception e) {
    		out.println("LG�ڷ��� ���� API�� ����� �� �����ϴ�. ȯ������ ������ Ȯ���� �ֽñ� �ٶ��ϴ�. ");
    		out.println(""+e.getMessage());  
    		SLibrary.alertScript("LG�ڷ��� ���� API�� ����� �� �����ϴ�.","");
    		return;
    	}
   	}

    /*
     * 2. �������� ��û ���ó��
     *
     * ���� ������û ��� ���� �Ķ���ʹ� �����޴����� �����Ͻñ� �ٶ��ϴ�.
     */
     if ( xpay.TX() ) {
         //1)������� ȭ��ó��(����,���� ��� ó���� �Ͻñ� �ٶ��ϴ�.)

         out.println( "������û�� �Ϸ�Ǿ����ϴ�.  <br>");
         out.println( "TX ������û Response_code = " + xpay.m_szResCode + "<br>");
         out.println( "TX ������û Response_msg = " + xpay.m_szResMsg + "<p>");
         
         out.println("�ŷ���ȣ : " + xpay.Response("LGD_TID",0) + "<br>");
         out.println("�������̵� : " + xpay.Response("LGD_MID",0) + "<br>");
         out.println("�����ֹ���ȣ : " + xpay.Response("LGD_OID",0) + "<br>");
         out.println("�����ݾ� : " + xpay.Response("LGD_AMOUNT",0) + "<br>");
         out.println("����ڵ� : " + xpay.Response("LGD_RESPCODE",0) + "<br>");
         out.println("����޼��� : " + xpay.Response("LGD_RESPMSG",0) + "<p>");

         for (int i = 0; i < xpay.ResponseNameCount(); i++)
         {
             out.println(xpay.ResponseName(i) + " = ");
             for (int j = 0; j < xpay.ResponseCount(); j++)
             {
                 out.println("\t" + xpay.Response(xpay.ResponseName(i), j) + "<br>");
             }
         }
         out.println("<p>");
         
         if( "0000".equals( xpay.m_szResCode ) ) {
         	//����������û ��� ���� DBó��
         	//out.println("����������û ��� ���� DBó���Ͻñ� �ٶ��ϴ�.<br>");            	
         	//����������û ��� ���� DBó�� ���н� Rollback ó��
         	boolean isDBOK = true; //DBó�� ���н� false�� ������ �ּ���.
         	
/*##################################################################*/
			String session_id = (String)session.getAttribute("user_id");
         	Connection conn = null;
         	SessionManagement sm = null;
         	String pay_code = "";
         	String pay_name = "";
         	int amount = SLibrary.intValue(xpay.Response("LGD_AMOUNT",0));
         	BooleanAndDescriptionVO badvo = null;
         	
         	try {
         		sm = new SessionManagement();
             	pay_code = SLibrary.IfNull(xpay.Response("LGD_PAYTYPE",0));
             	
             	conn = VbyP.getDB();
             	if (conn == null)throw new Exception("DB���ῡ ���� �Ͽ����ϴ�.");
             	
	         	if (pay_code.equals("SC0010")) pay_name="ī��";
	         	else if (pay_code.equals("SC0030")) pay_name="������ü";
	         	
	         	if (SLibrary.isNull(session_id)) throw new Exception("�α����� �ʿ� �մϴ�.");
	         	if (SLibrary.isNull(pay_code) || SLibrary.isNull(pay_name)) throw new Exception("���� ����� �����ϴ�.");

	         	
				BillingVO bvo = new BillingVO();
				bvo.setUser_id(session_id);
				bvo.setAdmin_id("PG");
				bvo.setAmount( amount );
				bvo.setMemo("");
				bvo.setMethod(pay_name);
				bvo.setOrder_no(xpay.Response("LGD_OID",0));
				bvo.setTid(xpay.Response("LGD_TID",0));
				bvo.setTimestamp(SLibrary.getDateTimeString("yyyyMMddHHmmss"));
				
				badvo = Billing.getInstance().setBilling(conn, bvo);
				
         	}catch(Exception e) {
         		out.println(SLibrary.alertScript(e.getMessage(), ""));
         		isDBOK = false;
         	}finally {
         		if (conn != null) {
         			try { if ( conn != null ) conn.close();
        			}catch(SQLException e) { VbyP.errorLog("payres >> conn.close() Exception!"); }
         		}
         	}
			
			if (isDBOK) {
				out.println(SLibrary.alertScript("������ �Ϸ� �Ǿ����ϴ�.","parent.window.location.reload();"));
			}
/*##################################################################*/
         	
         	
         	
         	if( !isDBOK ) {
         		xpay.Rollback("���� DBó�� ���з� ���Ͽ� Rollback ó�� [TID:" +xpay.Response("LGD_TID",0)+",MID:" + xpay.Response("LGD_MID",0)+",OID:"+xpay.Response("LGD_OID",0)+"]");
         		
                 out.println( "TX Rollback Response_code = " + xpay.Response("LGD_RESPCODE",0) + "<br>");
                 out.println( "TX Rollback Response_msg = " + xpay.Response("LGD_RESPMSG",0) + "<p>");
         		
                 if( "0000".equals( xpay.m_szResCode ) ) {
                 	out.println("�ڵ���Ұ� ���������� �Ϸ� �Ǿ����ϴ�.<br>");
                 }else{
         			out.println("�ڵ���Ұ� ���������� ó������ �ʾҽ��ϴ�.<br>");
                 }
         	}
         	
         }else{
         	//����������û ��� ���� DBó��
         	out.println("����������û ��� ���� DBó���Ͻñ� �ٶ��ϴ�.<br>");            	
         }
     }else {
         //2)API ��û���� ȭ��ó��
         out.println( "������û�� �����Ͽ����ϴ�.  <br>");
         out.println( "TX ������û Response_code = " + xpay.m_szResCode + "<br>");
         out.println( "TX ������û Response_msg = " + xpay.m_szResMsg + "<p>");
         
     	//����������û ��� ���� DBó��
     	out.println("����������û ��� ���� DBó���Ͻñ� �ٶ��ϴ�.<br>");
     	SLibrary.alertScript("������û�� �����Ͽ����ϴ�.","");
     }
%>