To run the code, type the following on the terminal:

java src/lm/BigramModel pos/atis 0.1
java src/lm/BigramModel pos/wsj 0.1
java src/lm/BigramModel pos/brown 0.1
java src/lm/BackwardBigramModel pos/atis 0.1
java src/lm/BackwardBigramModel pos/wsj 0.1
java src/lm/BackwardBigramModel pos/brown 0.1
java src/lm/BidirectionalBigramModel pos/atis 0.1
java src/lm/BidirectionalBigramModel pos/wsj 0.1
java src/lm/BidirectionalBigramModel pos/brown 0.1

If you want to compile any of the java files once again, type the following on the terminal (although you will not require it as I have supplied the .class files along with the code)
javac src/lm/<filename>.java

BigramModel.java - forward bigram model
BackwardBigramModel.java - backward bigram model
BidirectionalBigramModel.java - bidirectional bigram model
DoubleValue.java - a wrapper around double values
POSTaggedFile.java - java file to extract sentences from the corpus