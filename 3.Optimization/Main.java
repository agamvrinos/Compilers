
import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.storage.IRelation;

import syntaxtree.Goal;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Please give directory path and file path."); System.exit(-1);}
        
        final String projectDirectory = args[0];
        final String filePath = args[1];
        
        FileInputStream fis = new FileInputStream(projectDirectory + filePath);
        SpigletParser spig_parser = new SpigletParser(fis);
		
		Goal root = spig_parser.Goal();
		
		//==================== PHASE 1 ======================
		CollectLabelsVisitor eval = new CollectLabelsVisitor();	// Collect Labels
		Map <String, Integer> labels = root.accept(eval, null);
		//==================== PHASE 2 ======================
		FactsVisitor eval2 = new FactsVisitor(labels);			// Build Facts
		root.accept(eval2, null);
        

		File factsDir = new File("facts");
		factsDir.mkdir();


		PrintWriter out;
		out = new PrintWriter("facts/instruction.iris");
		out.print(FactsVisitor.instructions);
		out.flush(); out.close();
		out = new PrintWriter("facts/var.iris");
		out.print(FactsVisitor.vars);
		out.flush(); out.close();
		out = new PrintWriter("facts/next.iris");
		out.print(FactsVisitor.nexts);
		out.flush(); out.close();
		out = new PrintWriter("facts/varMove.iris");
		out.print(FactsVisitor.varMoves);
		out.flush(); out.close();
		out = new PrintWriter("facts/constMove.iris");
		out.print(FactsVisitor.constMoves);
		out.flush(); out.close();
		out = new PrintWriter("facts/varUse.iris");
		out.print(FactsVisitor.varUses);
		out.flush(); out.close();
		out = new PrintWriter("facts/varDef.iris");
		out.print(FactsVisitor.varDefs);
		out.flush(); out.close();
		
		Parser parser = new Parser();

        Map<IPredicate, IRelation> factMap = new HashMap<>();

        /** The following loop -- given a project directory -- will list and read parse all fact files in its "/facts"
         *  subdirectory. This allows you to have multiple .iris files with your program facts. For instance you can
         *  have one file for each relation's facts as our examples show.
         */
        final File factsDirectory = new File(projectDirectory + "/facts");
        if (factsDirectory.isDirectory()) {
            for (final File fileEntry : factsDirectory.listFiles()) {
            	System.out.println("FILENAME: " + fileEntry.getName());
                if (fileEntry.isDirectory())
                    System.out.println("Omitting directory " + fileEntry.getPath());

                else {
                    Reader factsReader = new FileReader(fileEntry);
                    parser.parse(factsReader);

                    // Retrieve the facts and put all of them in factMap
                    factMap.putAll(parser.getFacts());
                }
            }
        }
        else {
            System.err.println("Invalid facts directory path");
            System.exit(-1);
        }

        File rulesFile = new File(projectDirectory + "/rules/rules.iris");
        Reader rulesReader = new FileReader(rulesFile);

        File queriesFile = new File(projectDirectory + "/queries/queries.iris");
        Reader queriesReader = new FileReader(queriesFile);

        // Parse rules file.
        parser.parse(rulesReader);
        
        // Retrieve the rules from the parsed file.
        List<IRule> rules = parser.getRules();

        // Parse queries file.
        parser.parse(queriesReader);
        // Retrieve the queries from the parsed file.
        List<IQuery> queries = parser.getQueries();

        // Create a default configuration.
        Configuration configuration = new Configuration();

        // Enable Magic Sets together with rule filtering.
        configuration.programOptmimisers.add(new MagicSets());

        // Create the knowledge base.
        IKnowledgeBase knowledgeBase = new KnowledgeBase(factMap, rules, configuration);
        
        File resultsDir = new File("results");
        resultsDir.mkdir();
        
        // Evaluate all queries over the knowledge base.
        for (IQuery query : queries) {
            List<IVariable> variableBindings = new ArrayList<>();
            IRelation relation = knowledgeBase.execute(query, variableBindings);
            
            String file = (query.toString()).substring(3, (query.toString()).indexOf("("));
            out = new PrintWriter("results/" + file + ".out");
    		
            // Output the variable bindings.
            System.out.println("\n" + query.toString() + "\n" + variableBindings);

            // Output each tuple in the relation, where the term at position i
            // corresponds to the variable at position i in the variable
            // bindings list.
            for (int i = 0; i < relation.size(); i++) {
                System.out.println(relation.get(i));
                out.print(relation.get(i) + "\n");
            }

    		out.flush(); out.close();
        }
    }

}
