package com.linzhi.tree.bo;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 24, 2012 11:24:31 AM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 24, 2012 11:24:31 AM   
 * 修改备注：   
 * @version 
 */
public class DualTeamTree implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3989099747665147660L;
	DistributorIdTree idTree;
	public DistributorIdTree getIdTree() {
		return idTree;
	}
	public TreeSet <Long> getNonParentNode() {
		return nonParentNode;
	}
	public void setIdTree(DistributorIdTree idTree) {
		this.idTree = idTree;
	}
	public void setNonParentNode(TreeSet <Long> nonParentNode) {
		this.nonParentNode = nonParentNode;
	}
	TreeSet <Long> nonParentNode; //当前父节点不存在的节点集合
	/**
	 * @param node
	 */
	public void addNode(IdNode node) {
		// TODO Auto-generated method stub 
		long dtParentID = node.getBean().getSponsorDt();
		String positionDT = node.getBean().getPositionDt();
		int nodeStatusBefore;//之前节点状态
		int addStatus;// 1.新增 2.内容变化 3.节点位置信息发生变化
		//需要处理一种情况，子节点比父节点先加载，父节点后续加载时，计算totalSubNum需要特别处理
		DTNode dtNode = node.getDtNode();
		long parentNodeID = node.getBean().getSponsorDt();
		DTNode dtParentNode ;//父节点
		boolean hasParentInf = false;
		if(positionDT!=null&&(!("".equals(positionDT)))&&parentNodeID>0)
			hasParentInf =true;
			IdNode parentIdNode = null;
			if(hasParentInf)
			parentIdNode = idTree.getInnerTree().get(parentNodeID);
		if(dtNode==null)
		{   
			 //初始化该节点
			//初始化该节点
			dtNode = new DTNode();
			node.setDtNode(dtNode);
			dtNode.setIdNode(node);
			nodeStatusBefore = 1;//首次添加，且未在nonParentNode集合中存在
			if(parentIdNode!=null)//父节点存在
			{
			
				DTNode replacedNode = null;
				dtParentNode = parentIdNode.dtNode;
				dtNode.setParent(dtParentNode) ;
				if("L".equals(positionDT))
				{
					replacedNode = dtParentNode.left;
					dtParentNode.setLeft(dtNode)  ;
				}else if("R".equals(positionDT))
				{
					replacedNode = dtParentNode.right;
					dtParentNode.setRight(dtNode)  ;
				}
				if(replacedNode!=null)
					replacedNode.setParent(null);
				countSubTotalNums(dtParentNode,dtNode,replacedNode);
			}
			else if (hasParentInf&&(!nonParentNode.contains(node.getId())))
				nonParentNode.add(node.getId());
			
		}else if(nonParentNode.contains(node.getId()))
		{
			if(parentIdNode!=null)//父节点存在
			{
				dtParentNode = parentIdNode.dtNode;
				dtNode.setParent(dtParentNode);
				DTNode replacedNode = null;
				if("L".equals(positionDT))
				{
					replacedNode = dtParentNode.left;
					dtParentNode.setLeft(dtNode) ;
				}else if("R".equals(positionDT))
				{
					replacedNode = dtParentNode.right;
					dtParentNode.setRight( dtNode);
				}
				if(replacedNode!=null)
				replacedNode.setParent(null);
				dtNode.setParent(dtParentNode) ;
				countSubTotalNums(dtParentNode,dtNode,replacedNode);
				nonParentNode.remove(node.getId());
			}
		/*	else 
				nonParentNode.add(node);*/
			nodeStatusBefore = 2;//在nonParentNode集合中存在
		}else if (hasParentInf)// 修改操作，包括仅内容发生变化和节点位置发生变化,节点位置变化又可分为左右位置发生变化和父节点指向变化
		{
			dtParentNode = parentIdNode.dtNode;
			DTNode oldParent = node.getDtNode().getParent();
			DTNode replacedNode = null;
			if(dtParentNode!=oldParent)
			{//节点位置移动
				
			//用于判断需要被设置为空的老节点
				if(oldParent!=null)
				{
				if(oldParent.getLeft()!=null&&(oldParent.getLeft().getIdNode().getId()==node.getId()))
				{
					oldParent.setLeft(null);
				} else if (oldParent.getRight() != null
						&& (oldParent.getRight().getIdNode().getId() == node
								.getId())) {
					oldParent.setRight(null);
				}
				}
				DTNode currentNode = node.getDtNode();
				//从原来位置移走
				countSubTotalNums(oldParent,currentNode,null);
				//新移动到的位置
				if("L".equals(positionDT))
				{
					replacedNode = dtParentNode.left;
					dtParentNode.setLeft( dtNode);
				}else if("R".equals(positionDT))
				{
					replacedNode = dtParentNode.right;
					dtParentNode.setRight(dtNode);
				}
				if(replacedNode!=null)
				replacedNode.setParent(null);
				dtNode.setParent(dtParentNode) ;
				countSubTotalNums(dtParentNode,currentNode,replacedNode);
				
			}else {
				if("L".equals(positionDT))
				{
					if(dtParentNode.getLeft()==dtNode)
					{
						//do noting
					}
					else
					{
						
						//从右边移动到左边
						DTNode nodeLeft = dtParentNode.getLeft();
						if(nodeLeft!=null)
							nodeLeft.setParent(null);
						dtParentNode.setLeft(dtNode);
						dtNode.setParent(dtParentNode) ;
						countSubTotalNums(dtParentNode,dtNode,nodeLeft);
						countSubTotalNums(dtParentNode,null,dtNode);
					}
					
				}else if("R".equals(positionDT))
				{
					if(dtParentNode.getRight()==dtNode)
					{
						//do noting
					}
					else
					{
						//从左边移动到右边
						DTNode noderight = dtParentNode.getRight();
						if(noderight!=null)
							noderight.setParent(null);
						dtParentNode.setRight(dtNode);
						dtNode.setParent(dtParentNode) ;
						countSubTotalNums(dtParentNode,dtNode,noderight);
						countSubTotalNums(dtParentNode,null,dtNode);
					}
				}
			nodeStatusBefore =3;//该节点的位置信息已经明确 
		}
		}
		
	}
	/**
	 * @param dtParentNode
	 * @param dtNode
	 * @param replaceDtNode
	 */
	private void countSubTotalNums(DTNode dtParentNode, DTNode dtNode,
			DTNode replacedNode) {
		if(dtParentNode==null)
			return;
		// TODO now_jyl
		int replaceTotalNum = -1;
		if(replacedNode!=null)
			replaceTotalNum = replacedNode.getIdNode().getBean().getTotalSubNodeNum();
		int addTotalNum = -1;
		if(dtNode!=null)
			addTotalNum = dtNode.getIdNode().getBean().getTotalSubNodeNum();
	    int changeTotalNum = addTotalNum - replaceTotalNum;
	    DTNode currentDtNode = dtParentNode;
	    while(currentDtNode!=null)
	    {
	    	if(currentDtNode.getIdNode().getId()==6)
	    		System.out.println("test");
	    	currentDtNode.getIdNode().getBean().setTotalSubNodeNum(currentDtNode.getIdNode().getBean().getTotalSubNodeNum()+changeTotalNum); 
	    	currentDtNode = currentDtNode.parent;
	    }
	}
	/**
	 * @param node
	 */
	public void addNode(Long node) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		IdNode idnode = idTree.getNode(node);
		addNode(idnode);
	}

}
