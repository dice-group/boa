package de.uni_leipzig.simba.boa.backend.knowledgecreation.cuncurrent;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchPrintProgressTask;


public class KnowledgeCreationPrintProgressTask extends TimerTask {

    private List<Callable<Collection<Map<String, List<Triple>>>>> callableList;
    private DecimalFormat format = new DecimalFormat("##");
    private final NLPediaLogger logger = new NLPediaLogger(PatternSearchPrintProgressTask.class);

    public KnowledgeCreationPrintProgressTask(List<Callable<Collection<Map<String, List<Triple>>>>> todo) {

        this.callableList = todo;
    }

    @Override
    public void run() {
        
        this.logger.info("########################################");
        
        for (Callable<Collection<Map<String, List<Triple>>>> knowledgeCreationCallable : this.callableList) {

            KnowledgeCreationCallable knowledgeCreationThread = (KnowledgeCreationCallable) knowledgeCreationCallable;

            int progress    = Integer.valueOf(format.format(knowledgeCreationThread.getProgress() * 100));

            if (progress != 100 && (knowledgeCreationThread.getProgress() > 0 && knowledgeCreationThread.getProgress() < 100) ) {

                this.logger.info(knowledgeCreationThread.getName() + ": " + progress + "%. " +
                        "(" + knowledgeCreationThread.getNumberDone() + "/" + knowledgeCreationThread.getNumberTotal() + ")");
            }
        }
        this.logger.info("########################################");
    }

}
