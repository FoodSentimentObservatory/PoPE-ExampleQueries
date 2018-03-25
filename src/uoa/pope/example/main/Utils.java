package uoa.pope.example.main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import uoa.pope.example.main.Utils.Node;

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
		System.out.println(" -----");
		System.out.println("|QUERY|:");
		System.out.println(" -----\n");
	
		String printQuery = queryString;
		printQuery= printQuery.replace("Where", "\n Where \n ");
		String substring = printQuery.substring(printQuery.indexOf("{"), printQuery.indexOf("}"));
		String newSubstring = substring.replace(".", ".\n");
		printQuery=printQuery.replace(substring, newSubstring);
		printQuery= printQuery.replace("{", "{ \n ");
		printQuery=printQuery.replace("}", "\n } \n ");
		
		System.out.println (printQuery+"\n");
		System.out.println("RESULT:");
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results =  qe.execSelect();
		ResultSetFormatter.out(System.out, results, query);
		qe.close();
	}
	
public static QueryExecution buildQuery (String queryString, OntModel model) {
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		
		return qe;
	}

//courtesy of stack OverFlow :) https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagrampublic 
public static class Node {

    final String name;
    final List<Node> children;

    public Node(String name, List<Node> children) {
        this.name = name;
        this.children = children;
    }

    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1)
                    .print(prefix + (isTail ?"    " : "│   "), true);
        }
    }
}

public static Node buildTree(Resource node, OntModel model) {
	
	 List <Node> children = new ArrayList<Node> ();
			
	Iterator<Statement> it = node.listProperties(RDF.type);
	
	
	String  nodeName = null;
	  while(it.hasNext()) {
	  Statement s = it.next();
	  String  localName = ((Resource) s.getObject()).getLocalName();
	   if (localName==null){
		   localName ="";
	   }
	   
	   
	   if (localName.equals("AND_Node")||localName.equals("OR_Node")||localName.equals("XOR_Node")) {
		   nodeName= localName;
		   Resource leftNode = (Resource) node.getProperty(model.getProperty(Namespaces.PoPE_PREFIX+"lhs") ).getObject();
		   Resource rightNode =  (Resource) node.getProperty(model.getProperty(Namespaces.PoPE_PREFIX+"rhs") ).getObject();
		   if (leftNode!=null) {
			   children.add( buildTree( leftNode,  model));
			   
		   }
		   if (rightNode!=null) {
			   children.add( buildTree( rightNode,  model));
		   }
		   
	   }
	   if (localName.equals("LeafNode")) {
		   nodeName= localName;
		  
		   Resource parameter = (Resource) node.getProperty(model.getProperty(Namespaces.PoPE_PREFIX+"parameter") ).getObject();
		   children.add( buildTree( parameter,  model));
		   
	   }
	   
	   if (localName.equals("Entity")) {
		   nodeName= localName;  
		   
		   //check for inferred entity subtypes
		   Iterator<Statement> it2 =  s.getSubject().listProperties(RDF.type);
		   
			
		   while(it2.hasNext()) {
			   Statement s2 = it2.next();
			   String  uri = ((Resource) s2.getObject()).getURI();
			   if (uri!=null&&uri.contains(Namespaces.PoPE_PREFIX)) {
				   nodeName = ((Resource) s2.getObject()).getLocalName();
				  
			   }
			   
		   }
		    Statement result = s.getSubject().getProperty(model.getProperty(Namespaces.PROV_PREFIX+"value"));
		   
		  children.add(new Utils.Node ( s.getSubject().getURI() + " (prov:value \""+ result.getObject().asLiteral().toString()+  "\")",new ArrayList()));
		 
	   }
	   
	 }
	  
	 Node root=  new Utils.Node(nodeName,children);
	 
	 return root;
	
}

	
public static void printConstraintTree (String queryString,OntModel model) {
	
	
	QueryExecution qe = Utils.buildQuery(queryString, model);
	ResultSet result =  qe.execSelect();
	

	result.forEachRemaining(row -> {
		
		System.out.println("\n----> Constraint: " + row.get("constraint")+"|");
		
		Resource rootNode = row.getResource("rootNode");
	
			
			Iterator<Statement> it = rootNode.listProperties(RDF.type);
			
			  while(it.hasNext()) {
				  Statement s = it.next();
			
			String nodeName = ((Resource) s.getObject()).getLocalName();
			 if (nodeName==null){
				   continue;
			   }
			if (nodeName.equals("AND_Node")||nodeName.equals("OR_Node")||nodeName.equals("XOR_Node")||nodeName.equals("LeafNode")) {						
				
				Node root = Utils.buildTree((Resource) s.getSubject(),model) ;
				
				System.out.println("constrain  tree");
				
				//System.out.println("Rooot"+root.value);
				try {
					root.print();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
					
	}); 
	qe.close();
}

//At the moment this only returns one generated collection - all we need for this evaluation
public static Resource getGeneratedCollection (String activityUri, OntModel model) {
	String queryString = Namespaces.getAsPrefixes()+ "SELECT ?collection Where {?collection prov:wasGeneratedBy <"+activityUri+">; a prov:Collection. }";
	QueryExecution qe = Utils.buildQuery(queryString, model);
	ResultSet result =  qe.execSelect();
		  
	return result.next().get("collection").asResource();
}


}
