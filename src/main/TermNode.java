package main;
public class TermNode{
	public double termFreq;
	String term;
	public TermNode(){
		
	}
	public TermNode(String _term){
		this.term = _term;
		this.termFreq = 1;
	}
	public TermNode(String _term,double _termFreq){
		this.term = _term;
		this.termFreq = _termFreq;
	}
	@Override
	public String toString(){
		return term;
		
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	/**
	 * @return the termFreq
	 */
	public double getTermFreq() {
		return termFreq;
	}
	/**
	 * @param termFreq the termFreq to set
	 */
	public void setTermFreq(double termFreq) {
		this.termFreq = termFreq;
	}
//  term node will not be added in DTG because of JUNG only allow unique node in graph	
//	@Override
//	public boolean equals(Object o){
//		TermNode anotherNode = (TermNode)o;
//		if(this.term.equals(anotherNode.term)){
//			return true;
//		}
//		return false;
//	}
	
}