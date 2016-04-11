package RenrenAnalyze;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * ��ȡʱ����״̬�ؼ��������������û������ϲ������������ҳ��ȡ�Ͷ���Ϣ���ס����Ϣ
 * Crawl the status key word search result page and get the status data user posted. 
 * Then crawl the detailed profile page of each user and get the education and location data of each user
 * 
 * 6.0 ��ΪspiderBasic���connect����timed out���ʰ�spiderBasic��ΪspiderGo���crawl����
 * 5.0 ���� �־ӵ� ��¼���ܣ��������ݿ�reside�ֶ���
 * 4.0 ������Ҫ��¼����֤������������������һ���˺ŵ�½��������
 * 
 * */
public class CrawlSearchedStatus {
	public static void main(String []args) throws IOException, InterruptedException {
		long startTime=System.currentTimeMillis();   //��ȡ��ʼʱ��   
		ReadAccounts reader = new ReadAccounts();
		String accountsFilePath = "D:/OneDrive/JAVA workspace/spider1/�����˺�400��C+B+A.txt";
    	reader.exportAccounts(accountsFilePath);
    	int count =0 ;    //��ʼ�û���ţ�email[]�����index)
    	int accountNum = 400; //�ܹ��ж��ٿ����˺�
		int offset = 17750; //��ʼ��ȡoffset��ҳ��*10��
		String tableName = "exercise";
		//String keyword = "%E8%87%AA%E4%B9%A0"; //%E5%81%A5%E8%BA%AB-����
		String keyword = "%E5%81%A5%E8%BA%AB";
		//����mysql���ݿ�
		dbhelper boringdb = new dbhelper();
	    boringdb.connSQL();
	    //ģ���½������
	    RenrenSpider agent007 = new RenrenSpider();
	    for(int j=count;j<accountNum;j++){
	    	
	    	boolean login = agent007.login(reader.email[j], reader.psw[j]);
			if (!login) {  
		        System.err.println("��½ʧ��!");  
		        System.exit(0);  
		    }  
			else{
				System.out.println("****************************************");
				System.out.println("");
				System.out.println("email["+j+"]  �û� "+reader.email[j]+"  is logged in");
				System.out.println("");
				System.out.println("****************************************");
				for(int i=0;i<10;i++){
					System.out.println("=============offsetΪ"+offset+"====================");
					//URL url = new URL("http://browse.renren.com/s/status?q=%E6%97%A0%E8%81%8A&offset="+offset+"&l=10&sort=0&range=0");  //ͨ��ʹ�û�����õ����������/HTMLAnalyzer�������˻�ҳʱ�����������ַ
			    	//String result = spiderBasic.crawlHTML(url);
					String result = agent007.crawl("http://browse.renren.com/s/status?q="+keyword+"&offset="+offset+"&l=10&sort=0&range=0");
			    	//System.out.println(result);
					
					//�ɵ��������״̬�������л���������ȡ��status_msg.>\\s+<a href=.(.+?)\".+?namecard=\\d+>(.+?)</a>��(.+?)\\s*</div>\\s*.*\\s*<span.+?>(.+?)</span
					Matcher pplurl = Pattern.compile("status_msg.>\\s+<a href=.(.+?)\".+?namecard=\\d+>(.+?)</a>��([\\s\\S]*?)</div>[\\s\\S]*?<span.+?>(.+?)</span").matcher(result);//�����ڶ�������������������ʺš�����Ϊʲô������û�еĻ�ƥ��ľ��Ǻ������е�û�н���//���һ��ƥ��span.+����Ҳ�������ʺš���Ϊʲô   
			    	//����ƥ���ȡһ��ҳ��10���˵�������������״̬���ݡ�����ʱ��
			    	while (pplurl.find()) {
			        	String prflurl = pplurl.group(1);
			        	String name = RenrenHelper.stripEmoji(pplurl.group(2));
			        	String post = RenrenHelper.stripHtml(pplurl.group(3));
			        	String potime = pplurl.group(4);
						//Thread.sleep(10);//���� 
						System.out.println("��"+name+"�� "+prflurl);
						
						boolean isInsertedin1 = false; //��round 1ʱ��Ŀ�Ƿ���������Ѿ���inserted��flag
						for (int round=1;round<=2;round++){
							String info_result;
							if(round==1){
								//System.out.println("----round=1 ��������ҳ��õ����Ͷ�ѧУ����Ϣ----");
								info_result = agent007.crawl(prflurl+"?v=info_timeline");
							}
							else {
								//System.out.println("----round=2 ���ʸ�����ҳҳ��õ����־ӵء�----");
								info_result = agent007.crawl(prflurl);
							}
							
				        	//�ж��Ƿ���Ҫ�����˺����µ�½
							int changeCount = 0; //��¼���������˺ŵĴ���
				        	while(RenrenHelper.needChangeUser(info_result,j,reader.email[j])) {
				        		if(changeCount>4) {
				        			System.out.println("�����������½��˵�������˺ž���Ҫ������֤�룬��Ҫ�����˺���");
				        			long endTime=System.currentTimeMillis(); //��ȡ����ʱ��   
				        		    System.out.println("���γ�������ʱ�䣺 "+(endTime-startTime)+"ms");  
				        		    System.out.println("ʹ����"+(accountNum-count)+"���˺���ȡ"  );  
				        		    System.exit(0);
				        		}
				        		else{
				        			changeCount++;
				        		}
				        		
				        		//����j�Ƿ񳬳�email[]�����index������ȫ���˺�
				        		if(j==accountNum-1){
				    	    		System.out.println("�Ѿ�����"+accountNum+"���˺�! j=="+accountNum+"�ֻص���һ���˺�");
				    	    		j=0; //�ص���һ���˺ż�������
				    	    	}
				        		//������һ���˺�
				        		j++;
				        		
				        		//ģ���½
				        		if (!agent007.login(reader.email[j], reader.psw[j])) {  
				    		        System.err.println("��½ʧ��!");  
				    		        System.exit(0);  
				    		    }  
				        		System.out.println("****************************************");
								System.out.println("");
								System.out.println("email["+j+"]  �û� "+reader.email[j]+"  �油�ϳ���   logged in");
								System.out.println("");
								System.out.println("****************************************");
				        		info_result = agent007.crawl(prflurl+"?v=info_timeline");
				        	}
				        	
				        	
				        	//round 1��������ڵõ����Ͷ�ѧУ����Ϣ
			        		String schoolname = null;
				        	String schoolyear = null;
				        	if(round==1){
					        	Matcher mschool = Pattern.compile("<dt>��ѧ</dt>\\s+.*?pf_spread'>(.+?)</a>-.+?(\\d+)��").matcher(info_result);   
					            if(!mschool.find()){
					            	System.out.println("ѧУΪ�ջ����������ر�ҳ�棡");
					            }
					            else{
					                schoolname = mschool.group(1);
					                schoolyear = mschool.group(2);
					                //�ж��Ƿ�Ϊ�ڶ���
					                int poyr = Integer.parseInt(potime.substring(0, 4));
					                int schoolyr = Integer.parseInt(schoolyear);
					                int yeargap = poyr - schoolyr;
					                if(yeargap<=4&&yeargap>=0){
					                	System.out.print("����������"+schoolname);
					                    System.out.print(schoolyear);
					                	String insert = "insert into "+tableName+"(prflurl,name,post,potime,school,schoolyear,isEnrolling) values('"+prflurl+"','"+name+"','"+post+"','"+potime+"','"+schoolname+"','"+schoolyear+"','1')"; 
					                    if (boringdb.insertSQL(insert) == true) {  
					                        System.out.println("  insert successfully");  
					                        isInsertedin1 = true;
					                    }  
					                }
					                else{
					                	System.out.println("�����ڶ�ѧ����   "+schoolname+schoolyear);
					                }
					                
					            }
				        	}
				        	//round 2��������ڵõ����־ӵء�	
				        	else if(round==2){
				        		Matcher mreside = Pattern.compile("�־� (.+?)</li>").matcher(info_result);   
				        		if(!mreside.find()){
				        			System.out.println("û���־ӵأ�");
				        		}
				        		else{
				        			String resideCity = mreside.group(1);
				        			System.out.print("�־ӵأ�"+resideCity);
				        			//�����round 1 �������Ѿ���������
				        			if(isInsertedin1){
				        				//���߲�ѯ���id���и��£�UPDATE boring3 SET reside = 'NANJING' WHERE id = (select a.id from(select max(id) id from boring3 a)a)
				        				//String update = "UPDATE boring4 SET reside = '"+ resideCity +"' WHERE prflurl = '"+prflurl+"' AND potime = '"+ potime +"'"; 
				        				//����ѯpotimeֻ��ѯprflurl
				        				String update = "UPDATE "+tableName+" SET reside = '"+ resideCity +"' WHERE prflurl = '"+prflurl+"'"; 
					                    if (boringdb.insertSQL(update) == true) {  
					                        System.out.println("  update successfully");  
					                    }  
				        			}
				        			else {
					                	String insert = "insert into "+tableName+"(prflurl,name,post,potime,isEnrolling,reside) values('"+prflurl+"','"+name+"','"+post+"','"+potime+"','0','"+resideCity+"')"; 
					                	if (boringdb.insertSQL(insert) == true) {  
					                        System.out.println("  insert successfully"); 
					                    }  
				        			}
				        		}
				        		//System.exit(0);
				        	}
				        	
						}
			        	
 
			        }
				    //��ҳ
				    offset=offset+10;
				}
			}
			//������accountNum���˺�֮��
	    	if(j==accountNum-1){
	    		System.out.println("�Ѿ�����j=="+accountNum+"���˺�!");
	    		j=0; //�ص���һ���˺ż�������
	    	}
	    }
	    long endTime=System.currentTimeMillis(); //��ȡ����ʱ��   
	    System.out.println("���γ�������ʱ�䣺 "+(endTime-startTime)+"ms");  
	    System.out.println("ʹ����"+(accountNum-count)+"���˺���ȡ"  );  
	    
	}
	
	
}
