The project contains two Java files StatsParsing.java and StatsParsing1.java.
Both these java files can be used to train the parser on a source corpus, self-train on the target corpus if needed and test on a target corpus(which may be same as the source too in which case it would be in-domain testing)
I have customized the StatsParsing.java for training on WSJ and testing on Brown as Brown requires a split of 90-10. I have also customized StatsParsing1.java for training on Brown and testing on WSJ.
To run the files, go into the source directory, include sections 2-22 of WSJ in a folder mrg/wsj inside the source folder, the entire Brown corpus in the folder mrg/brown inside the source folder and type the following commands:

javac -cp "stanford-parser.jar:slf4j-api.jar:" StatsParsing.java
java -cp "stanford-parser.jar:slf4j-api.jar:" StatsParsing 1000 -train mrg/wsj 02-22 -selftrain mrg/brown 1-8 -test mrg/brown 1-8

The above command demonstrates training on WSJ sections 2-22(I explicitly put just sections 2-22 in a folder called wsj to reduce complexity), selftraining on Brown (90%) and testing on Brown(10%). In essence, the values 2-22 and 1-8 do not have significance in our experiment. 1000 indicates the size of the seed set. The size of the self-training set is fixed at 10000. In order to vary the self-training set, I just changed the parameter in StatsParsing.java code to make arg[0] point to self-training set size instead of seed size.

Below is the script for training on Brown(90%), self-training on WSJ(sections 2-22) and testing on WSJ(section 23)(I explicitly put just sections 2-22 in a folder called wsj to reduce complexity and section 23 in a folder called wsjtest to reduce complexity). In essence, the values 2-22 and 1-8 do not have significance in our experiment. 1000 indicates the size of the seed set. The size of the self-training set is fixed at 10000. In order to vary the self-training set, I just changed the parameter in StatsParsing1.java code to make arg[0] point to self-training set size instead of seed size.

Just include sections 2-22 of WSJ in a folder mrg/wsj inside the source folder, section 23 of WSJ in the folder mrg/wsjtest in the source folder, the entire Brown corpus in the folder mrg/brown inside the source folder and then run the following commands:

javac -cp "stanford-parser.jar:slf4j-api.jar:" StatsParsing1.java
java -cp "stanford-parser.jar:slf4j-api.jar:" StatsParsing1 1000 -train mrg/brown 1-8 -selftrain mrg/wsj 2-22 -test mrg/wsjtest 23

All the other scripts I used to run various versions of the experiment are in the folder condor.

The folder trace_files contains the trace_files generated through the experiment. The names of the trace files are self-explanatory.

The file hw3_Manu_ma53767_report.pdf is a report on this experiment. The report also contains a link to a google spreadsheet containing the entire results of the experiment.