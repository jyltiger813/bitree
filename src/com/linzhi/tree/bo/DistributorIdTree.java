package com.linzhi.tree.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;

/**
 * 类描述：   二叉平衡树，为了提高查询性能，需要两侧数据比较平衡
 * 创建人：jinyongliang
 * 创建时间：Dec 10, 2012 4:32:54 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 10, 2012 4:32:54 PM   
 * 修改备注：   
 * @version 
 */
public class DistributorIdTree  implements Serializable{
	static Logger logger = Logger.getLogger(DistributorIdTree.class);

	TreeMap <Long,IdNode> innerTree = new TreeMap <Long,IdNode> ();
	HashSet <Long> duplicateNode;
	public HashSet<Long> getDuplicateNode() {
		return duplicateNode;
	}

	public void setDuplicateNode(HashSet<Long> duplicateNode) {
		this.duplicateNode = duplicateNode;
	}

	public TreeMap<Long, IdNode> getInnerTree() {
		return innerTree;
	}

	public void setInnerTree(TreeMap<Long, IdNode> innerTree) {
		this.innerTree = innerTree;
	}

	public void addNode(IdNode node)
	{
		innerTree.put(node.id, node);
	}
	
	public IdNode getNode(long nodeId)
	{
		return innerTree.get(nodeId);
	}
	
	public IdNode addNode(TreeBaseInfoBeanImp bean, Boolean exist) {
		// Object obj = ;
		IdNode node = innerTree.get(bean.getId());
		if (node != null)// 该节点已经存在，为节点或节点移动情况
		// TODO 存在问题,子节点数计算时需特别注意,另外还存在一种情况，节点位置移动
		// 需要区分为三种情况，1.初次关联上 2.移动节点 3.两种角色 4.节点内容发生修改
		{
			if(node.getUlnode()!=null)
			{
			node.setBean(bean);////覆盖原有节点信息
		//	duplicateNode.add(node.id);// 第一次加载时使用，增加加载得去掉该逻辑
			exist = true;
			}
		} else {
			node = new IdNode();
			node.id = bean.getId();
			node.setBean(bean);
			innerTree.put(node.id, node);
		}
		return node;
	}

	/*//增量追加节点
	public IdNode addNodeIncrement(TreeBaseInfoBeanImp bean,Boolean exist)
	{
		IdNode node = new IdNode();
		node.id = bean.getId();
		 node.setBean(bean);
		 Object obj = innerTree.put(node.id, node);
		 if(obj!=null)//该节点已经存在，为节点或节点移动情况
			 //TODO 存在问题,子节点数计算时需特别注意,另外还存在一种情况，节点位置移动
			 //需要区分为三种情况，1.初次关联上  2.移动节点  3.两种角色 4.节点内容发生修改
		 {
			// duplicateNode.add(node.id);//第一次加载时使用，增加加载得去掉该逻辑
			 exist = true;
		 }
		return node;
	}*/
	public static void main(String args[])
	{
		TreeMap <Long,IdNode> innerTree = new TreeMap <Long,IdNode> ();
		IdNode id1 = new IdNode();
		IdNode id2 = new IdNode();
		id2.setId(200l);
		innerTree.put(1l, id1);
		innerTree.put(1l, id2);
		System.out.println(innerTree.size());


	}
}

