package RenrenAnalyze;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Statistic methods to analyze data
 * 
 * */
public class RenrenStats {

	public RenrenStats() {
		// TODO Auto-generated constructor stub
	}
	public void schoolCount(String tableName){
		//����mysql���ݿ�
		dbhelper db = new dbhelper();
		db.connSQL();
		System.out.println(tableName+" connected");
		ResultSet rs = db.selectSQL("SELECT school, count(1) AS counts FROM "+ tableName
				+ " GROUP BY school HAVING counts>1 ORDER BY `counts` DESC ");
		System.out.println(tableName+"���ݿ���ѧУ����Ƶ�������ǣ�");
	    try {
			while(rs.next()){        //ѭ��������ѯ�����
				
			    System.out.println(rs.getString("school")+"�� "+rs.getString("counts"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String tableName ="studying";
		RenrenStats stats = new RenrenStats();
		stats.schoolCount(tableName);
	}

}

