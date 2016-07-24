package yousei.propose;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import yousei.GeneralUtil;
import yousei.experiment.ChangeAnalyzer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by s-sumi on 16/07/23.
 * 本当は
 */
public class StatementRecommender {
    private String reposPath;
    private String resPath;
    private String bugRevisionId;
    /**
     * 短縮形でもフルでもよい
     */
    private Repository repository;
    private List<List<Integer>> preVector = new ArrayList<>();
    private List<List<Integer>> postVector = new ArrayList<>();

    public StatementRecommender(String reposPath, String resPath, String bugRevisionId) throws Exception {

        if (reposPath == null || resPath == null || bugRevisionId == null)
            throw new Exception("null argument found");

        this.reposPath = reposPath;
        this.resPath = resPath;
        this.bugRevisionId = bugRevisionId;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        this.repository = builder
                .setGitDir(new File(reposPath + "/" + Constants.DOT_GIT))
                .readEnvironment()
                .findGitDir()
                .build();
        //change analyzerとcppsource analyzerがいるかも
    }


    public void execute() throws Exception {
        System.out.println("process " + reposPath);

        RevWalk rw = GeneralUtil.getInitializedRevWalk(repository, RevSort.REVERSE);
        RevCommit commit = rw.next();
        commit = rw.next();
        while (commit != null && commit.getName().startsWith(this.bugRevisionId)) {
            if (commit.getParentCount() >= 1)
                updateGenealogy(commit, ".java");
            commit = rw.next();
        }


    }

    public void updateGenealogy(RevCommit newRev, String suffix) throws Exception {
        RevCommit oldRev = newRev.getParent(0);

        AbstractTreeIterator oldTreeIterator = ChangeAnalyzer.prepareTreeParser(repository,
                oldRev.getId().getName());
        AbstractTreeIterator newTreeIterator = ChangeAnalyzer.prepareTreeParser(repository,
                newRev.getId().getName());
        List<DiffEntry> diff = new Git(repository).diff().setOldTree(oldTreeIterator)
                .setNewTree(newTreeIterator)
                .setPathFilter(PathSuffixFilter.create(suffix))
                .call();

        for (DiffEntry entry : diff) {
            if (entry.getChangeType() == DiffEntry.ChangeType.MODIFY) {
                ObjectLoader olold;
                ByteArrayOutputStream bosold = new ByteArrayOutputStream();
                String oldSource;

                if (!entry.getNewId().toObjectId().equals(ObjectId.zeroId())) { // OLDが存在するか
                    olold = repository.open(entry.getNewId().toObjectId());
                    olold.copyTo(bosold);
                    oldSource = bosold.toString();
                } else {
                    continue;
                }

                ObjectLoader olnew;
                ByteArrayOutputStream bosnew = new ByteArrayOutputStream();
                String newSource;
                if (!entry.getNewId().toObjectId().equals(ObjectId.zeroId())) { // NEWが存在するか
                    olnew = repository.open(entry.getNewId().toObjectId());
                    olnew.copyTo(bosnew);
                    newSource = bosnew.toString();
                } else {
                    continue;
                }

                if (Objects.equals(oldSource, "") || Objects.equals(newSource, "")) //ソースの修正なら解析対象とする
                    continue;

                preVector.add(GeneralUtil.getSourceVector4Java(oldSource, ".java"));
                postVector.add(GeneralUtil.getSourceVector4Java(newSource, ".java"));

            }
        }

    }
}
