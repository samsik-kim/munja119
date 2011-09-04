package com.m.notic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.common.VbyP;
import com.common.db.PreparedExecuteQueryManager;


public class NoticDAO {

	public ArrayList<NoticVO> getList(Connection conn) {
		
		ArrayList<HashMap<String, String>> al = null;
		
		ArrayList<NoticVO> rslt = null;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticList"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			
			al = pq.ExecuteQueryArrayList();
			
			rslt = changeVO(al);
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public ArrayList<NoticVO> getListFAQ(Connection conn) {
		
		ArrayList<HashMap<String, String>> al = null;
		
		ArrayList<NoticVO> rslt = null;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("faqList"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			
			al = pq.ExecuteQueryArrayList();
			
			rslt = changeVO(al);
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public ArrayList<NoticVO> getList(Connection conn, int cnt) {
		
		ArrayList<HashMap<String, String>> al = null;
		
		ArrayList<NoticVO> rslt = null;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticListLimit"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setInt(1, cnt);
			
			al = pq.ExecuteQueryArrayList();
			
			rslt = changeVO(al);
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public int modify(Connection conn, NoticVO vo) {
		
		
		int rslt = 0;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticUpdate"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, vo.getTitle());
			pq.setString(2, vo.getContent());
			pq.setString(3, vo.getTimeWrite());
			pq.setInt(4, vo.getCnt());
			pq.setString(5, vo.getWriter());
			pq.setInt(6, vo.getIdx());
			
			
			rslt = pq.executeUpdate();
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public int delete(Connection conn, int idx) {
		
		
		int rslt = 0;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticDelete"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setInt(1, idx);
			
			
			rslt = pq.executeUpdate();
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public int insert(Connection conn, NoticVO vo) {
		
		
		int rslt = 0;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticInsert"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setString(1, vo.getTitle());
			pq.setString(2, vo.getContent());
			pq.setString(3, vo.getWriter());
			
			
			rslt = pq.executeUpdate();
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	public int updateCnt(Connection conn, int idx) {
		
		
		int rslt = 0;
		
		try {
			
			
			
			StringBuffer buf = new StringBuffer();
			buf.append(VbyP.getSQL("noticUpdateCnt"));
			PreparedExecuteQueryManager pq = new PreparedExecuteQueryManager();
			pq.setPrepared( conn, buf.toString() );
			pq.setInt(1, idx);
			
			
			rslt = pq.executeUpdate();
			
		}catch (Exception e) {}
		
		
		return rslt;
	}
	
	
	
	
	private ArrayList<NoticVO> changeVO(ArrayList<HashMap<String, String>> al) {
		
		ArrayList<NoticVO> rslt = null;
		int cnt = 0;
		HashMap<String, String> hm = null;
		
		if (al != null) {
			
			cnt = al.size();
			rslt = new ArrayList<NoticVO>();
			
			for (int i = 0; i < cnt; i++) {
				hm = al.get(i);
				rslt.add(new NoticVO(hm));
			}
			
		}
		
		return rslt;
	}
}