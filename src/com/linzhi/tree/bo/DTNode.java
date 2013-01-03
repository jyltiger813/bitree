package com.linzhi.tree.bo;

import java.io.Serializable;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 24, 2012 11:25:43 AM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 24, 2012 11:25:43 AM   
 * 修改备注：   
 * @version 
 */
public class DTNode implements Serializable {
	DTNode left ;
	DTNode parent ;
	DTNode right ;
	IdNode idNode ;
	public IdNode getIdNode() {
		return idNode;
	}
	public void setIdNode(IdNode idNode) {
		this.idNode = idNode;
	}
	public DTNode getLeft() {
		return left;
	}
	public DTNode getParent() {
		return parent;
	}
	public DTNode getRight() {
		return right;
	}
	public void setLeft(DTNode left) {
		this.left = left;
	}
	public void setParent(DTNode parent) {
		this.parent = parent;
	}
	public void setRight(DTNode right) {
		this.right = right;
	}

}
