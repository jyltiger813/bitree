package com.linzhi.tree.bo;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 10, 2012 4:45:56 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 10, 2012 4:45:56 PM   
 * 修改备注：   
 * @version 
 */
public class ULNode  implements Serializable{
	public ULNode getParent() {
		return parent;
	}
	public LinkedList<ULNode> getSubNodes() {
		return subNodes;
	}
	public IdNode getIdNode() {
		return idNode;
	}
	public void setParent(ULNode parent) {
		this.parent = parent;
	}
	public void setSubNodes(LinkedList<ULNode> subNodes) {
		this.subNodes = subNodes;
	}
	public void setIdNode(IdNode idNode) {
		this.idNode = idNode;
	}
	ULNode parent;
	ULNode right;
	ULNode left;
	public ULNode getLeft() {
		return left;
	}
	public void setLeft(ULNode left) {
		this.left = left;
	}
	public ULNode getRight() {
		return right;
	}
	public void setRight(ULNode right) {
		this.right = right;
	}
	LinkedList<ULNode> subNodes;
    IdNode idNode ;
   
}
