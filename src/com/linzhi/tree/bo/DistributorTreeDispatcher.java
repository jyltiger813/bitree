package com.linzhi.tree.bo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import util.date.DateTimeUtil;

import com.linzhi.tree.bean.TreeBaseInfoBeanImp;
import com.linzhi.tree.constant.TreeConstant;
import com.linzhi.tree.dao.TreeBaseInfoDaoImp;

/**
 * 类描述：转发调控类 
 * 创建人：jinyongliang
 * 创建时间：Dec 10, 2012 6:36:26 PM   
 * 修改人：jinyongliang   
 * 修改时间：Dec 10, 2012 6:36:26 PM   
 * 修改备注：   
 * @version 
 */
public class DistributorTreeDispatcher implements Serializable {
	static Logger logger = Logger.getLogger(DistributorTreeDispatcher.class);

	transient Connection con ;
	int loadDbInterval = 1000*10;
	public int getLoadDbInterval() {
		return loadDbInterval;
	}
	public void setLoadDbInterval(int loadDbInterval) {
		this.loadDbInterval = loadDbInterval;
	}

	boolean loadFromDb = true;//默认为true
	public boolean isLoadFromDb() {
		return loadFromDb;
	}
	public void setLoadFromDb(boolean loadFromDb) {
		this.loadFromDb = loadFromDb;
	}

	boolean isLock  = false;
	//如果时间小于当前日期，
	;//轮询数据库周期设置为20秒，时间大于 > beginT < endT(设置为系统当前时间-2秒),
								 //设置一个截止时间以减少表的锁定,不考虑时间差
	DistributorIdTree idTree = new DistributorIdTree();
	Timestamp lastQueryTime;//上次的查询时间
	DistributorInternalLoadThread loadTread;
	//Calendar lastQueryBak; //上次查询时间备份，用于有异常时恢复
	public DistributorIdTree getIdTree() {
		return idTree;
	}
	public void setIdTree(DistributorIdTree idTree) {
		this.idTree = idTree;
	}

	DistributorULTree ulTree = new DistributorULTree();
	DualTeamTree dtTree = new DualTeamTree();
	HashSet <Long> duplicateNode = new HashSet <Long>();//保存存在重复的数据
	TreeSet <Long> nonParentNode = new TreeSet <Long>();
	TreeSet <Long> nonDtParentNode = new TreeSet <Long>();
	//long maxNodeID =  5307040110697l;//
	transient TreeBaseInfoDaoImp dao = new TreeBaseInfoDaoImp();
	public void initTransientData(){
		dao = new TreeBaseInfoDaoImp();
	} 
	public DistributorTreeDispatcher(){
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	TreeMap<Integer,String> test = new TreeMap<Integer,String>();
		test.put(1, "aaa");
		test.put(2, "bbb");
		logger.info("size:"+test.size());

		if(3<2)
			return;*/
		DistributorTreeDispatcher dispatcher = new DistributorTreeDispatcher();
		dispatcher.loadDataFromDBInterval();
		//dispatcher.loadDataFromDB();
		/*try {
			Thread.sleep(1000*60*3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	/*	try {
			DistributorTreeDispatcher tree = dispatcher.loadDicFromDisk("dictatortree");
	        logger.info("size:"+tree.getIdTree().getInnerTree().size());
	        logger.info("size1:"+tree.duplicateNode.size());
	        for(long id : tree.duplicateNode)
			{
				logger.info("ID:"+id);

			}
	        nonParentNodeArr[] tree.nonParentNode.toArray();
	        for(int loopNum =0; loopNum<tree.nonParentNode.size();loopNum++)
			{
				int initNum = tree.nonParentNode.size();
				IdNode nodeId = tree.nonParentNode.get(loopNum);
				tree.nonParentNode.remove(nodeId);
				tree.ulTree.addNode(node);
				if(tree.nonParentNode.size()<initNum)
					loopNum --;
			}
	        for(IdNode id : tree.nonParentNode)
			{
				logger.info("nID:"+id.getId());
				if(id.getId()==2)
				{
					long idNum = id.getBean().getSponsorUl();
					logger.info("idNum:"+idNum);
					IdNode idParent = tree.getIdTree().getNode(idNum);

				}

			}
	        
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		}
		
		try {
			Thread.sleep(1000*60*10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		
	}

	
	/**
	 * 
	 */
	public void loadDataFromDBInterval() {
		// TODO now_jyl轮询加载新的数据,数据库部分的操作应该写成异步
		Calendar temp = Calendar.getInstance();
		temp.setTimeInMillis(lastQueryTime.getTime()) ;
		String queryTimeString = DateTimeUtil.getTimeStampQueryStr(lastQueryTime);
		//String queryTimeString = getQueryDateTimeWithZero(temp);
		String nextDateTimeStr = getQueryDateTimeWithZero(DateTimeUtil.getNextDay(temp));
		loadTread  = new DistributorInternalLoadThread();
		loadTread.setQueryTimeString(queryTimeString);
		loadTread.setNextDateTimeStr(nextDateTimeStr);
		loadTread.setDispatcher(this);
		loadTread.start();
		ArrayList<TreeBaseInfoBeanImp> beans = null;
		while(true)
		{
		
		synchronized(this)
		{
		try {
			if(isbreak)
			{
				this.notifyAll();
				break;
			}
			logger.info("isbreak:"+isbreak);
				
			this.wait();
			beans = loadTread.getBeans();
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		}
		//Collections.sort(beans,new Comparator<TreeBaseInfoBeanImp>(){
		/*//设置最后时间
		if(beans!=null)
		{
		for(TreeBaseInfoBeanImp bean : beans)
		{
			if(bean.getUpdatedAt().after(maxTimestamp))
				maxTimestamp = bean.getUpdatedAt();
		}
		}
		*/
		isLock = true;
		//加载节点
	//	Long tempLong = null;
		addBeans(beans);
		//处理未匹配的数据
		if(beans!=null&&beans.size()>0)
		{
		dealUnMatchedDatas();
		dealUnMatchedDtDatas();
		}
		isLock = false;
	//	Timestamp maxTimestamp  = lastQueryTime;
		//maxTimestamp.g
		Calendar temp1 = Calendar.getInstance();
		temp1.setTimeInMillis(lastQueryTime.getTime()) ;
		Calendar temp2 = Calendar.getInstance();
		if((temp2.getTimeInMillis()-temp.getTimeInMillis())/1000/3600>26)//比当前时间早26小时
		{
			
			loadTread.setLoadInterval(1);
			String queryTimeString1 = DateTimeUtil.getQueryDateTimeWithZero(temp);
			String nextDateTimeStr1 = getQueryDateTimeWithZero(DateTimeUtil.getNextDay(temp));
			//loadTread  = new DistributorInternalLoadThread();
			loadTread.setQueryTimeString(queryTimeString1);
			loadTread.setNextDateTimeStr(nextDateTimeStr1);
		}
		else 
		{
		loadTread.setLoadInterval(1000*20);
		String queryTimeString1 = DateTimeUtil.getTimeStampQueryStr(lastQueryTime);
		String nextDateTimeStr1 = getQueryDateTimeWithZero(temp2);//截止时间为当前时间
		//loadTread  = new DistributorInternalLoadThread();
		loadTread.setQueryTimeString(queryTimeString1);
		loadTread.setNextDateTimeStr(nextDateTimeStr1);
		}
		
		this.notifyAll();
		}
		}
		
	}
	
	/**
	 * 初始化从数据库中加载,初次加载
	 */
	public void loadDataFromDB() {
		
		long maxNodeID = TreeConstant.maxid_initload;
		ulTree.setIdTree(idTree);
		dtTree.setIdTree(idTree);
		dtTree.setNonParentNode(nonDtParentNode);
		ulTree.setNonParentNode(nonParentNode);
	//	dtTree.setNonParentNode(nonParentNode);
		idTree.setDuplicateNode(duplicateNode);
		Long i = 0l;
		int intervalNum = 1000;
		while(i<maxNodeID)
		{
			//long iTemp = i+100000l;
			
			ArrayList<TreeBaseInfoBeanImp> beans = dao.getNodes(i, intervalNum, getDefaultConnectionSingle());
		 i = addBeans(beans);
		}
		//处理未匹配的数据
		dealUnMatchedDatas();
		dealUnMatchedDtDatas();
	}

	/**
	 * 
	 */
	private void dealUnMatchedDtDatas() {
		// TODO now 未匹配的dt数据
		Long []nonParentNodeArr = new Long [nonDtParentNode.size()] ;
		nonParentNodeArr= nonDtParentNode.toArray(nonParentNodeArr);
		for(int loopNum =0; loopNum<nonParentNodeArr.length;loopNum++)
		{
			Long node = nonParentNodeArr[loopNum];
		//	if(node.getId()==2)
		//		logger.info("id:"+node.getId());
			if(node == null)
				logger.info("null_nodeid:"+node);

			logger.info("nodeid:"+node);
			logger.info("loopNum:"+loopNum);
			dtTree.addNode(node);
			/*if(nonParentNode.size()<initNum)
				loopNum --;*/
		}
		Long []nonParentNodeArr1 = new Long [nonDtParentNode.size()] ;
		nonParentNodeArr1= nonDtParentNode.toArray(nonParentNodeArr1);
		for(int loopNum =0; loopNum<nonParentNodeArr1.length;loopNum++)
		{
			Long node = nonParentNodeArr1[loopNum];
			dtTree.addNode(node);
		}
		
		logger.error("nonDtParentNodesize2:"+nonDtParentNode.size());

		
		//输出所有存在重复对应关系的id
		for(long id : duplicateNode)
		{
			logger.info("dID:"+id);

		}
		//输出父id不存在的节点
		for(long id : nonDtParentNode)
		{
			logger.info("nID:"+id);

		}
		/*try {
			Thread.sleep(1000*60*10);
		} catch (InterruptedException e) { 
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		}*/
	}
	/**
	 * 
	 */
	private void dealUnMatchedDatas() {
		// TODO Auto-generated method stub
		Long []nonParentNodeArr = new Long [nonParentNode.size()] ;
		nonParentNodeArr= nonParentNode.toArray(nonParentNodeArr);
		for(int loopNum =0; loopNum<nonParentNodeArr.length;loopNum++)
		{
			long node = nonParentNodeArr[loopNum];
			nonParentNode.remove(node);
			ulTree.addNode(node);
			/*if(nonParentNode.size()<initNum)
				loopNum --;*/
		}
		logger.info("nonParentNode.size():"+nonParentNode.size());

		
		Long []nonParentNodeArr1 = new Long [nonParentNode.size()] ;
		nonParentNodeArr1= nonParentNode.toArray(nonParentNodeArr1);
		for(int loopNum =0; loopNum<nonParentNodeArr1.length;loopNum++)
		{
			Long node = nonParentNodeArr1[loopNum];
			if(node == null)
			nonParentNode.remove(node);
			ulTree.addNode(node);
		}
		
		logger.error("nonUlParentNodeSize:"+nonParentNode.size());

		logger.error("dsize:"+duplicateNode.size());
		
		//输出所有存在重复对应关系的id
		for(long id : duplicateNode)
		{
			logger.info("dID:"+id);

		}
		//输出父id不存在的节点
		for(long id : nonParentNode)
		{
			logger.info("nID:"+id);

		}
		/*try {
			Thread.sleep(1000*60*10);
		} catch (InterruptedException e) { 
			// TODO Auto-generated catch block
			logger.error("tree;",e);
		}*/
	}
	/**
	 * @param beans
	 */
	private long addBeans(ArrayList<TreeBaseInfoBeanImp> beans) {
		long afterSql = System.currentTimeMillis();//毫秒
		long i = 0l;
       int k=0;
     //  long maxId = 0;
		for(TreeBaseInfoBeanImp bean:beans)
	    {   k++;
	    	i = bean.getId();
	    	if(i==1011901l)
	    		logger.info(i);
	    	//用于设置lastQueryTime的初始值
	    	if(lastQueryTime==null)
	    		lastQueryTime = bean.getUpdatedAt();
	    	else if(bean.getUpdatedAt().after(lastQueryTime))
	    		lastQueryTime = bean.getUpdatedAt();
			long beforeNodeTree = System.currentTimeMillis();//毫秒
			//需要区分第一次加载和后续修改加载，在计算子节点总数有所不同
			Boolean exist = false;
	    	IdNode node = idTree.addNode(bean,exist);
			long beforeulTree = System.currentTimeMillis();//毫秒
			 if(k%100==0)
			logger.info("idTree:"+(beforeulTree-beforeNodeTree));
	    	ulTree.addNode(node);
	    	
	    	//TODO now_jyl
	    	dtTree.addNode(node);
	    	
			long afterulTree = System.currentTimeMillis();//毫秒
			 if(k%100==0)
			 {
			logger.info("ulTree:"+(afterulTree-beforeulTree));
			logger.info("i:"+i);

			 }
	    }
		long oneend = System.currentTimeMillis();//毫秒
		 if(i%100==0)
		logger.info("treeTime:"+(oneend-afterSql));
		 return i;
	 //   i= iTemp;
	//		logger.info("num:"+num);

	//第一次加载完查看无父节点的节点数
//	logger.info("size1:"+nonParentNode.size());
	

	
		
	}
	public  Connection getDefaultConnectionSingle() {
		try {
			if(con!=null&&!con.isClosed())
			  return con;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			 String driver = "org.postgresql.Driver";
			 String url = TreeConstant.URL;
			 String user = TreeConstant.USER;
			 String password = TreeConstant.password;
			 Connection con1 = null;
			 try{
				 Class.forName(driver);
				 con1 = DriverManager.getConnection(url, user, password);
			 }catch(ClassNotFoundException e){
				 logger.info("数据库驱动不存在！");
				 logger.info(e.toString());
			 }catch(SQLException e2)
			 {
				 logger.info("数据库存在异常！");
				 logger.info(e2.toString());
			 }
			 this.con = con1;
			 return this.con;
	}
	
	/**
	 * @param schoolDic2
	 * @param string
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void flushDics2Disk() throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(TreeConstant.TREE_PATH));
				
				//试图将对象两次写入文件
				out.writeObject(this);
				out.flush();
		
	}
	
	private DistributorTreeDispatcher loadDicFromDisk( String string)  throws FileNotFoundException, IOException, ClassNotFoundException{
		//	URL url = ResumeFeatureExtractor.class.getClassLoader().getResource(string);
		//	URL url = this.getClass().getResource(string);
		//	logger.info("url:"+url.getPath());
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(TreeConstant.TREE_PATH));
		//	ObjectInputStream oin = new ObjectInputStream(new FileInputStream("D:\\wk3\\baiduHrWeb_Trunk\\web\\"+string));
			DistributorTreeDispatcher dic12 = (DistributorTreeDispatcher)oin.readObject();
			oin.close();
			return dic12;
			
		}
	
	public static  String getQueryDateTimeWithZero(Calendar cal) {
		// 返回用于数据库查询的日期时间字符串（例如：2011-01-03 00:00:00）
		 String monthStr =  cal.get(Calendar.MONTH)+1<10?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1)+"";
		    String dateStr =  cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)+"";
			return 	cal.get(Calendar.YEAR)+"-"+monthStr+"-"+dateStr+" "+cal.get(Calendar.HOUR_OF_DAY)+
			":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		
	}
	//用于异步加载的线程
	class DistributorInternalLoadThread extends Thread implements Serializable
	{
		int loadInterval  = 1000*10;
		ArrayList<TreeBaseInfoBeanImp> beans ;
		DistributorTreeDispatcher dispatcher ;
		String nextDateTimeStr;
		public String getNextDateTimeStr() {
			return nextDateTimeStr;
		}
		String queryTimeString;
		public int getLoadInterval() {
			return loadInterval;
		}
		/**
		 * @param nextDateTimeStr
		 */
		public void setNextDateTimeStr(String nextDateTimeStr) {
			// TODO Auto-generated method stub
			this.nextDateTimeStr = nextDateTimeStr;
		}
		public String getQueryTimeString() {
			return queryTimeString;
		}
		public void setLoadInterval(int loadInterval) {
			this.loadInterval = loadInterval;
		}
		public void setQueryTimeString(String queryTimeString) {
			this.queryTimeString = queryTimeString;
		}
		public DistributorTreeDispatcher getDispatcher() {
			return dispatcher;
		}
		public void setDispatcher(DistributorTreeDispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}
		public ArrayList<TreeBaseInfoBeanImp> getBeans() {
			return beans;
		}
		public void setBeans(ArrayList<TreeBaseInfoBeanImp> beans) {
			this.beans = beans;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(true)
			{
				try {
				
					
					logger.info("isbreak:"+isbreak);
					Thread.sleep(loadInterval);
					ArrayList<TreeBaseInfoBeanImp> beanTemp = dao.getNodesIncrement(queryTimeString,nextDateTimeStr,getDefaultConnectionSingle());
					//Collections.sort(beans,new Comparator<TreeBaseInfoBeanImp>(){
					//TODO now_jyl
					synchronized(dispatcher)
					{
						if(isbreak)
						{
							dispatcher.notifyAll();
							break;
						}
						beans = beanTemp;
						dispatcher.notifyAll();
						dispatcher.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("tree;",e);
				}
			}
		}
		
	}
	
	/**
	 * 
	 */
	public DistributorTreeDispatcher init() {
		// TODO Auto-generated method stub
		if(loadFromDb)
		{
			/*ulTree.setIdTree(idTree);
			ulTree.setNonParentNode(nonParentNode);
			idTree.setDuplicateNode(duplicateNode);
			lastQueryTime = getMinUpdatedTime();
			lastQueryTime.setTime(lastQueryTime.getTime()-1000);*/
		loadDataFromDB();
		return this;
		}
		else
			try {
			 return  loadDicFromDisk(TreeConstant.TREE_PATH);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error("tree;",e);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("tree;",e);
				return null;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error("tree;",e);
				return null;
			}
			
			//TODO now_jyl加载完后开始增量加载节点（按时间）
			
	}
	/**
	 * @return
	 */
	private Timestamp getMinUpdatedTime() {
		// TODO Auto-generated method stub
		try {
			return  dao.getMinTimeStamp( getDefaultConnectionSingle());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		logger.error(e);
			return null;
		}
	}
	/**
	 * @param b
	 */
	public void setBreak(boolean b) {
		// TODO Auto-generated method stub
		isbreak = b;
		logger.info("isbreak1:"+isbreak);
	}
	boolean isbreak = false;
}
