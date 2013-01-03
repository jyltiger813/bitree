package com.linzhi.tree.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import util.Log4jLoader;

import com.linzhi.tree.bo.DistributorTreeDispatcher;
import com.linzhi.tree.bo.DistributorTreeJsonGenerator;
import com.linzhi.tree.constant.TreeConstant;

/**
 * Servlet implementation class DistributorTreeServlet
 */
public class DistributorTreeServlet extends HttpServlet {
	static Logger logger = Logger.getLogger(DistributorTreeServlet.class);

	private static final long serialVersionUID = 1L;
	ThreadLoad load;
	DistributorTreeJsonGenerator dj = new DistributorTreeJsonGenerator();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DistributorTreeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DistributorTreeDispatcher dispatcher = (DistributorTreeDispatcher)this.getServletContext().getAttribute(TreeConstant.DISTRIBUTORTREE_NAME);
		String nodeStr = request.getParameter("node");
		String nodeType = request.getParameter("nodetype");
		int layerdepthint = 3;
		String authoStr = request.getParameter("at");
		if(!TreeConstant.AUTHORITY_TOKEN.equals(authoStr))
			return;//不允许访问
		String layerdepth = request.getParameter("layerdepth");
		String returnJson  ="";
		try{
			int  temp = Integer.parseInt(layerdepth);
			if(temp>0)
			layerdepthint =temp;
			}catch(Exception e)
			{
				e.printStackTrace();
				logger.error("tree", e);
			}
	
		try{
		long node = Long.parseLong(nodeStr);
		if("dt".equals(nodeType))
			returnJson = DistributorTreeJsonGenerator.getDtJsonStr(dispatcher, node, layerdepthint);
		else if("ul".equals(nodeType))
		returnJson = DistributorTreeJsonGenerator.getJsonStr(dispatcher, node, layerdepthint,false);
		else if("comb".equals(nodeType))
			returnJson = DistributorTreeJsonGenerator.getJsonStr(dispatcher, node, layerdepthint,true);
		ajaxResponse(response,returnJson);
		}catch(Exception e)
		{
			returnJson = "wrong params";
			logger.error("tree", e);
		}
		
		//DistributorTreeDispatcher
	}

	/**
	 * @param response
	 * @param message
	 */
	private void ajaxResponse(HttpServletResponse response, String message) {
		// "" ajax 返回信息
		 response.setHeader("Charset","UTF-8");
		 response.setContentType(
			"text/html; charset=UTF-8");
		//	"text/plain; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter out;
			try {
				out = response.getWriter();
				//System.out.println("jsonString:"+message);
				out.write(message);
				out.flush();
			} catch (IOException e) {
				// "" Auto-generated catch block
				logger.error("tree", e);
			}
			
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void init() throws ServletException {
		// TODO 初始化操作
	//	super.init();
		load = new ThreadLoad();
		load.setContext(this.getServletContext());
		load.start();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		DistributorTreeDispatcher dispatcher =
			(DistributorTreeDispatcher)this.getServletContext().getAttribute(TreeConstant.DISTRIBUTORTREE_NAME);
		try {
			load.setBreak(true);//通知其他线程结束
			dispatcher.flushDics2Disk();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("tree", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("tree", e);
		}
	}

	

	public static void main(String args[])
	{
		DistributorTreeServlet dts = new DistributorTreeServlet();
		try {
			dts.init();
			dts.destroy();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error("tree", e);
		}
	}

}

class ThreadLoad extends Thread{
	ServletContext context;
	DistributorTreeDispatcher dispatcher = null;
	static Logger logger = Logger.getLogger(ThreadLoad.class);

	/**
	 * @param b
	 */
	public void setBreak(boolean b) {
		// TODO Auto-generated method stub
		dispatcher.setBreak(b);
	}
	public ServletContext getContext() {
		return context;
	}
	public void setContext(ServletContext context) {
		this.context = context;
	}
	@Override
	 public void run(){
		dispatcher = new DistributorTreeDispatcher();
		loadProperties();
		Log4jLoader.loaddefault();//加载日志配置文件
		boolean loadFromDB =false;
		if("true".equals(TreeConstant.loadFromDB))
			loadFromDB = true;
		dispatcher.setLoadFromDb(loadFromDB);
		dispatcher = dispatcher.init();
		dispatcher.initTransientData();
		dispatcher.setLoadFromDb(loadFromDB);
		
		context.setAttribute(TreeConstant.DISTRIBUTORTREE_NAME, dispatcher);
		System.out.println("put2context");// init
		dispatcher.setBreak(false);//通知其他线程结束
		dispatcher.loadDataFromDBInterval();
//System.out.println("init");//init

	}
	/**
	 * 
	 */
	private void loadProperties() {
		// TODO Auto-generated method stub
		InputStream fis =ThreadLoad.class.getResourceAsStream("init.properties");
		Properties  pro  = new Properties ();
		TreeConstant constant = new TreeConstant();
		/*
		 * DISTRIBUTORTREE_NAME=distributorobj
			TREE_PATH=E://tree
			loadFromDB=true
			URL=jdbc:postgresql://og.live.db:5432/linzhi
			USER=postgres
			password=root
		 */
		try {
			pro.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("tree", e);
		}
		//加载常量参数
		constant.TREE_PATH  = pro.getProperty("TREE_PATH");
		constant.DISTRIBUTORTREE_NAME = pro.getProperty("DISTRIBUTORTREE_NAME");
		constant.loadFromDB = pro.getProperty("loadFromDB");
		constant.URL = pro.getProperty("URL");
		constant.USER =  pro.getProperty("USER");
		constant.password =  pro.getProperty("password");
		constant.AUTHORITY_TOKEN = pro.getProperty("AUTHORITY_TOKEN");
		try {
			constant.maxid_initload = Long.parseLong(pro
					.getProperty("maxid_initload"));
		} catch (Exception e) {
			logger.error(e);
		}
		context.setAttribute("treeconstant", constant);

	}
}
