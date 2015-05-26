package main;

public class ConceptInterest {
	ConceptInterest(String user_article,String topic,String concept,String day,String readingDay,double fi){
		int interest = ConceptsSimilarity(user_article , topic, concept) *  1/(day-readingDay)^(1/fi);
	}

}
