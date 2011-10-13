package de.uni_leipzig.simba.boa.backend.pipeline;

import java.util.Set;
import java.util.TreeSet;


public class Pipeline {

	private Set<PipelineModule> modules;
	
	private Pipeline() {
		
		this.modules = new TreeSet<PipelineModule>();
	}
}
