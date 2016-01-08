/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cobi.genepriori;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import hr.irb.fastRandomForest.FastRandomForest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.cobi.util.LocalFile;
import org.omg.CORBA.ARG_IN;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author mxli
 */
public class GeneMLReadPset0326 {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Scanner scanGene = new Scanner(new File(args[0])); /*This step is reading the candi genelist*//*Ex: genelist/Approved_essential_gene_list_1.txt*/


            List<String> subgenelist = new ArrayList<String>(); // put gene into ArrayList
            while (scanGene.hasNextLine()) {
                String line2 = scanGene.nextLine();
                String[] array = line2.split("\t", 2); /*candi genelist has two columns (GeneSymbol : type of genelist)*/

                subgenelist.add(array[0]);
            }
            GeneMLReadPset0326 app = new GeneMLReadPset0326();
            app.caseGenes = subgenelist.toArray(new String[subgenelist.size()]); // transform subgenelist into caseGenes []

            //for (int eleNum = 19; eleNum <= 20; eleNum++) {
            //  CombinationGenerator x = new CombinationGenerator(14, eleNum);
            //  String tmpTxt="20150113_data_table_toMX_noMAF_noNonCoding_subset.txt";
            String tmpTxt = "ddd.txt";
            Scanner scanPset = new Scanner(new File(args[1]));
            Instances traingData=null;
            while (scanPset.hasNextLine()) {
                String pset = scanPset.nextLine();
                //System.out.println(pset);
                String[] index = pset.split(",");
                int[] next = new int[index.length];
                for (int i = 0; i < index.length; i++) {
                    try {
                        next[i] = Integer.parseInt(index[i]);
                    } catch (NumberFormatException nfe) {
                    };
                }
                System.out.println(Arrays.toString(next) + " ");
                //app.generateFile(next, "20141029_data_table_toMX_test.txt");

                app.generateFile(next, tmpTxt);
                app.generateArff(tmpTxt, "tmp.arff");
                double tmpPerformance = app.predictionEvaluation("tmp.arff");
                if (tmpPerformance > app.optimalPerformance) {
                    app.optimalPerformance = tmpPerformance;
                    app.optimalList = Arrays.copyOf(next, next.length);
                    
                    //a copy of tmp.arff as optimal.arff                    
                    traingData = new weka.core.converters.ConverterUtils.DataSource("tmp.arff").getDataSet();
                    if (traingData.classIndex() == -1) {
                        traingData.setClassIndex(traingData.numAttributes() - 1);
                    }
                }
            }

            List<String> geneList = app.generateFile(app.optimalList, tmpTxt);
            String[] heads = app.generateArff1(tmpTxt, "test.arff");

            Instances testingData = new weka.core.converters.ConverterUtils.DataSource("test.arff").getDataSet();
            if (testingData.classIndex() == -1) {
                testingData.setClassIndex(testingData.numAttributes() - 1);
            }

            FastRandomForest aClassifiers = new FastRandomForest();
            aClassifiers.setOptions(new String[]{"-I", "100", "-threads", "7", "-import"});
            aClassifiers.buildClassifier(traingData);
            //app.predictionEvaluation("tmp.arff");
            double[] imp = aClassifiers.getFeatureImportances();
            System.out.println();
            for (int i = 0; i < imp.length - 1; i++) {
                System.out.println(heads[i + 1] + "\t" + imp[i]);
            }
            List<String> modelscore = new ArrayList<String>();
            for (int i = 0; i < testingData.numInstances(); i++) {
                double[] preV = aClassifiers.distributionForInstance(testingData.get(i));
                String score = new String(geneList.get(i) + "\t" + preV[1]);
                modelscore.add(score);
                //System.out.println(geneList.get(i) + "\t" + preV[1]);
            }
            FileUtils.writeLines(new File("model_score.txt"), modelscore);

            String[] item = geneList.toArray(new String[geneList.size()]);
            List<String> bkGeneList = new ArrayList<String>();
            for (int i = 0; i < item.length - 1; i++) {
                if (subgenelist.contains(item[i])) {
                } else {
                    bkGeneList.add(item[i]);
                }
            }
            FileUtils.writeLines(new File("control_geneset.txt"), bkGeneList);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public int[] optimalList;
    public double optimalPerformance = -1;
    public String[] caseGenes; // claim caseGenes[] as public to use

    public List<String> generateFile(int[] attrIndex, String outputFile) {
        try {
            for (int i = 0; i < attrIndex.length; i++) {
                System.out.print(attrIndex[i] + " ");
            }
            System.out.println();
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));
            //String[] ListContent = ReadFile("/home/jacobhsu/Downloads/GenePriori/20141029_data_table_toMX1.txt");
            //String[] ListContent = ReadFile("/home/jacobhsu/Server_mirror/Desktop_run/Java_test/gene_sampling_test/gene_features/DATA_TABLE/20150327/20150327_dataTable_noMAF_noNonCoding_plus_scores_tab_fillgap.txt");
            //String[] ListContent = ReadFile("/home/groups/pcsham/shared/Non_essential_gene/20150327_dataTable_noMAF_noNonCoding_plus_scores_tab_fillgap.txt");
            //String[] ListContent = ReadFile("/home/jacobhsu/Dropbox/HKU_LAB/Non_essential_paper/Gene_based/dataset_forJAVA/x_chr_test/GeneFeatures_for_only_X_chr_genes.txt");
            String[] ListContent = ReadFile("./Table1.txt");// This is for Bioinformatics journal 
            List<String> geneList = new ArrayList<String>();
            // This contains no non-coding details but non-coding tr count number 
            for (int i = 0; i < ListContent.length; i++) {
                String[] sp = ListContent[i].split("\t");
                String outputString = sp[0];
                //Ingore the head 
                if (i != 0) {
                    geneList.add(sp[0]);
                }
                for (int j = 0; j < attrIndex.length; j++) {
                    outputString += ("\t" + sp[attrIndex[j] + 1]);
                }
                bw.write(outputString.trim() + "\n");
            }
            bw.flush();
            bw.close();
            return geneList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String[] ReadFile(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(new File(
                filename)));
        LinkedList<String> add = new LinkedList<String>();
        while (input.ready()) {
            String storeString = input.readLine().trim();
            add.add(storeString);
        }
        String[] result = add.toArray(new String[add.size()]);
        input.close();
        return result;
    }

    public String[] generateArff(String retriveFile, String arffName) {
        List<String[]> allGeneList = new ArrayList<String[]>();
        LocalFile.retrieveData(retriveFile, allGeneList, "\t");
        Set<String> caseGeneSet = new HashSet<String>(Arrays.asList(caseGenes));
        //System.out.println(caseGeneSet.toString());
        String[] heads = allGeneList.remove(0);
        List<String[]> caseGeneList = new ArrayList<String[]>();
        List<String[]> bkGeneList = new ArrayList<String[]>();

        for (String[] item : allGeneList) {
            if (caseGeneSet.contains(item[0])) {
                caseGeneList.add(item);
            } else {
                bkGeneList.add(item);
            }
        }
        int sampleSize = caseGeneList.size();
        System.out.print("case gene number: " + sampleSize + ", control gene number:" + bkGeneList.size() + " ");

        writeWikaFormat(arffName, heads, caseGeneList, bkGeneList, 1);
        return heads;

        //  not to randomize it
        /*
         int bkgGeneNum = bkGeneList.size();
         int simuTimes = 2;
         List<String[]> contrlGeneList = new ArrayList<String[]>();
         long[] caseindexes = new long[sampleSize];
         RandomEngine re = new cern.jet.random.engine.MersenneTwister(new java.util.Date());
         for (int i = 0; i < simuTimes; i++) {
         // http://acs.lbl.gov/ACSSoftware/colt/api/cern/jet/random/sampling/RandomSampler.html
         RandomSampler.sample(sampleSize, bkgGeneNum, sampleSize, 0, caseindexes, 0, re);
         for (int j = 0; j < sampleSize; j++) {
         contrlGeneList.add(bkGeneList.get((int) caseindexes[j]));
         }

         writeWikaFormat(arffName, heads, caseGeneList, contrlGeneList, 1);
         contrlGeneList.clear();
         System.out.println(); 
         }
         */
    }

    public String[] generateArff1(String retriveFile, String arffName) {
        List<String[]> allGeneList = new ArrayList<String[]>();
        LocalFile.retrieveData(retriveFile, allGeneList, "\t");
        Set<String> caseGeneSet = new HashSet<String>(Arrays.asList(caseGenes));
        //System.out.println(caseGeneSet.toString());
        String[] heads = allGeneList.remove(0);

        int sampleSize = allGeneList.size();
        System.out.print("Total Effective  gene number: " + sampleSize + ".");

        writeWikaFormat(arffName, heads, allGeneList, null, 1);
        return heads;

        //  not to randomize it
        /*
         int bkgGeneNum = bkGeneList.size();
         int simuTimes = 2;
         List<String[]> contrlGeneList = new ArrayList<String[]>();
         long[] caseindexes = new long[sampleSize];
         RandomEngine re = new cern.jet.random.engine.MersenneTwister(new java.util.Date());
         for (int i = 0; i < simuTimes; i++) {
         // http://acs.lbl.gov/ACSSoftware/colt/api/cern/jet/random/sampling/RandomSampler.html
         RandomSampler.sample(sampleSize, bkgGeneNum, sampleSize, 0, caseindexes, 0, re);
         for (int j = 0; j < sampleSize; j++) {
         contrlGeneList.add(bkGeneList.get((int) caseindexes[j]));
         }

         writeWikaFormat(arffName, heads, caseGeneList, contrlGeneList, 1);
         contrlGeneList.clear();
         System.out.println(); 
         }
         */
    }

    public double predictionEvaluation(String datFileName) {
        try {
            // load traingData
            Instances data = new weka.core.converters.ConverterUtils.DataSource(
                    datFileName).getDataSet();
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }

            AbstractClassifier[] aClassifiers = new AbstractClassifier[1];
            aClassifiers[0] = new FastRandomForest();
            aClassifiers[0].setOptions(new String[]{"-I", "100", "-threads", "7"});

            /*	
             aClassifiers[1] = new weka.classifiers.meta.AdaBoostM1();
             aClassifiers[2] = new weka.classifiers.trees.J48();
             aClassifiers[3] = new weka.classifiers.functions.Logistic();
             aClassifiers[4] = new weka.classifiers.functions.SMO();
             aClassifiers[5] = new weka.classifiers.functions.SGD();
             aClassifiers[6] = new weka.classifiers.bayes.NaiveBayes();
             aClassifiers[7] = new weka.classifiers.bayes.NaiveBayesUpdateable();
             aClassifiers[4] = new weka.classifiers.functions.SimpleLogistic();
             */
            for (int ii = 0; ii < aClassifiers.length; ii++) {
                // traingData.deleteWithMissingClass();
                //   System.err.println(ii + " --- begin --- training ...");
                DoubleArrayList rocList = new DoubleArrayList();
                DoubleArrayList prList = new DoubleArrayList();
                DoubleArrayList fList = new DoubleArrayList();
                int repeatTime = 2;

                int numFolds = 10;
                double[] classProps = new double[data.numClasses()];
                for (int i = 0; i < data.numInstances(); i++) {
                    classProps[(int) data.instance(i).classValue()] += data
                            .instance(i).weight();
                }
                Utils.normalize(classProps);

                Evaluation eval = new Evaluation(data);
                Long millis = System.currentTimeMillis();
                for (int i = 0; i < repeatTime; i++) {
                    eval.crossValidateModel(aClassifiers[ii], data, numFolds, new Random(System.currentTimeMillis()));
                    double rocSum = 0.0;
                    double prcSum = 0.0;
                    double fSum = 0.0;
                    double sumClassProps = 0;

                    for (int c = 0; c < data.numClasses(); c++) {
                        if (Double.isNaN(eval.areaUnderROC(c))) {
                            continue;
                        }
                        rocSum += eval.areaUnderROC(c) * classProps[c];
                        prcSum += eval.areaUnderPRC(c) * classProps[c];
                        fSum += eval.fMeasure(c) * classProps[c];

                        // this should sum to 1.0 in the end, as all the classes
                        // with AUC==NaN should have weight 0
                        sumClassProps += classProps[c];

                    }
                    rocSum = rocSum / sumClassProps;
                    prcSum = prcSum / sumClassProps;
    
                    fSum = fSum / sumClassProps;

                    rocList.add(rocSum);
                    prList.add(prcSum);
                    fList.add(fSum);
                }

                long elapsedTime = System.currentTimeMillis() - millis;
                double tmp = Descriptive.mean(rocList);
                System.out.println(tmp + " " + Descriptive.sampleVariance(rocList, Descriptive.mean(rocList)));
                return tmp;
                /*
                 System.out.println(Descriptive.median(rocList)
                 + " "
                 + Descriptive.min(rocList)
                 + " "
                 + Descriptive.max(rocList)
                 + " "
                 + Descriptive.mean(rocList)
                 + " "
                 + Descriptive.sampleVariance(rocList,
                 Descriptive.mean(rocList)));
                 */
                /*	
                 System.out.println(Descriptive.median(prList)
                 + " "
                 + Descriptive.min(prList)
                 + " "
                 + Descriptive.max(prList)
                 + " "
                 + Descriptive.mean(prList)
                 + " "
                 + Descriptive.sampleVariance(prList,
                 Descriptive.mean(prList)));
                 System.out.println(Descriptive.median(fList)
                 + " "
                 + Descriptive.min(fList)
                 + " "
                 + Descriptive.max(fList)
                 + " "
                 + Descriptive.mean(fList)
                 + " "
                 + Descriptive.sampleVariance(fList,
                 Descriptive.mean(fList)));
                 */

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public void writeWikaFormat(String fileName, String[] feildNames,
            List<String[]> caseInstances, List<String[]> controlInstances,
            int startIndex) {
        String line = null;
        Set<String> tmpList = new HashSet<String>();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write("@relation predic\n");
            String[] nomals = new String[feildNames.length];
            for (int i = startIndex; i < feildNames.length; i++) {
                for (String[] values : caseInstances) {
                    if (!values[i].equals(".")) {
                        tmpList.add(values[i]);
                    }
                }
                if (controlInstances != null) {
                    for (String[] values : controlInstances) {
                        if (!values[i].equals(".")) {
                            tmpList.add(values[i]);
                        }
                    }
                }

                if (tmpList.size() < 5) {
                    String val = tmpList.toString();
                    nomals[i] = "{" + val.substring(1, val.length() - 1) + "}";
                    bw.write("@attribute " + feildNames[i] + " " + nomals[i]
                            + "\n");
                } else {
                    bw.write("@attribute " + feildNames[i] + " real\n");
                }
                tmpList.clear();
            }

            bw.write("@attribute case {no,yes}\n");
            bw.write("@data\n");

            for (String[] values : caseInstances) {
                for (int i = startIndex; i < values.length; i++) {
                    bw.write((values[i].equals(".") ? "?" : values[i]) + ",");
                }
                bw.write("yes\n");
            }
            if (controlInstances != null) {
                for (String[] values : controlInstances) {
                    for (int i = startIndex; i < values.length; i++) {
                        bw.write((values[i].equals(".") ? "?" : values[i]) + ",");
                    }
                    bw.write("no\n");
                }
            }
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
