package com.linzhi.tree.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import util.codegenerate.srbaseclass.AbstractBaseDAO;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;

public abstract class TreeBaseInfoDaoAbstract extends AbstractBaseDAO 

{
public void save(TreeBaseInfoBeanImp bean,Connection con)
{String sqlStr = "insert into linzhi.ul_tree_temp(id,lifetime_rank,login,firstname,lastname,role_code,sponsor_ul,sponsor_dt,position_dt,updated_at,child_id_dt_left,child_id_dt_right)values(?,?,?,?,?,?,?,?,?,?,?,?)";
ArrayList<Object> values = new ArrayList<Object> ();
Object objId = bean.getId();
  values.add(objId);
Object objLifetimeRank = bean.getLifetimeRank();
  values.add(objLifetimeRank);
Object objLogin = bean.getLogin();
  values.add(objLogin);
Object objFirstname = bean.getFirstname();
  values.add(objFirstname);
Object objLastname = bean.getLastname();
  values.add(objLastname);
Object objRoleCode = bean.getRoleCode();
  values.add(objRoleCode);
Object objSponsorUl = bean.getSponsorUl();
  values.add(objSponsorUl);
Object objSponsorDt = bean.getSponsorDt();
  values.add(objSponsorDt);
Object objPositionDt = bean.getPositionDt();
  values.add(objPositionDt);
Object objUpdatedAt = bean.getUpdatedAt();
  values.add(objUpdatedAt);
Object objChildIdDtLeft = bean.getChildIdDtLeft();
  values.add(objChildIdDtLeft);
Object objChildIdDtRight = bean.getChildIdDtRight();
  values.add(objChildIdDtRight);

try {
	excutePreparedParams( sqlStr,  values, con);
} catch (SQLException e) {
e.printStackTrace();
}finally{
if(con!=null)
try {
	con.close();
} catch (SQLException e) {
e.printStackTrace();
}
}}



public void saveBeans(ArrayList<TreeBaseInfoBeanImp> beans,Connection con)
{String sqlStrs = "";
for(TreeBaseInfoBeanImp bean:beans)
{
Object objId = bean.getId();
String IdvalueStr = convertObj2string(objId); 
Object objLifetimeRank = bean.getLifetimeRank();
String LifetimeRankvalueStr = convertObj2string(objLifetimeRank); 
Object objLogin = bean.getLogin();
String LoginvalueStr = convertObj2string(objLogin); 
Object objFirstname = bean.getFirstname();
String FirstnamevalueStr = convertObj2string(objFirstname); 
Object objLastname = bean.getLastname();
String LastnamevalueStr = convertObj2string(objLastname); 
Object objRoleCode = bean.getRoleCode();
String RoleCodevalueStr = convertObj2string(objRoleCode); 
Object objSponsorUl = bean.getSponsorUl();
String SponsorUlvalueStr = convertObj2string(objSponsorUl); 
Object objSponsorDt = bean.getSponsorDt();
String SponsorDtvalueStr = convertObj2string(objSponsorDt); 
Object objPositionDt = bean.getPositionDt();
String PositionDtvalueStr = convertObj2string(objPositionDt); 
Object objUpdatedAt = bean.getUpdatedAt();
String UpdatedAtvalueStr = convertObj2string(objUpdatedAt); 
Object objChildIdDtLeft = bean.getChildIdDtLeft();
String ChildIdDtLeftvalueStr = convertObj2string(objChildIdDtLeft); 
Object objChildIdDtRight = bean.getChildIdDtRight();
String ChildIdDtRightvalueStr = convertObj2string(objChildIdDtRight); 
if("".equals(sqlStrs))
sqlStrs = "insert into linzhi.ul_tree_temp(id,lifetime_rank,login,firstname,lastname,role_code,sponsor_ul,sponsor_dt,position_dt,updated_at,child_id_dt_left,child_id_dt_right)values("+IdvalueStr+","+LifetimeRankvalueStr+","+LoginvalueStr+","+FirstnamevalueStr+","+LastnamevalueStr+","+RoleCodevalueStr+","+SponsorUlvalueStr+","+SponsorDtvalueStr+","+PositionDtvalueStr+","+UpdatedAtvalueStr+","+ChildIdDtLeftvalueStr+","+ChildIdDtRightvalueStr+")";
else

sqlStrs = sqlStrs+",("+IdvalueStr+","+LifetimeRankvalueStr+","+LoginvalueStr+","+FirstnamevalueStr+","+LastnamevalueStr+","+RoleCodevalueStr+","+SponsorUlvalueStr+","+SponsorDtvalueStr+","+PositionDtvalueStr+","+UpdatedAtvalueStr+","+ChildIdDtLeftvalueStr+","+ChildIdDtRightvalueStr+")";}
try {
excuteSqlStrs( sqlStrs, con);
} catch (SQLException e) {
e.printStackTrace();
}finally{
if(con!=null)
try {
	con.close();
} catch (SQLException e) {
e.printStackTrace();
}
}}


}

