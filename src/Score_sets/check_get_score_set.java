package Score_sets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class check_get_score_set {
public static void main (String[] args) throws FileNotFoundException{
// Date : Sep/02/2015
// Purpose : 	
// This is for checking a gene for all scores and whether this gene is included in our training set. Also, this script can extract all gene-based scores. 
// I haven't finished this yet	
	 
	Scanner ADlist= new Scanner(new File("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/AD_genelist.txt"));
	Scanner ARlist= new Scanner(new File("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/AR_genelist.txt"));
	Scanner XLlist= new Scanner(new File("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/XL_genelist.txt"));
	Scanner PAElist= new Scanner(new File("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/Pediatric_genelist.txt"));
	ArrayList<String> ADgenelist = new ArrayList<String>();
	ArrayList<String> ARgenelist = new ArrayList<String>();
	ArrayList<String> XLgenelist = new ArrayList<String>();
	ArrayList<String> PAEgenelist = new ArrayList<String>();
	
	while (ADlist.hasNextLine()) {
		String ADgene = ADlist.nextLine();
		ADgenelist.add(ADgene);
	}
	while (ARlist.hasNextLine()) {
		String ARgene = ARlist.nextLine();
		ARgenelist.add(ARgene);
	}
	while (XLlist.hasNextLine()) {
		String XLgene = XLlist.nextLine();
		XLgenelist.add(XLgene);
	}
	while (PAElist.hasNextLine()) {
		String PAEgene = PAElist.nextLine();
		PAEgenelist.add(PAEgene);
	}
	ADlist.close();
	ARlist.close();
	XLlist.close();
	PAElist.close();
//==========================================================================================================================================================================    
	Scanner score_scan = new Scanner(new File("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/score/four_scores.txt"));
    Map<String, String> score_sets = new HashMap<String, String>();
    while (score_scan.hasNextLine()) {
		String CGDgene = score_scan.nextLine();
		String [] array = CGDgene.split(",", 2);
        String geneName = array[0];
        String scores = array[1];
        //System.out.println(geneName+"\t"+text);
        //Double score = Double.parseDouble(scores);
        score_sets.put(geneName, scores);	
	}
	score_scan.close();
//===========================================================================================================================================================================   
	Scanner Candigene = new Scanner(new File(args[0]));
    while (Candigene.hasNextLine()){
    	String data = Candigene.nextLine();
        if(ADgenelist.contains(data)){
        System.out.println(data+"\t"+"AD");  // The gene is in AD training set
        }
        else if (ARgenelist.contains(data)) {
        	System.out.println(data+"\t"+"AR"); // The gene is in AR training set
		}
        else if (XLgenelist.contains(data)) {
        	System.out.println(data+"\t"+"XL"); // The gene is in XL training set
		}
        else{
        	System.out.println(data+"\t"+"NO training"); // The gene is NOT in any inheritance mode training sets
        } 
    }
    Candigene.close();
//==========================================================================================================================================================================   
    Scanner Candigene2 = new Scanner(new File(args[0]));
    while (Candigene2.hasNextLine()) {
		String gene = Candigene2.nextLine();
		if (PAEgenelist.contains(gene)) {
			System.out.println(gene+"\t"+"Paediatric training set"); // The gene is in Paediatric training set
		}
		else {System.out.println(gene+"\t"+"NOT in Paediatric training");}
	}
    Candigene2.close();
//=========================================================================================================================================================================    
    Scanner Candigene3 = new Scanner(new File(args[0]));
    while (Candigene3.hasNextLine()) {
		String cgdgene = Candigene3.nextLine();
		if (score_sets.containsKey(cgdgene)) {
			System.out.println(cgdgene+"\t"+score_sets.get(cgdgene));
		}
		else {System.out.println(cgdgene+"\t"+"does NOT have any score ");}
	}
    Candigene3.close();
    
    
    
/*    
    try{
        File fileTwo=new File(args[2]);
        FileOutputStream fos=new FileOutputStream(fileTwo);
            PrintWriter pw=new PrintWriter(fos);

            for(Entry<String, String> m :score_sets.entrySet()){
                pw.println(m.getKey()+"\t"+m.getValue());
            };hns\
            pw.flush();
            pw.close();
            fos.close();
        }catch(Exception e){}
*/
}	
}