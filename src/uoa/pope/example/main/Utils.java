package uoa.pope.example.main;

import java.util.ArrayList;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class Utils {

	public static void printSearchStats (String resultsName, ArrayList<Tweet> dummyResults, String string, String string2, String string3) {
		
		int keyword1Counter =0;
		int keyword2Counter =0;
		int keyword3Counter =0;
		
		for (int i = 0;i<dummyResults.size();i++) {
	    	Tweet tweet =  dummyResults.get(i);
			if (tweet.body.contains(string)) {
				keyword1Counter ++ ;
			}
			if (tweet.body.contains(string2)) {
				keyword2Counter ++ ;
			}
			if (tweet.body.contains(string3)) {
				keyword3Counter ++ ;
			}
	      }
		System.out.println("Results statistics for : " +  resultsName);
		
		System.out.println(string + " : " + keyword1Counter );
		System.out.println(string2 + " : " + keyword2Counter );
		System.out.println(string3 + " : " + keyword3Counter );
  	   
		
	}
	
	public static void runQueryWithFormatter (String queryString, OntModel model) {
		
		System.out.println("Executing QUERY:");
		System.out.println (queryString+"\n");
		System.out.println("RESULT:");
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		ResultSetFormatter.out(System.out, results, query);
		qe.close();
	}
	
public static void runQueryWithoutFormatter (String queryString, OntModel model) {
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		qe.close();
	}
	
	
}
