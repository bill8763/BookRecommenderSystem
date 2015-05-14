package user_profile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import ontology.DBconnect;

public class user_in_article extends DBconnect {

	public user_in_article() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void UIA(int user_id,double phi) throws SQLException, IOException{ //對user每篇閱讀過的文章去做計算，輸出
		
		PreparedStatement  select_user_article = null;
		select_user_article = conn.prepareStatement("select * from behavior where user_id = ?");
		select_user_article.setInt(1, user_id);
		ResultSet user_article_rs = select_user_article.executeQuery();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:/DataTemp/processing/Explicit_profile/"+user_id + "_UIA.txt"));
		
		while(user_article_rs.next()){
			double dateDis;
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");	
			String s = sdf.format(user_article_rs.getDate("last_time"));       
			String e = sdf.format(new Date(System.currentTimeMillis()));
			
			
			if (calcutePeriod(s,e) <=1  ){
				dateDis = 1; //避免相距天數小於等於1
			}
			else{
				
				dateDis = (double)Math.pow((calcutePeriod(s,e)),(double)1/phi); //最後發生的行為和目前日期的間隔天數 取(1/2)次方
			}
			double uia = (double)1/dateDis; //分子為使用者行為，目前暫不做，需要user去評估
			
			bw.write(user_article_rs.getInt("article_id")+";"+uia);
			bw.newLine();
		}
		bw.flush();
		bw.close();	
		conn.close();
	}
	
	private static long calcutePeriod(String dateStr1,String dateStr2) { //計算相差日期
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
	    ParsePosition pos1 = new ParsePosition(0);
	    ParsePosition pos2 = new ParsePosition(0);
	    Date date1= sdf.parse(dateStr1,pos1);
	    Date date2= sdf.parse(dateStr2,pos2);
	    long dateDiff = 0;
	    if(date1.getTime()<date2.getTime()) {
	        dateDiff = ((date2.getTime()/1000-date1.getTime()/1000)/(24*60*60));
	    }
	    else {
	        dateDiff = ((date1.getTime()/1000-date2.getTime()/1000)/(24*60*60));
	    }
	 
	    return dateDiff;
	}
	public static void main(String[] args) throws Exception {
		user_in_article uia_test = new user_in_article();
		uia_test.UIA(1, 1);
	}

}
