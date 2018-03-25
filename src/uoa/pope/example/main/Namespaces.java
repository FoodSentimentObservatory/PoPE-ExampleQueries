package uoa.pope.example.main;

public class Namespaces {

	public static String PoPE_PREFIX = "http://w3id.org/abdn/policy/pope#";
	public static String PROV_PREFIX  = "http://www.w3.org/ns/prov#";
	public static String OA_PREFIX  = "";
	public static String EXAMPLE_PREFIX  = "http://example.org#";
	public static String EXAMPLE_POST_ONTOLOGY_PREFIX  = "http://foobs.org/Post#"; 
	public static String EXAMPLE_TOOL_ONTOLOGY_PREFIX  = "http://foobs.org/Tool#";
	public static String EXAMPLE_COLLECTION_ONTOLOGY_PREFIX  = "http://foobs.org/Collection#";
	
	public static String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String SKOS_PREFIX = "http://www.w3.org/2004/02/skos/core#";
	public static String OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
	public static String RDFS_PREFIX = "http://www.w3.org/2000/01/rdf-schema#";
	public static String ODRL_PREFIX = "http://www.w3.org/ns/odrl/2/";


public static String getAsPrefixes () {
    String prefixes = "";
    
    prefixes = prefixes +"PREFIX prov:<" + PROV_PREFIX +">";
    prefixes = prefixes +" PREFIX pope:<" + PoPE_PREFIX +">";
    prefixes = prefixes +"PREFIX rdf:<" + RDF_PREFIX +">";
    prefixes = prefixes +" PREFIX skos:<" + SKOS_PREFIX +">";
    prefixes = prefixes +" PREFIX owl:<" + OWL_PREFIX +">";
    prefixes = prefixes +" PREFIX rdfs:<" + RDFS_PREFIX +">";
    prefixes = prefixes +" PREFIX examplePostOntology:<" + EXAMPLE_POST_ONTOLOGY_PREFIX +">";
    prefixes = prefixes +" PREFIX odrl:<" + ODRL_PREFIX +">";
    prefixes = prefixes +" PREFIX ex:<" + EXAMPLE_PREFIX +">";
    prefixes = prefixes +" PREFIX ex_coll:<" + EXAMPLE_COLLECTION_ONTOLOGY_PREFIX +">";
    prefixes = prefixes +" PREFIX ex_tool:<" + EXAMPLE_TOOL_ONTOLOGY_PREFIX +">";
	
	return prefixes+"\n";
}
}
