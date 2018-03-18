package uoa.pope.example.main;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
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
	
	private  Property hadMember ;
	
	
	public AnnotationFactory (OntModel model) {
		
		this.model = model;
		//get/add some standard PROV properties 
		used =  model.getProperty(Namespaces.PROV_PREFIX+"used");
		qualifiedUsage = model.getProperty(Namespaces.PROV_PREFIX+"qualifiedUsage");
		entity = model.getProperty(Namespaces.PROV_PREFIX+"entity");
		
		wasGeneratedBy =  model.getProperty(Namespaces.PROV_PREFIX+"wasGeneratedBy");
		qualifiedGeneration =  model.getProperty(Namespaces.PROV_PREFIX+"qualifiedGeneration");
		
		
		hadRole =  model.getProperty(Namespaces.PROV_PREFIX+"hadRole");
		activity =  model.getProperty(Namespaces.PROV_PREFIX+"activity");
		provValue =  model.getProperty(Namespaces.PROV_PREFIX+"value");
		qualifiedAssociation = model.getProperty(Namespaces.PROV_PREFIX+"qualifiedAssociation");
		hadPlan = model.getProperty(Namespaces.PROV_PREFIX+"hadPlan");
		agent = model.getProperty(Namespaces.PROV_PREFIX+"agent");
		actedOnBehalfOf = model.getProperty(Namespaces.PROV_PREFIX+"actedOnBehalfOf");
		wasAssociatedWith = model.getProperty(Namespaces.PROV_PREFIX+"wasAssociatedWith");
		
		hadMember = model.getProperty(Namespaces.PROV_PREFIX+"hadMember");
		
		
	}
	
	public void createSearchAnnotations (String keywords, String groupID, Object object, double lat, double lon, double radius, int search, ArrayList<Tweet> dummyResults, String user) {
	
	 //create relevant user agents 
     String userURI = Namespaces.EXAMPLE_PREFIX +user;
     Resource userAgent = model.createResource(userURI);
     userAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Person"));
   //create processing tool agent  
     Resource	toolAgent =  model.createResource(Namespaces.EXAMPLE_PREFIX + "SoftwareAgent/"+ UUID.randomUUID());
     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_TOOL_ONTOLOGY_PREFIX+"FoobsTool"));
     toolAgent.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"SoftwareAgent"));
     		
	 //create searchActivity 
	 String activityURI = Namespaces.EXAMPLE_PREFIX + "SearchActivity/"+ UUID.randomUUID();
	 Resource searchActivity = model.createResource(activityURI);
	 searchActivity.addProperty( RDF.type, Namespaces.PROV_PREFIX+"Activity");
	
	 //associate PoPE role
	 String associationURI = Namespaces.EXAMPLE_PREFIX + "Association/"+ UUID.randomUUID();
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
	 
	 searchActivity.addProperty( used, apiEntity);
	 
	 Resource usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, apiEntity);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "ApiEndpoint"));
	 
	 
	 //create parameter describing location of the search
	 Resource location = model.createResource(Namespaces.EXAMPLE_PREFIX + "Location/"+groupID);
	 location.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 location.addProperty(provValue, model.createLiteral("e.g. Scotland - Note:Location should be defined by third party ontologies"));
	 searchActivity.addProperty( used, location);
	 
	 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, location);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "Location"));
	  
	 
	 //create parameter describing group tag of the search
	 Resource groupTagEntity = model.createResource(Namespaces.EXAMPLE_PREFIX + "GroupID/"+groupID);
	 groupTagEntity.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	 groupTagEntity.addProperty(provValue, model.createLiteral(groupID));
	 searchActivity.addProperty( used, groupTagEntity);
	 
	 usage = model.createResource(Namespaces.EXAMPLE_PREFIX + "Ussage/"+UUID.randomUUID());
	 usage.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Usage"));
	 searchActivity.addProperty( qualifiedUsage, usage);
	 usage.addProperty( entity, groupTagEntity);
	 usage.addProperty( hadRole, model.getResource(Namespaces.PoPE_PREFIX + "GroupTag"));
	 
	 //create collections of data generated by the activity
	 
	 Resource resultCollection = model.createResource(Namespaces.EXAMPLE_PREFIX + "ResultCollection/"+UUID.randomUUID());
	 resultCollection.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Collection"));
	 
	 Resource generation = model.createResource(Namespaces.EXAMPLE_PREFIX + "Generation/"+UUID.randomUUID());
	 generation.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Generation"));
	 resultCollection.addProperty(qualifiedGeneration,generation);
	 resultCollection.addProperty(wasGeneratedBy,searchActivity);
	 
	 generation.addProperty( activity,searchActivity);
	 generation.addProperty( hadRole,model.getResource(Namespaces.PoPE_PREFIX + "TwitterDataSet"));
	 
	 //create example structure of post entity
	 model.createClass(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody");

	 
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
	   Resource dataItem = model.createResource(Namespaces.EXAMPLE_PREFIX + "DataItem/"+UUID.randomUUID());
	   dataItem.addProperty( RDF.type, model.getResource(Namespaces.PROV_PREFIX+"Entity"));
	   resultCollection.addProperty( hadMember,dataItem);
	 
	   //link to description of Tweet body
	 
	   Resource body = model.createResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody/"+UUID.randomUUID());
	   
	   body.addProperty( RDF.type, model.getResource(Namespaces.EXAMPLE_POST_ONTOLOGY_PREFIX + "TweetBody"));
	   body.addProperty( RDF.value,  model.createLiteral(dummyResults.get(i).body));
	   
	   body.addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), dataItem);
	   
	   
	   //link keyword to body
	  
	   
	   for (int j=0 ; j< keywordsList.size();j++ ) {
		   
		   keywordsList.get(j).addProperty( model.getProperty(Namespaces.PoPE_PREFIX + "includedIn"), body);
	   }
	   
	 } 
	 
	 
	 
	 
	 
	
	}


}
