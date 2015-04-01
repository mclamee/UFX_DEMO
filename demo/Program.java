package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.hundsun.t2sdk.impl.client.T2Services;
import com.hundsun.t2sdk.interfaces.IClient;
import com.hundsun.t2sdk.interfaces.T2SDKException;

import com.hundsun.t2sdk.impl.configuration.DefaultConfigurationHelper;
import com.hundsun.t2sdk.interfaces.configuration.IConfiguration;

import java.util.Scanner;


public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IClient client = null;
		T2Services server = T2Services.getInstance();
		
		//DefaultConfigurationHelper configHelp=new DefaultConfigurationHelper();
		//IConfiguration configuration  = configHelp.loadFileToConfiguration("../../lib/t2sdk-config.xml");
		
		try {
			//server.init(configuration);
			
			server.init();
			server.start();
			
			
			client = server.getClient("ufx");
																	
			//设置回调类发送用的接口
			CallBack.setClient(client);
			
			Impl szImpl = new Impl(client);
			
			int iRet = szImpl.Login();

			
			if (iRet != 0)
			{
				System.out.println("Stop");
				server.stop();
				return;
			}
			
			
			
			System.out.print("1.订阅-证券成交回报. 2.证券委托  3.证券委托查询  4.委托撤单   5.持仓查询  6.资金查询   0.退出该系统!\n请输入你的操作:");
			

			InputStreamReader is_reader = new InputStreamReader(System.in);
			String str = null;
			
			int i = -1;
			
			while (i != 0)
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
            		i = Integer.valueOf(str);
            	}
        		catch (NumberFormatException e)
            	{
        			i = -1;
            		System.out.println("输入为非数值，请重新输入: ");
            		continue;
            	}
            	
                switch (i)
                {
                    case 1:
                    	szImpl.sub_deal();
                        break;
                    case 2:
                    	szImpl.entrust();
                        break;

                    //查委托
                    case 3:
                    	szImpl.query_entrust();

                        break;

                    //撤单
                    case 4:
                    	szImpl.entrust_withdraw();
                        break;

                    //查持仓
                    case 5:
                    	szImpl.SecuStkQry();
                        break;

                    //查资金
                    case 6:
                    	szImpl.FundAllQry();
                        break;

                    case 0:
                        break;

                    default:
                        System.out.println("输入值错误，请重新输入!");
                        break;
                }
				
                if (i == 0)
                {
                	break;
                }

                System.out.print("1.订阅-证券成交回报. 2.证券委托  3.证券委托查询  4.委托撤单   5.持仓查询  6.资金查询   0.退出该系统!\n请输入你的操作:");
            }
       
			server.stop();	
					
		} catch (T2SDKException e) {
			// TODO: handle exception
			//System.out.println("Exception!");
			e.printStackTrace();
		} 	
		
	}

}
