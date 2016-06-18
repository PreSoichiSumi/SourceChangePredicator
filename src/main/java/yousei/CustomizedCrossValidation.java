package yousei;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by s-sumi on 2016/06/17.
 */
public class CustomizedCrossValidation {

    private int runs;
    private int folds;

    int num_classified = 0;
    int num_correct = 0;
    int num_correct_neo=0;
    int num_incorrect = 0;

    private double sumPrecision = 0;
    private double sumRecall = 0;

    private File csvFile;
    private FileWriter csvFileWriter;
    private BufferedWriter bw;

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
/*
    public void vectoredPrediction(Classifier classifier, Instances data, int numFolds, Random random) throws Exception {
        int num=data.numAttributes()/2;
        for (int i = 0; i < numFolds; i++) {
            List<Classifier> classifiers=new ArrayList<>();
            List<Instances> testDatas=new ArrayList<>();
            for(int j=0;j<num;j++){
                Instances train = data.trainCV(numFolds, i, random);
                train=Util.removeAttrWithoutI(j,train);
                train.setClassIndex(num);
                train=Util.useFilter(train,j);

                LinearRegression lr=new LinearRegression();
                Classifier copied=Classifier.makeCopy(classifier);
                copied.buildClassifier(train);

                Instances test =data.testCV(numFolds,i);
                test.setClassIndex(train.classIndex());

            }
            Classifier copiedClassifier = Classifier.makeCopy(classifier);
            copiedClassifier.buildClassifier(train);
            Instances test = data.testCV(numFolds, i);
            test.setClassIndex(data.classIndex());
            evaluateModel(copiedClassifier, test, sb);
        }
    }*/


}
