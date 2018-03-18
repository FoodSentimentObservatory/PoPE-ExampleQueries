package uoa.pope.example.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import uoa.pope.example.queries.RunPoPEInferences;
import uoa.pope.example.queries.RunQueries;

public class RunEvaluation {
    
	
	
	 ArrayList <Tweet> dummyResultsSearch1 = new ArrayList ();
	 ArrayList <Tweet> dummyResultsSearch2 = new ArrayList ();
	 ArrayList <Tweet> dummyResultsSearch3 = new ArrayList ();
	
	
	public RunEvaluation () {
		    
	      
	      OntModel model = ModelFactory.createOntologyModel();
	      System.out.println("Loading ontologies");
	      try 
	      {
	          InputStream in = FileManager.get().open("pope.rdf");
	          try 
	          {
	        	  model.read(in, null);
	          } 
	          catch (Exception e) 
	          {
	              e.printStackTrace();
	          }
	         
	      } 
	      catch (JenaException je) 
	      {
	          System.err.println("ERROR" + je.getMessage());
	          je.printStackTrace();
	          System.exit(0);
	      }
	      
	      performExampleTwitterSearch (model);
	      
	      Utils.printSearchStats("Search 1",dummyResultsSearch1, "chicken","beef", "raw");
	      Utils.printSearchStats("Search 2",dummyResultsSearch2, "chicken","beef", "raw");
	      Utils.printSearchStats("Search 3 - check for keywords from Search 1 and 2",dummyResultsSearch3, "chicken","beef", "raw");
	      Utils.printSearchStats("Search 3 - check for keywords from Search 3",dummyResultsSearch3, "car","bike", "raw");
	      
	      //model.write(System.out, "RDF/XML") ;
	      
	      //Enrich with inferencess 
	      RunPoPEInferences inf = new   RunPoPEInferences (model);
	      inf.runInferences();
	      
	      RunQueries queryFactory = new RunQueries (model);
	      queryFactory.runQuerySet1();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

      new RunEvaluation ();
      
      
	}
	
	
	
	private  void performExampleTwitterSearch (OntModel model) {
		
		//perform two consecutive searches utilising Twitter API reference end point
		
		String user = "Bob";
		String keywords = "chicken AND beef";
		double lat = 54.23  ;
		double lon = 1.5 ; 		
		double radius = 12;	
		// aunique id grouping two or more searches
		String groupID = "DummySearch";
		
		// Search 1 
		//helper variable to help us simulate appropriate timestamps
		int search = 1;
		//perform request and store results
		dummyResultsSearch1 = searchTwitter (keywords, groupID, null, lat,lon,radius,search);
		
			
		// -------  create PoPE annotations START ----------------
		AnnotationFactory popeAnnotationsFactory  = new AnnotationFactory (model);
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID, null, lat,lon,radius,search,dummyResultsSearch1,user);		
		// -------  create PoPE annotations END ----------------
		
		// Search 2 - same group, same keywords as Search 1
		//helper variable to help us simulate appropriate timestamps
		search = 2;
		//perform request and store results
		dummyResultsSearch2 = searchTwitter (keywords, groupID, null, lat,lon,radius,search);
		
		// -------  create PoPE annotations START ----------------
		popeAnnotationsFactory  = new AnnotationFactory (model);
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID, null, lat,lon,radius,search,dummyResultsSearch2,user);		
		// -------  create PoPE annotations END ----------------
		
		// Search 2 - different group, different keywords as Search 1 and Search 2
		//helper variable to help us simulate appropriate timestamps
		search = 1;
		keywords = "car AND bike";
		groupID = "DummySearch_Control_Group";
		//perform request and store results
		dummyResultsSearch3 = searchTwitter (keywords, groupID, null, lat,lon,radius,search);
				
		// -------  create PoPE annotations START ----------------
		popeAnnotationsFactory  = new AnnotationFactory (model);
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID, null, lat,lon,radius,search,dummyResultsSearch3,user);		
		// -------  create PoPE annotations END ----------------
		
	}
	
    private  void filterByDateAndKeyword (Model model) {
		
	}
	
    private  void removeReTweets (Model model) {
		
	}

    private  void performFrequencyAnalysis (Model model) {
		
	}
    
    /*
     * This is a dummy method for simulating some output from Twitter API 
     */
    private  ArrayList searchTwitter (String keywordsString, String GroupId, String LastTweetPost, double lat, double lon, double radius, int search) {
		
    	String[] keywords = keywordsString.split ("AND");
    	ArrayList tweets = new ArrayList ();
		Date d1;
		Date d2;
		
		//set timestamp range for simulated output
		if (search==1) {
			d1 = new Date(java.sql.Timestamp.valueOf("2017-03-11 10:10:10.0").getTime());
			d2 = new Date(java.sql.Timestamp.valueOf("2017-03-18 10:10:10.0").getTime());
		}
		else {
			d1 = new Date(java.sql.Timestamp.valueOf("2017-03-19 10:10:10.0").getTime());
			d2 = new Date(java.sql.Timestamp.valueOf("2017-03-26 10:10:10.0").getTime());
		}
		
		//generate dummy output containing Tweets
				for (int i = 0 ;i<100 ;i++) {

					long random = ThreadLocalRandom.current().nextLong(d1.getTime(), d2.getTime());
					Tweet tweet = new Tweet (); 
					tweet.created = new Date(random);
					
					//construct sample body of Tweet
					
					//decide if it will be a re-Tweet
					int randomNum = ThreadLocalRandom.current().nextInt(0,11);
					String tweetBody = "";
					if (randomNum<4) {
						tweetBody = "RT @someAuthor ";
					}
					//add search keywords in the body
					tweetBody = tweetBody + keywords[0] + " " + keywords[1];
					//add additional filtering keyword
					if (randomNum<2 || randomNum>8) {
						tweetBody = tweetBody +" raw";
					}
					//add random body
					tweetBody = tweetBody +" " + UUID.randomUUID().toString();
					tweet.body = tweetBody;
					tweets.add(tweet);
				}
		
    	return tweets;
	}

}
