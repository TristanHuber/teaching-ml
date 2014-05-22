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
	
	
	/*
	 * Create document using word count, or normalized word count
	 */
	public Document(String name, Scanner docScan){
		wordCounts = new HashMap<String, Double>();
		this.name = name;
		while(docScan.hasNext()){
			String word = docScan.next();
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
	
	/*
	 * create document using tfidf
	 */
	public Document(String name, Scanner docScan, Map<String, Integer> docFrequency, int libSize){
		if(libSize == 0){
			throw new IllegalArgumentException();
		}
		
		wordCounts = new HashMap<String, Double>();
		this.name = name;
		while(docScan.hasNext()){
			String word = docScan.next();
			if(!wordCounts.containsKey(word)){
				wordCounts.put(word, 0.0);
			}
			wordCounts.put(word, wordCounts.get(word) + 1);
			size++;
		}
		
		double max = 0;
		for(String word : wordCounts.keySet()){
			if(wordCounts.get(word) > max){
				max = wordCounts.get(word);
			}
		}
		
		for(String word : wordCounts.keySet()){
			double count = wordCounts.get(word) / max;
			double idf = Math.log(libSize / (1.0 + docFrequency.get(word)));
			if(idf == Double.NEGATIVE_INFINITY || count == Double.NaN){
				System.out.println(count + ", " + idf + ", " + libSize + ", " + docFrequency.get(word));
				
			}
			wordCounts.put(word, count * idf);
		}
		
		normalize();
	}
	
	
	
	public String getName(){
		return name;
	}
	
	public int getSize(){
		return size;
	}
	
	public double getCount(String word){
		if (!wordCounts.containsKey(word)){
			return 0.0;
		} else {
			return wordCounts.get(word);
		}
	}
	
	public double euclidianDistance(Document other){
		// distance from my words to theirs
		double result = 0;
		for (String word : wordCounts.keySet()){
			double temp = wordCounts.get(word) - other.getCount(word);
			result += temp * temp;
		}
		
		// increment by each word that is in other, but not on mine
		for (String word : other.wordCounts.keySet()){
			if (!this.wordCounts.containsKey(word)){
				double temp = other.wordCounts.get(word);
				result += temp * temp;
			}
		}
		return Math.sqrt(result);
	}
	
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
}
