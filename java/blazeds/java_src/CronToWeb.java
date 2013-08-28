import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * How to send a request directly using {@link HttpClient}.
 *
 * @since 4.0
 */
public class CronToWeb {

	public static void main(String args[]){
        URL url;//URL �ּ� ��ü
        URLConnection connection;//URL������ ������ ��ü
        InputStream is;//URL���ӿ��� ������ �б����� Stream
        InputStreamReader isr;
        BufferedReader br;
        try{
            //URL��ü�� �����ϰ� �ش� URL�� �����Ѵ�..
            url = new URL(args[0]);
            connection = url.openConnection();
            //������ �о�������� InputStream��ü�� �����Ѵ�..
            is = connection.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            //������ �о ȭ�鿡 ����Ѵ�..
            String buf = null;
            while(true){
                buf = br.readLine();
                if(buf == null) break;
                System.out.println(buf);
            }
        }catch(MalformedURLException mue){
            System.err.println("�߸��� URL�Դϴ�. ���� : java CronToWeb http://hostname/path]");
            System.exit(1);
        }catch(IOException ioe){
            System.err.println("IOException " + ioe);
            ioe.printStackTrace();
            System.exit(1);
        }
    }


}