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
    private String bugSourceCode;
    private String resPath;
    private String bugRevisionId;
    /**
     * 短縮形でもフルでもよい
     */
    private Repository repository;


    public StatementRecommender(String reposPath,String bugSourceCode, String resPath, String bugRevisionId) throws Exception {

        if (reposPath == null || resPath == null || bugRevisionId == null)
            throw new Exception("null argument found");

        this.reposPath = reposPath;
        this.bugSourceCode=bugSourceCode;
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

        System.out.println("predict the size of next change");

        if()



    }

}
