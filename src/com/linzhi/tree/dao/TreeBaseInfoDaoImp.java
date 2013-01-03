package com.linzhi.tree.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import util.Log4jLoader;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;
import com.linzhi.tree.constant.TreeConstant;
public  class TreeBaseInfoDaoImp extends TreeBaseInfoDaoAbstract implements Serializable
{
	static Logger logger = Logger.getLogger(TreeBaseInfoDaoImp.class);

	Connection con;
	String loadAllSqlStr = ""
			+ "select d.id,d.lifetime_rank,u.login,ad.firstname,ad.lastname ,r.role_code,"
			+ "d.personal_sponsor_distributor_id sponsor_ul,"
			+ "d.dualteam_sponsor_distributor_id sponsor_dt,"
			+ "d.dualteam_current_position position_dt,d.updated_at from "
			+ "(select d.user_id,d.id, d.lifetime_rank,d.personal_sponsor_distributor_id ,"
			+ "d.dualteam_sponsor_distributor_id ,"
			+ "d.dualteam_current_position ,d.updated_at from distributors d  where d.id > '?1'"
			+ ")d left join users u on  d.user_id = u.id left "
			+ "join addresses ad on u.sold_address_id = ad.id left "
			+ "join roles_users ru on u.id = ru.user_id left join roles r on ru.role_id = r.id "
			+ "left join distributors d1 on d1.dualteam_sponsor_distributor_id = d.id and d1.dualteam_current_position='L' "
			+ "left join distributors d2 on d2.dualteam_sponsor_distributor_id = d.id and d2.dualteam_current_position='R' "
			+ "order by d.id limit '?2' ";
	
/*	String loadAllSqlStr = ""
		+ "select d.id,d.lifetime_rank,u.login,ad.firstname,ad.lastname ,r.role_code,"
		+ "d.personal_sponsor_distributor_id sponsor_ul,"
		+ "d.dualteam_sponsor_distributor_id sponsor_dt,"
		+ "d.dualteam_current_position position_dt,d.updated_at from "
		+ "(select d.user_id,d.id, d.lifetime_rank,d.personal_sponsor_distributor_id ,"
		+ "d.dualteam_sponsor_distributor_id ,"
		+ "d.dualteam_current_position ,d.updated_at from distributors d  where d.id in (10000269812,10000274124,10000282375)"
		+ ")d left join users u on  d.user_id = u.id left "
		+ "join addresses ad on u.sold_address_id = ad.id left "
		+ "join roles_users ru on u.id = ru.user_id left join roles r on ru.role_id = r.id "
		+ "left join distributors d1 on d1.dualteam_sponsor_distributor_id = d.id and d1.dualteam_current_position='L' "
		+ "left join distributors d2 on d2.dualteam_sponsor_distributor_id = d.id and d2.dualteam_current_position='R' "
		+ "order by d.id limit '5' ";*/

	String loadDataIntervalSqlStr = ""
			+ "select d.id,d.lifetime_rank,u.login,ad.firstname,ad.lastname ,r.role_code,"
			+ "d.personal_sponsor_distributor_id sponsor_ul,"
			+ "d.dualteam_sponsor_distributor_id sponsor_dt,"
			+ "d.dualteam_current_position position_dt,d.updated_at from "
			+ "(select d.user_id,d.id, d.lifetime_rank,d.personal_sponsor_distributor_id ,"
			+ "d.dualteam_sponsor_distributor_id ,"
			+ "d.dualteam_current_position ,d.updated_at from distributors d  where d.updated_at > '?1' and d.updated_at <= '?2' "
			+ ")d left join users u on  d.user_id = u.id left "
			+ "join addresses ad on u.sold_address_id = ad.id left "
			+ "join roles_users ru on u.id = ru.user_id left join roles r on ru.role_id = r.id "
			+ "left join distributors d1 on d1.dualteam_sponsor_distributor_id = d.id and d1.dualteam_current_position='L' "
			+ "left join distributors d2 on d2.dualteam_sponsor_distributor_id = d.id and d2.dualteam_current_position='R' "
			+ "order by d.id  ";
	//用于初次加载时使用
	public ArrayList<TreeBaseInfoBeanImp> getNodes(Long beginId,long endId,Connection con)
	{
		
		String querySql = loadAllSqlStr.replaceAll("\\?1", beginId+"").replaceAll("\\?2", endId+"");
		return	getNodesCommon( querySql, con);
		
		
	}
	
	
	/**
	 * @param querySql
	 * @param con2
	 * @return
	 */
	private ArrayList<TreeBaseInfoBeanImp> getNodesCommon(String querySql,
			Connection con2) {
		HashMap<Long,TreeBaseInfoBeanImp> tempMap = new HashMap<Long,TreeBaseInfoBeanImp>();
		ArrayList<TreeBaseInfoBeanImp> result = new ArrayList<TreeBaseInfoBeanImp>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			 stmt = con2.createStatement();
			 rs = stmt.executeQuery(querySql);
			while(rs.next())
			{
				TreeBaseInfoBeanImp bean = new TreeBaseInfoBeanImp();
				bean.setId(rs.getLong(1));
				bean.setLifetimeRank(rs.getInt(2));
				bean.setLogin(rs.getString(3)==null?null:rs.getString(3).trim());//username
				bean.setFirstname(rs.getString(4)==null?null:rs.getString(4).trim());
				bean.setLastname(rs.getString(5)==null?null:rs.getString(5).trim());
				bean.setRoleCode(rs.getString(6)==null?null:rs.getString(6).trim());
				bean.setSponsorUl(rs.getLong(7));
				bean.setSponsorDt(rs.getLong(8));
				bean.setPositionDt(rs.getString(9)==null?null:rs.getString(9).trim());
				bean.setUpdatedAt(rs.getTimestamp(10));
				/*
				 * ,r.role_code,d.personal_sponsor_distributor_id sponsor_ul," +
		"d.dualteam_sponsor_distributor_id sponsor_dt," +
		"d.dualteam_current_position position_dt,d.updated_at 
				 */
				TreeBaseInfoBeanImp existBean =tempMap.get(bean.getId());
				if(existBean!=null)
				{
					existBean.setRoleCode((existBean.getRoleCode()+","+bean.getRoleCode()));
				}
				else
				{
				tempMap.put(bean.getId(), bean);
				result.add(bean);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("bitree",e);
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error("bitree",e);
				}
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.error("bitree",e);
				}
		}
		return result;
	}


	/**
	 * @return
	 */
	public static Connection getDefaultConnectionSingle() {
//		 String user = "rencai_dev";
		//	 String password = "rencai_dev";
		//	 String url = "jdbc:mysql://211.151.137.113:3306/rencai_ad_dev?useUnicode=true&characterEncoding=utf8";
			 String driver = "org.postgresql.Driver";
			 String url = TreeConstant.URL;
			 String user = TreeConstant.USER;
			 String password = TreeConstant.password;
			 Connection con = null;
			 try{
				 Class.forName(driver);
				 con = DriverManager.getConnection(url, user, password);
			 }catch(ClassNotFoundException e){
				 System.out.println("数据库驱动不存在！");
				 System.out.println(e.toString());
			 }catch(SQLException e2)
			 {
				 System.out.println("数据库存在异常！");
				 System.out.println(e2.toString());
			 }
			 
			 return con;
	}
	public static void main(String args[]){
		TreeBaseInfoDaoImp tid = new TreeBaseInfoDaoImp();
		Connection con = tid.getDefaultConnectionSingle();
		ArrayList<TreeBaseInfoBeanImp> result = tid.getNodes(0l, 10000, con);
		System.out.println("result_length:"+result.size());
	}


	/**
	 * @param queryTimeString
	 * @param nextDateTimeStr 
	 * @param defaultConnectionSingle
	 * @return
	 */
	public ArrayList<TreeBaseInfoBeanImp> getNodesIncrement(
			String queryTimeString, String nextDateTimeStr, Connection defaultConnectionSingle) {

		String querySql = loadDataIntervalSqlStr.replaceAll("\\?1", queryTimeString+"").replaceAll("\\?2", nextDateTimeStr+"");
		System.out.println("querySql:"+querySql);
		return	getNodesCommon( querySql, defaultConnectionSingle);
	}


	/**
	 * @param defaultConnectionSingle
	 * @return
	 * @throws SQLException 
	 */
	public Timestamp getMinTimeStamp(Connection defaultConnectionSingle) throws SQLException {
		// TODO Auto-generated method stub
		String sqlStr = "select min(updated_at) from distributors";
		Statement st = defaultConnectionSingle.createStatement();
		ResultSet rs = st.executeQuery(sqlStr);
		rs.next();
		return rs.getTimestamp(1);
	}
	
	}
