package Score_sets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class get_scores {
public static void main (String[] args) throws FileNotFoundException{
	
	// genelist --> /home/jacobhsu/Server_mirror/Desktop_run/Java_test/gene_sampling_test/genelist/From_literature/CGD/genelist/AD_genelist.txt
	// score --> /media/90F87AAFF87A92E8/Users/sjhsu/Google\ Drive/HKU_LAB/Non\ essential\ genes/20150511_Scoresets_temp/CGD_AD_model_score.txt
	
	Scanner genelistScanner	 = new Scanner(new File(args[0]));
    Scanner scoreScanner = new Scanner(new File(args[1]));
	/*read whatever genelist & Scoresets */  
    
    ArrayList<String> genelist = new ArrayList<String>(); 
    Map<String, Double> score_pair = new HashMap<String, Double>();
    
//==========================================================================================================================================================================    
    
    while (genelistScanner.hasNextLine()){
    	String data = genelistScanner.nextLine();
        String [] array = data.split("\t", 1);
        String geneName = array[0];
        genelist.add(geneName);
    }
    genelistScanner.close();
    
        
	//genelistScanner.nextLine();/*skip the first line*/    
    while (scoreScanner.hasNextLine()){
    	String data = scoreScanner.nextLine();
        String [] array = data.split("\t", 2);
        String geneName = array[0];
        String text = array[1];
        //System.out.println(geneName+"\t"+text);
        Double score = Double.parseDouble(text);
        if(genelist.contains(geneName)){
        	score_pair.put(geneName, score);	
        }
        else{} 
    }
    scoreScanner.close();
    
    
    try{
        File fileTwo=new File(args[2]);
        FileOutputStream fos=new FileOutputStream(fileTwo);
            PrintWriter pw=new PrintWriter(fos);

            for(Entry<String, Double> m :score_pair.entrySet()){
                pw.println(m.getKey()+"\t"+m.getValue());
            }
            pw.flush();
            pw.close();
            fos.close();
        }catch(Exception e){}
	
}	
}
