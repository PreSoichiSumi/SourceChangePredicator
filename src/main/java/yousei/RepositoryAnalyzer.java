package yousei;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

import java.io.*;
import java.util.*;

import static yousei.Util.getSourceVector;

/**
 * Created by s-sumi on 2016/05/09.
 */
public class RepositoryAnalyzer {
    private ChangeAnalyzer ca;
    private Repository repository;
    private Map<String, List<Map<String, Integer>>> genealogy=new HashMap<>();
    private List<List<Map<String, Integer>>> deletedGenealogies = new ArrayList<>();

    public RepositoryAnalyzer(String reposPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        this.repository = builder
                .setGitDir(new File(reposPath + "/" + Constants.DOT_GIT))
                .readEnvironment()
                .findGitDir()
                .build();
        this.ca = new ChangeAnalyzer();
        this.ca.setRepo(this.repository);
    }

    public void analyzeRepository() throws Exception {

        RevWalk rw = getInitializedRevWalk(repository, RevSort.REVERSE);//最古
        RevCommit commit = rw.next();
        initGenealogy(commit);
        commit = rw.next();
        while (commit != null) {
            if (commit.getParentCount() >= 1) {
                updateGenealogy(commit);
            }
            commit = rw.next();
        }
        addDeleted2Genealogy();
        File f = Util.allGenealogy2Arff(genealogy);
        Util.predict(f);
        //Util.vectoredPrediction(f);
        f.delete();
/*        Set<String> names=new HashSet<>();
        for(Map.Entry<String,List<Map<String,Integer>>> e:genealogy.entrySet()){
            for(Map<String,Integer> string:e.getValue()){
                names.addAll(string.keySet());
            }
            if(e.getValue().size()<2)
                continue;
            File f=Util.singleGenealogy2Arff(e.getValue());
            Util.predict(f);
            f.delete();
        }
        names.forEach(System.out::println);
        Util.enumNotFoundNodes(names);*/
    }

    // Reverseで最古から最新へ
    private RevWalk getInitializedRevWalk(Repository repo,
                                          RevSort revSort) throws IOException {
        RevWalk rw = new RevWalk(repo);
        AnyObjectId headId;

        headId = repo.resolve(Constants.HEAD);
        RevCommit root = rw.parseCommit(headId);
        rw.sort(revSort);
        rw.markStart(root); // この時点ではHeadをさしている．nextで最初のコミットが得られる．
        return rw;
    }


    public void initGenealogy(RevCommit firstCommit) throws Exception {
        initForGivenSuffix(firstCommit,".cpp");
        initForGivenSuffix(firstCommit,".c");
    }

    public void initForGivenSuffix(RevCommit firstCommit,String suffix)throws Exception{
        RevTree revTree = firstCommit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(revTree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathSuffixFilter.create(suffix));

        while (treeWalk.next()) {
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader objectLoader = repository.open(objectId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            objectLoader.copyTo(baos);

            List<Map<String, Integer>> fileGenealogy = new ArrayList<>();
            fileGenealogy.add(getSourceVector(baos.toString()));

            genealogy.put(treeWalk.getPathString(), fileGenealogy);  //mergeでもどっちでもいい
        }
    }


    //map返さなくてもいいかも
    /**
     * コミットを解析して，ファイルの系譜を作成する
     * @param newRev
     * @return
     * @throws Exception
     */
    public void updateGenealogy(RevCommit newRev) throws Exception {
        updateForGivenSuffix(newRev, ".cpp");
        updateForGivenSuffix(newRev, ".c");

    }

    public void updateForGivenSuffix(RevCommit newRev,String suffix) throws Exception{
        File workingDir = new File("WorkingDir");
        CppSourceAnalyzer csa = new CppSourceAnalyzer("", "", "");
        String source;

        RevCommit oldRev = newRev.getParent(0);

        //-----------------まずはCPPについて--------------------------------//

        AbstractTreeIterator oldTreeIterator = ChangeAnalyzer.prepareTreeParser(repository,
                oldRev.getId().getName());
        AbstractTreeIterator newTreeIterator = ChangeAnalyzer.prepareTreeParser(repository,
                newRev.getId().getName());
        List<DiffEntry> diff = new Git(repository).diff().setOldTree(oldTreeIterator)
                .setNewTree(newTreeIterator)
                .setPathFilter(PathSuffixFilter.create(suffix))
                .call();

        //削除系を先に．リネーム，追加を後に処理
        diff.sort(Util::compareDiffEntries);

        //誠に遺憾ながらcdtはStringを元にASTを構築してくれないので，
        //一旦StringからFileを作成して解析する．終わったら削除
        for (DiffEntry entry : diff) {
            if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {         //deleteまでの系譜を保存しておきたい
                //if(genealogy.remove(entry.getOldPath())==null)
                //    throw new Exception();//add忘れがあるということ
                List<Map<String,Integer>> tmp=genealogy.get(entry.getOldPath());
                if(tmp!=null)
                    deletedGenealogies.add(tmp);
                genealogy.remove(entry.getOldPath());

            } else if (entry.getChangeType() == DiffEntry.ChangeType.RENAME) {
                List<Map<String, Integer>> tmp = genealogy.get(entry.getOldPath());
                genealogy.remove(entry.getOldPath());
                genealogy.put(entry.getNewPath(), tmp);
            } else if (entry.getChangeType() == DiffEntry.ChangeType.MODIFY) {
                ObjectLoader olnew;
                ByteArrayOutputStream bosnew = new ByteArrayOutputStream();

                if (!entry.getNewId().toObjectId().equals(ObjectId.zeroId())) { // NEWが存在するか
                    olnew = repository.open(entry.getNewId().toObjectId()); // ソースを読み込んで，コメントなどを消去
                    olnew.copyTo(bosnew);
                    source = bosnew.toString();
                } else {
                    continue;
                }

                if (Objects.equals(source, ""))
                    continue;

                File tmpFile = File.createTempFile("new", suffix, workingDir);
                BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
                bw.write(source);
                bw.close();


                csa.setFilePath(tmpFile.getAbsolutePath());
                Map<String, Integer> res = csa.analyzeFile();
                tmpFile.delete();

                if (Objects.equals(entry.getOldPath(), entry.getNewPath())) {
                    List<Map<String, Integer>> tmp = genealogy.get(entry.getOldPath());
                    tmp.add(res);
                    genealogy.put(entry.getOldPath(), tmp);
                } else {
                    List<Map<String, Integer>> tmp = genealogy.get(entry.getOldPath());
                    tmp.add(res);
                    genealogy.remove(entry.getOldPath());
                    genealogy.put(entry.getNewPath(), tmp);
                }

            } else if (entry.getChangeType() == DiffEntry.ChangeType.ADD) {
                ObjectLoader olnew;
                ByteArrayOutputStream bosnew = new ByteArrayOutputStream();

                if (!entry.getNewId().toObjectId().equals(ObjectId.zeroId())) { // NEWが存在するか
                    olnew = repository.open(entry.getNewId().toObjectId()); // ソースを読み込んで，コメントなどを消去
                    olnew.copyTo(bosnew);
                    source = bosnew.toString();
                } else {
                    continue;
                }

                if (Objects.equals(source, ""))
                    continue;

                File tmpFile = File.createTempFile("new", suffix, workingDir);
                BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
                bw.write(source);
                bw.close();


                csa.setFilePath(tmpFile.getAbsolutePath());
                Map<String, Integer> res = csa.analyzeFile();
                tmpFile.delete();

                List<Map<String, Integer>> tmp = new ArrayList<>();

                tmp.add(res);
                genealogy.put(entry.getNewPath(), tmp);
            } else {      //copy
                List<Map<String, Integer>> tmp = new ArrayList<>(genealogy.get(entry.getOldPath()));
                genealogy.put(entry.getNewPath(), tmp);
            }
        }

    }

    /**
     * ソースファイルの修正の過程で削除されてしまったファイルの系譜を全てgenealogyに追加する
     * 系譜は2以上でないと学習データとして使えないが
     */
    public void addDeleted2Genealogy()throws Exception{
        String prefix="DeletedSourceFileHOGEEE";
        int size=deletedGenealogies.size();
        for(int i=0;i<size;i++){
            List<Map<String, Integer>> tmp=deletedGenealogies.get(i);
            if(genealogy.put(prefix+Integer.toString(i),tmp)!=null)
                throw new Exception("something is already here");
        }
    }
}
