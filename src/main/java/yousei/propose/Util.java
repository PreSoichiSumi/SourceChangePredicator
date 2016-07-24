package yousei.propose;

import yousei.GeneralUtil;
import yousei.util.NodeClasses4Java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import static yousei.GeneralUtil.diffIsBig;
import static yousei.GeneralUtil.writeVector;

/**
 * Created by s-sumi on 16/07/23.
 * 提案手法用のUtil集
 */
public class Util {
    public static File genealogy2Arff4VectorPrediction(List<List<Integer>> preVector, List<List<Integer>> postVector) throws Exception {
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

            GeneralUtil.smallchange++;

            writeVector(bw, preVector.get(i));
            bw.write(",");
            writeVector(bw, postVector.get(i));
            bw.newLine();
        }

        bw.close();
        return tmpFile;
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
