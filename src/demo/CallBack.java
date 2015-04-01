package demo;

import com.hundsun.t2sdk.impl.client.ClientSocket;
import com.hundsun.t2sdk.interfaces.ICallBackMethod;
import com.hundsun.t2sdk.interfaces.IClient;
import com.hundsun.t2sdk.interfaces.T2SDKException;
import com.hundsun.t2sdk.interfaces.share.event.EventTagdef;
import com.hundsun.t2sdk.interfaces.share.event.IEvent;
import com.hundsun.t2sdk.common.share.event.PackService;
import com.hundsun.t2sdk.interfaces.share.event.IPack;
import com.hundsun.t2sdk.interfaces.share.dataset.DatasetColumnType;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.hundsun.t2sdk.common.share.dataset.DatasetService;

public class CallBack implements ICallBackMethod{

	private static IClient client = null;

	public static void setClient(IClient client) {
		CallBack.client = client;
	}
	
	@Override
	public void execute(IEvent event, ClientSocket arg1) {
		// TODO Auto-generated method stub
		// 获取消息功能号
		//相当于IBizMessage的GetFunction()
		long iFunctionID = event.getIntegerAttributeValue(EventTagdef.TAG_FUNCTION_ID);
 
		if (iFunctionID == 620000) { // 消息中心心跳
			event.changeToresponse();
			try {
				//System.out.println("Receive 620000");
				CallBack.client.send(event);
			} catch (T2SDKException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// 收到发布过来的主推
		else if (iFunctionID == 620003) {
			//System.out.println("Receive 620003");
			
			//获取订阅类型
			long iIssueType = event.getIntegerAttributeValue(EventTagdef.TAG_ISSUE_TYPE);
			System.out.println("消息类型：" + iIssueType);
			
			// 获取过滤信息
			byte[] keyInfo = event.getByteArrayAttributeValue(EventTagdef.TAG_KEY_INFO);
			IPack outPack = PackService.getPacker(keyInfo, "gbk");
			
			if (outPack.getDatasetCount() == 0)
			{
				System.out.println("收到主推消息，无过滤信息");
			}
			else
			{
				System.out.println("过滤信息：");
				IDataset ds = outPack.getDataset(0);
				int columnCount = ds.getColumnCount();
				
				for (int j = 1; j <= columnCount; j++) {
					//System.out.printf("%s", "[" + j + "]" + ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
					System.out.printf("%s", ds.getColumnName(j) + ":" + ds.getColumnType(j) + "|");
				}
				System.out.println();
				
				ds.beforeFirst();
				while (ds.hasNext()) {
					ds.next();

					for (int j = 1; j <= columnCount; j++) {
						System.out.printf("%15s", ds.getString(j) + "|");
					}
					System.out.println();
				}
			}
			
			IDatasets result = event.getEventDatas();
			
			if (result.getDatasetCount() == 0)
			{
				System.out.println("收到主推，无数据包!");
				return;
			}
		
			System.out.println("数据包：");
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
}
