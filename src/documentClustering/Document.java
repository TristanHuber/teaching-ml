package documentClustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Document {
	private Map<String, Double> wordCounts;
	private String name;
	private int size;
	
	
	/**
	 * Create a document with literal word counts.
	 * 
	 * @param name		- name of this document
	 * @param docScan	- a scanner over the file where this document is stored. 
	 */
	public Document(String name, Scanner docScan){
		docScan.useDelimiter("[^\\w']+");
		wordCounts = new HashMap<String, Double>();
		this.name = name;
		while(docScan.hasNext()){
			String word = docScan.next().toLowerCase();
			if(!wordCounts.containsKey(word)){
				wordCounts.put(word, 0.0);
			}
			wordCounts.put(word, wordCounts.get(word) + 1);
			size++;
		}
		normalize();
	}
	
	private void normalize(){
		for (String word : wordCounts.keySet()){
			double count = wordCounts.get(word);
			wordCounts.put(word, count / size);
		}
	}
	
	/**
	 * Construct a document which stores TF-IDF instead of word counts.
	 * 
	 * @param name 			- the name this document will have
	 * @param docScan		- Scanner access to the file where this document is stored
	 * @param docFrequency	- data about the corpus this document is a part of. specifically a map from
	 * 						  words to the number of documents with such a word
	 * @param libSize		- number of documents in the corpus
	 */
	public Document(String name, Scanner docScan, Map<String, Integer> docFrequency, int libSize){
		if(libSize == 0 || docFrequency == null){
			throw new IllegalArgumentException();
		}
		docScan.useDelimiter("[^\\w']+");

		wordCounts = new HashMap<String, Double>();
		this.name = name;
		
		// initialize count of words
		while(docScan.hasNext()){
			String word = docScan.next().toLowerCase();
			if(!wordCounts.containsKey(word)){
				wordCounts.put(word, 0.0);
			}
			wordCounts.put(word, wordCounts.get(word) + 1);
			this.size++;
		}
		
		// highest word occurence (used for normalization of document size)
		/*double max = 0.0;
		for(String word : wordCounts.keySet()){
			if(wordCounts.get(word) > max){
				max = wordCounts.get(word);
			}
		}*/
		double max = this.size;
				
		// replace word counts with tf-idf value
		for(String word : wordCounts.keySet()){
			double tf = wordCounts.get(word) / max;
			double idf = Math.log(libSize / (1.0 + docFrequency.get(word)));
			wordCounts.put(word, tf * idf);
		}
		
		
	}
	
	
	/**
	 * 
	 * @return the name this document was given
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return the number of words in this document
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * return the 'count' of a given word. Note that this is not necessarily the integer
	 * number of occurences of this word, it may be some other metric: % frequency of word,
	 * or tf-idf value.
	 * 
	 * @param word 	- word's frequency to return
	 * @return the metric for that word. (0, if this word was not in original document)
	 */
	public double getCount(String word){
		if (!wordCounts.containsKey(word)){
			return 0.0;
		} else {
			return wordCounts.get(word);
		}
	}
	
	/**
	 * Compute the Euclidian Distance between this document and another. Euclidian distance
	 * is understood to be:
	 * 
	 * square root of ( sum over w of (this.getCount(w) - other.getCount(w))^2 )
	 * 
	 * Note about runtime: guaranteed to be O(this.size + other.size), in the worst case
	 * 
	 * @param other 	- document to compute distance to
	 * @return	a double equal to the euclidian distance between this and other
	 */
	public double euclidianDistance(Document other){
		// distance from my words to theirs
		double result = 0;
		for (String word : wordCounts.keySet()){
			double temp = wordCounts.get(word) - other.getCount(word);
			result += temp * temp;
		}
		
		// hanlde words that are in other, but not on my document
		for (String word : other.wordCounts.keySet()){
			if (!this.wordCounts.containsKey(word)){
				double temp = other.wordCounts.get(word);
				result += temp * temp;
			}
		}
		return Math.sqrt(result);
	}
	
	/**
	 * Return a list of the top rated words in this document. Top rated are the words
	 * with the highest metric. Returned in sorted order such that:
	 * 
	 * for all i: this.getCount(result.get(i)) >= this.getCount(result.get(i+1))
	 * 
	 * Complexity: O(count * this.size)
	 * 
	 * @param count 	- the number of top words to return
	 * @return	a sorted list of the count top words. Possible that the returned list is shorter
	 * 		than count, if this document has fewer than count distinct words.
	 */
	public List<String> getTopWords(int count){
		List<String> result = new ArrayList<String>(count);
		for(int i = 0; i < count; i++){
			double max = 0;
			String maxWord = "";
			for(String word : this.wordCounts.keySet()){
				if(wordCounts.get(word) > max && !result.contains(word)){
					max = wordCounts.get(word);
					maxWord = word;
				}
			}
			result.add(maxWord);
		}
		return result;
	}
	
	@Override
	public String toString(){
		return this.name + " (" + this.size + ")";
	}
}
