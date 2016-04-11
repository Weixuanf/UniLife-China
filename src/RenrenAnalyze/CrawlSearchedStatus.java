package RenrenAnalyze;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 爬取时间线状态关键字搜索出来的用户的资料并访问其个人主页获取就读信息与居住地信息
 * Crawl the status key word search result page and get the status data user posted. 
 * Then crawl the detailed profile page of each user and get the education and location data of each user
 * 
 * 6.0 因为spiderBasic里的connect总是timed out，故把spiderBasic换为spiderGo里的crawl方法
 * 5.0 加入 现居地 记录功能，插入数据库reside字段中
 * 4.0 遇到需要登录或验证码的特殊情况，更换下一个账号登陆继续运行
 * 
 * */
public class CrawlSearchedStatus {
	public static void main(String []args) throws IOException, InterruptedException {
		long startTime=System.currentTimeMillis();   //获取开始时间   
		ReadAccounts reader = new ReadAccounts();
		String accountsFilePath = "D:/OneDrive/JAVA workspace/spider1/人人账号400个C+B+A.txt";
    	reader.exportAccounts(accountsFilePath);
    	int count =0 ;    //起始用户编号（email[]数组的index)
    	int accountNum = 400; //总共有多少可用账号
		int offset = 17750; //起始爬取offset（页数*10）
		String tableName = "exercise";
		//String keyword = "%E8%87%AA%E4%B9%A0"; //%E5%81%A5%E8%BA%AB-健身
		String keyword = "%E5%81%A5%E8%BA%AB";
		//连接mysql数据库
		dbhelper boringdb = new dbhelper();
	    boringdb.connSQL();
	    //模拟登陆人人网
	    RenrenSpider agent007 = new RenrenSpider();
	    for(int j=count;j<accountNum;j++){
	    	
	    	boolean login = agent007.login(reader.email[j], reader.psw[j]);
			if (!login) {  
		        System.err.println("登陆失败!");  
		        System.exit(0);  
		    }  
			else{
				System.out.println("****************************************");
				System.out.println("");
				System.out.println("email["+j+"]  用户 "+reader.email[j]+"  is logged in");
				System.out.println("");
				System.out.println("****************************************");
				for(int i=0;i<10;i++){
					System.out.println("=============offset为"+offset+"====================");
					//URL url = new URL("http://browse.renren.com/s/status?q=%E6%97%A0%E8%81%8A&offset="+offset+"&l=10&sort=0&range=0");  //通过使用火狐内置的网络分析器/HTMLAnalyzer分析出了换页时真正的请求地址
			    	//String result = spiderBasic.crawlHTML(url);
					String result = agent007.crawl("http://browse.renren.com/s/status?q="+keyword+"&offset="+offset+"&l=10&sort=0&range=0");
			    	//System.out.println(result);
					
					//旧的正则如果状态内容里有还行则不能爬取：status_msg.>\\s+<a href=.(.+?)\".+?namecard=\\d+>(.+?)</a>：(.+?)\\s*</div>\\s*.*\\s*<span.+?>(.+?)</span
					Matcher pplurl = Pattern.compile("status_msg.>\\s+<a href=.(.+?)\".+?namecard=\\d+>(.+?)</a>：([\\s\\S]*?)</div>[\\s\\S]*?<span.+?>(.+?)</span").matcher(result);//倒数第二个括号里必须有最后的问号”？”为什么？？？没有的话匹配的就是后面所有的没有界限//最后一个匹配span.+后面也必须有问号。。为什么   
			    	//正则匹配获取一整页的10个人的姓名、新鲜事状态内容、发布时间
			    	while (pplurl.find()) {
			        	String prflurl = pplurl.group(1);
			        	String name = RenrenHelper.stripEmoji(pplurl.group(2));
			        	String post = RenrenHelper.stripHtml(pplurl.group(3));
			        	String potime = pplurl.group(4);
						//Thread.sleep(10);//毫秒 
						System.out.println("【"+name+"】 "+prflurl);
						
						boolean isInsertedin1 = false; //在round 1时条目是否符合条件已经被inserted的flag
						for (int round=1;round<=2;round++){
							String info_result;
							if(round==1){
								//System.out.println("----round=1 访问资料页面得到“就读学校”信息----");
								info_result = agent007.crawl(prflurl+"?v=info_timeline");
							}
							else {
								//System.out.println("----round=2 访问个人主页页面得到“现居地”----");
								info_result = agent007.crawl(prflurl);
							}
							
				        	//判断是否需要更换账号重新登陆
							int changeCount = 0; //记录连续更换账号的次数
				        	while(RenrenHelper.needChangeUser(info_result,j,reader.email[j])) {
				        		if(changeCount>4) {
				        			System.out.println("过多次连续登陆，说明此组账号均需要输入验证码，需要更换账号组");
				        			long endTime=System.currentTimeMillis(); //获取结束时间   
				        		    System.out.println("本次程序运行时间： "+(endTime-startTime)+"ms");  
				        		    System.out.println("使用了"+(accountNum-count)+"个账号爬取"  );  
				        		    System.exit(0);
				        		}
				        		else{
				        			changeCount++;
				        		}
				        		
				        		//检验j是否超出email[]的最大index即用完全部账号
				        		if(j==accountNum-1){
				    	    		System.out.println("已经用完"+accountNum+"个账号! j=="+accountNum+"现回到第一个账号");
				    	    		j=0; //回到第一个账号继续运行
				    	    	}
				        		//更换下一个账号
				        		j++;
				        		
				        		//模拟登陆
				        		if (!agent007.login(reader.email[j], reader.psw[j])) {  
				    		        System.err.println("登陆失败!");  
				    		        System.exit(0);  
				    		    }  
				        		System.out.println("****************************************");
								System.out.println("");
								System.out.println("email["+j+"]  用户 "+reader.email[j]+"  替补上场了   logged in");
								System.out.println("");
								System.out.println("****************************************");
				        		info_result = agent007.crawl(prflurl+"?v=info_timeline");
				        	}
				        	
				        	
				        	//round 1：如果正在得到“就读学校”信息
			        		String schoolname = null;
				        	String schoolyear = null;
				        	if(round==1){
					        	Matcher mschool = Pattern.compile("<dt>大学</dt>\\s+.*?pf_spread'>(.+?)</a>-.+?(\\d+)年").matcher(info_result);   
					            if(!mschool.find()){
					            	System.out.println("学校为空或不能正常加载本页面！");
					            }
					            else{
					                schoolname = mschool.group(1);
					                schoolyear = mschool.group(2);
					                //判断是否为在读生
					                int poyr = Integer.parseInt(potime.substring(0, 4));
					                int schoolyr = Integer.parseInt(schoolyear);
					                int yeargap = poyr - schoolyr;
					                if(yeargap<=4&&yeargap>=0){
					                	System.out.print("符合条件！"+schoolname);
					                    System.out.print(schoolyear);
					                	String insert = "insert into "+tableName+"(prflurl,name,post,potime,school,schoolyear,isEnrolling) values('"+prflurl+"','"+name+"','"+post+"','"+potime+"','"+schoolname+"','"+schoolyear+"','1')"; 
					                    if (boringdb.insertSQL(insert) == true) {  
					                        System.out.println("  insert successfully");  
					                        isInsertedin1 = true;
					                    }  
					                }
					                else{
					                	System.out.println("不是在读学生！   "+schoolname+schoolyear);
					                }
					                
					            }
				        	}
				        	//round 2：如果正在得到“现居地”	
				        	else if(round==2){
				        		Matcher mreside = Pattern.compile("现居 (.+?)</li>").matcher(info_result);   
				        		if(!mreside.find()){
				        			System.out.println("没有现居地！");
				        		}
				        		else{
				        			String resideCity = mreside.group(1);
				        			System.out.print("现居地："+resideCity);
				        			//如果在round 1 中数据已经被插入了
				        			if(isInsertedin1){
				        				//或者查询最大id进行更新：UPDATE boring3 SET reside = 'NANJING' WHERE id = (select a.id from(select max(id) id from boring3 a)a)
				        				//String update = "UPDATE boring4 SET reside = '"+ resideCity +"' WHERE prflurl = '"+prflurl+"' AND potime = '"+ potime +"'"; 
				        				//不查询potime只查询prflurl
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
				    //翻页
				    offset=offset+10;
				}
			}
			//当用完accountNum个账号之后
	    	if(j==accountNum-1){
	    		System.out.println("已经用完j=="+accountNum+"个账号!");
	    		j=0; //回到第一个账号继续运行
	    	}
	    }
	    long endTime=System.currentTimeMillis(); //获取结束时间   
	    System.out.println("本次程序运行时间： "+(endTime-startTime)+"ms");  
	    System.out.println("使用了"+(accountNum-count)+"个账号爬取"  );  
	    
	}
	
	
}
