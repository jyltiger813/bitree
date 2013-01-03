package com.linzhi.tree.bean;

import java.io.Serializable;
import java.sql.Timestamp;
public abstract class TreeBaseInfoBeanAbstract implements Serializable

{
/**
	 * 
	 */
	private static final long serialVersionUID = -4912218452322573110L;
private  long Id;
private  int LifetimeRank;
private  String Login;
private  String Firstname;
private  String Lastname;
private  String RoleCode;
private  long SponsorUl = -1l;
private  long SponsorDt;
private  String PositionDt;
private  Timestamp UpdatedAt;
private  long ChildIdDtLeft;
private  long ChildIdDtRight;

public   void setId(long Id)
		{
				this.Id = Id;
		}

public  long getId()
		{
				return this.Id;
		}

public   void setLifetimeRank(int LifetimeRank)
		{
				this.LifetimeRank = LifetimeRank;
		}

public  int getLifetimeRank()
		{
				return this.LifetimeRank;
		}

public   void setLogin(String Login)
		{
				this.Login = Login;
		}

public  String getLogin()
		{
				return this.Login;
		}

public   void setFirstname(String Firstname)
		{
				this.Firstname = Firstname;
		}

public  String getFirstname()
		{
				return this.Firstname;
		}

public   void setLastname(String Lastname)
		{
				this.Lastname = Lastname;
		}

public  String getLastname()
		{
				return this.Lastname;
		}

public   void setRoleCode(String RoleCode)
		{
				this.RoleCode = RoleCode;
		}

public  String getRoleCode()
		{
				return this.RoleCode;
		}

public   void setSponsorUl(long SponsorUl)
		{
				this.SponsorUl = SponsorUl;
		}

public  long getSponsorUl()
		{
				return this.SponsorUl;
		}

public   void setSponsorDt(long SponsorDt)
		{
				this.SponsorDt = SponsorDt;
		}

public  long getSponsorDt()
		{
				return this.SponsorDt;
		}

public   void setPositionDt(String PositionDt)
		{
				this.PositionDt = PositionDt;
		}

public  String getPositionDt()
		{
				return this.PositionDt;
		}

public   void setUpdatedAt(Timestamp UpdatedAt)
		{
				this.UpdatedAt = UpdatedAt;
		}

public  Timestamp getUpdatedAt()
		{
				return this.UpdatedAt;
		}

public   void setChildIdDtLeft(long ChildIdDtLeft)
		{
				this.ChildIdDtLeft = ChildIdDtLeft;
		}

public  long getChildIdDtLeft()
		{
				return this.ChildIdDtLeft;
		}

public   void setChildIdDtRight(long ChildIdDtRight)
		{
				this.ChildIdDtRight = ChildIdDtRight;
		}

public  long getChildIdDtRight()
		{
				return this.ChildIdDtRight;
		}

int directSubNodeNum = 0;
public int getDirectSubNodeNum() {
	return directSubNodeNum;
}
public int getTotalSubNodeNum() {
	return totalSubNodeNum;
}
public void setDirectSubNodeNum(int directSubNodeNum) {
	this.directSubNodeNum = directSubNodeNum;
}
public void setTotalSubNodeNum(int totalSubNodeNum) {
	this.totalSubNodeNum = totalSubNodeNum;
}
int totalSubNodeNum = 0;

}

