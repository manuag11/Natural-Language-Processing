package lm;

import java.io.*;
import java.util.*;

/** 
 *
 * @author Ray Mooney
 * Methods for processing Linguistic Data Consortium (LDC,www.ldc.upenn.edu) 
 * data files that are tagged for Part Of Speech (POS). Converts tagged files
 * into simple untagged Lists of sentences which are Lists of String tokens.
*/

public class ProcessFilesForMallet {

    /** The name of the LDC POS file */
    public File file = null;
    /** The I/O reader for accessing the file */
    protected BufferedReader reader = null;

    /** Create an object for a given LDC POS tagged file */
    public ProcessFilesForMallet(File file) {
	this.file = file;
	try {
	    this.reader = new BufferedReader(new FileReader(file));
	}
	catch (IOException e) {
	    System.out.println("\nCould not open POSTaggedFile: " + file);
	    System.exit(1);
	}
    }

    /** Return the next line of POS tagged tokens from this file.
        Returns "\n" if end of sentence and start of a new one. 
        Returns null if end of file */
    protected String getNextPOSLine() {
	String line = null;
	try {
	    do {
		// Read a line from the file
		line = reader.readLine();
		if (line == null) {
		    // End of file, no more tokens, return null
		    reader.close();
		    return null;
		}
		// Sentence boundary indicator
		if (line.startsWith("======="))
		    line = "\n";
		// If sentence number indicator for ATIS or comment for Brown, ignore it
		if (line.startsWith("[ @") || line.startsWith("*x*"))
		    line = "";
	    } while (line.equals(""));
	}
	catch (IOException e) {
	    System.out.println("\nCould not read from TextFileDocument: " + file);
	    System.exit(1);
	}
	return line;
    }

    /** Take a line from the file and return a list of String tokens in the line */    
    protected List<String> getTokens (String line, boolean flag) {
	List<String> tokenList = new ArrayList<String>();
	line = line.trim();
	// Use a tokenizer to extract token/POS pairs in line, 
	// ignore brackets indicating chunk boundaries
	StringTokenizer tokenizer = new StringTokenizer(line, " []");
	while (tokenizer.hasMoreTokens()) {
	    String tokenPos = tokenizer.nextToken();
	    tokenList.add(segmentToken(tokenPos,flag));
	    // If last token in line has end of sentence tag ".", 
	    // add a sentence end token </S>
	    if (tokenPos.endsWith("/.") && !tokenizer.hasMoreTokens()) {
		tokenList.add("</S>");
		}
	}
	return tokenList;
    }

    /* check for common English suffixes, orthographic features, hyphens and numbers */
    String processToken(String tokenPos)
    {
        if(tokenPos.endsWith("s"))
        {
            tokenPos=tokenPos+" "+"s";
        }
        if(tokenPos.endsWith("ing"))
        {
            tokenPos=tokenPos+" "+"ing";
        }
        if(tokenPos.endsWith("ed"))
        {
            tokenPos=tokenPos+" "+"ed";
        }
        if(tokenPos.endsWith("ly"))
        {
            tokenPos=tokenPos+" "+"ly";
        }
        if((tokenPos.endsWith("er"))||(tokenPos.endsWith("or")))
        {
            tokenPos=tokenPos+" "+"eror";
        }
        if(tokenPos.endsWith("ion"))
        {
            tokenPos=tokenPos+" "+"ion";
        }
        if(tokenPos.endsWith("ble"))
        {
            tokenPos=tokenPos+" "+"ble";
        }
        if(tokenPos.endsWith("al"))
        {
            tokenPos=tokenPos+" "+"al";
        }
        if(tokenPos.endsWith("y"))
        {
            tokenPos=tokenPos+" "+"y";
        }
        if(tokenPos.endsWith("ness"))
        {
            tokenPos=tokenPos+" "+"ness";
        }
        if(tokenPos.endsWith("ment"))
        {
            tokenPos=tokenPos+" "+"ment";
        }
        if(tokenPos.endsWith("ic"))
        {
            tokenPos=tokenPos+" "+"ic";
        }
        if(tokenPos.endsWith("ous"))
        {
            tokenPos=tokenPos+" "+"ous";
        }
        if(tokenPos.endsWith("en"))
        {
            tokenPos=tokenPos+" "+"en";
        }
        if(tokenPos.endsWith("er"))
        {
            tokenPos=tokenPos+" "+"er";
        }
        if(tokenPos.endsWith("ive"))
        {
            tokenPos=tokenPos+" "+"ive";
        }
        if(tokenPos.endsWith("ful"))
        {
            tokenPos=tokenPos+" "+"ful";
        }
        if(tokenPos.endsWith("less"))
        {
            tokenPos=tokenPos+" "+"less";
        }
        if(tokenPos.endsWith("est"))
        {
            tokenPos=tokenPos+" "+"est";
        }
        if(Character.isDigit(tokenPos.charAt(0)))
        {
            tokenPos=tokenPos+" "+"num";
        }
        if(Character.isUpperCase(tokenPos.charAt(0)))
        {
            tokenPos=tokenPos+" "+"caps";
        }
        if(tokenPos.contains("-"))
        {
            tokenPos=tokenPos+" "+"hyphen";
        }
        return tokenPos;
    }
    
    /** Segment a token/POS string and return just the token */
    protected String segmentToken (String tokenPos, boolean flag) {
	// POS tag follows the last slash
	int slash = tokenPos.lastIndexOf("/");
        String s;
	if (slash < 0)
        {
	    s=tokenPos;
        }
	else
        {
            s=tokenPos.substring(0,slash);
        }
        // add orthographic features to the string
        if(flag)
        {
            s=processToken(s);
        }
        if(slash>=0)
            s=s+" "+tokenPos.substring(slash+1);
        return s;
    }

    /** Write a sentence to the output stream represented by PrintWriter */
    
    void writetofile(PrintWriter pw, List<String> sentence)
    {
        for(String token:sentence)
        {
            pw.write(token+"\n");
        }
        pw.write("\n");
    }
    
    /** Write a List of sentences each represented as a List of String tokens for 
        the sentences in this file to the output stream represented by PrintWriter */
    protected void tokenLists(PrintWriter pw, boolean flag) {
	List<String> sentence = new ArrayList<String>();
	String line;
	while ((line=getNextPOSLine()) != null) {
	    // Newline line indicates new sentence
	    if (line.equals("\n")) {
		if (!sentence.isEmpty()) {
		    // Save completed sentence
		    writetofile(pw,sentence);
		    // and start a new sentence
		    sentence = new ArrayList<String>();
                    
		}
	    }
	    else {
		// Get the tokens in the line
		List<String> tokens = getTokens(line,flag);
		if (!tokens.isEmpty()) {
		    // If last token is an end-sentence token "</S>"
		    if (tokens.get(tokens.size()-1).equals("</S>")) {
			// Then remove it 
			tokens.remove(tokens.size()-1);
			// and add final sentence tokens
			sentence.addAll(tokens);
			// Save completed sentence
			writetofile(pw,sentence);
			// and start a new sentence
			sentence = new ArrayList<String>();
		    }
		    else {
			// Add the tokens in the line to the current sentence
			sentence.addAll(tokens);
		    }
		}
	    }
	}
	// File should always end at end of a sentence
	assert(sentence.isEmpty());
    }


    /** Take a list of LDC tagged input files or directories and convert them to a List of sentences
       each represented as a List of token Strings */
    public static void writeTokenLists(File[] files, PrintWriter pw, boolean flag) { 
	for (int i = 0; i < files.length; i++) {
	    File file = files[i];
	    if (!file.isDirectory()) {
		if (!file.getName().contains("CHANGES.LOG"))
		    new ProcessFilesForMallet(file).tokenLists(pw,flag);
	    }
	    else 
	    {
		File[] dirFiles = file.listFiles();
		writeTokenLists(dirFiles,pw,flag);
	    }
		
	}          
    }
	
    /** Convert LDC POS tagged files to just lists of tokens for each sentence
     *  and print them out. */
    public static void main(String[] args) throws IOException {
        //flag for including orthographic features
        boolean flag=false;
	File[] files = new File[args.length];
	for (int i = 0; i < files.length; i++) 
	    files[i] = new File(args[i]);
        String filename="output_check.txt";
        FileWriter fw=new FileWriter(filename);
        BufferedWriter bw=new BufferedWriter(fw);
        PrintWriter pw=new PrintWriter(bw);
	writeTokenLists(files,pw,flag);	
        pw.close();
        bw.close();
        fw.close();
    }

}
