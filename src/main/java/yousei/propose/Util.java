package yousei.propose;

import org.eclipse.core.runtime.CoreException;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import yousei.GeneralUtil;
import yousei.util.NodeClasses4Java;

import java.io.*;
import java.util.*;

import static yousei.GeneralUtil.writeQuestions;
import static yousei.GeneralUtil.diffIsBig;
import static yousei.GeneralUtil.writeVector;

/**
 * Created by s-sumi on 16/07/23.
 * 提案手法用のUtil集
 */
public class Util {
    public static File genealogy2Arff4VectorPrediction(List<List<Integer>> preVector, List<List<Integer>> postVector) throws Exception {
    public static int smallchange=0;

    //create arff having only 1 instance, maybe being all postvector ? is ok
    public static File createSouceArff(String source)throws Exception{
        NodeClasses4Java nc = new NodeClasses4Java();
        File tmpFile = File.createTempFile("bugSource", ".arff", GeneralUtil.workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();

        List<Integer> sourceVector=GeneralUtil.getSourceVector4Java(source,".java");//4java ttearundakara javasikanakunai

        writeVector(bw, sourceVector);
        bw.write(",");
        writeQuestions(bw,sourceVector);
        bw.newLine();
        return tmpFile;
    }

    public static File genealogy2Arff4VectorPred(List<List<Integer>> preVector, List<List<Integer>> postVector) throws Exception {
        smallchange=0;
        NodeClasses4Java nc = new NodeClasses4Java();
        File tmpFile = File.createTempFile("genealogy4VectorPrediction", ".arff", GeneralUtil.workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        for (int i = 0; i < preVector.size(); i++) {
            if (GeneralUtil.diffIsBig(preVector.get(i), postVector.get(i), GeneralUtil.SMALLTHRESHOLD))
                continue;

            smallchange++;

            writeVector(bw, preVector.get(i));
            bw.write(",");
            writeVector(bw, postVector.get(i));
            bw.newLine();
        }

        bw.close();
        return tmpFile;
    }

    public static List<Double> vectoredPrediction(File arffData, File bugArffData) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(arffData));
        Instances instances = new Instances(br);
        int numAttribute = instances.numAttributes() / 2;//arff has 2 vector in an instance
        br.close();

        List<Instances> filteredData = GeneralUtil.getFilteredData(arffData, numAttribute);


        LinearRegression lr = new LinearRegression();
        String[] options = {"-S", "0"};
        lr.setOptions(options);

        List<Classifier> classifiers = new ArrayList<>();
        for (int i = 0; i < numAttribute; i++) {         //各ノードに対する予測器を構築
            Classifier copied = AbstractClassifier.makeCopy(lr);
            copied.buildClassifier(filteredData.get(i));
            classifiers.add(copied);
        }

        List<Instance> attrSelectedData=getAttrSelectedData(bugArffData,filteredData);
        List<Double> res=new ArrayList<>();
        for (int i = 0; i < numAttribute; i++) {
            res.add(
                classifiers.get(i).classifyInstance(attrSelectedData.get(i))
            );
        }

        return res;
    }

    public static List<Instance> getAttrSelectedData(File bugArff,List<Instances> filteredData)throws Exception{
        BufferedReader br1 = new BufferedReader(new FileReader(bugArff));
        Instances instances = new Instances(br1);
        int numAttribute = instances.numAttributes() / 2;//arff has 2 vector in an instance
        br1.close();

        List<Instances> tmp=new ArrayList<>();

        for(int i=0;i<numAttribute;i++){
            BufferedReader br2 = new BufferedReader(new FileReader(bugArff));
            Instances bugInstances = new Instances(br2);
            br2.close();

            Instances filteredInstances=filteredData.get(i);
            for(int j=0;j<filteredInstances.numAttributes()-1;j++){
                tmp.add(makeSameAttrData(filteredInstances,bugInstances));
            }
        }

        List<Instance> res=new ArrayList<>();
        for(Instances i:tmp){
            if(i.numInstances()!=1)
                throw new Exception("there is 2 or more instances. what happened.");

            res.add(i.get(0));
        }

        return res;
    }

    public static Instances makeSameAttrData(Instances valid,Instances manipulate)throws Exception{
        Instances copied=new Instances(manipulate);

        StringBuilder remaining =new StringBuilder();
        for(int i=0;i<valid.numAttributes()-1;i++){ //make a string like: 1,3,5,6,7-10
            remaining.append(
                    String.valueOf(getAttrNum(valid.attribute(i),copied)+1)
                            +",");
        }
        remaining.append(copied.numAttributes());//add classindex

        Remove remove=new Remove();
        remove.setAttributeIndices(remaining.toString());
        remove.setInvertSelection(true);
        remove.setInputFormat(copied);

        Instances res=Filter.useFilter(copied,remove);
        res.setClassIndex(res.numAttributes()-1);
        return res;
    }
    public static Integer getAttrNum(Attribute attribute,Instances testee)throws Exception{//testee has n attribute and 1 classvalue
        for(int i=0;i<testee.numAttributes()-1;i++)
            if(Objects.equals(testee.attribute(i).name(),attribute.name()))
                return i;

        throw new Exception("an error occured");
    }

    /**
     * 全ての変更から変更の大きさ予測用の学習データを作成する
     * @param preVector
     * @param postVector
     * @return
     * @throws Exception
     */
    public static File genealogy2Arff4SizePrediction(List<List<Integer>> preVector, List<List<Integer>> postVector) throws Exception {
        NodeClasses4Java nc = new NodeClasses4Java();
        File tmpFile = File.createTempFile("genealogy4SizePrediction", ".arff", GeneralUtil.workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary4j.entrySet()) {
            bw.write("@attribute size {big,small}");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        for (int i = 0; i < preVector.size(); i++) {
            writeVector(bw, preVector.get(i)); bw.write(",");
            writeVector(bw, postVector.get(i)); bw.write(",");

            if(diffIsBig(preVector.get(i),postVector.get(i),GeneralUtil.SMALLTHRESHOLD)){
                bw.write("big");
            }else{
                bw.write("small");
            }
            bw.newLine();
        }

        bw.close();
        return tmpFile;
    }
}
