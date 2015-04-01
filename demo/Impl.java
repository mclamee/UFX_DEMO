package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hundsun.t2sdk.common.core.context.ContextUtil;
import com.hundsun.t2sdk.common.share.dataset.DatasetService;

import com.hundsun.t2sdk.interfaces.IClient;
import com.hundsun.t2sdk.interfaces.share.dataset.DatasetColumnType;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.hundsun.t2sdk.interfaces.share.event.EventReturnCode;
import com.hundsun.t2sdk.interfaces.share.event.EventTagdef;
import com.hundsun.t2sdk.interfaces.share.event.EventType;
import com.hundsun.t2sdk.interfaces.share.event.IEvent;
import com.hundsun.t2sdk.common.share.event.PackService;
import com.hundsun.t2sdk.interfaces.share.event.IPack;
import com.hundsun.t2sdk.interfaces.share.exception.DatasetRuntimeException;

import com.hundsun.t2sdk.interfaces.T2SDKException;


public class Impl {

	public Impl(IClient client)
	{
		m_IClient = client;
	}
	
	public int Login() 
	{
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("331100", EventType.ET_REQUEST);
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way", DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("input_content", DatasetColumnType.DS_INT);
		dataset.addColumn("account_content", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);
		dataset.addColumn("content_type", DatasetColumnType.DS_STRING);
		
		//新增一行
		dataset.appendRow();
		try
		{
			dataset.updateInt("op_branch_no", 0);
			dataset.updateInt("op_entrust_way", 5);
			dataset.updateString("op_station", "IP:127.0.0.1;MAC;HD;");
			dataset.updateInt("branch_no", 1000);
			dataset.updateInt("input_content", 1);
			dataset.updateString("account_content", "10000014");
			dataset.updateString("password", "111111");
			dataset.updateInt("password_type", 2);
			dataset.updateString("content_type", "1");
		}
		catch (DatasetRuntimeException e)
		{
			e.printStackTrace();
			return -1;
		}
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return -2;
		}
		
		//if (rsp.getErrorNo() != EventReturnCode.I_OK)
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("登录失败： " + "ReturnCode: " + rsp.getReturnCode() + "; ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return -1;
			}
			
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			
			m_iSystemId = ds.getInt("sysnode_id");
			m_iBranchNo = ds.getInt("branch_no");
			m_szUserToken = new String(ds.getString("user_token"));
			char cColType;
			// 遍历单个结果集列信息
			System.out.println("columnCount:" + columnCount);
			for (int j = 1; j <= columnCount; j++) {
				
				cColType = ds.getColumnType(j);
				
				System.out.printf("%25s", ds.getColumnName(j) + "|" + ds.getColumnType(j));
				
				
				switch (cColType)
				{
				case DatasetColumnType.DS_INT:
					System.out.println(ds.getInt(j));
					//System.out.printf("%25d", ds.getInt(j));
					break;
				case DatasetColumnType.DS_DOUBLE:
					System.out.printf("%#.4f%n", ds.getDouble(j));
					break;
				case DatasetColumnType. DS_BYTE_ARRAY:
					System.out.println(ds.getByteArray(j));
					break;
				default:
					System.out.println(ds.getString(j));
					break;
				}	
				
			}
		}
		return iRet;
		
	}
	
	//订阅
	public void sub_deal()
	{
		System.out.println("sub_deal");
		
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("620001", EventType.ET_REQUEST);
		
		// 设置订阅类型issuetype，
		event.setIntegerAttributeValue(EventTagdef.TAG_ISSUE_TYPE, 12);
		
		// 设置订阅关键字keyinfo
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);
		
		dataset.addColumn("issue_type", DatasetColumnType.DS_INT);
		
		dataset.appendRow();
		
		dataset.updateInt("op_branch_no", 0);
		dataset.updateInt("op_entrust_way", 5);
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);
		dataset.updateString("client_id", "10000014");
		dataset.updateString("fund_account", "10000014");
		dataset.updateString("password", "111111");
		dataset.updateInt("password_type", 2);
		dataset.updateString("user_token", m_szUserToken);
		
		dataset.updateInt("issue_type", 12);
		
        //放入PACK获取二进制
		IPack outPack = PackService.getPacker(IPack.VERSION_2, "gbk");
		outPack.addDataset(dataset);

		event.setByteArrayAttributeValue(EventTagdef.TAG_KEY_INFO, outPack.Pack());

		//这里是同步发送接收订阅应答
		IEvent rsp;
	
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("订阅失败:： " + "ReturnCode:" + rsp.getReturnCode() + "; ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		//相当于IBizMessage的GetKeyInfo
	    byte[] info = rsp.getByteArrayAttributeValue(EventTagdef.TAG_KEY_INFO);
	    IPack infoPack = PackService.getPacker(info, "gbk");
	    
	    if (infoPack.getDatasetCount() == 0)
		{
			System.out.println("No Datas");
			return;
		}
	    
	    IDataset ds = infoPack.getDataset(0);

		int columnCount = ds.getColumnCount();
		
		// 遍历单个结果集列信息
		//System.out.println("columnCount:" + columnCount);
		for (int j = 1; j <= columnCount; j++) {
			
			System.out.printf("%25s", ds.getColumnName(j) + "|" + ds.getColumnType(j)+ " ");
			
			System.out.println(ds.getString(j));
								
		}
	}
	
	public void entrust()
	{
		//System.out.println("entrust");
		
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("333002", EventType.ET_REQUEST);
		
		event.setIntegerAttributeValue(EventTagdef.TAG_SYSTEM_NO, m_iSystemId);
		
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);
		dataset.addColumn("exchange_type", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_code", DatasetColumnType.DS_STRING);
		dataset.addColumn("entrust_amount", DatasetColumnType.DS_DOUBLE);
		dataset.addColumn("entrust_price", DatasetColumnType.DS_DOUBLE);
		dataset.addColumn("entrust_bs", DatasetColumnType.DS_INT);
		dataset.addColumn("entrust_prop",DatasetColumnType.DS_STRING);
		dataset.addColumn("batch_no", DatasetColumnType.DS_INT);
		
		//新增一行
		dataset.appendRow();
		dataset.updateInt("op_branch_no", 0);
		dataset.updateInt("op_entrust_way", 5);
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);
		dataset.updateString("client_id", "");
		dataset.updateString("fund_account", "10000014");
		dataset.updateString("password", "111111");
		dataset.updateInt("password_type", 2);
		dataset.updateString("user_token", m_szUserToken);

		dataset.updateString("exchange_type", "1");
		dataset.updateString("stock_account", "");
		dataset.updateString("stock_code", "600570");
		dataset.updateDouble("entrust_amount", 200);
		dataset.updateDouble("entrust_price", 55);
		dataset.updateInt("entrust_bs" ,1);
		dataset.updateString("entrust_prop", "0");
		dataset.updateInt("batch_no", 0);
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		//if (rsp.getErrorNo() != EventReturnCode.I_OK)
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("委托失败:： " + "ReturnCode:" + rsp.getReturnCode() + "; ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			//获得结果集总数
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return;
			}
		
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			
			// 遍历单个结果集列信息
			//System.out.println("columnCount:" + columnCount);
			for (int j = 1; j <= columnCount; j++) {
				
				System.out.printf("%25s", ds.getColumnName(j) + "|" + ds.getColumnType(j)+ " ");
				
				System.out.println(ds.getString(j));
									
			}
								
				
			
			
		}
	}
	
	public void query_entrust()
	{

		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("333101", EventType.ET_REQUEST);
		
		event.setIntegerAttributeValue(EventTagdef.TAG_SYSTEM_NO, m_iSystemId);
		
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);   
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);  
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);      
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);   
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);    
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);  
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);  

		dataset.addColumn("exchange_type", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_code", DatasetColumnType.DS_STRING);   
		dataset.addColumn("sort_direction", DatasetColumnType.DS_INT);
		dataset.addColumn("report_no", DatasetColumnType.DS_INT);
		dataset.addColumn("action_in", DatasetColumnType.DS_INT);
		dataset.addColumn("locate_entrust_no", DatasetColumnType.DS_INT);
		dataset.addColumn("query_type", DatasetColumnType.DS_INT);
		dataset.addColumn("query_mode", DatasetColumnType.DS_INT);
		dataset.addColumn("position_str", DatasetColumnType.DS_STRING);
		dataset.addColumn("request_num", DatasetColumnType.DS_INT);
		dataset.addColumn("etf_flag", DatasetColumnType.DS_INT);
		
		//新增一行
		dataset.appendRow();
		dataset.updateInt("op_branch_no", 0);
		dataset.updateInt("op_entrust_way", 5);
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);
		dataset.updateString("client_id", "10000014");
		dataset.updateString("fund_account", "10000014");
		dataset.updateString("password", "111111");
		dataset.updateInt("password_type", 2);
		dataset.updateString("user_token", m_szUserToken);

		dataset.updateString("exchange_type", "1");
		dataset.updateString("stock_account", "");
		dataset.updateString("stock_code", "600570");
		dataset.updateInt("sort_direction", 0);
		dataset.updateInt("report_no", 0);
		dataset.updateInt("action_in", 0);
		dataset.updateInt("locate_entrust_no", 0);
		dataset.updateInt("query_type", 0);
		dataset.updateInt("query_mode", 0);
		dataset.updateString("position_str", "");
		dataset.updateInt("request_num", 0);
		dataset.updateInt("etf_flag", 0);
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		//if (rsp.getErrorNo() != EventReturnCode.I_OK)
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("委托查询失败:： " + "ReturnCode:" + rsp.getReturnCode() + "; ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return;
			}
		
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			
			//char cColType;
			// 遍历单个结果集列信息
			//System.out.println("columnCount:" + columnCount);
			//System.out.printf("%5s", "Row|");
			for (int j = 1; j <= columnCount; j++) {
				//System.out.printf("%s", "[" + j + "]" + ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
				System.out.printf("%s", ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
			}
			System.out.println();
			
			//int iRow = 1;
			ds.beforeFirst();
			while (ds.hasNext()) {
				ds.next();
				//System.out.printf("%4d",iRow++);
				//System.out.print("|");
				
				for (int j = 1; j <= columnCount; j++) {
					//System.out.printf("%10s", "[" + j + "]" + ds.getString(j) + "|");
					System.out.printf("%15s", ds.getString(j) + "|");
				}
				System.out.println();
			}
					
				
			
			
		}
	}
	
	public void entrust_withdraw()
	{
		//System.out.println("entrust_withdraw");
		
		int iEntrustNo_WithDraw = -1;
		
		InputStreamReader is_reader = new InputStreamReader(System.in);
		String str = null;
		
		System.out.println("请输入需要撤单的委托号：");
		while (true)
        {          	
        	try	{
        		str = new  BufferedReader(is_reader).readLine();
        	}catch (IOException e){
    			e.printStackTrace();
    			continue;
    		}
        	
        	if (str == null || str.equals(""))
    		{
    			continue;
    		}
        	
        	try
        	{
        		iEntrustNo_WithDraw = Integer.valueOf(str);
        		if (iEntrustNo_WithDraw == 0)
        			continue;
        		else
        			break;
        	}
    		catch (NumberFormatException e)
        	{
        		System.out.println("输入为非数值，请重新输入: ");
        		continue;
        	}
        }
		
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("333017", EventType.ET_REQUEST);
		
		event.setIntegerAttributeValue(EventTagdef.TAG_SYSTEM_NO, m_iSystemId);
		
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);  
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);      
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);   
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);    
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);  
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);  

		dataset.addColumn("batch_flag", DatasetColumnType.DS_INT);
		dataset.addColumn("exchange_type", DatasetColumnType.DS_STRING);
		dataset.addColumn("entrust_no", DatasetColumnType.DS_INT);
		
		//新增一行
		dataset.appendRow();
		dataset.updateInt("op_branch_no", 0);                    
		dataset.updateInt("op_entrust_way", 5);                  
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);             
		dataset.updateString("client_id", "10000014");           
		dataset.updateString("fund_account", "10000014");        
		dataset.updateString("password", "111111");              
		dataset.updateInt("password_type", 2);                   
		dataset.updateString("user_token", m_szUserToken);       

		dataset.updateInt("batch_flag", 0);
		dataset.updateString("exchange_type", "");
		dataset.updateInt("entrust_no", iEntrustNo_WithDraw);
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		//if (rsp.getErrorNo() != EventReturnCode.I_OK)
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("撤单失败:： " + "ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return;
			}
			
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			
			for (int j = 1; j <= columnCount; j++) {
				
				System.out.printf("%25s", ds.getColumnName(j) + "|" + ds.getColumnType(j)+ " ");
				
				System.out.println(ds.getString(j));
									
			}
			
		}
	}
	
	public void SecuStkQry()
	{
		//System.out.println("SecuStkQry");
		
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("333104", EventType.ET_REQUEST);
		
		event.setIntegerAttributeValue(EventTagdef.TAG_SYSTEM_NO, m_iSystemId);
		
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);

		dataset.addColumn("exchange_type", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("stock_code", DatasetColumnType.DS_STRING);

		dataset.addColumn("query_mode", DatasetColumnType.DS_INT);
		dataset.addColumn("position_str", DatasetColumnType.DS_STRING);
		dataset.addColumn("request_num", DatasetColumnType.DS_INT);
		
		//新增一行
		dataset.appendRow();
		dataset.updateInt("op_branch_no", 0);
		dataset.updateInt("op_entrust_way", 5);
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);
		dataset.updateString("client_id", "10000014");
		dataset.updateString("fund_account", "10000014");
		dataset.updateString("password", "111111");      
		dataset.updateInt("password_type", 2);
		dataset.updateString("user_token", m_szUserToken);

		dataset.updateString("exchange_type", "");
		dataset.updateString("stock_account", "");
		dataset.updateString("stock_code", "");

		dataset.updateInt("query_mode", 0);
		dataset.updateString("position_str", "");
		dataset.updateInt("request_num", 0);
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("持仓查询失败:： " + "ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return;
			}
								

			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			

			for (int j = 1; j <= columnCount; j++) {
				//System.out.printf("%s", "[" + j + "]" + ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
				System.out.printf("%s", ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
			}
			System.out.println();
			
			//int iRow = 1;
			ds.beforeFirst();
			while (ds.hasNext()) {
				ds.next();
				//System.out.printf("%4d",iRow++);
				//System.out.print("|");
				
				for (int j = 1; j <= columnCount; j++) {
					//System.out.printf("%10s", "[" + j + "]" + ds.getString(j) + "|");
					System.out.printf("%15s", ds.getString(j) + "|");
				}
				System.out.println();
			}	
			
			
		}
	}
	
	public void FundAllQry()
	{
		//System.out.println("FundAllQry");
		
		IEvent event = ContextUtil.getServiceContext().getEventFactory().getEventByAlias("332255", EventType.ET_REQUEST);
		// 往event中添加dataset
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		
		dataset.addColumn("op_branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("op_entrust_way",  DatasetColumnType.DS_INT);
		dataset.addColumn("op_station", DatasetColumnType.DS_STRING);
		dataset.addColumn("branch_no", DatasetColumnType.DS_INT);
		dataset.addColumn("client_id", DatasetColumnType.DS_STRING);
		dataset.addColumn("fund_account", DatasetColumnType.DS_STRING);
		dataset.addColumn("password", DatasetColumnType.DS_STRING);
		dataset.addColumn("password_type", DatasetColumnType.DS_INT);
		dataset.addColumn("user_token", DatasetColumnType.DS_STRING);

		dataset.addColumn("money_type", DatasetColumnType.DS_STRING);
		
		//新增一行
		dataset.appendRow();
		dataset.updateInt("op_branch_no", 0);
		dataset.updateInt("op_entrust_way", 5);
		dataset.updateString("op_station","IP:127.0.0.1;MAC;HD");
		dataset.updateInt("branch_no", m_iBranchNo);
		dataset.updateString("client_id", "10000014");
		dataset.updateString("fund_account", "10000014");
		dataset.updateString("password", "111111");      
		dataset.updateInt("password_type", 2);
		dataset.updateString("user_token", m_szUserToken);

		dataset.updateString("money_type", "");
		
		event.putEventData(dataset);
		
		IEvent rsp;
		try {
			rsp = m_IClient.sendReceive(event);
		}catch (T2SDKException e)
		{
			System.out.println("SendReceive Exception");
			//e.printStackTrace();
			return;
		}
		
		//if (rsp.getErrorNo() != EventReturnCode.I_OK)
		int iRet = rsp.getReturnCode();
		
		if(iRet !=  EventReturnCode.I_OK){ //返回错误
			System.out.println("委托失败:： " + "ErrorNo: " + rsp.getErrorNo() +"; ErrorInfo:  " + rsp.getErrorInfo());
		}
		
		{
			//获得结果集
			IDatasets result = rsp.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("No Datas");
				return;
			}
								
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(0);
			int columnCount = ds.getColumnCount();
			
			for (int j = 1; j <= columnCount; j++) {
				//System.out.printf("%s", "[" + j + "]" + ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
				System.out.printf("%s", ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
			}
			System.out.println();
			
			//int iRow = 1;
			ds.beforeFirst();
			while (ds.hasNext()) {
				ds.next();
				//System.out.printf("%4d",iRow++);
				//System.out.print("|");
				
				for (int j = 1; j <= columnCount; j++) {
					//System.out.printf("%10s", "[" + j + "]" + ds.getString(j) + "|");
					System.out.printf("%15s", ds.getString(j) + "|");
				}
				System.out.println();
			}	
		}
	}
	
	private int m_iBranchNo;
	private String m_szUserToken;
	private long m_iSystemId;
	private IClient m_IClient;
}
