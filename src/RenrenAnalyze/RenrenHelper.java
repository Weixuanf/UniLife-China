package RenrenAnalyze;

public class RenrenHelper {
	
	/**
	 * filter emoji-过滤掉emoji表情
	 * 
	 * */
	public static String stripEmoji(String content){
		
	    content = content.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
	    return content; 
	}
	
	/**
	 * filter HTML-过滤掉HTML标签
	 * 
	 * */
    public static String stripHtml(String content) { 
    // <p>段落替换为换行 
    //content = content.replaceAll("<p .*?>", "\r\n"); 
    // <br><br/>替换为换行 
    //content = content.replaceAll("<br\\s*/?>", "\r\n"); 
    // 去掉其它的<>之间的东西 
    content = content.replaceAll("\\<.*?>", ""); 
    //去掉换行符等空字符
    content = content.replaceAll("\n", "  "); 
    //去掉空字符
    content = content.replaceAll("\\s*", ""); 
    // 还原HTML 
    // content = HTMLDecoder.decode(content); 
    //filter emoji过滤掉emoji表情
    content = content.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
    return content; 
    }
	
	/**
	 * 判断爬取的人人profile页面是否需要更换新账号登陆（由于被封号或频繁登陆需要验证码）
	 * */
	public static boolean needChangeUser(String info_result,int currentJ,String curentEmail) {
		//判断是否需要登录（频繁登陆需验证码）
    	if(info_result.indexOf("请输入密码")!=-1) {
    		System.out.println("");
    		System.out.println("【【【需要登录！！账号 email["+currentJ+"] "+curentEmail+"或已被查封】】】");
    		System.out.println("");
    		return true;
        }
    	//判断是否频繁登陆需验证码
    	else if (info_result.indexOf("刚刚你看了100个同学的页面，谢谢你对人人网的支持")!=-1) {
			System.out.println("");
			System.out.println("频繁登陆需验证码！  email["+currentJ+"] "+curentEmail+"，刚刚你看了100个同学的页面");
			System.out.println("");
			//System.exit(0);  //结束程序
			return true;
		}
    	else return false;
	}
}

