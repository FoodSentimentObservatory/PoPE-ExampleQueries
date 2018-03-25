package uoa.pope.example.queries;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.update.UpdateAction;

import uoa.pope.example.main.Namespaces;
import uoa.pope.example.main.Utils;

public class RunPoPEInferences {

	OntModel model;
	
	public RunPoPEInferences (OntModel model) {
		this.model = model;
	}
	
	public void runInferences () {
		/* These rules will be applied to all entities used and generated entities
		 * If some entities should be ignored the SPARQL statements need to change
		 * For example add an extra restriction only to consider entities associated with a role defined in PoPE
		 * 
		 * 
		 * */
		
		// used entities subclasses 
		String queryString = Namespaces.getAsPrefixes()+ " INSERT {?entitySubClass rdfs:subClassOf prov:Entity.?entity a ?entitySubClass;a skos:Concept; skos:inSchema pope:PopeSkosSchema.} Where {?usage a prov:Usage; prov:entity ?entity; prov:hadRole ?role. BIND (URI(CONCAT(STR(?role),\"Entity\")) as ?entitySubClass)}";
		
		UpdateAction.parseExecute(queryString, model);
		
		//generated entities subclasses
        queryString = Namespaces.getAsPrefixes()+ " INSERT {?entitySubClass rdfs:subClassOf prov:Entity.?entity a ?entitySubClass;a skos:Concept; skos:inSchema pope:PopeSkosSchema.} Where {?entity prov:qualifiedGeneration ?generation. ?generation prov:hadRole ?role. BIND (URI(CONCAT(STR(?role),\"Entity\")) as ?entitySubClass)}";
		
		UpdateAction.parseExecute(queryString, model);
	
	
		/* These rules will be applied to all activities with a plan
		 * If some activities should be ignored the SPARQL statements need to change
		 * For example add an extra restriction only to consider activities associated with a plan defined in PoPE
		 * 
		 * 
		 * */
		
		//activities subclasses
        queryString = Namespaces.getAsPrefixes()+ " INSERT {?activitySubClass rdfs:subClassOf prov:Activity.?activity a ?activitySubClass} Where {?activity prov:qualifiedAssociation ?association. ?association prov:hadPlan ?plan. BIND (URI(CONCAT(STR(?plan),\"Activity\")) as ?activitySubClass)}";
		
		UpdateAction.parseExecute(queryString, model);
	
		
		//inferr dat aitems
        queryString = Namespaces.getAsPrefixes()+ " INSERT {?entitySubClass rdfs:subClassOf prov:Entity.?dataItem a ?entitySubClass;a skos:Concept; skos:inSchema pope:PopeSkosSchema.} Where {?entity prov:qualifiedGeneration ?generation; a prov:Collection; prov:hadMember ?dataItem. ?generation prov:hadRole ?role.  BIND (URI(CONCAT(STR(?role),\"Item\")) as ?entitySubClass)}";
		
		UpdateAction.parseExecute(queryString, model);
		
		
		//UpdateAction.parseExecute(queryString, model);
		
	}
	
	
}
