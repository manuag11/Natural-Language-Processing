import edu.stanford.nlp.parser.lexparser.*;

import java.io.*;
import java.io.FileFilter;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.common.ArgUtils;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Timing;


import edu.stanford.nlp.process.DocumentPreprocessor;
import java.util.ArrayList;

/* This class can be used to train the Stanford Parser on WSJ and test on Brown. 
The code uses 90-10 split for the Brown corpus*/

class StatsParsing {
    // This function returns a treebank of all the files in the folder indicated by 'folder'
    public static MemoryTreebank generatesplit(File folder)
    {
        File[] listOfFiles = folder.listFiles();
        MemoryTreebank newtreebank=new MemoryTreebank("utf-8");
        for (File file : listOfFiles) {
        if(file.isDirectory())
        {
            MemoryTreebank m=new MemoryTreebank("utf-8");
            m=generatesplit(file);
            for(Tree t:m)
            {
                newtreebank.add(t);
            }
        }
        else {
            MemoryTreebank m=new MemoryTreebank("utf-8");
            m.loadPath(file);
            for(Tree t:m)
            {
                newtreebank.add(t);
            }
        }
    }
    return newtreebank;
    }
  /**
   * The main method demonstrates the easiest way to train a parser, self-train if needed and test 
   * on a corpus.
   *
   * Usage: {@code java StatsParsing [train_corpus] [train_size] [self-train_corpus] [self-train_size] [test_corpus] [test_size]}
   *
   */
  public static void main(String[] args) {
    int argIndex=1;
    FileFilter trainFilter = null;
    String traintreebankPath = null;
    FileFilter selftrainFilter = null;
    String selftraintreebankPath = null;
    FileFilter testFilter = null;
    String testtreebankPath = null;
    int size_seed=Integer.parseInt(args[0]);
    
    while (argIndex < args.length && args[argIndex].charAt(0) == '-') {
      if (args[argIndex].equalsIgnoreCase("-train")){
    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-train");
    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
    traintreebankPath = treebankDescription.first();
    trainFilter = treebankDescription.second();
      }
    else if (args[argIndex].equalsIgnoreCase("-selftrain")){
    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-selftrain");
    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
    selftraintreebankPath = treebankDescription.first();
    selftrainFilter = treebankDescription.second();
      }
    else if (args[argIndex].equalsIgnoreCase("-test")){
    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-test");
    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
    testtreebankPath = treebankDescription.first();
    testFilter = treebankDescription.second();
      }
    }
    Options op = new Options();
    op.doDep = false;
    op.doPCFG = true;
    op.setOptions("-goodPCFG", "-evals", "tsv");
    
    //generating three tree_banks one each for training, self-training and testing
    MemoryTreebank traintreebank = new MemoryTreebank("utf-8");
    traintreebank.loadPath(traintreebankPath);
    MemoryTreebank selftraintreebank = new MemoryTreebank("utf-8");
    MemoryTreebank testtreebank = new MemoryTreebank("utf-8");
    
    ArrayList<MemoryTreebank> list=new ArrayList<MemoryTreebank>();
    
    //Split the self-train corpus (Brown in our case) into 90-10.
    File folder = new File(selftraintreebankPath);
    File[] listOfFiles = folder.listFiles();
    for (File file : listOfFiles) {
        MemoryTreebank m=new MemoryTreebank("utf-8");
        System.out.println(file.toPath());
        m=generatesplit(file);
        list.add(m);
    }
    
    for(MemoryTreebank m:list)
    {
        int sizing=m.size();
        double num1=sizing*0.9;
        int num=(int)num1;
        for(int i=0;i<num;i++)
        {
            selftraintreebank.add(m.get(i));
        }
        for(int i=num;i<sizing;i++)
        {
            testtreebank.add(m.get(i));
        }
    }

    MemoryTreebank train_truncatedtreebank = new MemoryTreebank();
    for(int i=0;i<size_seed;i++)
    {
        train_truncatedtreebank.add(traintreebank.get(i));
    }
    LexicalizedParser lp = LexicalizedParser.trainFromTreebank(train_truncatedtreebank,op);
   
    for (Tree t : selftraintreebank) {
        List<? extends HasWord> sen = t.yieldWords();
        Tree taggedSen = lp.apply(sen);
        train_truncatedtreebank.add(taggedSen);
    }
    
    Options op1 = new Options();
    op1.doDep = false;
    op1.doPCFG = true;
    op1.setOptions("-goodPCFG", "-evals", "tsv");
    
    //retraining the parser
    LexicalizedParser lp1 = LexicalizedParser.trainFromTreebank(train_truncatedtreebank,op1);
    
    //testing
    EvaluateTreebank eval1=new EvaluateTreebank(lp1);
    eval1.testOnTreebank(testtreebank);
    }
}
