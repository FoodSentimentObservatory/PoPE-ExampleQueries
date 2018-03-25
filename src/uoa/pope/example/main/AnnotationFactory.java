package uoa.pope.example.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class AnnotationFactory {
		
		
		
	private OntModel model ;
	
	//get/add some standard PROV properties 
	private  Property used  ;
	private  Property qualifiedUsage  ;
	private  Property entity  ;
	
	private  Property wasGeneratedBy ;
	private  Property qualifiedGeneration ;
	
	private  Property hadRole ;
	private  Property activity ;
	private  Property provValue ;
	private  Property qualifiedAssociation ;
	private  Property hadPlan ;
	private  Property agent ;
	private  Property actedOnBehalfOf ;
	private  Property wasAssociatedWith ;
	private  Property wasDerivedFrom ;
	private  Property wasAttributedTo ;
	
	
	
	private  Property hadMember ;
	
	private  Property specializationOf ;
	
	
	private Resource twitterAgent;
	private Resource endpoint;
	
	public AnnotationFactory (OntModel model) {
		
		this.model = model;
		//get/add some standard PROV properties 
		used =  model.getProperty(Namespaces.PROV_PREFIX+"used");
		qualifiedUsage = model.getProperty(Namespaces.PROV_PREFIX+"qualifiedUsage");
		entity = model.getProperty(Namespaces.PROV_PREFIX+"entity");
		
		wasGeneratedBy =  model.getProperty(Namespaces.PROV_PREFIX+"wasGeneratedBy");
		qualifiedGeneration =  model.getProperty(Namespaces.PROV_PREFIX+"qualifiedGeneration");
		wasDerivedFrom=  model.getProperty(Namespaces.PROV_PREFIX+"wasDerivedFrom");
		
		
		hadRole =  model.getProperty(Namespaces.PROV_PREFIX+"hadRole");
		activity =  model.getProperty(Namespaces.PROV_PREFIX+"activity");
		provValue =  model.getProperty(Namespaces.PROV_PREFIX+"value");
		qualifiedAssociation = model.getProperty(Namespaces.PROV_PREFIX+"qualifiedAssociation");
		hadPlan = model.getProperty(Namespaces.PROV_PREFIX+"hadPlan");
		agent = model.getProperty(Namespaces.PROV_PREFIX+"agent");
		actedOnBehalfOf = model.getProperty(Namespaces.PROV_PREFIX+"actedOnBehalfOf");
		wasAssociatedWith = model.getProperty(Namespaces.PROV_PREFIX+"wasAssociatedWith");
		wasAttributedTo = model.getProperty(Namespaces.PROV_PREFIX+"wasAttributedTo");
		
		hadMember = model.getProperty(Namespaces.PROV_PREFIX+"hadMember");
		
		twitterAgent = model.createResource(Namespaces.EXAMPLE_PREFIX +"Twitter");
		twitterAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
		endpoint = model.createResource(Namespaces.EXAMPLE_PREFIX +"APIendpoint");
		endpoint.addProperty( wasAttributedTo,twitterAgent);
		specializationOf =  model.getProperty(Namespaces.PROV_PREFIX+"specializationOf");
	}
	
	public void createSearchAnnotations (String keywords, String groupID,  double lat, double lon, double radius, int search, ArrayList<Tweet> dummyResults, String user, Date startedAt, Date endedAt) {
     Calendar cal = GregorianCalendar.getInstance();
	 //create relevant user agents 
     String userURI = Namespaces.EXAMPLE_PREFIX +user;
     Resource userAgent = model.createResource(userURI);
     userAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Person"));
   //create processing tool agent  
     Resource	toolAgent =  model.createResource(Namespaces.EXAMPLE_PREFIX + "SoftwareAgent_"+ UUID.randomUUID());
     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_TOOL_ONTOLOGY_PREFIX+"FoobsTool"));
     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
     		
	 //create searchActivity 
	 String activityURI = Namespaces.EXAMPLE_PREFIX + "SearchActivity_"+ groupID+"_"+search;
	 Resource searchActivity = model.createResource(activityURI);
	 searchActivity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Activity"));
	 cal.setTime(startedAt);
	 searchActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"startedAtTime"), model.createTypedLiteral(cal));
	 cal.setTime(endedAt);
	 searchActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"endedAtTime"), model.createTypedLiteral(cal));
	 
	
	 //associate PoPE role
	 String associationURI = Namespaces.EXAMPLE_PREFIX + "Association_"+ UUID.randomUUID();
	 Resource association = model.createResource(associationURI);
	 searchActivity.addProperty( qualifiedAssociation, association);
	 association.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Association"));
	 association.addProperty(hadPlan, model.getResource(Namespaces.PoPE_PREFIX+"CollectionViaApi"));
	 association.addProperty(agent, userAgent);
	 association.addProperty(agent, toolAgent);
	 toolAgent.addProperty(actedOnBehalfOf, userAgent);
	 searchActivity.addProperty( wasAssociatedWith, toolAgent);
	 searchActivity.addProperty( wasAssociatedWith, userAgent);
	 
	 //create entity representing Twitter API end point and link to search activity
	 
	 String twitterAPIURI = Namespaces.EXAMPLE_PREFIX + "TwitterAPIEndpoint";
	 Resource apiEntity = model.createResource(twitterAPIURI);
	 apiEntity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 apiEntity.addProperty(provValue, model.createLiteral("api.twitter.com/search"));
	 apiEntity.addProperty(specializationOf,endpoint);
	 
	 searchActivity.addProperty( used, apiEntity);
	 
	 Resource usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage_"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, apiEntity);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "ApiEndpoint"));
	 
	 
	 //create parameter describing location of the search
	 Resource location = model.createResource(Namespaces.EXAMPLE_PREFIX + "Location_"+groupID);
	 location.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 location.addProperty(provValue, model.createLiteral("Scotland - Note:Location details such as lon and lat can be defined by third party concepts describing the parameter entity"));
	 searchActivity.addProperty( used, location);
	 
	 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, location);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Location"));
	  
	 
	 //create parameter describing group tag of the search
	 Resource groupTagEntity = model.createResource(Namespaces.EXAMPLE_PREFIX + "GroupID_"+groupID);
	 groupTagEntity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 groupTagEntity.addProperty(provValue, model.createLiteral(groupID));
	 searchActivity.addProperty( used, groupTagEntity);
	 
	 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, groupTagEntity);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "GroupTag"));
	 
	 //create collections of data generated by the activity
	 
	 Resource resultCollection = model.createResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_"+UUID.randomUUID());
	 resultCollection.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Collection"));
	 
	 Resource generation = model.createResource(Namespaces.EXAMPLE_PREFIX + "Generation_"+UUID.randomUUID());
	 generation.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Generation"));
	 resultCollection.addProperty(qualifiedGeneration,generation);
	 resultCollection.addProperty(wasGeneratedBy,searchActivity);
	 
	 generation.addProperty( activity,searchActivity);
	 generation.addProperty( hadRole,model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
	 
	 //create example structure of post entity
	 model.createClass(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody");
     
	 
	 //attach example policy
	 Resource policy = model.createResource(Namespaces.EXAMPLE_PREFIX + "Policy/"+UUID.randomUUID());
	 policy.addProperty( RDF.type, model.getResource(Namespaces.ODRL_PREFIX+"Policy"));
	 policy.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "appliesTo"), resultCollection);
	 
	
	 ArrayList <Resource> keywordsList = new ArrayList <Resource> ();
	//create keyword entities used by the activity with role pope:Keyword
		 String [] keywordsArray = keywords.split("AND");
		 for (int i = 0 ; i<keywordsArray.length;i++) {
			 String keywordURI = Namespaces.EXAMPLE_PREFIX + groupID+"Keyword"+i;
			 Resource keywordEntity = model.createResource(keywordURI); 
			 keywordsList.add(keywordEntity);
			 keywordEntity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
			 keywordEntity.addProperty(provValue, keywordsArray[i]);
			 searchActivity.addProperty( used, keywordEntity);
			 
			 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
			 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
			 searchActivity.addProperty( qualifiedUsage, usage);
			 usage.addProperty( entity, keywordEntity);
			 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Keyword"));
			 
		 }
	 
	//CREATE RESULTS
	 for (int i =0 ; i<dummyResults.size();i++) {
	   Resource dataItem = model.createResource(Namespaces.EXAMPLE_PREFIX + "DataItem_"+UUID.randomUUID());
	   dataItem.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	   resultCollection.addProperty( hadMember,dataItem);
	 
	   //link to description of Tweet body
	 
	   Resource body = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody_"+UUID.randomUUID());
	   
	   body.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody"));
	   body.addProperty( RDF.value,  model.createLiteral(dummyResults.get(i).body));
	   
	   body.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
	   
	   //create representation of Tweet timestamp
	   Resource tweetTimestamp = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimeOfCreation_"+UUID.randomUUID());
	   tweetTimestamp.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimeOfCreation"));
	   
	   cal.setTime(dummyResults.get(i).created);
	   tweetTimestamp.addProperty( RDF.value,  model.createTypedLiteral(cal));
	   tweetTimestamp.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
	   
	 //create representation of Tweet location
	   Resource tweetLocation = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetLocation_"+UUID.randomUUID());
	   tweetLocation.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetLocation"));
	   tweetLocation.addProperty( RDF.value,  model.createTypedLiteral(dummyResults.get(i).location));
	   tweetLocation.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
	   
	   
	   //link keyword to body
	   for (int j=0 ; j< keywordsList.size();j++ ) {
		   
		   keywordsList.get(j).addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), body);
	   }
	   
	 //link keyword to tweetLocation
	   
		   
		   location.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includes"), tweetLocation);
	   
	   
	 } 
	 
	 /// add search keyword constraint parameter
	 
	 
	 Resource searchConstraint = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_"+UUID.randomUUID());
	 searchConstraint.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 searchConstraint.addProperty(provValue, model.createLiteral("Search keyword constraint"));
	
	 
	 searchActivity.addProperty( used, searchConstraint);
	 
	 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, searchConstraint);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Postcondition"));
	 
	 Resource leaafNode1 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
	 leaafNode1.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
	 Resource leaafNode2 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
	 leaafNode2.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
	 
	 leaafNode1.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), keywordsList.get(0));
	 leaafNode2.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), keywordsList.get(1));
	 
	 
	 Resource andNode = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint/AND_Node/"+UUID.randomUUID());
	 
	 andNode.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"AND_Node"));
	 andNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "lhs"), leaafNode1);
	 andNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "rhs"), leaafNode2);
	 
	
	 
     //location link
	 Resource leaafNode3 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
	 leaafNode3.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
	 
	 leaafNode3.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), location);
	 
	 //rootNode
	 Resource rootAndNode = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_AND_Node_"+UUID.randomUUID());
	 
	 rootAndNode.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"AND_Node"));
	 rootAndNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "lhs"), andNode);
	 rootAndNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "rhs"), leaafNode3);
	 
	 
	 searchConstraint.addProperty(model.getProperty(Namespaces.PoPE_PREFIX + "describedAs"), rootAndNode);
	 
	}

	public void createFilteringAnnotations(String groupID, int search,ArrayList<Tweet> filteringResult, String string, Date threshold1, Date threshold2,
			String user, Date startedAt, Date endedAt) {
		 Calendar cal = GregorianCalendar.getInstance();
		 //create relevant user agents 
	     String userURI = Namespaces.EXAMPLE_PREFIX +user;
	     Resource userAgent = model.createResource(userURI);
	     userAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Person"));
	   //create processing tool agent  
	     Resource	toolAgent =  model.createResource(Namespaces.EXAMPLE_PREFIX + "SoftwareAgent_"+ UUID.randomUUID());
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_TOOL_ONTOLOGY_PREFIX+"FoobsTool"));
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
		
		//create filteringActivity 
		 String activityURI = Namespaces.EXAMPLE_PREFIX + "DataFilteringActivity_"+search;
		 Resource filteringActivity = model.createResource(activityURI);
		 filteringActivity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Activity"));
		 
		 //link to collections generated by data collection activity
 
	    	//Resource searchActivity1 = model.getResource("http://example.org//SearchActivity/DummySearch/1");
	    	//System.out.println(searchActivity1);
	    	//Resource searchActivity2 = model.getResource("http://example.org//SearchActivity/DummySearch/2");
	    	Resource col1 = Utils.getGeneratedCollection("http://example.org#SearchActivity_DummySearch_1", model);
	    	Resource col2 = Utils.getGeneratedCollection("http://example.org#SearchActivity_DummySearch_2", model);
	    
	    	filteringActivity.addProperty( used, col1);
	    	filteringActivity.addProperty( used, col2);
	    	
	    	Resource usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage_"+UUID.randomUUID());
			 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
			 filteringActivity.addProperty( qualifiedUsage, usage);
			 usage.addProperty( entity, col1);
			 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
			 
			 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
			 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
			 filteringActivity.addProperty( qualifiedUsage, usage);
			 usage.addProperty( entity, col2);
			 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
	    	
		 
		 /* Timestamps for activities already demonstrated elsewhere 
		 cal.setTime(startedAt);
		 filteringActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"startedAtTime"), model.createTypedLiteral(cal));
		 cal.setTime(endedAt);
		 filteringActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"endedAtTime"), model.createTypedLiteral(cal));
		 */
		
		 //associate PoPE role
		 String associationURI = Namespaces.EXAMPLE_PREFIX + "Association_"+ UUID.randomUUID();
		 Resource association = model.createResource(associationURI);
		 filteringActivity.addProperty( qualifiedAssociation, association);
		 association.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Association"));
		 association.addProperty(hadPlan, model.getResource(Namespaces.PoPE_PREFIX+"DataFiltering"));
		 association.addProperty(agent, userAgent);
		 association.addProperty(agent, toolAgent);
		 toolAgent.addProperty(actedOnBehalfOf, userAgent);
		 filteringActivity.addProperty( wasAssociatedWith, toolAgent);
		 filteringActivity.addProperty( wasAssociatedWith, userAgent); 
		 
		//create parameter describing time threshold1
		 Resource timestamp1 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Timestamp_"+UUID.randomUUID());
		 timestamp1.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 cal.setTime(threshold1);
		 timestamp1.addProperty(provValue, model.createTypedLiteral(cal));
		 filteringActivity.addProperty( used, timestamp1);
		 
		  usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 filteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, timestamp1);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Time"));
		 
		//create parameter describing time threshold2
		 Resource timestamp2 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Timestamp_"+UUID.randomUUID());
		 timestamp2.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 cal.setTime(threshold2);
		 timestamp2.addProperty(provValue, model.createTypedLiteral(cal));
		 filteringActivity.addProperty( used, timestamp2);
		 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 filteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, timestamp2);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Time"));
		 
		 
		 //create filtering keyword parameter
		 String keywordURI = Namespaces.EXAMPLE_PREFIX + groupID+"_FilteringKeyword_"+search;
		 Resource keywordEntity = model.createResource(keywordURI); 
		 keywordEntity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 keywordEntity.addProperty(provValue, string);
		 filteringActivity.addProperty( used, keywordEntity);
		 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage_"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 filteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, keywordEntity);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Keyword"));
		 
		//create result collection
		 
		//create collections of data generated by the activity
		 
		 Resource resultCollection = model.createResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_FilteredTweets");
		 resultCollection.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Collection"));
		 
		 resultCollection.addProperty( wasDerivedFrom,col1);
		 resultCollection.addProperty( wasDerivedFrom,col2);
		 
		 Resource generation = model.createResource(Namespaces.EXAMPLE_PREFIX + "Generation_"+UUID.randomUUID());
		 generation.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Generation"));
		 resultCollection.addProperty(qualifiedGeneration,generation);
		 resultCollection.addProperty(wasGeneratedBy,filteringActivity);
		 
		 generation.addProperty( activity,filteringActivity);
		 generation.addProperty( hadRole,model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
		 
		 //create example structure of post entity
		 model.createClass(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimestamp");
	     
		//CREATE RESULTS
		 for (int i =0 ; i<filteringResult.size();i++) {
		   Resource dataItem = model.createResource(Namespaces.EXAMPLE_PREFIX + "DataItem_"+UUID.randomUUID());
		   dataItem.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		   resultCollection.addProperty( hadMember,dataItem);
		 
		   //link to description of Tweet body
		 
		   Resource timestamp = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimestamp_"+UUID.randomUUID());
		   
		   timestamp.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimestamp"));
		   cal.setTime(filteringResult.get(i).created);
		   timestamp.addProperty( RDF.value,  model.createTypedLiteral(cal));
		   
		   timestamp.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
		   
		   //create representation of Tweet timestamp
		   Resource tweetTimestamp = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimeOfCreation_"+UUID.randomUUID());
		   tweetTimestamp.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetTimeOfCreation"));
		   
		   cal.setTime(filteringResult.get(i).created);
		   tweetTimestamp.addProperty( RDF.value,  model.createTypedLiteral(cal));
		   tweetTimestamp.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
		   
		 //create representation of Tweet location
		   Resource tweetBody = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody_"+UUID.randomUUID());   
		   tweetBody.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody"));
		   tweetBody.addProperty( RDF.value,  model.createLiteral(filteringResult.get(i).body));
		   tweetBody.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
		   
		   timestamp1.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "lessThan"), tweetTimestamp);
		   timestamp2.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "moreThan"), tweetTimestamp);
		   keywordEntity.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), tweetBody);
		  }
		 
		   //add constraint
		  
		/// add search keyword constraint parameter
		 
		 
		 Resource searchConstraint = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_"+UUID.randomUUID());
		 searchConstraint.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 searchConstraint.addProperty(provValue, model.createLiteral("Filtering constraint"));
		
		 
		 filteringActivity.addProperty( used, searchConstraint);
		 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 filteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, searchConstraint);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Postcondition"));
		 
		 Resource leaafNode1 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
		 leaafNode1.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
		 Resource leaafNode2 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
		 leaafNode2.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
		 
		 leaafNode1.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), timestamp1);
		 leaafNode2.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), timestamp2);
		 
		 
		 Resource andNode = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_AND_Node_"+UUID.randomUUID());
		 
		 andNode.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"AND_Node"));
		 andNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "lhs"), leaafNode1);
		 andNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "rhs"), leaafNode2);
		 
	     //location link
		 Resource leaafNode3 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
		 leaafNode3.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
		 
		 leaafNode3.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), keywordEntity);
		 
		 //rootNode
		 Resource rootAndNode = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_AND_Node_"+UUID.randomUUID());
		 
		 rootAndNode.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"AND_Node"));
		 rootAndNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "lhs"), andNode);
		 rootAndNode.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "rhs"), leaafNode3);
		 
		 
		 searchConstraint.addProperty(model.getProperty(Namespaces.PoPE_PREFIX + "describedAs"), rootAndNode);   
		
		   
		 
	}

	
	public void createRetweetFilteringAnnotations(ArrayList<Tweet> retweetFilteringResults,  String user,
			Date startedAt, Date endedAt, String regexValue) {
		Calendar cal = GregorianCalendar.getInstance();
		 //create relevant user agents 
	     String userURI = Namespaces.EXAMPLE_PREFIX +user;
	     Resource userAgent = model.createResource(userURI);
	     userAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Person"));
	   //create processing tool agent  
	     Resource	toolAgent =  model.createResource(Namespaces.EXAMPLE_PREFIX + "SoftwareAgent_"+ UUID.randomUUID());
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_TOOL_ONTOLOGY_PREFIX+"FoobsTool"));
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
		
		//create frequencyCountActivity 
		 String activityURI = Namespaces.EXAMPLE_PREFIX + "RetweetFilteringActivity_1";
		 Resource retweetFilteringActivity = model.createResource(activityURI);
		 retweetFilteringActivity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Activity"));
		 
		
		 //associate PoPE role
		 String associationURI = Namespaces.EXAMPLE_PREFIX + "Association_"+ UUID.randomUUID();
		 Resource association = model.createResource(associationURI);
		 retweetFilteringActivity.addProperty( qualifiedAssociation, association);
		 association.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Association"));
		 association.addProperty(hadPlan, model.getResource(Namespaces.PoPE_PREFIX+"DataFiltering"));
		 association.addProperty(agent, userAgent);
		 association.addProperty(agent, toolAgent);
		 toolAgent.addProperty(actedOnBehalfOf, userAgent);
		 retweetFilteringActivity.addProperty( wasAssociatedWith, toolAgent);
		 retweetFilteringActivity.addProperty( wasAssociatedWith, userAgent); 
		
		 cal.setTime(startedAt);
		 retweetFilteringActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"startedAtTime"), model.createTypedLiteral(cal));
		 cal.setTime(endedAt);
		 retweetFilteringActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"endedAtTime"), model.createTypedLiteral(cal));
		
		//create parameter denoting regex as a filterikng condition
		 Resource regex = model.createResource(Namespaces.EXAMPLE_PREFIX + "WordCount/"+UUID.randomUUID());
		 regex.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 regex.addProperty(provValue, model.createTypedLiteral(regexValue));
		 retweetFilteringActivity.addProperty( used, regex);
		 
		 Resource usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 retweetFilteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, regex);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Regex"));
		 
		 
		 Resource input = model.getResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_FilteredTweets");
		//link the collection from previous filtering activity 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 retweetFilteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, input);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
		 
		 //create result for this activity
		 
		 Resource resultCollection = model.createResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_retweetRemovedResult");
		 resultCollection.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Collection"));
		 resultCollection.addProperty( wasDerivedFrom,input);
		 
		 Resource generation = model.createResource(Namespaces.EXAMPLE_PREFIX + "Generation/"+UUID.randomUUID());
		 generation.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Generation"));
		 resultCollection.addProperty(qualifiedGeneration,generation);
		 resultCollection.addProperty(wasGeneratedBy,retweetFilteringActivity);
		 
		 generation.addProperty( activity,retweetFilteringActivity);
		 generation.addProperty( hadRole,model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
		 
		 
		//CREATE RESULTS
		 for (int i =0 ; i<retweetFilteringResults.size();i++) {
		   Resource dataItem = model.createResource(Namespaces.EXAMPLE_PREFIX + "DataItem/"+UUID.randomUUID());
		   dataItem.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		   resultCollection.addProperty( hadMember,dataItem);
		 
		   
		   
		 //create representation of Tweet location
		   Resource tweetBody = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody_"+UUID.randomUUID());   
		   tweetBody.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody"));
		   tweetBody.addProperty( RDF.value,  model.createLiteral(retweetFilteringResults.get(i).body));
		   tweetBody.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
		 
		   regex.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "doesNotMatch"), tweetBody);
		  }
		 
		 Resource searchConstraint = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_"+UUID.randomUUID());
		 searchConstraint.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 searchConstraint.addProperty(provValue, model.createLiteral("No retweets filtering constraint"));
		
		 
		 retweetFilteringActivity.addProperty( used, searchConstraint);
		 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 retweetFilteringActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, searchConstraint);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Postcondition"));
		 
		 Resource leaafNode1 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
		 leaafNode1.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
		
		 
		 leaafNode1.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), regex);
		
		 
		 
		 searchConstraint.addProperty(model.getProperty(Namespaces.PoPE_PREFIX + "describedAs"), leaafNode1);
		 
	}
	
	
	
	public void createFrequencyCountAnnotations(ArrayList<Tweet> wordcountResults, String user,
			Date startedAt, Date endedAt, int parameterWordCount) {
		 Calendar cal = GregorianCalendar.getInstance();
		 //create relevant user agents 
	     String userURI = Namespaces.EXAMPLE_PREFIX +user;
	     Resource userAgent = model.createResource(userURI);
	     userAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Person"));
	   //create processing tool agent  
	     Resource	toolAgent =  model.createResource(Namespaces.EXAMPLE_PREFIX + "SoftwareAgent_"+ UUID.randomUUID());
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_TOOL_ONTOLOGY_PREFIX+"FoobsTool"));
	     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
		
		//create frequencyCountActivity 
		 String activityURI = Namespaces.EXAMPLE_PREFIX + "FrequencyCountActivity_1";
		 Resource frequencyCountActivity = model.createResource(activityURI);
		 frequencyCountActivity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Activity"));
		 
		
		 //associate PoPE role
		 String associationURI = Namespaces.EXAMPLE_PREFIX + "Association_"+ UUID.randomUUID();
		 Resource association = model.createResource(associationURI);
		 frequencyCountActivity.addProperty( qualifiedAssociation, association);
		 association.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Association"));
		 association.addProperty(hadPlan, model.getResource(Namespaces.PoPE_PREFIX+"WordFrequencyCount"));
		 association.addProperty(agent, userAgent);
		 association.addProperty(agent, toolAgent);
		 toolAgent.addProperty(actedOnBehalfOf, userAgent);
		 frequencyCountActivity.addProperty( wasAssociatedWith, toolAgent);
		 frequencyCountActivity.addProperty( wasAssociatedWith, userAgent); 
		 
		
		 cal.setTime(startedAt);
		 frequencyCountActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"startedAtTime"), model.createTypedLiteral(cal));
		 cal.setTime(endedAt);
		 frequencyCountActivity.addProperty( model.getProperty(Namespaces.PROV_PREFIX+"endedAtTime"), model.createTypedLiteral(cal));
		 
		 
		 //create parameter denoting a number of words returned by the frequency count
		 Resource wordCount = model.createResource(Namespaces.EXAMPLE_PREFIX + "WordCount_"+UUID.randomUUID());
		 wordCount.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 wordCount.addProperty(provValue, model.createTypedLiteral(parameterWordCount));
		 frequencyCountActivity.addProperty( used, wordCount);
		 
		 Resource usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage_"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 frequencyCountActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, wordCount);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "StatisticalMeasure"));
		 
		 
		 Resource input = model.getResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_retweetRemovedResult");
		 
		 //link the collection from re-tweets removed result
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 frequencyCountActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, input);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
		 
		 
		 
		 Resource resultCollection = model.createResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection_wordCountResult");
		 resultCollection.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Collection"));
		 resultCollection.addProperty( wasDerivedFrom,input);
		 
		 Resource generation = model.createResource(Namespaces.EXAMPLE_PREFIX + "Generation_"+UUID.randomUUID());
		 generation.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Generation"));
		 resultCollection.addProperty(qualifiedGeneration,generation);
		 resultCollection.addProperty(wasGeneratedBy,frequencyCountActivity);
		 
		 generation.addProperty( activity,frequencyCountActivity);
		 generation.addProperty( hadRole,model.getResource(Namespaces.PoPE_PREFIX + "WordFrequencyDataSet"));
		 
		 //create example representation of collection size
		 model.createClass(Namespaces.EXAMPLE_COLLECTION_ONTOLOGY_PREFIX + "CollectionSize");
		 Resource collSize = model.createResource(Namespaces.EXAMPLE_COLLECTION_ONTOLOGY_PREFIX + "CollectionSize_"+UUID.randomUUID());
		   
		 collSize.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_COLLECTION_ONTOLOGY_PREFIX + "CollectionSize"));
		 collSize.addProperty( RDF.value, model.createTypedLiteral(parameterWordCount));
		 collSize.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "appliesTo"), resultCollection);
		 
		 wordCount.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "moreThanOrEqualTo"), collSize);
		
		 
		 
		 
		//CREATE RESULTS
		 for (int i =0 ; i<wordcountResults.size();i++) {
		   Resource dataItem = model.createResource(Namespaces.EXAMPLE_PREFIX + "DataItem_"+UUID.randomUUID());
		   dataItem.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		   resultCollection.addProperty( hadMember,dataItem);
		   dataItem.addProperty(provValue,model.createTypedLiteral(wordcountResults.get(i)));
		   
		 
		   
		  }
		 
		 
		 Resource searchConstraint = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_"+UUID.randomUUID());
		 searchConstraint.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
		 searchConstraint.addProperty(provValue, model.createLiteral("Restriction on the number of returned words"));
		
		 
		 frequencyCountActivity.addProperty( used, searchConstraint);
		 
		 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage_"+UUID.randomUUID());
		 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
		 frequencyCountActivity.addProperty( qualifiedUsage, usage);
		 usage.addProperty( entity, searchConstraint);
		 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Postcondition"));
		 
		 Resource leaafNode1 = model.createResource(Namespaces.EXAMPLE_PREFIX + "Constraint_Leaf_Node_"+UUID.randomUUID());
		 leaafNode1.addProperty( RDF.type, model.getResource(Namespaces.PoPE_PREFIX+"LeafNode"));
		
		 
		 leaafNode1.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "parameter"), wordCount);
		
		 
		 
		 searchConstraint.addProperty(model.getProperty(Namespaces.PoPE_PREFIX + "describedAs"), leaafNode1);
		 
		 
	}


}
