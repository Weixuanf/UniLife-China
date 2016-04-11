package RenrenAnalyze;

public class RenrenHelper {
	
	/**
	 * filter emoji-���˵�emoji����
	 * 
	 * */
	public static String stripEmoji(String content){
		
	    content = content.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
	    return content; 
	}
	
	/**
	 * filter HTML-���˵�HTML��ǩ
	 * 
	 * */
    public static String stripHtml(String content) { 
    // <p>�����滻Ϊ���� 
    //content = content.replaceAll("<p .*?>", "\r\n"); 
    // <br><br/>�滻Ϊ���� 
    //content = content.replaceAll("<br\\s*/?>", "\r\n"); 
    // ȥ��������<>֮��Ķ��� 
    content = content.replaceAll("\\<.*?>", ""); 
    //ȥ�����з��ȿ��ַ�
    content = content.replaceAll("\n", "  "); 
    //ȥ�����ַ�
    content = content.replaceAll("\\s*", ""); 
    // ��ԭHTML 
    // content = HTMLDecoder.decode(content); 
    //filter emoji���˵�emoji����
    content = content.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
    return content; 
    }
	
	/**
	 * �ж���ȡ������profileҳ���Ƿ���Ҫ�������˺ŵ�½�����ڱ���Ż�Ƶ����½��Ҫ��֤�룩
	 * */
	public static boolean needChangeUser(String info_result,int currentJ,String curentEmail) {
		//�ж��Ƿ���Ҫ��¼��Ƶ����½����֤�룩
    	if(info_result.indexOf("����������")!=-1) {
    		System.out.println("");
    		System.out.println("��������Ҫ��¼�����˺� email["+currentJ+"] "+curentEmail+"���ѱ���⡿����");
    		System.out.println("");
    		return true;
        }
    	//�ж��Ƿ�Ƶ����½����֤��
    	else if (info_result.indexOf("�ո��㿴��100��ͬѧ��ҳ�棬лл�����������֧��")!=-1) {
			System.out.println("");
			System.out.println("Ƶ����½����֤�룡  email["+currentJ+"] "+curentEmail+"���ո��㿴��100��ͬѧ��ҳ��");
			System.out.println("");
			//System.exit(0);  //��������
			return true;
		}
    	else return false;
	}
}

