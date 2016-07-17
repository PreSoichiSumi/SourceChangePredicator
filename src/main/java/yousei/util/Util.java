package yousei.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.diff.DiffEntry;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Remove;
import yousei.experiment.CustomizedCrossValidation;

import java.io.*;
import java.util.*;


/**
 * wekaを使った予測のUtil
 * Created by s-sumi on 2016/05/23.
 */
public class Util {
    private static File workingDir = new File("WorkingDir");
    public static int smallchange = 0;
    private static final int SMALLTHRESHOLD = 5;

    public static Map<String, Integer> addMap(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> res = new HashMap<>(map1);
        /*for(Map.Entry<String,Integer> e:map2.entrySet()){
            res.merge(e.getKey(), e.getValue(),
                    (a,b)-> a + b );
        }*/
        map2.entrySet().stream()
                .forEach(e -> res.merge(e.getKey(), e.getValue(), (a, b) -> a + b));
        return res;
    }

    public static Map<String, Integer> getSourceVector(String source) throws IOException, CoreException {
        return getSourceVector(source,".cpp");
    }

    public static Map<String, Integer> getSourceVector(String source,String suffix) throws IOException, CoreException {
        File tmpFile = File.createTempFile("tmp", suffix, workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write(source);
        bw.close();
        CppSourceAnalyzer csa = new CppSourceAnalyzer("", "", "");
        csa.setFilePath(tmpFile.getAbsolutePath());
        Map<String,Integer> res=csa.analyzeFile();
        tmpFile.delete();
        return res;
    }

    public static List<Integer> getSourceVector4Java(String source,String suffix) throws IOException, CoreException {
        File tmpFile = File.createTempFile("tmp", suffix, workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write(source);
        bw.close();
        JavaSourceAnalyzer jsa = new JavaSourceAnalyzer("", "", "");
        jsa.setFilePath(tmpFile.getAbsolutePath());
        List<Integer> res=jsa.analyzeFile();
        tmpFile.delete();
        return res;
    }

    /**
     * mapで表された状態ベクトルをListに変換する
     *
     * @param vector
     * @return
     */
    public static List<Integer> convertVector2List(Map<String, Integer> vector) {
        NodeClasses nc = new NodeClasses();
        List<Integer> list = new ArrayList<>(Collections.nCopies(nc.dictionary.size(), 0));
        for (Map.Entry<String, Integer> e : vector.entrySet()) {
            Integer i = nc.dictionary.get(e.getKey());
            i = i == null ? nc.dictionary.size() : i;
            list.set(i, e.getValue());
        }
        return list;
    }

    public static File singleGenealogy2Arff(List<Map<String, Integer>> genealogy) throws IOException {
        NodeClasses nc = new NodeClasses();
        File tmpFile = File.createTempFile("genealogy", ".arff", workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }

        //bw.write("@attribute UNKNOWN numeric");
        //bw.newLine();
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        if (genealogy.size() < 2) {
            bw.close();
            return tmpFile;
        }
        List<Integer> pre;
        List<Integer> now = convertVector2List(genealogy.get(0));
        for (int i = 1; i < genealogy.size(); i++) {//系譜の長さ分ループ
            pre = now;
            now = convertVector2List(genealogy.get(i));
            writeVector(bw, pre);
            bw.write(",");
            writeVector(bw, now);
            bw.newLine();
        }

        bw.close();
        return tmpFile;
    }

    public static void writeVector(BufferedWriter bw, List<Integer> vector) throws IOException {
        for (int i = 0; i < vector.size(); i++) {
            bw.write(vector.get(i).toString());
            if (i != vector.size() - 1)
                bw.write(",");
        }
    }

    //Logging処理が埋め込まれてて読みにくい
    public static void predict(File arffData, String resultPath,boolean updown) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(arffData));
        Instances instances = new Instances(br);
        int num = instances.numAttributes() / 2;
        int[] changedNum = Util.changedDataCounter(instances);
        br.close();

        String outputPath=updown ?
                "results/res-" + resultPath + "-updown.csv" : "results/res-" + resultPath + "-normal.csv";

        File f = new File(outputPath);
        BufferedWriter resBw = new BufferedWriter(new FileWriter(f));
        resBw.write("prediction result summary\n");
        resBw.write("node name,num_classified,num_correct,num_incorrect,precision,num_changed,Attribute used in classifier\n");

        for (int i = 0; i < num; i++) {
            br = new BufferedReader(new FileReader(arffData));
            instances = new Instances(br);
            Attribute attr = instances.attribute(i);
            String attrName = attr.name();
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances = Util.useFilter(instances);

            LinearRegression lr = new LinearRegression();
            String[] options = {"-S", "0"};
            lr.setOptions(options);
            lr.buildClassifier(instances);
            String debug = lr.toString().replace("\n", "").replace("Linear Regression Model", "");

            CustomizedCrossValidation ccv = new CustomizedCrossValidation();
            ccv.evaluate(lr, instances, 10, new Random(1),updown);
            resBw.write(attrName);
            resBw.write(",");
            resBw.write(Integer.toString(ccv.num_classified));
            resBw.write(",");
            resBw.write(Integer.toString(ccv.num_correct));
            resBw.write(",");
            resBw.write(Integer.toString(ccv.num_incorrect));
            resBw.write(",");
            resBw.write(String.valueOf((double) ccv.num_correct / (double) ccv.num_classified));
            resBw.write(",");
            resBw.write(Integer.toString(changedNum[i]));
            resBw.write(",");
            resBw.write(debug);


            resBw.newLine();
            System.out.println("end");
            br.close();
        }
        resBw.close();
    }

    public static void predictWithSomeClassifiers(File arffData, String resultPath, List<Classifier> classifiers,boolean updown) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(arffData));
        Instances instances = new Instances(br);
        int num = instances.numAttributes() / 2;
        int[] changedNum = Util.changedDataCounter(instances);
        br.close();


        for (Classifier cls : classifiers) {
            System.out.println(cls.getClass().getSimpleName());
            File f = new File("results/res-" + cls.getClass().getSimpleName() + "-" + resultPath + ".csv");
            BufferedWriter resBw = new BufferedWriter(new FileWriter(f));
            resBw.write("prediction result summary\n");
            resBw.write("node name,num_classified,num_correct,num_incorrect,precision,num_changed,Attribute used in classifier\n");

            for (int i = 0; i < num; i++) {
                br = new BufferedReader(new FileReader(arffData));
                instances = new Instances(br);
                Attribute attr = instances.attribute(i);
                String attrName = attr.name();
                instances = Util.removeAttrWithoutI(i, instances);
                instances.setClassIndex(num);
                //if(!cls.getClass().getSimpleName().equals("SMOreg"))
                instances = Util.useFilter(instances);

                CustomizedCrossValidation ccv = new CustomizedCrossValidation();

                if(cls.getClass().getSimpleName().contains("SVM") && !Util.isPredictable(instances)){
                    ccv.num_classified=instances.numInstances();
                    ccv.num_correct=ccv.num_classified;
                    ccv.num_incorrect=ccv.num_classified-ccv.num_correct;
                }else {
                    cls.buildClassifier(instances);
                    ccv.evaluate(cls, instances, 10, new Random(1),updown);
                }


                resBw.write(attrName);
                resBw.write(",");
                resBw.write(Integer.toString(ccv.num_classified));
                resBw.write(",");
                resBw.write(Integer.toString(ccv.num_correct));
                resBw.write(",");
                resBw.write(Integer.toString(ccv.num_incorrect));
                resBw.write(",");
                resBw.write(String.valueOf((double) ccv.num_correct / (double) ccv.num_classified));
                resBw.write(",");
                resBw.write(Integer.toString(changedNum[i]));


                resBw.newLine();
                System.out.println("end");
                br.close();
            }
            resBw.close();
        }

    }


    public static void vectoredPrediction(File arffData, String resultPath,boolean updown) throws Exception {
        String outputPath=updown ?
                "results/res-" + resultPath + "-updown-vectored.csv" : "results/res-" + resultPath + "-normal-vectored.csv";

        File f = new File(outputPath);
        BufferedWriter resBw = new BufferedWriter(new FileWriter(f));
        resBw.write("prediction result summary\n");

        LinearRegression lr = new LinearRegression();
        String[] options = {"-S", "0"};
        lr.setOptions(options);

        CustomizedCrossValidation ccv = new CustomizedCrossValidation();
        ccv.randomized = false;
        ccv.vectoredPrediction(lr, arffData, 10, new Random(1),updown);


        resBw.write(Integer.toString(ccv.num_classified));
        resBw.write(",");
        resBw.write(Integer.toString(ccv.num_correct));
        resBw.write(",");
        resBw.write(Integer.toString(ccv.num_incorrect));
        resBw.write(",");
        resBw.write(String.valueOf((double) ccv.num_correct / (double) ccv.num_classified));
        resBw.write("\n\n");
        if (!ccv.randomized) {
            resBw.write("num,classified,correct,incorrect");
            resBw.newLine();
            for (int i = 0; i < ccv.num_classifiedArray.length; i++) {
                resBw.write(Integer.toString(i) + "," +
                        Integer.toString(ccv.num_classifiedArray[i]) + "," +
                        Integer.toString(ccv.num_correctArray[i]) + "," +
                        Integer.toString(ccv.num_classifiedArray[i] - ccv.num_correctArray[i]));
                resBw.newLine();
            }
        }
        resBw.close();
    }

    //TODO updownに対応して出力ファイル名をかえる
    public static void vectoredPredictionWithSomeClassifiers(File arffData, String resultPath, List<Classifier> classifiers,boolean updown) throws Exception {
        for (Classifier cls : classifiers) {

            File f = new File("results/res-" + cls.getClass().getSimpleName() + "-" + resultPath + "-vectored.csv");
            BufferedWriter resBw = new BufferedWriter(new FileWriter(f));
            resBw.write("prediction result summary\n");

            CustomizedCrossValidation ccv = new CustomizedCrossValidation();
            ccv.randomized = false;
            ccv.vectoredPrediction(cls, arffData, 10, new Random(1),updown);

            resBw.write(Integer.toString(ccv.num_classified));
            resBw.write(",");
            resBw.write(Integer.toString(ccv.num_correct));
            resBw.write(",");
            resBw.write(Integer.toString(ccv.num_incorrect));
            resBw.write(",");
            resBw.write(String.valueOf((double) ccv.num_correct / (double) ccv.num_classified));
            resBw.write("\n\n");
            if (!ccv.randomized) {
                resBw.write("num,classified,correct,incorrect");
                resBw.newLine();
                for (int i = 0; i < ccv.num_classifiedArray.length; i++) {
                    resBw.write(Integer.toString(i) + "," +
                            Integer.toString(ccv.num_classifiedArray[i]) + "," +
                            Integer.toString(ccv.num_correctArray[i]) + "," +
                            Integer.toString(ccv.num_classifiedArray[i] - ccv.num_correctArray[i]));
                    resBw.newLine();
                }
            }
            resBw.close();
        }
    }

    /**
     * 変化した要素が1以上のデータ
     *
     * @param data
     * @return
     */
    public static int[] changedDataCounter(Instances data) {
        int num = data.numAttributes() / 2;
        int[] res = new int[num];
        Arrays.fill(res, 0);
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < data.numInstances(); j++) {
                if (Math.round(data.instance(j).value(i)) != Math.round(data.instance(j).value(i + num))) {
                    res[i]++;
                }
            }
        }
        return res;
    }

    public static int[] getDistanceOfChanges(Instances data) {
        int num = data.numAttributes() / 2;
        int res[] = new int[data.numInstances()];
        for (int i = 0; i < data.numInstances(); i++) {
            int dist = 0;
            for (int j = 0; j < num; j++) {
                dist += Math.abs(Math.round(data.instance(i).value(j)) - Math.round(data.instance(i).value(j + num)));
            }
            res[i] = dist;

        }
        return res;
    }

    /**
     * ２つ目の状態ベクトルの
     *
     * @param i
     * @param instances
     * @return
     */
    public static Instances removeAttrWithoutI(int i, Instances instances) throws Exception {
        int num = instances.numAttributes() / 2;
        int counter = 0;

        Remove remove = new Remove();
        remove.setAttributeIndices("1-" + Integer.toString(num) + "," + Integer.toString(num + i + 1));
        remove.setInvertSelection(true);
        remove.setInputFormat(instances);

        return Filter.useFilter(instances, remove);
    }

    public static File allGenealogy2Arff(Map<String, List<Map<String, Integer>>> genealogy) throws IOException {
        NodeClasses nc = new NodeClasses();
        File tmpFile = File.createTempFile("genealogy", ".arff", workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        for (Map.Entry<String, List<Map<String, Integer>>> e : genealogy.entrySet()) {
            if (e.getValue().size() < 2)
                continue;
            List<Integer> pre;
            List<Integer> now = convertVector2List(e.getValue().get(0));
            for (int i = 1; i < e.getValue().size(); i++) {//系譜の長さ分ループ
                pre = now;
                now = convertVector2List(e.getValue().get(i));
                if (diffIsBig(pre, now, SMALLTHRESHOLD))
                    continue;
                smallchange++;
                writeVector(bw, pre);
                bw.write(",");
                writeVector(bw, now);
                bw.newLine();
            }
        }

        bw.close();
        return tmpFile;
    }

    public static File allGenealogy2Arff(List<Map<String,Integer>> preVector,List<Map<String,Integer>> postVector)throws Exception{
        NodeClasses nc = new NodeClasses();
        File tmpFile = File.createTempFile("genealogy", ".arff", workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        for (int i=0;i<preVector.size();i++) {
            List<Integer> pre=convertVector2List(preVector.get(i));
            List<Integer> post=convertVector2List(preVector.get(i));

            if(diffIsBig(pre,post,SMALLTHRESHOLD))
                continue;

            smallchange++;

            writeVector(bw,pre);
            bw.write(",");
            writeVector(bw,post);
            bw.newLine();
        }

        bw.close();
        return tmpFile;
    }

    public static File allGenealogy2Arff4Java(List<List<Integer>> preVector,List<List<Integer>> postVector)throws Exception{
        NodeClasses4Java nc = new NodeClasses4Java();
        File tmpFile = File.createTempFile("genealogy", ".arff", workingDir);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write("@relation StateVector");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + " numeric");
            bw.newLine();
        }
        for (Map.Entry<String, Integer> e : nc.dictionary.entrySet()) {
            bw.write("@attribute " + e.getKey() + "2 numeric");
            bw.newLine();
        }
        bw.newLine();

        bw.write("@data");
        bw.newLine();
        for (int i=0;i<preVector.size();i++) {
            if(diffIsBig(preVector.get(i),postVector.get(i),SMALLTHRESHOLD))
                continue;

            smallchange++;

            writeVector(bw,preVector.get(i));
            bw.write(",");
            writeVector(bw,postVector.get(i));
            bw.newLine();
        }

        bw.close();
        return tmpFile;
    }

    public static Instances useFilter(Instances data) throws Exception {

        Instances newData = Filter.useFilter(data, Util.getAttrSelectFilter(data));
        newData.setClassIndex(newData.numAttributes() - 1);
        //if (newData.classIndex() == -1)
        //    newData.setClassIndex(predictNum);
        return newData;
    }

    public static Filter getAttrSelectFilter(Instances data) throws Exception {
        AttributeSelection filter = new AttributeSelection();
        CfsSubsetEval eval = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        String[] options = {"-D", "1", "-N", "5"};
        search.setOptions(options);
        filter.setEvaluator(eval);
        filter.setInputFormat(data);
        return filter;
    }


    public static void enumNotFoundNodes(Set<String> set) {
        System.out.println("\nNotFoundNodes:");
        NodeClasses nc = new NodeClasses();
        for (String s : set) {
            if (!nc.dictionary.containsKey(s)) {
                System.out.println(s);
            }
        }
    }

    public static boolean diffIsBig(List<Integer> a, List<Integer> b, int threshold) {
        int counter = 0;
        for (int i = 0; i < a.size(); i++) {
            counter += Math.abs(a.get(i) - b.get(i));
        }
        return counter > threshold;
    }

    public static int compareDiffEntries(DiffEntry a, DiffEntry b) {
        return convertTypeToInteger(a.getChangeType()) - convertTypeToInteger(b.getChangeType());
    }

    public static int convertTypeToInteger(DiffEntry.ChangeType ct) {
        if (ct == DiffEntry.ChangeType.DELETE) {
            return 0;
        } else if (ct == DiffEntry.ChangeType.RENAME) {
            return 1;
        } else if (ct == DiffEntry.ChangeType.MODIFY) {
            return 2;
        } else if (ct == DiffEntry.ChangeType.ADD) {
            return 3;
        } else {//copy
            return 4;
        }
    }

    public static int getInstanceNum(int numFolds, int numFold, int index, int numInstances) {
        int numInstForFold, first, offset;

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > numInstances) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }
        numInstForFold = numInstances / numFolds;
        if (numFold < numInstances % numFolds) {
            numInstForFold++;
            offset = numFold;
        } else {
            offset = numInstances % numFolds;
        }

        first = numFold * (numInstances / numFolds) + offset;
        //copyInstances(first, test, numInstForFold); //シャッフルされてなければ，testの範囲はfirst-numInstForFold
        return first + index;
    }

    public static boolean isPredictable(Instances data){
        boolean flag=false;
        double tmp=data.instance(0).classValue();
        for(int i=1;i<data.numInstances();i++){
            if(tmp!=data.instance(i).classValue()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean isBugfix(String commitMessage){
        return commitMessage.toLowerCase().contains("bug") || commitMessage.toLowerCase().contains("fix");
    }

    public static boolean judgeResult(double predict,double actual, boolean updown){
        List<Long> list = new ArrayList<>();
        list.add(Math.round(Math.ceil(predict)));//切り捨て，切り上げしてLongへ
        list.add(Math.round(Math.floor(predict)));
        return updown ?
                list.contains(Math.round(actual)) :
                Objects.equals(Math.round(predict), Math.round(actual));
    }


}
