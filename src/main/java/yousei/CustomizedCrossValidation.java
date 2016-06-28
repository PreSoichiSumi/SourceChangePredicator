package yousei;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.*;
import java.util.*;

/**
 * Created by s-sumi on 2016/06/17.
 */
public class CustomizedCrossValidation {

    int num_classified = 0;
    int num_correct = 0;
    int num_incorrect = 0;
    int num_classifiedArray[]=new int[6];
    int num_correctArray[]=new int[6];
    boolean randomized=true;
    int dist[];


    public CustomizedCrossValidation() {
        super();
        Arrays.fill(num_classifiedArray,0);
        Arrays.fill(num_correctArray,0);
    }

    /**
     * 予測器を交差検証する
     *
     * @param classifier 予測器
     * @param filteredData       学習データと検証データ
     * @param numFolds   データを何分割するか
     * @param random     シード new Random(int seed)を与える.nullならシャッフルしない
     * @throws Exception
     */
    public String evaluate(Classifier classifier, Instances filteredData, int numFolds, Random random) throws Exception {
        // Do the folds
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numFolds; i++) {
            Instances train;
            if(randomized) {
                train = filteredData.trainCV(numFolds, i, random);
            }else{
                train=filteredData.trainCV(numFolds,i);
            }
            Classifier copiedClassifier = AbstractClassifier.makeCopy(classifier);
            if(copiedClassifier.getClass().getSimpleName().contains("SVM") && !Util.isPredictable(train)){
                num_classified=train.numInstances();
                num_correct=num_classified;
                num_incorrect=num_classified-num_correct;
            }else {
                copiedClassifier.buildClassifier(train);
                Instances test = filteredData.testCV(numFolds, i);
                test.setClassIndex(filteredData.classIndex());
                evaluateModel(copiedClassifier, test, sb);
            }

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
    //与えられるclassifierは既にビルドされているものとする
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
        int numAllInstance=instances.numInstances();
        br.close();

        this.dist=Util.getDistanceOfChanges(instances);

        List<Instances> filteredData=new ArrayList<>();
        for(int i=0;i<num;i++){
            br = new BufferedReader(new FileReader(data));
            instances = new Instances(br);
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances=Util.useFilter(instances);
            filteredData.add(instances);
        }


        for (int i = 0; i < numFolds; i++) { //交差検証
            List<Classifier> classifiers=new ArrayList<>();
            List<Instances> testDatas=new ArrayList<>();

            int numTestDataInstances=0;

            for(int j=0;j<num;j++){         //各ノードに対する予測器を構築
                Instances train;
                if(randomized){
                    train=filteredData.get(j).trainCV(numFolds, i, random);
                }else{
                    train=filteredData.get(j).trainCV(numFolds,i);
                }

                Classifier copied=AbstractClassifier.makeCopy(classifier);
                if(copied.getClass().getSimpleName().contains("SVM") && !Util.isPredictable(train)){
                    classifiers.add(null);
                    testDatas.add(null);
                }else {
                    copied.buildClassifier(train);
                    Instances test = filteredData.get(j).testCV(numFolds, i);
                    test.setClassIndex(train.classIndex());
                    classifiers.add(copied);
                    testDatas.add(test);
                }

                if(numTestDataInstances==0)
                    numTestDataInstances=filteredData.get(0).testCV(numFolds,i).numInstances();

            }
            vectoredEvaluateModel(classifiers,testDatas,numTestDataInstances,num,numFolds,i,numAllInstance);//精度確認．正解数などを記録
        }
    }

    public void vectoredEvaluateModel(List<Classifier> classifiers,List<Instances> testDatas,int numInstances,int numAttribute
                                    ,int numFolds,int numFold,int numAllInstance)throws Exception{

        for(int i=0;i<numInstances;i++){
            long res;
            boolean correct=true;
            for(int j=0;j<numAttribute;j++){
                if(classifiers.get(j)==null)//svmかつclassvalueの変化が無かったときにnullになる．そのときは必ず正解するので飛ばす
                    continue;
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
            if(!randomized){
                num_classifiedArray[dist[Util.getInstanceNum(numFolds,numFold,i, numAllInstance)]]++;
                if(correct){
                    num_correctArray[dist[Util.getInstanceNum(numFolds,numFold,i, numAllInstance)]]++;
                }
            }
        }
    }
}
