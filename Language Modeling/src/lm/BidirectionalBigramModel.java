package lm;

import java.io.*;
import java.util.*;

/** 
 * @author Manu Agarwal
 * A bidirectional bigram language model that uses simple fixed-weight interpolation
 * with a unigram model for smoothing.
*/

public class BidirectionalBigramModel {
    /* An object for forward Bigram Model */
    public BigramModel object1=null;
    /* An object for backward Bigram Model */
    public BackwardBigramModel object2=null;

    /* Initialize the two objects */
    public BidirectionalBigramModel() {
	object1=new BigramModel();
        object2=new BackwardBigramModel();
    }

    /** Train the model on a List of sentences represented as
     *  Lists of String tokens */
    public void train (List<List<String>> sentences) {
	// Train the model using forward bigram model
	object1.train(sentences);
        // Train the model using backward bigram model
        object2.train(sentences);
    }

    
    /** Function to test the performance of our model on a test dataset */
    public void test (List<List<String>> sentences) {
	double totalLogProb = 0;
	double totalNumTokens = 0;
	for (List<String> sentence : sentences) {
	    totalNumTokens += sentence.size();
	    double sentenceLogProb = sentenceLogProb(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    totalLogProb += sentenceLogProb;
	}
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Word Perplexity = " + perplexity );
    }
    
     /* Compute log probability of sentence given current model */
    public double sentenceLogProb (List<String> sentence) {
        //calculating the forward probability of the tokens in the sentence
        double forward_probability[]=object1.sentenceTokenProbs(sentence);
        //calculating the backward probability of the tokens in the sentence
        double backward_probability[]=object2.sentenceTokenProbs(sentence);
        int n=sentence.size();
        /*final_probablity shall be the interpolated probability of the forward 
        and the backward bigram model that this function will return. For this
        project, we have used equal weights for the two probabilities.*/
        double final_probability=0;
        for(int i=0;i<n;i++)
        {
            final_probability+=Math.log(0.5*forward_probability[i]+0.5*backward_probability[n-1-i]);
        }
        return final_probability;
    }

    /** Returns vector of probabilities of predicting each token in the sentence */
    public double[] sentenceTokenProbs (List<String> sentence) {
	// Vector for storing token prediction probs
	double[] tokenProbs = new double[sentence.size() + 1];
	double[] tokenProbs_forward = object1.sentenceTokenProbs(sentence);
        double[] tokenProbs_backward = object2.sentenceTokenProbs(sentence);
        int n=tokenProbs.length;
        for(int i=0;i<n;i++)
        {
            tokenProbs[i]=0.5*tokenProbs_forward[i]+0.5*tokenProbs_backward[i];
        }
	return tokenProbs;
    }

    
    public static int wordCount (List<List<String>> sentences) {
	int wordCount = 0;
	for (List<String> sentence : sentences) {
	    wordCount += sentence.size();
	}
	return wordCount;
    }

    /** Train and test a bigram model.
     *  Command format: "nlp.lm.BidirectionalBigramModel [DIR]* [TestFrac]" where DIR 
     *  is the name of a file or directory whose LDC POS Tagged files should be 
     *  used for input data; and TestFrac is the fraction of the sentences
     *  in this data that should be used for testing, the rest for training.
     *  0 < TestFrac < 1
     *  Uses the last fraction of the data for testing and the first part
     *  for training.
     */
    public static void main(String[] args) throws IOException {
	// All but last arg is a file/directory of LDC tagged input data
	File[] files = new File[args.length - 1];
	for (int i = 0; i < files.length; i++) 
	    files[i] = new File(args[i]);
	// Last arg is the TestFrac
	double testFraction = Double.valueOf(args[args.length -1]);
	// Get list of sentences from the LDC POS tagged input files
	List<List<String>> sentences = 	POSTaggedFile.convertToTokenLists(files);
	int numSentences = sentences.size();
	// Compute number of test sentences based on TestFrac
	int numTest = (int)Math.round(numSentences * testFraction);
	// Take test sentences from end of data
	List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
	// Take training sentences from start of data
	List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
	System.out.println("# Train Sentences = " + trainSentences.size() + 
			   " (# words = " + wordCount(trainSentences) + 
			   ") \n# Test Sentences = " + testSentences.size() +
			   " (# words = " + wordCount(testSentences) + ")");
	// Create a bigram model and train it.
	BidirectionalBigramModel model = new BidirectionalBigramModel();
	System.out.println("Training...");
	model.train(trainSentences);
	// Test on training data using test and test2
	model.test(trainSentences);
	System.out.println("Testing...");
	// Test on test data using test and test2
	model.test(testSentences);
    }

}
