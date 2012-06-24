package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

public class DrugBankObjectProperty extends
		DefaultObjectPropertyBackgroundKnowledgeCollectorModule {
	private String createObjectPropertyQuery(String property){
	return	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"+
			"SELECT ?d1label  ?d2label WHERE {"+
			"?d1 rdfs:label ?d1label ."+
			"?di drugbank:interactionDrug1 ?d1 ."+
			"?di drugbank:interactionDrug2 ?d2 ."+
			"?d2 rdfs:label ?d2label . }";
	}
}
