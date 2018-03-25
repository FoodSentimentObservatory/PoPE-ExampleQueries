package uoa.pope.example.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import uoa.pope.example.queries.RunPoPEInferences;
import uoa.pope.example.queries.RunQueries;

public class RunEvaluation {
    
	
	
	 ArrayList <Tweet> dummyResultsSearch1 = new ArrayList ();
	 ArrayList <Tweet> dummyResultsSearch2 = new ArrayList ();
	 ArrayList <Tweet> dummyResultsSearch3 = new ArrayList ();
	 ArrayList <Tweet> filteringResult = new ArrayList ();
	 ArrayList <Tweet> retweetRemovalResult = new ArrayList ();
	 
	
	
	public RunEvaluation () throws IOException {
		    
	      
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
	      
	  //    Utils.printSearchStats("Search 1",dummyResultsSearch1, "chicken","beef", "raw");
	   //   Utils.printSearchStats("Search 2",dummyResultsSearch2, "chicken","beef", "raw");
	    //  Utils.printSearchStats("Search 3 - check for keywords from Search 1 and 2",dummyResultsSearch3, "chicken","beef", "raw");
	     // Utils.printSearchStats("Search 3 - check for keywords from Search 3",dummyResultsSearch3, "car","bike", "raw");
	      
	     // model.write(System.out, "RDF/XML") ;
	      
	      //Enrich with inferencess 
	      RunPoPEInferences inf = new   RunPoPEInferences (model);
	      inf.runInferences();
	     
	      ExtendedIterator <Individual> it = model.listIndividuals();
	      //System.out.println( it.toList());
	      //System.out.println("Number of individuals" + it.toList().size());
	      //count stats 
	      it = model.listIndividuals();
	       int attributeCounter = 0;
	       int individualCount = 0;
	       while (it.hasNext()) {
	    	   individualCount++;
	    	   if (it.next().getURI().contains("http://foobs.org/Post#")) {
	    		   attributeCounter++; 
	    	   }
	       }
	       System.out.println("INDIVIDUAL COUNT " +individualCount );
	       System.out.println("ATTRIBUTE COUNT " +attributeCounter );
	      
	      //System.out.println( it.toList());
	      //System.out.println("Number of individuals" + it.toList().size());
	      
	      RunQueries queryFactory = new RunQueries (model);
	      
	      queryFactory.runQuerySetDataItems();
	      queryFactory.runQuerySet1();
	      
	      String fileName = "dataset.ttl";
	      FileWriter out = new FileWriter( fileName );
	      try {
	          model.writeAll( out, "TTL" );
	      }
	      finally {
	         try {
	             out.close();
	         }
	         catch (IOException closeException) {
	             // ignore
	         }
	      }
	      
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

      try {
		new RunEvaluation ();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      
	}
	
	
	
	private  void performExampleTwitterSearch (OntModel model) {
		
		//perform two consecutive searches utilising Twitter API reference end point
		
		String user = "Bob";
		String user2 = "ALice";
		String user3 = "Barbara";
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
		
		
		
		Date startedAt = new Date(java.sql.Timestamp.valueOf("2017-02-18 11:10:10.0").getTime());
		Date endedAt = new Date(java.sql.Timestamp.valueOf("2017-02-18 13:10:10.0").getTime());
			
		// -------  create PoPE annotations START ----------------
		AnnotationFactory popeAnnotationsFactory  = new AnnotationFactory (model);
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID,  lat,lon,radius,search,dummyResultsSearch1,user,startedAt,endedAt);		
		// -------  create PoPE annotations END ----------------
		
		// Search 2 - same group, same keywords as Search 1
		//helper variable to help us simulate appropriate timestamps
		search = 2;
		//perform request and store results
		dummyResultsSearch2 = searchTwitter (keywords, groupID, null, lat,lon,radius,search);
		
		
		// -------  create PoPE annotations START ----------------
		startedAt = new Date(java.sql.Timestamp.valueOf("2017-02-22 11:10:10.0").getTime());
		endedAt = new Date(java.sql.Timestamp.valueOf("2017-02-26 12:10:10.0").getTime());
		
		
	
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID,  lat,lon,radius,search,dummyResultsSearch2,user,startedAt,endedAt);		
		// -------  create PoPE annotations END ----------------
		
		
		//Perform filtering activity
		ArrayList [] input = {dummyResultsSearch1,dummyResultsSearch2};
		filteringResult =  filterByDateAndKeyword (input,"raw",new Date(java.sql.Timestamp.valueOf("2017-02-16 15:10:10.0").getTime()), new Date(java.sql.Timestamp.valueOf("2017-02-20 15:10:10.0").getTime()));
		
		// -------  create PoPE annotations START ----------------
			
	    popeAnnotationsFactory.createFilteringAnnotations(groupID,search,filteringResult,"raw", new Date(java.sql.Timestamp.valueOf("2017-02-16 15:10:10.0").getTime()),new Date(java.sql.Timestamp.valueOf("2017-02-20 15:10:10.0").getTime()), user,startedAt,endedAt);		
			// -------  create PoPE annotations END ----------------
		
		
		//System.out.println("FILTERING"+filteringResult.size());
		for (int i=0;i<filteringResult.size();i++) {
    		String tweetBody = filteringResult.get(i).body;
    		//System.out.println(tweetBody);
    	}
		
		
		retweetRemovalResult =  removeReTweets (filteringResult);
		
		// -------  create PoPE annotations START ----------------
				startedAt = new Date(java.sql.Timestamp.valueOf("2017-02-22 14:02:10.0").getTime());
				endedAt = new Date(java.sql.Timestamp.valueOf("2017-02-26 14:04:10.0").getTime());
			    popeAnnotationsFactory.createRetweetFilteringAnnotations(retweetRemovalResult,  user3,startedAt,endedAt, "^RT\\s@");		
					// -------  create PoPE annotations END ----------------
		
		
		
		ArrayList frequencyCount = performFrequencyAnalysis (retweetRemovalResult, 3); 
		
		
		
		// -------  create PoPE annotations START ----------------
		startedAt = new Date(java.sql.Timestamp.valueOf("2017-02-22 14:10:10.0").getTime());
		endedAt = new Date(java.sql.Timestamp.valueOf("2017-02-26 14:10:10.0").getTime());
	    popeAnnotationsFactory.createFrequencyCountAnnotations(frequencyCount,  user2,startedAt,endedAt, 3);		
			// -------  create PoPE annotations END ----------------
		
		
		
		// Search 2 - different group, different keywords as Search 1 and Search 2
		//helper variable to help us simulate appropriate timestamps
		search = 1;
		keywords = "car AND bike";
		groupID = "DummySearch_Control_Group";
		//perform request and store results
		dummyResultsSearch3 = searchTwitter (keywords, groupID, null, lat,lon,radius,search);
		
		startedAt = new Date(java.sql.Timestamp.valueOf("2017-02-18 14:10:10.0").getTime());
		endedAt = new Date(java.sql.Timestamp.valueOf("2017-02-18 15:10:10.0").getTime());
				
		// -------  create PoPE annotations START ----------------
	
		popeAnnotationsFactory.createSearchAnnotations(keywords, groupID,  lat,lon,radius,search,dummyResultsSearch3,user,startedAt,endedAt);		
		// -------  create PoPE annotations END ----------------
		
		
		System.out.println( "Number of datasetItems generated: "+(dummyResultsSearch3.size()+frequencyCount.size()+retweetRemovalResult.size()+filteringResult.size()+dummyResultsSearch2.size()+dummyResultsSearch1.size()));
		
	}
	
    private  ArrayList<Tweet> filterByDateAndKeyword (ArrayList [] input, String filteringKeyword, Date startTimestampthreshold,Date endTimestampthreshold) {
		
    	ArrayList <Tweet> result = new ArrayList();
    	
    	for (int i=0;i<input.length;i++) {
    		for (int j=0;j<input[i].size();j++) {
        		Tweet tweet = (Tweet) input[i].get(j);
        		//THIS isi the condition captured in provenance record with parameters and a constraint
        		if (tweet.body.contains(filteringKeyword)&&tweet.created.getTime()<endTimestampthreshold.getTime()&&tweet.created.getTime()>startTimestampthreshold.getTime()) {
        			result.add(tweet);
        		}
        	}
    	}
    	
    	return result;
	}
	
    private  ArrayList<Tweet> removeReTweets (ArrayList <Tweet> input) {
    	ArrayList <Tweet> result = new ArrayList();
    	
    	for (int i=0;i<input.size();i++) {
    		String tweetBody = input.get(i).body;
    		if (!tweetBody.startsWith("RT @", 0)) {
    			result.add(input.get(i));
    		}
    	}
    	
    	return result;
	}

    private   ArrayList<String> performFrequencyAnalysis (ArrayList <Tweet> input, int numberOfWords) {
		   
    	ArrayList <String> result = new ArrayList();
    	
    	//just generate some dummy results
    	result.add("beef");
    	result.add("chicken");
    	result.add("raw");
    	
    	
    	
    	return result;
	}
    
    /*
     * This is a dummy method for simulating some output from Twitter API 
     */
    private  ArrayList searchTwitter (String keywordsString, String GroupId, String LastTweetPost, double lat, double lon, double radius, int search) {
		String [] dummyLocations = {"Glasgow", "Aberdeen", "Edinburgh"};
    	String[] keywords = keywordsString.split ("AND");
    	ArrayList tweets = new ArrayList ();
		Date d1;
		Date d2;
		
		//set timestamp range for simulated output
		if (search==1) {
			d1 = new Date(java.sql.Timestamp.valueOf("2017-02-11 10:10:10.0").getTime());
			d2 = new Date(java.sql.Timestamp.valueOf("2017-02-18 10:10:10.0").getTime());
		}
		else {
			d1 = new Date(java.sql.Timestamp.valueOf("2017-02-19 10:10:10.0").getTime());
			d2 = new Date(java.sql.Timestamp.valueOf("2017-02-22 10:10:10.0").getTime());
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
					
					randomNum = ThreadLocalRandom.current().nextInt(0,3);
					tweet.location = dummyLocations[randomNum];
					tweets.add(tweet);
				}
		
    	return tweets;
	}

}
