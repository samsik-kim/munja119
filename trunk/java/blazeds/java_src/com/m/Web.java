package com.m;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.VbyP;
import com.common.db.PreparedExecuteQueryManager;
import com.common.util.SLibrary;
import com.common.util.StopWatch;
import com.m.M;
import com.m.address.Address;
import com.m.address.AddressVO;
import com.m.billing.Billing;
import com.m.common.AdminSMS;
import com.m.common.BooleanAndDescriptionVO;
import com.m.common.Filtering;
import com.m.common.PointManager;
import com.m.excel.ExcelLoader;
import com.m.excel.ExcelLoaderResultVO;
import com.m.member.Join;
import com.m.member.JoinVO;
import com.m.member.SessionManagement;
import com.m.member.UserInformationVO;
import com.m.mobile.LogVO;
import com.m.mobile.PhoneListVO;
import com.m.mobile.SMS;
import com.m.mobile.SMSClientVO;
import com.m.notic.NoticDAO;
import com.m.notic.NoticVO;
import com.m.sent.SentFactory;
import com.m.sent.SentFactoryAble;
import com.m.sent.SentGroupVO;
import com.m.sent.SentVO;

import flex.messaging.FlexContext;


public class Web extends SessionManagement{
	
	public Web() {}
	
	public String test() {
		System.out.println("BlazeDS!!!");
		return "OK";
	}

	/*###############################
	#	Join						#
	###############################*/
	public BooleanAndDescriptionVO checkID(String user_id) {
		
		BooleanAndDescriptionVO bvo = new BooleanAndDescriptionVO();
		Join join = new Join();
		
		if (join.idDupleCheck(user_id)) {
			bvo.setbResult(false);
			bvo.setstrDescription("�ߺ��� ���̵� �Դϴ�.");
		} else {
			bvo.setbResult(true);
		}
		return bvo;
	}
	public BooleanAndDescriptionVO checkJumin(String jumin) {
		
		BooleanAndDescriptionVO bvo = new BooleanAndDescriptionVO();
		Join join = new Join();
		
		if (join.juminDupleCheck(jumin)) {
			bvo.setbResult(false);
			bvo.setstrDescription("�ߺ��� �ֹε�Ϲ�ȣ �Դϴ�.");
		} else {
			bvo.setbResult(true);
		}
		return bvo;
	}
	public BooleanAndDescriptionVO join(String user_id, String password, String password_re, String name, String jumin, String hp, String returnPhone) {
		
		BooleanAndDescriptionVO bvo = new BooleanAndDescriptionVO();
		Join join = new Join();
		
		JoinVO vo = new JoinVO();
		vo.setUser_id(user_id);
		vo.setPassword(password);
		vo.setName(name);
		vo.setJumin(jumin);
		vo.setHp(hp);
		vo.setReturnPhone(returnPhone);
		
		int rslt = join.insert(vo);
		PointManager.getInstance().initPoint( user_id, 0);
		
		if (rslt < 1) {
			bvo.setbResult(false);
			bvo.setstrDescription("���� �Ͽ����ϴ�.");
		}else {
			bvo.setbResult(true);
		}
		return bvo;
	}
	
	public BooleanAndDescriptionVO modify(String user_id, String password, String password_re, String name, String jumin, String hp, String returnPhone) {
		
		BooleanAndDescriptionVO bvo = new BooleanAndDescriptionVO();
		Join join = new Join();
		
		JoinVO vo = new JoinVO();
		vo.setUser_id(user_id);
		vo.setPassword(password);
		vo.setName(name);
		vo.setJumin(jumin);
		vo.setHp(hp);
		vo.setReturnPhone(returnPhone);
		
		int rslt = join.update(vo);
		//PointManager.getInstance().initPoint( user_id, 0);
		
		if (rslt < 1) {
			bvo.setbResult(false);
			bvo.setstrDescription("���� �Ͽ����ϴ�.");
		}else {
			bvo.setbResult(true);
		}
		return bvo;
	}
	
	
	
	/*###############################
	#	login						#
	###############################*/
	public BooleanAndDescriptionVO login(String user_id, String password) {

		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		
		try {
			
			conn = VbyP.getDB();
			if ( SLibrary.isNull(user_id) ) {
				rvo.setbResult(false);
				rvo.setstrDescription("����� ���̵� �Է��ϼ���.");
			}else if ( SLibrary.isNull(password) ) {
				rvo.setbResult(false);
				rvo.setstrDescription("��й�ȣ�� �Է��ϼ���.");
			}else {
				rvo = super.login(conn, user_id, password);
			}
		}catch (Exception e) {}
		finally {
			
			try {
				if ( conn != null )
					conn.close();
			}catch(SQLException e) {
				VbyP.errorLog("login >> conn.close() Exception!"); 
			}
			
			conn = null;
			
		}
		
		return rvo;
	}
	public BooleanAndDescriptionVO logout_session() {
		
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		String user_id = this.getSession();		
		this.session_logout();		
		if (!this.bSession()) {
			
			VbyP.accessLog(user_id+" >>"+FlexContext.getHttpRequest().getRemoteAddr()+" �α׾ƿ� ����");
			rvo.setbResult(true);
			rvo.setstrDescription("�α� �ƿ� �Ǿ����ϴ�.");
		}
		else {
			VbyP.accessLog(user_id+" >> �α׾ƿ� ����");
			rvo.setbResult(false);
			rvo.setstrDescription("�α� �ƿ� ���� �Դϴ�..");
		}
		
		return rvo;
	}
	
	public UserInformationVO getUserInformation() {
		
		Connection conn = null;
		UserInformationVO vo = null;
		try {
			
			conn = VbyP.getDB();
			if ( !SLibrary.IfNull( super.getSession() ).equals("") )
				vo = super.getUserInformation(conn);
		}catch (Exception e) {}
		finally {
			
			try {
				if ( conn != null )
					conn.close();
			}catch(SQLException e) {
				VbyP.errorLog("getUserInformation >> conn.close() Exception!"); 
			}
			conn = null;
		}
		
		return vo;
	}
	
	private boolean isLogin() {
		
		String user_id = getSession();
		
		if (user_id != null && !user_id.equals("")) 
			return true;
		else
			return false;
	}
	
	/*###############################
	#	mobile						#
	###############################*/

	public static HashMap<String, String> STATE = new HashMap<String, String>();
	
	public static int getState(String user_id) { return SLibrary.parseInt( M.STATE.get(user_id) ); }
	public static void setState(String user_id , int cnt) { M.STATE.put(user_id, Integer.toString(cnt)); }
	public static void removeState(String user_id) { M.STATE.remove(user_id); }
	
	public BooleanAndDescriptionVO sendSMS(String message, ArrayList<PhoneListVO> al, String returnPhone, String reservationDate ) {
		
		Connection conn = null;
		Connection connSMS = null;
		SMS sms = SMS.getInstance();
		String user_id = null;
		UserInformationVO mvo = null;
		int sendCount = 0;
		int year = 0;
		int month = 0;
		boolean bReservation = false;
		LogVO lvo = null;
		ArrayList<SMSClientVO> alClientVO = null;
		int logKey = 0;
		ArrayList<String[]> phoneAndNameArrayList = null;
		String requestIp = null;
		
		
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		
		try {
			
			/*###############################
			#		validity check			#
			###############################*/
			//stopWatch play
			StopWatch sw = new StopWatch();
			sw.play();
			
			if (!isLogin()) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			user_id = getSession();
			requestIp = FlexContext.getHttpRequest().getRemoteAddr();
			
			//�߼�ī��Ʈ �ʱ�ȭ
			M.setState(user_id, 0);
			
			if (al == null)
				throw new Exception("���۸���� ��� �ֽ��ϴ�.");
			
			VbyP.accessLog(user_id+" >> ���� ��û : " + requestIp + " => ["+message+"] ["+al.size()+"] ["+ returnPhone+"] ["+reservationDate+"]");
			
			if ( !SLibrary.isNull(reservationDate.trim()) )
				bReservation = true;
			if ( bReservation && SLibrary.getTime(reservationDate, "yyyy-MM-dd HH:mm:ss") == 0 )
				throw new Exception("�߸��� ������ ���� ��¥ �Դϴ�.");
			
			if ( bReservation ) {
				
				year = SLibrary.parseInt( SLibrary.getDateTimeStringStandard(reservationDate, "yyyy") );
				month = SLibrary.parseInt( SLibrary.getDateTimeStringStandard(reservationDate, "MM") );
				
				if ( year < SLibrary.parseInt( SLibrary.getDateTimeString("yyyy") ) )
					throw new Exception("���ų����� ���� �Ͻ� �� �����ϴ�.");
				
				if (year == SLibrary.parseInt( SLibrary.getDateTimeString("yyyy") ) && month < SLibrary.parseInt( SLibrary.getDateTimeString("MM")) )
					throw new Exception("���ſ��� ���� �Ͻ� �� �����ϴ�.");
			}else {
				
				year = SLibrary.parseInt( SLibrary.getDateTimeString("yyyy") );
				month = SLibrary.parseInt( SLibrary.getDateTimeString("MM") );
				reservationDate = SLibrary.getDateTimeString("yyyy-MM-dd HH:mm:ss");							
			}
				
			if (year == 0 || month == 0)
				throw new Exception("�ش� ����� ���� ���� ���߽��ϴ�.");
			
			conn = VbyP.getDB();
			if (conn == null)
				throw new Exception("DB���ῡ ���� �Ͽ����ϴ�.");
			
			
			mvo = getUserInformation( conn );
			
			connSMS = VbyP.getDB(mvo.getLine());
								
			if (connSMS == null)
				throw new Exception("SMS DB���ῡ ���� �Ͽ����ϴ�.");
			
			/*###############################
			#		Process					#
			###############################*/
			
			
			
			returnPhone = SLibrary.replaceAll(returnPhone, "-", "");
			phoneAndNameArrayList = sms.getPhone(conn, mvo.getUser_id(), al);		
			sendCount = phoneAndNameArrayList.size();
			//message ���๮�� ����
			message = SLibrary.replaceAll(message, "\r", "\r\n");
			
			// sk���� �ϰ�� 80����Ʈ�� ������ ���÷����� ������
			if (mvo.getLine().equals("sk") && SLibrary.getByte(message) > 80) {
				mvo.setLine("sms1");
			}
			
			checkSMSSend( conn, sendCount, mvo, message, requestIp );
			
			/* Send Process */
			//step1
			lvo = sms.getLogVO( mvo, bReservation, message, phoneAndNameArrayList, returnPhone, reservationDate, requestIp);
			logKey = sms.insertSMSLog(conn, lvo, year, month);
			if ( logKey == 0 )
				throw new Exception("���۳��� �αװ� ���� ���� �ʾҽ��ϴ�.");
			VbyP.accessLog(user_id+" >> ���� ��û : �α� ���� ���� ("+logKey+")"+ "��� �ð� : "+sw.getTime());
			
			//step2
			if ( sms.sendPointPut(conn, mvo, sendCount*-1 ) != 1 )
				throw new Exception("�Ǽ� ������ ���� �ʾҽ��ϴ�.");
			VbyP.accessLog(user_id+" >> ���� ��û : �Ǽ� ���� ����" + "��� �ð� : "+sw.getTime());
			
			//step3	
			alClientVO = sms.getSMSClientVO(conn, mvo, bReservation, logKey, message, phoneAndNameArrayList, returnPhone, reservationDate, requestIp);
			VbyP.accessLog(user_id+" >> ���� ��û : getSMSClientVO ����" + "��� �ð� : "+sw.getTime());
			
			//timeout ������ ���� �ݴ´�.
			try { if ( conn != null ) { conn.close(); conn = null; } } catch(Exception e) { VbyP.errorLog("sendSMS >> conn.close() timeout ����"+e.toString());}
			
			int clientResult = 0;
			
			clientResult = sms.insertSMSClient(connSMS, alClientVO, mvo.getLine());
			
			VbyP.accessLog(user_id+" >> ���� ��û : �������̺� ���� ����" + "��� �ð� : "+sw.getTime());
			
			if ( clientResult != sendCount)
				throw new Exception("�������̺� �Է� : "+ Integer.toString(clientResult)+" �߼۵����� : "+ Integer.toString( alClientVO.size() ) );
			else{
				rvo.setbResult(true);
				rvo.setstrDescription(Integer.toString(clientResult)+","+year+","+month+","+logKey+","+mvo.getLine());
			}
			
			//�뷮�߼� ����͸� 
			if ( Integer.parseInt(VbyP.getValue("moniterSendCount")) < sendCount ){
				
				conn = VbyP.getDB();
				AdminSMS asms = AdminSMS.getInstance();
				String tempMessage = ( SLibrary.getByte( message ) > 15 )? SLibrary.cutBytes(message, 20, true, "...") : message ;
				asms.sendAdmin(conn, 
						"[�뷮�߼�]\r\n" + user_id + "\r\n"+Integer.toString( sendCount )+"��\r\n" 
						+ tempMessage  );
			}
				
		}catch (Exception e) {
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			System.out.println(e.toString());
		}
		finally {
			
			try {
				if ( conn != null ) conn.close();
				if ( connSMS != null ) connSMS.close();
			}catch(SQLException e) {
				VbyP.errorLog("sendSMS >> finally conn.close() or connSMS.close() Exception!"+e.toString()); 
			}
			conn = null;
			connSMS = null;
		}
		
		VbyP.accessLog(user_id+" >> ���� ��û ��� : "+rvo.getstrDescription());
		return rvo;
	}
	
	private void checkSMSSend( Connection conn, int sendCount, UserInformationVO mvo, String message, String requestIp ) throws Exception {
		
		//�ִ� �߼۰Ǽ�
		if ( Integer.parseInt(VbyP.getValue("maxSendCount")) < sendCount )
			throw new Exception( VbyP.getValue("maxSendCount")+" �� �̻� �߼� �Ͻ� �� �����ϴ�.");
		
		//Ż��ȸ�� üũ
		if( mvo.getLevaeYN().equals("Y") ){
			logout_session();
			throw new Exception("�߸��� �����Դϴ�.");
		}
		
		if( Integer.parseInt(mvo.getPoint()) < sendCount )
			throw new Exception("�ܿ��Ǽ��� �����մϴ�. ( "+ Integer.toString(sendCount)+" / "+ mvo.getPoint()+" )");
		
		//message ���͸�
		if ( Integer.parseInt(VbyP.getValue("filterMinCount")) <= sendCount  ) {
			
			String filterMessage = null;
			String bGlobal = "";
			filterMessage = Filtering.globalMessageFiltering(message);
			if (filterMessage == null )
				filterMessage = Filtering.messageFiltering(mvo.getUser_id(), message);
			else
				bGlobal = "��ü";
			
			if (filterMessage != null) {
				
				VbyP.accessLog(mvo.getUser_id() +" >> ���� ��û : �������� ("+filterMessage+")");
				AdminSMS asms = AdminSMS.getInstance();
				asms.sendAdmin(conn, 
						"M["+bGlobal+"��������]\r\n" + mvo.getUser_id() + "\r\n" 
						+ filterMessage  );
				throw new Exception("���Լ� ������ �߰� �Ǿ����ϴ�.");
			}
		}
		//ip ���͸�
		if ( Filtering.ipFiltering(mvo.getUser_id(), requestIp) != null ) {
			VbyP.accessLog(mvo.getUser_id() +" >> ���� ��û : IP���� ("+Filtering.ipFiltering(mvo.getUser_id(), requestIp)+")");
			throw new Exception("������ ���� �߼��� ���ѵǾ� �ֽ��ϴ�.");
		}
		
		//�޽��� ����� ������ �ѱ� Ȯ��
		String isMessage = SMS.getInstance().isMessage(message);
		if ( isMessage != null )
			throw new Exception("["+isMessage+"] ���ڰ� ������� ��߳��ϴ�.�����ϼ���.");
		
	}
	
	public BooleanAndDescriptionVO saveReturnPhone(String returnPhone) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		
		String rp = SLibrary.IfNull(returnPhone);
		PreparedExecuteQueryManager pq = null;
		
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			
			VbyP.accessLog(" >> ȸ�Ź�ȣ ���� ��û "+ rp +" , "+ user_id);
			
			pq = new PreparedExecuteQueryManager();
			pq.setPrepared(conn, VbyP.getSQL("updateReturnPhone"));
			
			pq.setString(1, rp);
			pq.setString(2, user_id);
			pq.executeUpdate();

			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			
			conn = null;
		}
		
		return rvo;
	}
	
	/*###############################
	#	address_book				#
	###############################*/
	public String getAddressOfGroup() {
		
		Connection conn = null;
		Address address = null;
		StringBuffer buf = new StringBuffer();
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �׷캰 ����Ʈ ��û "+ user_id);
			buf = address.SelectTreeData(conn, user_id);
			
		}catch (Exception e) { VbyP.errorLogDaily("getAddressOfGroup >>"+e.toString()); }	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getAddressOfGroup >> conn.close() Exception!"); }
			conn = null;
		}
		return buf.toString();
	}
	
	public ArrayList<HashMap<String, String>> getAddress() {
		
		Connection conn = null;
		Address address = null;
		ArrayList<HashMap<String, String>> al = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �ּҺ� ����Ʈ ��û "+ user_id);
			al = address.SelectMember(conn, user_id);
			
		}catch (Exception e) { VbyP.errorLogDaily("getAddress >>"+e.toString()); }	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getAddress >> conn.close() Exception!"); }
			conn = null;
		}
		return al;
	}
	
	public ArrayList<HashMap<String, String>> getAddressSearch(String search) {
		
		Connection conn = null;
		Address address = null;
		ArrayList<HashMap<String, String>> al = null;
		
		String s = SLibrary.IfNull(search);
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �ּҺ� ����Ʈ ��û "+ user_id);
			al = address.SearchMember(conn, user_id,s);
			
		}catch (Exception e) { VbyP.errorLogDaily("getAddress >>"+e.toString()); }	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getAddress >> conn.close() Exception!"); }
			conn = null;
		}
		return al;
	}
	
	public String getAddressAllSend() {
		
		Connection conn = null;
		Address address = null;
		ArrayList<HashMap<String, String>> al = null;
		StringBuffer buf  = new StringBuffer();
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> ��ü �ּ� ����Ʈ ��û "+ user_id);
			al = address.SelectMember(conn, user_id);
			
			int cnt = al.size();
			HashMap<String, String> hm = null;
			
			for (int i = 0; i < cnt; i++) {
				hm = al.get(i);
				buf.append(SLibrary.IfNull(hm, "phone")+"||"+SLibrary.IfNull(hm, "name")+",");
			}
			buf.setLength(buf.length()-1);
			
		}catch (Exception e) { VbyP.errorLogDaily("getAddress >>"+e.toString()); }	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getAddress >> conn.close() Exception!"); }
			conn = null;
		}
		return buf.toString();
	}
	
	public BooleanAndDescriptionVO addGroup(String groupName) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �׷� ���� ��û "+ groupName +" , "+ user_id);
						
			if ( address.checkDuplicationGroup(conn, user_id, groupName) )
				throw new Exception(groupName+" �׷��� ���� �մϴ�.");
			
			if ( address.InsertGroup(conn, user_id, groupName) < 1)
				throw new Exception("�׷� ������ ���� �Ͽ����ϴ�.");
			
			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	public BooleanAndDescriptionVO modifyGroup(String oldGroupName, String groupName) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �׷� ���� ��û "+ oldGroupName + " >> " +groupName +" , "+ user_id);
			
			if ( address.UpdateGroup(conn, oldGroupName, user_id, groupName) < 1)						
				throw new Exception("�׷������ ���� �Ͽ����ϴ�.");
			
			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("modifyGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	
	public BooleanAndDescriptionVO deleteGroup(String groupName) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �׷� ���� ��û "+ groupName +" , "+ user_id);
			
			if ( address.DeleteGroup(conn, groupName, user_id) < 1 )				
				throw new Exception("������ ���� �Ͽ����ϴ�.");
	
			
			rvo.setstrDescription(groupName);
			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	public BooleanAndDescriptionVO addAddress(String groupName, String phone, String name, String memo) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		AddressVO vo = new AddressVO();
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> ��ȭ��ȣ ���� ��û "+ user_id);

			vo.setUser_id(user_id);
			vo.setGrp(AddressVO.ADDRESS_FLAG);
			vo.setGrpName(groupName);
			vo.setPhone(phone);
			vo.setName(name);
			vo.setMemo(memo);
			
			if ( address.InsertMember(conn, vo) < 1)
				throw new Exception("��ȣ ���忡 ���� �Ͽ����ϴ�.");
			
			rvo.setstrDescription(vo.getGrpName());
			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	public BooleanAndDescriptionVO modifyAddress(int modifyAddressIdx, String groupName, String phone, String name, String memo) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		AddressVO vo = new AddressVO();
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> ��ȭ��ȣ ���� ��û  "+ Integer.toString(vo.getIdx()) + user_id);
			
			vo.setIdx(modifyAddressIdx);
			vo.setbModify(true);
			vo.setUser_id(user_id);
			vo.setGrp(AddressVO.ADDRESS_FLAG);
			vo.setGrpName(groupName);
			vo.setPhone(phone);
			vo.setName(name);
			vo.setMemo(memo);
			
			if ( address.UpdateMember(conn, vo.getIdx(), vo) < 1)
				throw new Exception("��ȣ ������ ���� �Ͽ����ϴ�.");
			
			rvo.setstrDescription(vo.getGrpName());

			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	
	public BooleanAndDescriptionVO deleteAddress(int deleteAddressIdx) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> ��ȭ��ȣ ���� ��û "+ Integer.toString(deleteAddressIdx) +" , "+ user_id);
			if ( address.DeleteMember(conn, deleteAddressIdx, user_id) < 1)
				throw new Exception("��ȣ ������ ���� �Ͽ����ϴ�.");

			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	
	/*###############################
	#	billing						#
	################################*/
	public BooleanAndDescriptionVO setCash( String account, String amount, String method, String reqname) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Billing billing = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			billing = Billing.getInstance();
			
			VbyP.accessLog(" >> ������ ���� ��û "+ user_id);
			
			rvo = billing.setCash( conn , user_id, account, amount, method, reqname );
			
			AdminSMS asms = AdminSMS.getInstance();
			String tempMessage = "[�����忹��] "+user_id+" , "+reqname+" , "+account+" , "+amount+" , "+method;
			asms.sendAdmin(conn, tempMessage );
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("setCash >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	
	/*###############################
	#	excel						#
	###############################*/
	public ExcelLoaderResultVO getExcelLoaderData(byte[] bytes, String fileName){
		
		VbyP.accessLog(" >> �����δ� ��û ");
		ExcelLoaderResultVO evo = new ExcelLoaderResultVO();
		String path = VbyP.getValue("excelUploadPath");

		ExcelLoader el = new ExcelLoader();
		String uploadFileName = "";
		evo.setbResult(true);
		
		try {
			uploadFileName = el.uploadExcelFile(bytes, path, fileName);
		}catch(Exception e){
			evo.setbResult(false);
			evo.setstrDescription("���� ������ ���ε� ���� �ʾҽ��ϴ�.");
		}
		
		try {
			evo.setList( el.getExcelData(path, uploadFileName) );
		}catch(IOException ie) {
			System.out.println(ie.toString());
		}catch(Exception e) {
			System.out.println(e.toString());
			evo.setbResult(false);
			evo.setstrDescription("���Ŀ� ��߳� ���� ���� �Դϴ�. ���������� ���� ���Ͽ����ϴ�.");
		}
		finally {		 
			new File(path + uploadFileName).delete();
		}
	    
		return evo;
	}
	public BooleanAndDescriptionVO addAddress(ArrayList<AddressVO> al) {
		
		Connection conn = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		Address address = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �����δ� �ּҷ� ���� ��û "+ user_id);
			
			if ( address.InsertMember( conn , user_id, al ) < 1)
				throw new Exception("��ȣ ���忡 ���� �Ͽ����ϴ�.");
			
			rvo.setbResult(true);
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addGroup >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rvo;
	}
	
	public String[] getAddressGroup() {
		
		Connection conn = null;
		Address address = null;
		String[] arr = null;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			address = Address.getInstance();
			
			VbyP.accessLog(" >> �׷� ����Ʈ ��û "+ user_id);
			arr = address.SelectGroup(conn, user_id);
			
			
		}catch (Exception e) { VbyP.errorLogDaily("getAddressGroup >>"+e.toString()); }	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getAddressGroup >> conn.close() Exception!"); }
			conn = null;
		}
		return arr;
	}
	
	/*###############################
	#	sent						#
	###############################*/
	public List<SentGroupVO> getSentGroupList( String fromDate, String endDate, boolean bReservation) {

		
		Connection conn = null;
		List<SentGroupVO> list = null;
		
		SentFactory sf = null;
		sf = SentFactory.getInstance();
		
		if (isLogin()) {		
		
			try {
				
				conn = VbyP.getDB();
				String user_id = getSession();
				VbyP.accessLog(user_id+" >> ���۳��� �׷� ��û :"+fromDate+"~"+endDate+","+bReservation);
				
				if (user_id != null && !user_id.equals("")) {
					
					list = sf.getSentGroupList(conn, user_id, fromDate, endDate, bReservation);
				}
			}catch (Exception e) {}	finally {			
				try { if ( conn != null ) conn.close();
				}catch(SQLException e) { VbyP.errorLog("getSentGroupList >> conn.close() Exception!"); }
				conn = null;
			}
		}
		
		return list;
	}
	public List<SentVO> getSentList(int groupIndex, String line) {

		
		Connection connSMS = null;
		List<SentVO> list = null;
		
		SentFactoryAble sf = null;
		sf = SentFactory.getInstance();
			
		if (isLogin()) {		
		
			try {
				
				connSMS = VbyP.getDB(line);
				String user_id = getSession();
				VbyP.accessLog(user_id+" >> "+line+" ���۳��� ��û :"+ Integer.toString(groupIndex));
				
				if (user_id != null && !user_id.equals("")) {
					
					list = sf.getSentList(connSMS, user_id, line, Integer.toString(groupIndex) );
				}
			}catch (Exception e) {}	finally {			
				try { if ( connSMS != null ) connSMS.close();
				}catch(SQLException e) { VbyP.errorLog("getSentGroupList >> conn.close() Exception!"); }
				connSMS = null;
			}
		}
		
		return list;
	}
	
	public BooleanAndDescriptionVO deleteSent(int groupIndex, String line) {

		
		Connection conn = null;
		
		SentFactory sf = null;
		sf = SentFactory.getInstance();
		
		BooleanAndDescriptionVO bvo = null;
			
		if (isLogin()) {		
		
			try {
				
				conn = VbyP.getDB();
				String user_id = getSession();
				VbyP.accessLog(user_id+" >> "+line+" ���۳��� ���� ��û :"+ Integer.toString(groupIndex));
				
				if (user_id != null && !user_id.equals("") && groupIndex > 0 && !SLibrary.isNull(line)) {
					
					bvo = sf.deleteSentGroupList( conn, user_id, groupIndex );
				}
			}catch (Exception e) {}	finally {			
				try { if ( conn != null ) conn.close();
				}catch(SQLException e) { VbyP.errorLog("getSentGroupList >> conn.close() Exception!"); }
				conn = null;
			}
		}
		
		return bvo;
	}
	
	public BooleanAndDescriptionVO cancelSent( int idx, String sendLine) {
		
		Connection conn = null;
		Connection connSMS = null;
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		rvo.setbResult(false);
		
		SentFactory sf = null;
		sf = SentFactory.getInstance();
		
		try {	
				String user_id = getSession();
				if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
				VbyP.accessLog(user_id+" >> ���� ��� ��û :"+idx+","+sendLine);			
				
				conn = VbyP.getDB();
				if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");								
					
				UserInformationVO vo = getUserInformation();
				
				connSMS = VbyP.getDB(sendLine);
				if (connSMS == null) throw new Exception("SMS DB������ �Ǿ� ���� �ʽ��ϴ�.("+sendLine+")");
				rvo = sf.cancelSentGroupList(conn, connSMS, vo, idx, sendLine);
				
				
				if (rvo.getbResult()) {
					rvo.setstrDescription("��� �Ǿ����ϴ�.");
					VbyP.accessLog(user_id+" >> ���� ��� ���� :"+idx+","+sendLine);
				}
				else {
					VbyP.accessLog(user_id+" >> ���� ��� ���� :"+rvo.getstrDescription());
				}
			
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { 
				if ( conn != null ) conn.close();
				if (connSMS != null) connSMS.close();
			}catch(SQLException e) { VbyP.errorLog("cancelSentGroupList >> conn.close() Exception!"); }
			conn = null;
			connSMS = null;
		}
		return rvo;
	}
	
	
	public String[] getHomeEmoti() {

		
		Connection conn = null;
		String [] arr = null;
		
		try {
			
			conn = VbyP.getDB();
			VbyP.accessLog(" >>  Home �̸�Ƽ�� ��û ");
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("homeEmoti"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			arr = pq.ExecuteQuery();
			
		}catch (Exception e) {}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getHomeEmoti >> conn.close() Exception!"); }
			conn = null;
		}
		
		return arr;
	}
	
	public String[] getEmotiCate(String gubun, String category, int page) {

		
		Connection conn = null;
		String [] arr = null;
		int count = 8;
		
		int from = 0;
		
		try {
			
			conn = VbyP.getDB();
			
			page += 1;
			from = count * (page -1);
			
			VbyP.accessLog(" >>  �̸�Ƽ�� ��û("+gubun+"/"+category+") "+Integer.toString(from));
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("homeEmotiCate"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, gubun);
			pq.setString(2, "%"+category+"%");
			pq.setInt(3, from);
			pq.setInt(4, count);
			
			arr = pq.ExecuteQuery();
			
		}catch (Exception e) {}	finally {			
			try { 
				if ( conn != null ) 
				conn.close();
			}catch(SQLException e) { VbyP.errorLog("getEmotiCate >> conn.close() Exception!"); }
			conn = null;
		}
		
		return arr;
	}
	
	public ArrayList<HashMap<String, String>> getMymsg(int page) {

		
		Connection conn = null;
		ArrayList<HashMap<String, String>> al = null;
		int count = 8;
		
		int from = 0;
		
		try {
			

			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			
			conn = VbyP.getDB();
			
			page += 1;
			from = count * (page -1);
			
			VbyP.accessLog(" >>  ��Ϲ��� ��û "+Integer.toString(from));
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("select_mymsg"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, user_id);
			pq.setInt(2, from);
			pq.setInt(3, count);
			
			al = pq.ExecuteQueryArrayList();
			
		}catch (Exception e) {}	finally {			
			try { 
				if ( conn != null ) 
				conn.close();
			}catch(SQLException e) { VbyP.errorLog("getMymsg >> conn.close() Exception!"); }
			conn = null;
		}
		
		return al;
	}
	
	public String[] getHomeEmotiCate(String gubun, String category, int page) {

		
		Connection conn = null;
		String [] arr = null;
		int count = 4;
		
		int from = 0;
		
		try {
			
			conn = VbyP.getDB();
			
			page += 1;
			from = count * (page -1);
			
			VbyP.accessLog(" >>  Ȩ �̸�Ƽ�� ��û("+gubun+"/"+category+") "+Integer.toString(from));
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("homeEmotiCate"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, gubun);
			pq.setString(2, "%"+category+"%");
			pq.setInt(3, from);
			pq.setInt(4, count);
			
			arr = pq.ExecuteQuery();
			
		}catch (Exception e) {}	finally {			
			try { 
				if ( conn != null ) 
					conn.close();
			}catch(SQLException e) { VbyP.errorLog("getHomeEmotiCate >> conn.close() Exception!"); }
			conn = null;
		}
		
		return arr;
	}
	
	public String[] getHomeMymsg(int page) {

		
		Connection conn = null;
		String [] arr = null;
		int count = 4;
		
		int from = 0;
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			
			conn = VbyP.getDB();
			
			page += 1;
			from = count * (page -1);
			
			VbyP.accessLog(" >>  Ȩ ��Ϲ��� ��û "+Integer.toString(from));
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("select_mymsg"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, user_id);
			pq.setInt(2, from);
			pq.setInt(3, count);
			
			arr = pq.ExecuteQuery();
			
		}catch (Exception e) {}	finally {			
			try { 
				if ( conn != null ) 
					conn.close();
			}catch(SQLException e) { VbyP.errorLog("getHomeMymsg >> conn.close() Exception!"); }
			conn = null;
		}
		
		return arr;
	}
	
	public String[] getEmoti(int page, String category) {
		

		Connection conn = null;
		String [] arr = null;
		int count = 8;
		
		int from = 0;
		
		try {
			
			conn = VbyP.getDB();
			
			if (page == 0) page = 1;
			from = count * (page -1);
			
			VbyP.accessLog(" >>  �̸�Ƽ�� ��û("+category+") "+Integer.toString(from));
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("emoti"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, category);
			pq.setInt(2, from);
			pq.setInt(3, count);
			
			arr = pq.ExecuteQuery();
			
		}catch (Exception e) {}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("getEmoti >> conn.close() Exception!"); }
			conn = null;
		}
		
		return arr;
	}
	
	
	
	// DS
	public ArrayList<NoticVO> getNotic() {
		
		Connection conn = null;
		ArrayList<NoticVO> rslt = null;
		NoticDAO nd = null;
		
		try {
			
			nd = new NoticDAO();
			conn = VbyP.getDB();
			
			VbyP.accessLog(" >>  �������� ����Ʈ ��û");
			
			rslt = nd.getList(conn);
			
		}catch (Exception e) {}	
		finally {
			try { 
				if ( conn != null ) 
					conn.close();
			}catch(SQLException e) { VbyP.errorLog("getNotic >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rslt;
	}
	
	// FAQ
	public ArrayList<NoticVO> getFaq() {
			
		Connection conn = null;
		ArrayList<NoticVO> rslt = null;
		NoticDAO nd = null;
		
		try {
			
			nd = new NoticDAO();
			conn = VbyP.getDB();
			
			VbyP.accessLog(" >>  FAQ ����Ʈ ��û");
			
			rslt = nd.getListFAQ(conn);
			
		}catch (Exception e) {}	
		finally {
			try { 
				if ( conn != null ) 
					conn.close();
			}catch(SQLException e) { VbyP.errorLog("getFaq >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rslt;
	}
	
	public ArrayList<NoticVO> getNoticMain(int cnt) {
		
		Connection conn = null;
		ArrayList<NoticVO> rslt = null;
		NoticDAO nd = null;
		
		try {
			
			nd = new NoticDAO();
			conn = VbyP.getDB();
			
			VbyP.accessLog(" >>  �������� ����Ʈ ��û(main)");
			
			rslt = nd.getList(conn,cnt);
			
		}catch (Exception e) {}	
		finally {
			try { 
				if ( conn != null ) 
					conn.close();
			}catch(SQLException e) { VbyP.errorLog("getNoticMain >> conn.close() Exception!"); }
			conn = null;
		}
		
		return rslt;
	}
	
	
	
	public int updateCntNotic(int idx) {
		
		Connection conn = null;
		NoticDAO nd = null;
		int rslt = 0;
		
		try {
			
			nd = new NoticDAO();
			conn = VbyP.getDB();
			
			VbyP.accessLog(" >>  �������� ī��Ʈ ���� ��û");
			
			rslt = nd.updateCnt(conn, idx);
			
		}catch (Exception e) {}	
		finally {
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("updateCntNotic >> conn.close() Exception!"); }
		}
		
		return rslt;
	}
	
	/**
	 * ��� ���� ����
	 */
	public BooleanAndDescriptionVO addMymsg(String msg) {
		
		Connection conn = null;
		VbyP.accessLog(getAdminSession()+" >> ��Ϲ��� �߰� "+msg);
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		

		try {
			conn = VbyP.getDB();
			
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("insert_mymsg"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, user_id);
			pq.setString(2, msg);
			pq.executeUpdate();
			
			rvo.setbResult(true);
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("addMymsg >> conn.close() Exception!"); }
		}

		return rvo;
		
	}
	
	/**
	 * ��� ���� ����
	 */
	public BooleanAndDescriptionVO delMymsg(int idx) {
		
		Connection conn = null;
		VbyP.accessLog(getAdminSession()+" >> ��Ϲ��� ���� "+idx);
		BooleanAndDescriptionVO rvo = new BooleanAndDescriptionVO();
		

		try {
			conn = VbyP.getDB();
			
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("delete_mymsg"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, user_id);
			pq.setInt(2, idx);
			pq.executeUpdate();
			
			rvo.setbResult(true);
		}catch (Exception e) {
			
			rvo.setbResult(false);
			rvo.setstrDescription(e.getMessage());
			
		}	finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("delMymsg >> conn.close() Exception!"); }
		}

		return rvo;
		
	}
	
	/*###############################
	#	coupon						#
	###############################*/
	public String setCoupon(String key) {
		
		Connection conn = null;
		String resultString = "";
		
		try {
			String user_id = getSession();
			if (user_id == null || user_id.equals("")) throw new Exception("�α��� �Ǿ� ���� �ʽ��ϴ�.");
			conn = VbyP.getDB();
			if (conn == null) throw new Exception("DB������ �Ǿ� ���� �ʽ��ϴ�.");
			if (SLibrary.isNull(key)) throw new Exception("������ȣ�� �Է��ϼ���.");
			
			VbyP.accessLog(" >> ���� ��û "+ user_id+" : "+ key);
			
			String SQL = VbyP.getSQL( "couponUserSelect" );
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared(conn, SQL);
			pq.setString(1, user_id);
			String usedKey = pq.ExecuteQueryString();
			if (!SLibrary.isNull(usedKey)) {
				VbyP.accessLog(" >> ���� ��û "+ user_id+" : ����� key "+ usedKey);
				throw new Exception("���� id�� ������ ��� �ϼ̽��ϴ�.");
			}
			SQL = VbyP.getSQL( "couponKeySelect" );
			pq.setPrepared(conn, SQL);
			pq.setString(1, key);
			
			String keyCheck = pq.ExecuteQueryString();
			if (SLibrary.isNull(keyCheck)) {
				VbyP.accessLog(" >> ���� ��û "+ user_id+" : ���ǰų� ���� key "+ usedKey);
				throw new Exception("�������� �ʰų� ���� �����Դϴ�.");
			}
			
			SQL = VbyP.getSQL( "couponUse" );
			pq.setPrepared(conn, SQL);
			pq.setString(1, user_id);
			pq.setString(2, key);
			int rslt = pq.executeUpdate();
			
			if (rslt < 1) throw new Exception("���� ��Ͽ� �����Ͽ����ϴ�.");
			
			int addCount = SLibrary.intValue( VbyP.getValue("couponCount") );
			
			UserInformationVO uvo = new SessionManagement().getUserInformation(conn, user_id);
			PointManager pm = PointManager.getInstance();
			rslt = pm.insertUserPoint(conn, uvo, 96, addCount * PointManager.DEFULT_POINT);
			
			if (rslt != 1)
				throw new Exception("�Ǽ� ������ ���� �Ͽ����ϴ�.");
			
			
			
		}catch (Exception e) {
			resultString = e.getMessage();
			VbyP.errorLogDaily("setCoupon >>"+e.toString()); 
		}	
		finally {			
			try { if ( conn != null ) conn.close();
			}catch(SQLException e) { VbyP.errorLog("setCoupon >> conn.close() Exception!"); }
			conn = null;
		}
		return resultString;
	}
	
}
