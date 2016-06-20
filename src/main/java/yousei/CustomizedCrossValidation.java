package yousei;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;

import java.io.*;
import java.util.*;

/**
 * Created by s-sumi on 2016/06/17.
 */
public class CustomizedCrossValidation {

    int num_classified = 0;
    int num_correct = 0;
    int num_incorrect = 0;

    /**
     * 予測器を交差検証する
     *
     * @param classifier 予測器
     * @param filteredData       学習データと検証データ
     * @param numFolds   データを何分割するか
     * @param random     シード new Random(int seed)を与える
     * @throws Exception
     */
    public String evaluate(Classifier classifier, Instances filteredData, int numFolds, Random random) throws Exception {
        // Do the folds
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numFolds; i++) {
            Instances train = filteredData.trainCV(numFolds, i, random);
            Classifier copiedClassifier = Classifier.makeCopy(classifier);
            copiedClassifier.buildClassifier(train);
            Instances test = filteredData.testCV(numFolds, i);
            test.setClassIndex(filteredData.classIndex());
            evaluateModel(copiedClassifier, test, sb);
        }
        sb.append("classified: ");
        sb.append(num_classified);
        sb.append("\n");

        sb.append("correct: ");
        sb.append(num_correct);
        sb.append("\n");

        sb.append("incorrect: ");
        sb.append(num_incorrect);
        sb.append("\n");

        sb.append("precision: ");
        sb.append((double) num_correct / (double) num_classified);
        sb.append("\n");


        return sb.toString();
    }

    public void evaluateModel(Classifier classifier, Instances test, StringBuilder sb) throws Exception {
        if (test.classIndex() < 0)
            throw new Exception("please set ClassIndex to test data");

        for (int i = 0; i < test.numInstances(); i++) {

            double res = classifier.classifyInstance(test.instance(i));
            if (Objects.equals(Math.round(res),Math.round(test.instance(i).value(test.classIndex())))) {
                num_correct++;
            } else {
                num_incorrect++;
            }
            num_classified++;
        }
    }
    //まずfiltereddataを作ってその後fold
    public void vectoredPrediction(Classifier classifier, File data, int numFolds, Random random) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(data));
        Instances instances = new Instances(br);
        int num = instances.numAttributes() / 2;
        br.close();

        List<Instances> filteredData=new ArrayList<>();
        for(int i=0;i<num;i++){
            br = new BufferedReader(new FileReader(data));
            instances = new Instances(br);
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances=Util.useFilter(instances);
            filteredData.add(instances);
        }


        for (int i = 0; i < numFolds; i++) {
            List<Classifier> classifiers=new ArrayList<>();
            List<Instances> testDatas=new ArrayList<>();
            for(int j=0;j<num;j++){
                Instances train = filteredData.get(j).trainCV(numFolds, i, random);

                Classifier copied=Classifier.makeCopy(classifier);
                copied.buildClassifier(train);

                Instances test =filteredData.get(j).testCV(numFolds,i);
                test.setClassIndex(train.classIndex());

                classifiers.add(copied);
                testDatas.add(test);
            }
            vectoredEvaluateModel(classifiers,testDatas,testDatas.get(0).numInstances(),num);
        }
    }

    public void vectoredEvaluateModel(List<Classifier> classifiers,List<Instances> testDatas,int numInstances,int numAttribute)throws Exception{
        if(testDatas.get(0).classIndex()<0)
            throw new Exception("please set classindex to testData");

        for(int i=0;i<numInstances;i++){
            long res;
            boolean correct=true;
            for(int j=0;j<numAttribute;j++){
                res=Math.round(classifiers.get(j).classifyInstance(testDatas.get(j).instance(i))); //intは超えない
                if(!Objects.equals(res,Math.round(testDatas.get(j).instance(i).value(testDatas.get(j).classIndex())))){
                    correct=false;
                }
            }
            if(correct){
                num_correct++;
            }else {
                num_incorrect++;
            }
            num_classified++;
        }
    }
}
