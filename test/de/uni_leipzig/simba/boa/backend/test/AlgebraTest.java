package de.uni_leipzig.simba.boa.backend.test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;


public class AlgebraTest {

	public static void main(String[] args) {

		Query query = QueryFactory.create("Select * where {?s ?p ?o}");
		Op op = Algebra.compile(query) ;
		
		Query query1 = QueryFactory.create("Select * where {?o ?i ?l . Filter ( ?l = \"label\") . OPTIONAL{ ?o ?s ?r }}");
		Op op1 = Algebra.compile(query1) ;
		
		System.out.println(op.toString());
		System.out.println(op1.toString());
	}
}
