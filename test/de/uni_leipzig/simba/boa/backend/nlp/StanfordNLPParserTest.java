package de.uni_leipzig.simba.boa.backend.nlp;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;


public class StanfordNLPParserTest {

    public static void main(String[] args) {

        NLPediaSetup s = new NLPediaSetup(true);
        
//        LexicalizedParser lp = new LexicalizedParser(NLPediaSettings.BOA_BASE_DIRECTORY + "training/parser/englishPCFG.ser.gz");
//        lp.setOptionFlags(new String[] { "-maxLength", "80", "-retainTmpSubcategories" });
//
//        String sentence = "This is an easy sentence.";
//        Tree parse = (Tree) lp.apply(sentence);
//
//        TreePrint tp = new TreePrint("penn");
//        tp.printTree(parse);
    }
}
