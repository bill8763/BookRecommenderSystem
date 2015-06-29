package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import tw.edu.ncu.im.Util.HttpIndexSearcher;
import document_clustering.Avg_link_all;


public class articleTopicSimilarity {
	public static void main(String[] args) throws Exception{
		simToDB();
	}
	public static void simToDB() throws Exception{
		String path = "D:/dataset/";
		String articlePath = "D:/dataset/mainWords/";
		HttpIndexSearcher searcher = new HttpIndexSearcher();
		searcher.url = "http://140.115.82.105/searchweb/";
		new DBconnect();
		PreparedStatement selectProfile = null;
		selectProfile = DBconnect
				.getConn()
				.prepareStatement(
						"select * from concept_article");
		ResultSet concept_article = selectProfile.executeQuery();
		while(concept_article.next()){
			String tempConcept=concept_article.getString("concept_id");
			String tempTopic=concept_article.getString("topic_id");
			String article=concept_article.getString("article_id");
			Avg_link_all docLink = new Avg_link_all();
			Double sim = docLink.conceptArticleSim(path, tempConcept,
					tempTopic, articlePath + article, searcher);
			
			PreparedStatement insertConcept = null;
			insertConcept = DBconnect.getConn().prepareStatement(
					"insert into concept_userarticle(concept_id,topic_id,article_id,similarity) "
							+ "Values (?,?,?,?) ON DUPLICATE KEY UPDATE similarity=?");
			insertConcept.setString(1, tempConcept);
			insertConcept.setString(2, tempTopic);
			insertConcept.setString(3, article);
			insertConcept.setDouble(4, sim);
			insertConcept.setDouble(5, sim);
			insertConcept.executeUpdate();
		}
		
	}
}
