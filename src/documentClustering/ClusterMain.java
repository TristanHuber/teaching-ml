package documentClustering;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ClusterMain {
	public static final String[] LIBRARY = {"grimm.txt", "sherlockHolmes.txt", "hamlet.txt", "kafka.txt",
											"smallmoby.txt", "huckleberryFinn.txt"};
	private static Collection<Document> library;
	
	
	// run shell for interacting with documents.
	public static void main(String[] args) throws FileNotFoundException {

		//library = parseLibrary();
		library = parseLibraryTFIDF();
		
		Scanner console = new Scanner(System.in);
		String input;
		intro();
		
		do {
			System.out.print("> ");
			input = console.next();
			switch (input) {
				case "help":
					printHelp();
					break;
				case "ls":
					list();
					break;
				case "docInfo":
					String documentName = console.next();
					docInfo(documentName);
					break;
				case "distance":
					String doc1 = console.next();
					String doc2 = console.next();
					reportDistance(doc1, doc2);
					break;
				case "closest":
					documentName = console.next();
					getClosest(documentName);
					break;
				default:
					System.out.println("unknown command");
					break;
			}
		} while (!input.equals("q"));	
	}
	
	
	
	
	
	//// more methods and impl stuff ////
	private static void getClosest(String documentName) {
		Document target = getDoc(documentName);
		if(target == null){
			System.out.println("no such document");
			return;
		}
		Map<Document, Double> documentDistances = new HashMap<Document, Double>();
		for(Document doc : library){
			documentDistances.put(doc, target.euclidianDistance(doc));
		}
		
		// display in sorted order
		System.out.println("documents, in order of distance:");
		double prevMin = -1;
		for(int i = 0; i < library.size(); i++){
			double min = Double.MAX_VALUE;
			Document minDoc = null;
			for(Document doc : documentDistances.keySet()){
				if(documentDistances.get(doc) < min && documentDistances.get(doc) > prevMin){
					minDoc = doc;
					min = documentDistances.get(doc);
				}
			}
			prevMin = min;
			System.out.println("\t" + minDoc.getName() + " distance: " + documentDistances.get(minDoc));
		}
		
	}

	private static void getDistances(String documentName) {
		for(Document doc1 : library){
			for(Document doc2 : library){
				reportDistance(doc1.getName(), doc2.getName());
			}
		}
	}

	private static void reportDistance(String docName1, String docName2) {
		Document doc1 = getDoc(docName1);
		Document doc2 = getDoc(docName2);
		if(doc1 == null || doc2 == null){
			System.out.println("one of your documents was not found..");
		} else {
			System.out.print("distance between " + doc1.getName() + " and " + doc2.getName() + " : ");
			System.out.println(doc1.euclidianDistance(doc2));
		}
		
	}
	
	private static Document getDoc(String documentName){
		for(Document doc : library){
			if(doc.getName().equalsIgnoreCase(documentName)){
				return doc;
			}
		}
		return null;
	}
	

	private static void docInfo(String documentName) {
		Document doc = getDoc(documentName);
		if(doc != null){
			System.out.println(doc.getName() + " " + doc.getSize() + ":");
			List<String> topWords = doc.getTopWords(100);
			for(String word : topWords){
				System.out.println("\t" + word + " : " + doc.getCount(word));
			}
		} else {
			System.out.println("No such document: " + documentName);
		}
	}

	private static void list() {
		System.out.println("documents in library are:");
		for(Document doc : library){
			System.out.println("\t" + doc.getName() + " (" + doc.getSize() + ")");
		}
	}

	private static void printHelp() {
		System.out.println("Available commands are:");
		System.out.println("\tls : list all available documents");
		System.out.println("\tdocInfo <documentName> : provide information about a document");
		System.out.println("\tclosest <documentName> : list other documents ordered by distance to this document");
		System.out.println("\tdistance <documentName> <documentName> : report distance between two documents");
		System.out.println("\tq : quit");
		
	}

	public static void intro(){
		System.out.println("Hi there, I have a library of documents");
		System.out.println("You can search for documents you know, or");
		System.out.println("you can ask for the distance between documents");
		System.out.println("or ask for information about docs.");
		System.out.println("Type help for help..");
		
	}
	
	public static Collection<Document> parseLibraryTFIDF() throws FileNotFoundException {
		Set<Document> result = new HashSet<Document>();
		Map<String, Integer> docFrequency = new HashMap<String, Integer>();
		
		for (String filename : LIBRARY){
			Set<String> seenWords = new HashSet<String>();
			String filePath = "data/" + filename;
			Scanner fileScan = new Scanner(new File(filePath));
			while(fileScan.hasNext()){
				String word = fileScan.next();
				if(!seenWords.contains(word)){
					seenWords.add(word);
					if(!docFrequency.containsKey(word)){
						docFrequency.put(word, 0);
					}
					docFrequency.put(word, docFrequency.get(word) + 1);
				}
			}
			
		}
		for (String filename : LIBRARY){
			String filePath = "data/" + filename;
			Scanner fileScan = new Scanner(new File(filePath));
			result.add(new Document(filename, fileScan, docFrequency, LIBRARY.length));
		}
		return result;
	}
	
	
	public static Collection<Document> parseLibrary() throws FileNotFoundException{
		Set<Document> result = new HashSet<Document>();
		for (String filename : LIBRARY){
			String filePath = "data/" + filename;
			Scanner fileScan = new Scanner(new File(filePath));
			result.add(new Document(filename, fileScan));
		}
		return result;
	}
}
