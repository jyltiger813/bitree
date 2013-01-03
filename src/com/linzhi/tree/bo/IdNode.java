package com.linzhi.tree.bo;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 10, 2012 4:45:26 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 10, 2012 4:45:26 PM   
 * 修改备注：   
 * @version 
 */
public class IdNode  implements Serializable,java.lang.Comparable{
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (int) (this.id ^ (this.id>>>32)); 
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.id == ((IdNode)obj).id;
	}
	public IdNode getNodeLeft() {
		return nodeLeft;
	}
	public IdNode getNodeRight() {
		return nodeRight;
	}
	public long getId() {
		return id;
	}
	public ULNode getUlnode() {
		return ulnode;
	}
	public TreeBaseInfoBeanImp getBean() {
		return bean;
	}
	public void setNodeLeft(IdNode nodeLeft) {
		this.nodeLeft = nodeLeft;
	}
	public void setNodeRight(IdNode nodeRight) {
		this.nodeRight = nodeRight;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setUlnode(ULNode ulnode) {
		this.ulnode = ulnode;
	}
	public void setBean(TreeBaseInfoBeanImp bean) {
		this.bean = bean;
	}
	IdNode nodeLeft;
	IdNode nodeRight;
	DTNode dtNode ;
	public DTNode getDtNode() {
		return dtNode;
	}
	public void setDtNode(DTNode dtNode) {
		this.dtNode = dtNode;
	}
	long id;
	ULNode ulnode;
	TreeBaseInfoBeanImp bean;
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.id-((IdNode)o).getId()>0?1:-1;
	}
	public static  void main(String args[])
	{
		/*TreeSet <IdNode> nonDtParentNode = new TreeSet <IdNode>();
		IdNode id1 = new IdNode();
		id1.setId(1l);
		id1.setNodeLeft(null);
		nonDtParentNode.add(id1);
		IdNode id2 = new IdNode();
		id2.setId(1l);
		nonDtParentNode.add(id1);
		System.out.println("set_sieze:"+nonDtParentNode.size());

		System.out.println(id1.equals(id2));

		System.out.println(nonDtParentNode.contains(id1));*/
		
		TreeSet <Long> nonDtParentNode = new TreeSet <Long>();
		nonDtParentNode.add(1l);
		IdNode id2 = new IdNode();
		id2.setId(1l);
		nonDtParentNode.add(1l);
		nonDtParentNode.add(2l);
		System.out.println("set_sieze:"+nonDtParentNode.contains(3l));

		System.out.println("set_sieze:"+nonDtParentNode.size());

		/*TreeMap <Long,IdNode> innerTree = new TreeMap <Long,IdNode> ();
		IdNode id1 = new IdNode();
		id1.setId(1l);
		id1.setNodeLeft(null);
		IdNode id2 = new IdNode();
		id2.setId(1l);
		innerTree.put(1l, id1);
		innerTree.put(1l, id2);
		System.out.println("size:"+innerTree.size());*/

	}
}