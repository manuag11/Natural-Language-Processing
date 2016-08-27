The project involves an experiment to compare the performance of HMM and CRF. We use Mallet's implementation of HMM and CRF for our purposes. Mallet is a Java based package for statistical Natural Language Processing and related tasks. We downloaded the source code of Mallet from http://mallet.cs.umass.edu/. We also downloaded HMMSimpleTagger.java from /u/mooney/cs388-code/nlp/pos/HMMSimpleTagger.java into our source folder. We use mallet-2.0.8RC3 for the purposes of this experiment. We then compiled it using Apache Ant. 
A preprocessing code was written to convert the pos files to a form compatible with mallet. The source code is contained in the file ProcessFilesForMallet.java.
We made changes to four files of Mallet for calculating the OOV accuracy, namely HMMSimpleTagger.java, SimpleTagger.java, TokenAccuracyEvaluator.java and TransduverEvaluator.java. We created HashMap<String,Boolean> in each of HMMSimpleTagger.java's main method and SimpleTagger.java's main method to store the tokens encountered during training. We then passed this map along with a boolean variable (indicating whether to predict OOV accuracy or not) to evaluate method of the class TransduverEvaluator.java which in turn invokes the EvaluateInstanceList method of class TokenAccuracyEvaluator.java. We have made a change to the signature of the method evaluate of class TransduverEvaluator.java. We have also modified the EvaluateInstanceList method of class TokenAccuracyEvaluator.java where we also calculate the OOV accuracy now. These changed files are in the folder mallet_codebase. The preprocessing code is in the folder converter_codebase. 
The commands used to run the experiment were as follows:
$ java -cp "mallet-2.0.8RC3/class:mallet-2.0.8RC3/lib/mallet-deps.jar"
       cc.mallet.fst.HMMSimpleTagger
       --train true --model-file model_file
       --training-proportion 0.8
       --test lab atis.txt

$ java -cp "mallet-2.0.8RC3/class:mallet-2.0.8RC3/lib/mallet-deps.jar"
       cc.mallet.fst.SimpleTagger
       --train true --model-file model_file
       --training-proportion 0.8
       --test lab wsj.txt
$ condor_submit condor_file.condor

The first two commands are for running java programs from the command line and the last one is for submitting a condor job. 

We also include the condor files in the folder condor_scripts. 

We also generate multiple trace files which are in the folder trace_files. The trace_files have been named in such a way which makes them easily understandable. 

Finally, we also include the preprocessed files that act as input for mallet in the folder mallet_pos.