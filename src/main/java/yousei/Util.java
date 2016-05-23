package yousei;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

import java.io.*;
import java.util.*;

/**
 * Created by s-sumi on 2016/05/23.
 */
public class Util {
    public static File workingDir=new File("WorkingDir");
    public static Map<String, Integer> addMap(Map<String,Integer> map1, Map<String, Integer> map2){
        Map<String, Integer> res= new HashMap<>(map1);
        /*for(Map.Entry<String,Integer> e:map2.entrySet()){
            res.merge(e.getKey(), e.getValue(),
                    (a,b)-> a + b );
        }*/
        map2.entrySet().stream()
                .forEach(e -> res.merge(e.getKey(), e.getValue(), (a,b) -> a+b));
        return res;
    }
    public static Map<String, List<Map<String,Integer>>> initGenealogy(RevCommit firstCommit, Repository repo)
                                                                                    throws IOException,GitAPIException,CoreException{
        RevTree revTree=firstCommit.getTree();
        TreeWalk treeWalk=new TreeWalk(repo);
        treeWalk.addTree(revTree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathSuffixFilter.create(".cpp"));

        Map<String,List<Map<String,Integer>>> genealogy=new HashMap<>();

        while(treeWalk.next()){
            ObjectId objectId=treeWalk.getObjectId(0);
            ObjectLoader objectLoader=repo.open(objectId);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            objectLoader.copyTo(baos);

            List<Map<String,Integer>> fileGenealogy=new ArrayList<>();
            fileGenealogy.add(getSourceVector(baos.toString()));

            genealogy.put(treeWalk.getPathString(),fileGenealogy);  //mergeでもどっちでもいい
        }
        return genealogy;
    }

    public static Map<String, Integer> genealogyUtil(RevCommit newRev, Map<String, Integer> genealogy, Repository repo )
                                                                                throws IOException,GitAPIException, CoreException{
        File workingDir=new File("WorkingDir");
        CppSourceAnalyzer csa=new CppSourceAnalyzer("","","");
        String oldsourceString;
        String newsourceString;

        RevCommit oldRev=newRev.getParent(0);

        AbstractTreeIterator oldTreeIterator = ChangeAnalyzer.prepareTreeParser(repo,
                oldRev.getId().getName());
        AbstractTreeIterator newTreeIterator = ChangeAnalyzer.prepareTreeParser(repo,
                newRev.getId().getName());
        List<DiffEntry> diff = new Git(repo).diff().setOldTree(oldTreeIterator)
                .setNewTree(newTreeIterator)
                .setPathFilter(PathSuffixFilter.create(".cpp"))
                .call();

        //削除系を先に．リネーム，追加を後に処理
        diff.sort(Util::compareDiffEntries);

        //誠に遺憾ながらcdtはStringを元にASTを構築してくれないので，
        //一旦StringからFileを作成して解析する．終わったら削除
        for (DiffEntry entry : diff) {
            ObjectLoader olold;
            ObjectLoader olnew;

            ByteArrayOutputStream bosold = new ByteArrayOutputStream();
            ByteArrayOutputStream bosnew = new ByteArrayOutputStream();

            if (entry.getChangeType() == DiffEntry.ChangeType.MODIFY) { // ソースの変更の場合のみ

                if (!(entry.getOldId().toObjectId().equals(ObjectId.zeroId()))) { // OLDが存在するか
                    olold = repo.open(entry.getOldId().toObjectId()); // ソースを読み込んで，コメントなどを消去
                    olold.copyTo(bosold);
                    oldsourceString = bosold.toString();

                } else {
                    oldsourceString = "";
                }

                if (!entry.getNewId().toObjectId().equals(ObjectId.zeroId())) { // NEWが存在するか
                    olnew = repo.open(entry.getNewId().toObjectId()); // ソースを読み込んで，コメントなどを消去
                    olnew.copyTo(bosnew);
                    newsourceString = bosnew.toString();
                } else {
                    newsourceString = "";
                }

                if (Objects.equals(oldsourceString, "") || Objects.equals(newsourceString, ""))
                    continue;

                File tmpFileOld = File.createTempFile("old", ".cpp", workingDir);
                BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileOld));
                bw.write(oldsourceString);
                bw.close();

                csa.setFilePath(tmpFileOld.getAbsolutePath());
                Map<String, Integer> resOld = csa.analyzeFile();

                File tmpFileNew = File.createTempFile("new", ".cpp", workingDir);
                bw = new BufferedWriter(new FileWriter(tmpFileNew));
                bw.write(newsourceString);
                bw.close();

                csa.setFilePath(tmpFileNew.getAbsolutePath());
                Map<String, Integer> resNew = csa.analyzeFile();

            }

        }
        return null;
    }
    public static int convertTypeToInteger(DiffEntry.ChangeType ct){
        if(ct== DiffEntry.ChangeType.DELETE){
            return 0;
        }else if(ct== DiffEntry.ChangeType.RENAME){
            return 1;
        }else if(ct== DiffEntry.ChangeType.MODIFY){
            return 2;
        }else if(ct== DiffEntry.ChangeType.RENAME){
            return 3;
        }else if(ct== DiffEntry.ChangeType.ADD){
            return 4;
        }else{  //copy
            return 5;
        }
    }

    public static int compareDiffEntries(DiffEntry a, DiffEntry b){
        return convertTypeToInteger(a.getChangeType()) - convertTypeToInteger(b.getChangeType());
    }

    public static Map<String, Integer> getSourceVector(String source)throws IOException,CoreException{
        File tmpFile=File.createTempFile("tmp",".cpp",workingDir);
        BufferedWriter bw=new BufferedWriter(new FileWriter(tmpFile));
        bw.write(source);
        bw.close();
        CppSourceAnalyzer csa=new CppSourceAnalyzer("","","");
        csa.setFilePath(tmpFile.getAbsolutePath());
        return csa.analyzeFile();
    }



}
