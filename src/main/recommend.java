package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBconnect;

public class recommend {
	public static void main(String[] args){
		
	}
	public void constructSim(){
		PreparedStatement selectRatingTime = null;
		selectRatingTime = DBconnect.getConn().prepareStatement(
				"select * from behavior where user_id = ? and article_id = ?");
		selectRatingTime.setString(1, user);
		selectRatingTime.setString(2, article);
		ResultSet ratingTime = selectRatingTime.executeQuery();
		String temp = "";
		while (ratingTime.next()) {
			temp = ratingTime.getDate("ratingTime").toString();
		}
	}
}
