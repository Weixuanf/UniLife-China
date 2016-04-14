package RenrenAnalyze;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * ������httpclient���棺��ȡ��Ҫ��¼��ҳ������� ģ���½
 * 
 * */
public class RenrenSpider {
    /** ���� */
    private static final String domain = "renren.com";  
    /** key_id */ 
    private static final String keyID = "1";  
    /** ���ύurl */ 
    private static String loginURL = "http://www.renren.com/PLogin.do";  
   /** ��½�ɹ�����ת��·��*/  
    private static final String targetUrl = "http://www.renren.com/";  
    /** ������(��תurl) */
    private static final String _ORGI_URL = "origURL";  
    /** ������(����) */ 
    private static final String _DOMAIN = "domain";  
    /** ������(key_id) */  
    private static final String _KEY_ID = "key_id";  
    /** ������(�ʺ�) */
    private static final String _EMAIL = "email";  
    /** ������(����) */  
    private static final String _PASSWORD = "password";  
    /** ThreadSafeClientConnManager��֤���̰߳�ȫ */
    private static DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());  
    
    
    public boolean login(String userName, String password) {  
        boolean isLogin = false;  
        HttpPost httpost = new HttpPost(loginURL); 
        //αװ�ɻ�������
        //httpost.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
        // Ϊ���������ֵ  
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        nvps.add(new BasicNameValuePair(_ORGI_URL, targetUrl));  
        nvps.add(new BasicNameValuePair(_DOMAIN, domain));  
        nvps.add(new BasicNameValuePair(_KEY_ID, keyID));  
        nvps.add(new BasicNameValuePair(_EMAIL, userName));  
        nvps.add(new BasicNameValuePair(_PASSWORD, password));  
        try {  
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
            // ��ȡ������Ӧ  
            client.execute(httpost);  
            //System.out.println(response.getStatusLine());// ����302  
            // ����cookie,renren.com���������֤��cookie������,���ֱַ���p��t.  
            // HttpClientParams.setCookiePolicy(client.getParams(),  
            // CookiePolicy.BROWSER_COMPATIBILITY);  
            // ��ΪHttpClient 4.0Ĭ��cookie���ԻᱨWARN���棬�����ֶ�����cookie����  
            CookieSpecFactory csf = new CookieSpecFactory() {  
                public CookieSpec newInstance(HttpParams params) {  
                    return new BrowserCompatSpec() {  
                        @Override  
                        public void validate(Cookie cookie, CookieOrigin origin)  
                                throws MalformedCookieException {  
                            // nothing to do  
                        }  
                    };  
                }  
            };  
            client.getCookieSpecs().register("easy", csf);  
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");  
            isLogin = true;  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("UnsupportedEncodingException!");  
        } catch (ClientProtocolException e) {  
            System.err.println("ClientProtocolException!");  
        } catch (IOException e) {  
            System.err.println("IOException!");  
        } finally {  
            httpost.abort();  
        }  
        return isLogin;  
    }  
    public String crawl(String url) {
    	String result=""; 
        HttpGet httpget = new HttpGet(url); 
        HttpResponse response;
		try {
			response = client.execute(httpget);
	        //System.out.println(response2.getStatusLine().toString()); // HTTP/1.1 302 Found 
	    	// ��ȡEntity  
	        HttpEntity entity = response.getEntity();  
	        // ����html
	        result = EntityUtils.toString(entity);
	        //��ӡ��ȡ���
	        //System.out.print(result);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("IOException");  
			e.printStackTrace();
		}
        
        return result;
        
    }

	public static void main(String[] args) {
		// TODO ��½������ȡҳ��HTML (crawl HTML code after login
		// ONLY FOR TESTING
		
		//example url
		String url = "http://www.renren.com/355823257/profile?v=info_timeline"; 
		String username = "yourUserName";
		String password = "yourPassword";
        try {  
        	RenrenSpider agent = new RenrenSpider();
            if (!agent.login(username, password)) {  
                System.err.println("��½ʧ��!");  
                System.exit(0);  
            }  
        	
            String result = agent.crawl(url);
            System.err.println(result);
        } catch (ParseException e) {  
            System.err.println("ParseException!");  
        } finally {  
            // When HttpClient instance is no longer needed,  
            // shut down the connection manager to ensure  
            // immediate deallocation of all system resources  
            client.getConnectionManager().shutdown();  
        } 


	}

}

