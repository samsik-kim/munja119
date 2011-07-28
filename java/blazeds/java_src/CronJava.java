import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;


public class CronJava {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args == null || args.length == 0) {
			System.out.println("Useage : java CronJava http://.....");
		}else {
			HttpClient client = new HttpClient();
			
			HttpMethod method = new GetMethod(args[0]);
			
			try {
				int statusCode = client.executeMethod(method);
				
				if (statusCode == HttpStatus.SC_OK){
					System.out.println("����");
					System.out.println(method.getResponseBodyAsString());
				}else {
					System.out.println("��û ����");
				}
			}catch(Exception e){
				
			}
		}
	}

}
