package com.linzhi.tree.bo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 10, 2012 5:29:35 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 10, 2012 5:29:35 PM   
 * 修改备注：   
 * @version 
 */
public class DistributorULTree  implements Serializable{

	static Logger logger = Logger.getLogger(DistributorULTree.class);

	//ULNode root;
	DistributorIdTree idTree;
	TreeSet <Long> nonParentNode; //当前父节点不存在的节点集合s
	public TreeSet <Long> getNonParentNode() {
		return nonParentNode;
	}

	public void setNonParentNode(TreeSet <Long> nonParentNode) {
		this.nonParentNode = nonParentNode;
	}

	public DistributorIdTree getIdTree() {
		return idTree;
	}

	public void setIdTree(DistributorIdTree idTree) {
		this.idTree = idTree;
	}

	
	public void addNode(IdNode node)
	{
		//TODO now
		if(node.getId()<0)
			logger.info("node"+node);
		long i = node.getBean().getId();
		if(i==1011901l)
    		System.out.println(i);
		int modifyStatus = 1;//1.新增  2.节点信息发生变化 3.节点移动 ,默认为新增
		if(node.getUlnode()!=null)//节点已加载过一次，原无父节点者得特殊处理
		{
			if(node.getUlnode().parent==null||(node.getBean().getSponsorUl()!=node.getUlnode().parent.idNode.getId()))
			{
				modifyStatus = 3;
			}
			else 
				modifyStatus = 2;
		}
		switch(modifyStatus)
		{
		case 3:
			if(node.getUlnode().parent!=null)
			//移动节点时，原节点关系调整
			{
			ULNode nodeLeft = node.getUlnode().getLeft();
			ULNode noderight = node.getUlnode().getRight();
			if(nodeLeft!=null&&noderight!=null)
			{
				nodeLeft.setRight(noderight);
				noderight.setLeft(nodeLeft);
				if(nodeLeft.getIdNode().getId()==noderight.getIdNode().getId())
				{
					while(true)
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							logger.error("tree;",e);
						}
						System.out.println("nodeid:"+nodeLeft.getIdNode().getId());
					}
				}
			}
			else if(nodeLeft!=null)
			{
				nodeLeft.setRight(null);
			}
			else if(noderight!=null)
			{
				noderight.setLeft(null);
			}
			//移动节点时，原节点关系调整
			node.getUlnode().parent.getSubNodes().remove(node.getUlnode());
			}
			long parentId1 = node.getBean().getSponsorUl();
			IdNode parentNode = idTree.getNode(parentId1);
			if(parentNode==null)
			{
				if(!nonParentNode.contains(node.getId()))
				nonParentNode.add(node.getId());
				node.getUlnode().parent = null;
			}
			else
			{
				LinkedList<ULNode> subNodes = parentNode.getUlnode().getSubNodes();
				if(subNodes==null)
				{
					subNodes = new LinkedList<ULNode> ();
					parentNode.getUlnode().setSubNodes(subNodes);
				}
				boolean isDuplicate = false;
			
				if(subNodes.size()>0&&(node.getId()==subNodes.getLast().getIdNode().getId()))
					isDuplicate = true;
				node.getUlnode().parent = parentNode.getUlnode();
				//设置右侧node关系
				ULNode ulNode = node.getUlnode();
				if(subNodes.size()>0&&(!isDuplicate))
				{
					ULNode lastNode = subNodes.getLast();
					lastNode.setRight(ulNode);
					ulNode.setLeft(lastNode);
				}
				if(!isDuplicate)
					 subNodes.add(node.getUlnode());//转移节点
				
				if(nonParentNode.contains(node.getId()))
					nonParentNode.remove(node.getId());
				//重新计算节点数
				countNodeNums(parentNode.getUlnode(),ulNode);
			}
			break;
		case 2://不需要做任何变动
			break;
		case 1://计算直接子节点数及下面节点所有子节点数
		long parentId = node.bean.getSponsorUl();
		ULNode uNode = new ULNode();
		uNode.setIdNode(node);
		node.setUlnode(uNode);
		IdNode pNode = idTree.getNode(parentId);
		if(pNode==null)
		{
			//放到父节点不存在集合中
			if(!nonParentNode.contains(node.getId()))
			nonParentNode.add(node.getId());
		}
		else
		{
			LinkedList<ULNode> subNodes = pNode.getUlnode().getSubNodes();
			if(subNodes==null)
			{
				subNodes = new LinkedList<ULNode> ();
				pNode.getUlnode().setSubNodes(subNodes);
			}
			ULNode pUNode = pNode.getUlnode();
			//节点数暂时不处理
			/*ulNode.setDirectSubNodeNum(ulNode.getDirectSubNodeNum()+1);
			int baseNum = 
			//所有祖宗节点的总节点数加1
			ULNode current = ulNode.parent;
			while(current!=null)
			{
				current.setDirectSubNodeNum(current.getDirectSubNodeNum()+1);
				 current = ulNode.parent; 
			}*/
			boolean isDuplicate = false;
			if(subNodes.size()>0&&(node.getId()==subNodes.getLast().getIdNode().getId()))
				isDuplicate = true;
			if(subNodes.size()>0&&!isDuplicate)
			{
				ULNode lastNode = subNodes.getLast();
				lastNode.setRight(uNode);
				uNode.setLeft(lastNode);
			}
			if(!isDuplicate)
			subNodes.add(uNode);
			uNode.setParent(pUNode);
			if(modifyStatus==1)
			countNodeNums(pUNode,uNode);
		}
		}
	}

	/**
	 * @param pUNode
	 * @param uNode 
	 */
	private void countNodeNums(ULNode pUNode, ULNode uNode) {
		// TODO 总数存在问题，先把直接总数搞对
		TreeBaseInfoBeanImp bean = pUNode.getIdNode().getBean();
		bean.setDirectSubNodeNum(pUNode.getSubNodes().size());
		int totalAddNum = uNode.getIdNode().getBean().getTotalSubNodeNum()+1;
	//	bean.setTotalSubNodeNum(uNode.getIdNode().getBean().getTotalSubNodeNum()+totalAddNum);
		ULNode current = pUNode.parent;
		while(current!=null)
		{
			TreeBaseInfoBeanImp currentBean = pUNode.getIdNode().getBean();
			//currentBean.setDirectSubNodeNum(bean.getDirectSubNodeNum()+1);
			//TODO 需要区分为两种情况，一种是
			//currentBean.setTotalSubNodeNum(currentBean.getTotalSubNodeNum()+totalAddNum);
			current = current.getParent();
		}
		
	}

	/**
	 * @param node
	 */
	public void addNode(long node) {
		// TODO Auto-generated method stub
		IdNode idnode = idTree.getNode(node);
		addNode(idnode);
	}
}
