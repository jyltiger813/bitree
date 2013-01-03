package com.linzhi.tree.bo;

import java.sql.Timestamp;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;
import com.linzhi.tree.constant.TreeConstant;

/**
 * 类描述：   
 * 创建人：jinyongliang
 * 创建时间：Dec 17, 2012 9:02:41 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 17, 2012 9:02:41 PM   
 * 修改备注：   
 * @version 
 */
public class DistributorTreeJsonGenerator {
	static Logger logger = Logger.getLogger(DistributorTreeJsonGenerator.class);

	
	public static String getJsonStr(DistributorTreeDispatcher dt,long nodeID,int subLayer, boolean b)
	{
		if(subLayer>5)
			subLayer = 5;//最多只允许5层
		DistributorIdTree  idTree = dt.getIdTree();
		IdNode  idNode = idTree.getNode(nodeID);
		if(idNode == null)
		{
			logger.info("jsonStr:");
			return "null";
		}
		ULNode ulnode = idNode.getUlnode();
		//遍历ulnode，获取数据
		/*
		 * 方向：向下，向右，向上(从左往右遍历),回到原点，标志结束
		 */
		ULNode currentNode = ulnode;
		StringBuffer sbf = new StringBuffer();
		int lastStepSymbol = 0;//向下:0，向右:1，向上:2
		int currentStepSymbol = 0;//
		boolean isBreak = false;
		int currentLayerDepth  =1;
		 int nodeNum = 0;
		while(true)
		{
		long id = currentNode.getIdNode().getId();
		lastStepSymbol = currentStepSymbol;
		if(isBreak)
			break;
		 if(currentStepSymbol==2&&currentNode==ulnode)//回到原点，直接跳出
		 {
			 isBreak = true;
			 break;
		 }
		//TODO now_jyl增加当前node的json字符串,如果lastStepSymbol是向上(2)就不需要拼接添加字符串
		if(currentStepSymbol!=2)//向上回到之前经过的节点，不需要追加拼接json
		{
		String jsonStr = getOneNodeJson(currentNode);
		sbf.append(jsonStr);
		if(b)//b:true,混合节点树
		{
		DTNode dtnode = currentNode.getIdNode().getDtNode();
		if(dtnode!=null)
		{
		String dtNodeJson = getDtNodeJson(dtnode,2);//默认都改成两级结构
		sbf.append("\",\"dtSubNode\":"+(dtNodeJson==null?"\"\"":dtNodeJson));
		}
		
		}
		else
			sbf.append("\"");

		}
		nodeNum++;
		/*//测试使用
		try{
		logger.info("nodeNum:"+nodeNum);
		logger.info("currentStepSymbol:"+currentStepSymbol);
		logger.info("id:"+id);
		logger.info("left:"+currentNode.getLeft().getIdNode().getId());
		logger.info("right:"+currentNode.getRight().getIdNode().getId());
		logger.info("currentLayerDepth:"+currentLayerDepth);
		logger.info("parentId:"+currentNode.getParent().getIdNode().getId());
		logger.info("directNum:"+currentNode.getParent().getDirectSubNodeNum());
		logger.info("totalNum:"+currentNode.getParent().getTotalSubNodeNum());


		}catch(Exception e){
			logger.error("tree;",e);
		}*/
	
		//begin 三种分支，向下、向右以及向上,优先分别为向下、向右、向上
		 LinkedList<ULNode> subNode = currentNode.getSubNodes();
		// currentLayerDepth++;
		//TODO 向上操作得特殊处理，不能向下，只能向上或向下
		 if(currentStepSymbol == 2)
		 {
			 if(currentLayerDepth ==1 )//第一级直接跳出
				 isBreak = true;
			 else
			 {
			 if(currentNode.right ==null)//不存在右侧节点，向上
			    currentStepSymbol = 2;
			 else
				currentStepSymbol = 1;
			 }
		 }
		 else if(subNode==null||currentLayerDepth>=subLayer)//没有子节点或层级深度达到限制
		 {
			 if(currentLayerDepth ==1 )//第一级直接跳出
				 isBreak = true;
			 else
			 {
			 if(currentNode.right ==null)//不存在右侧节点，向上
			    currentStepSymbol = 2;
			 else
				currentStepSymbol = 1;
			 }
		 }else//继续向下
		 {
			 currentStepSymbol = 0;
			// currentNode =  subNode.getFirst();
		 }
		//end 三种分支，向下、向右以及向上,优先分别为向下、向右、向上
		
		//当前节点变化
		
		 switch(currentStepSymbol)
			{
		 //TODO now_jyl
			case 0://三种分支，向下、向右以及向上
				sbf.append(",\"subnode\":[");
				 currentLayerDepth++;
				 currentNode = currentNode.subNodes.getFirst();
				break;
			case 1:// 向右
				sbf.append("},");
				currentNode = currentNode.getRight();
				break;
			case 2: //向上后续有两种方向，继续向上，以及向右,向上
				if(lastStepSymbol!=2)
				{
				sbf.append("}]");
				}
				else
				{
					if(sbf.substring(sbf.length()-1).equals("]"))
					sbf.append("}]");
					else
					sbf.append("]");

					}	
				currentLayerDepth --;
				currentNode = currentNode.getParent();
				break;
			}
		}
		sbf.append("}");
		logger.info("sbf:"+sbf);
		return sbf.toString();
	}
	/**
	 * @param currentNode
	 * @param i
	 * @return
	 */
	private static String getDtNodeJson(DTNode dtnode, int subLayer) {
		// TODO now_jyl,返回dt_json,左序遍历
	
		if(subLayer>5)
			subLayer = 5;//最多只允许5层
		//String oneNodeBasicJson = getOneNodeDtJson(currentNode);
		int lastStepSymbol = 0;//向下(向左):0，向右:1，向上:2
		int currentStepSymbol = 0;//
		StringBuffer sbf = new StringBuffer();
		boolean isBreak = false;
		if(dtnode.getLeft()==null&&dtnode.getRight()==null)
			return "\"\"";
		DTNode currentNode = dtnode;
		DTNode lastOpNode = dtnode;
		int currentLayerDepth  =1;
		 int nodeNum = 0;
		 if(dtnode.getIdNode().getId()==2)
			 logger.info("dtNode:"+dtnode);
		while(true)
		{
			try{
				if(isBreak)
					break;
		long id = currentNode.getIdNode().getId();
		lastStepSymbol = currentStepSymbol;
		
		boolean lastNodeIsLeftOrCurrentRightIsNull = getCurrentNodePos(lastOpNode)==2||currentNode.getRight()==null;
		 //if(currentStepSymbol==2&&currentNode==dtnode)//回到原点，直接跳出,左向上不能跳出，右向上跳出
		 if((currentStepSymbol==2&&currentNode==dtnode)&&lastNodeIsLeftOrCurrentRightIsNull)//回到原点，直接跳出,左向上不能跳出，右向上跳出,左侧向上且存在右侧节点回到原点不能退出
		 {
			 isBreak = true;
		//	 break;
		 }
		//TODO now_jyl增加当前node的json字符串,如果lastStepSymbol是向上(2)就不需要拼接添加字符串
		if(currentStepSymbol!=2)//向上回到之前经过的节点，不需要追加拼接json
		{
		String jsonStr = getOneNodeDtJson(currentNode);
		sbf.append(jsonStr);

		}
		nodeNum++;
		
		lastOpNode = currentNode;
		//begin 三种分支，向左、向 上向右以及向上,优先分别为向左、向上右、向上
		// currentLayerDepth++;
		//TODO 向上操作得特殊处理，不能向下(向左)，只能向上或向右
		 if(currentStepSymbol == 2)//向上操作
		 {
			 if(currentLayerDepth ==1 &&lastNodeIsLeftOrCurrentRightIsNull)//第一级直接跳出
				 isBreak = true;
			 else
			 {//currentnode存在问题,应该用之前一个节点状态
			 if(lastNodeIsLeftOrCurrentRightIsNull)//不存在右侧节点，向上，如果是右侧节点向上，继续向上
			    currentStepSymbol = 2;
			 else
				currentStepSymbol = 1;
			 }
		 }//非向上，向左或向右
		 else if((currentNode.right ==null&&currentNode.left ==null)||currentLayerDepth>=subLayer)//没有子节点或层级深度达到限制
		 {
			 if(currentLayerDepth ==1 )//第一级直接跳出
				 isBreak = true;
			 else
			    currentStepSymbol = 2;//向上
		 }
		 else if(currentNode.getLeft()!=null)//继续向下,优先左向下
		 {
			 currentStepSymbol = 0;
			// currentNode =  subNode.getFirst();
		 }
		 else if(currentNode.getRight()!=null)//继续向下
		 {
			 currentStepSymbol = 1;
			// currentNode =  subNode.getFirst();
		 }
		//end 三种分支，向下、向右以及向上,优先分别为向下、向右、向上
		
		//当前节点变化
		
		 switch(currentStepSymbol)
			{
		 //TODO now_jyl
			case 0://三种分支，向左、向右以及向上
				sbf.append(",\"leftNode\":");
				 currentLayerDepth++;
				 currentNode = currentNode.getLeft();
				break;
			case 1:// 向右
				sbf.append(",\"rightNode\":");
				 currentLayerDepth++;
				currentNode = currentNode.getRight();
				break;
			case 2: //向上后续有两种方向，向左且有节点向上，以及向右,向上
				/*if(lastStepSymbol!=2)
				{
				sbf.append("}}");
				}
				else
				{*/
					
					sbf.append("}");

				//	}	
				currentLayerDepth --;
				currentNode = currentNode.getParent();
				break;
			}
		 if	(currentNode==null)
			 logger.info("currentNode");

			}catch(Exception e)
			{
				//e.printStackTrace();
				logger.error(e);
				logger.error("currentNodeisnull"+currentNode);
				logger.error("currentNodeisnull"+currentNode.getIdNode());

				return null;
			}
		}
	//	sbf.append("}");
		logger.info("sbf:"+sbf);
		//前序遍历
		return sbf.toString();
	}
	/**
	 * @param currentNode
	 * @return 1：左节点 2:右节点
	 */
	private static int getCurrentNodePos(DTNode currentNode) {
		// TODO Auto-generated method stub
		DTNode parentNode = currentNode.getParent();
		if(parentNode!=null)
		{
			if(parentNode.getLeft()==currentNode)
			{
				return 1;
			}
			else if(parentNode.getRight()==currentNode)
			{
				return 2;
			}
			
		}
		return -1;
	}
	/**
	 * @param currentNode
	 * @return
	 */
	private static String getOneNodeDtJson(DTNode currentNode) {
		StringBuffer sbf = new StringBuffer();
		sbf.append("{");
		//String jsonStr = "{";
		TreeBaseInfoBeanImp tb = currentNode.getIdNode().getBean();
		sbf.append("\"id\":\""+tb.getId());
		sbf.append("\",\"lifetimerank\":\""+tb.getLifetimeRank());
		sbf.append("\",\"firstname\":\"");
		sbf.append(tb.getFirstname()==null?"":tb.getFirstname());
		sbf.append("\",\"lastname\":\"");
		sbf.append(tb.getLastname()==null?"":tb.getLastname());
		sbf.append("\",\"rolecode\":\"");
		sbf.append(tb.getRoleCode()==null?"":tb.getRoleCode());
		sbf.append("\",\"sponsorul\":\""+tb.getSponsorUl());
		sbf.append("\",\"sponsordt\":\""+tb.getSponsorDt());
		sbf.append("\",\"positiondt\":\"");
		sbf.append(tb.getPositionDt()==null?"":tb.getPositionDt());
		sbf.append("\",\"updatedat\":\""+(tb.getUpdatedAt()==null?"\"\"":tb.getUpdatedAt()));
		sbf.append("\",\"totalsubdtnodes\":\""+tb.getTotalSubNodeNum()+"\"");
		return sbf.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		DistributorTreeDispatcher dispatcher = new DistributorTreeDispatcher();
		dispatcher.setLoadFromDb(true);
		dispatcher = dispatcher.init();
		dispatcher.initTransientData();
		dispatcher.setLoadFromDb(true);
	//	context.setAttribute(TreeConstant.DISTRIBUTORTREE_NAME, dispatcher);
		logger.info("put2context");// init
		dispatcher.setBreak(false);//通知其他线程结束
	//	dispatcher.loadDataFromDBInterval();
		DistributorTreeJsonGenerator generator = new DistributorTreeJsonGenerator();
	
		//logger.info("jsonStr:"+jsonStr);
		
	}

	public static String getOneNodeJson(ULNode ulnode)
	{
	//10000410697
	//	107517001
		StringBuffer sbf = new StringBuffer();
		sbf.append("{");
		//String jsonStr = "{";
		TreeBaseInfoBeanImp tb = ulnode.getIdNode().getBean();
		sbf.append("\"id\":\""+tb.getId());
		sbf.append("\",\"lifetimerank\":\""+tb.getLifetimeRank());
		sbf.append("\",\"firstname\":\"");
		sbf.append(tb.getFirstname()==null?"":tb.getFirstname());
		sbf.append("\",\"lastname\":\"");
		sbf.append(tb.getLastname()==null?"":tb.getLastname());
		sbf.append("\",\"rolecode\":\"");
		sbf.append(tb.getRoleCode()==null?"":tb.getRoleCode());
		sbf.append("\",\"sponsorul\":\""+tb.getSponsorUl());
		sbf.append("\",\"sponsordt\":\""+tb.getSponsorDt());
		sbf.append("\",\"positiondt\":\"");
		sbf.append(tb.getPositionDt()==null?"":tb.getPositionDt());
		sbf.append("\",\"updatedat\":\""+(tb.getUpdatedAt()==null?"\"\"":tb.getUpdatedAt()));
		/*sbf.append("\",\"directsubnodenum\":\""+tb.getDirectSubNodeNum());
		sbf.append("\",\"totalsubnodenum\":\""+tb.getTotalSubNodeNum());
		sbf.append("\",\"childleft\":\""+tb.getChildIdDtLeft());
		sbf.append("\",\"childright\":\""+tb.getChildIdDtRight()+"\"");*/
		return sbf.toString();
	}
	/**
	 * @param dispatcher
	 * @param node
	 * @param layerdepthint
	 * @return
	 */
	public static String getDtJsonStr(DistributorTreeDispatcher dispatcher,
			long node, int subLayer) {
		// TODO Auto-generated method stub
		if(subLayer>5)
			subLayer = 5;//最多只允许5层
		DistributorIdTree  idTree = dispatcher.getIdTree();
		IdNode  idNode = idTree.getNode(node);
		DTNode dtNode = idNode.getDtNode();
		return getDtNodeJson(dtNode,subLayer);
	}
	
	
}
