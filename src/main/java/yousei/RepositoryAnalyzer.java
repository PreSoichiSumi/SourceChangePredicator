package yousei;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by s-sumi on 2016/05/09.
 */
public class RepositoryAnalyzer {
    public ChangeAnalyzer ca;
    public Repository repository;
    public RepositoryAnalyzer(String reposPath) throws IOException{
        FileRepositoryBuilder builder=new FileRepositoryBuilder();
        this.repository=builder
                .setGitDir( new File(reposPath+"/"+ Constants.DOT_GIT))
                .readEnvironment()
                .findGitDir()
                .build();
        this.ca=new ChangeAnalyzer();
        this.ca.setRepo(this.repository);
    }

    public void analyzeRepository() throws Exception{

        RevWalk rw=getInitializedRevWalk(repository,RevSort.REVERSE);//最古
        RevCommit commit=rw.next();
        while(commit!=null){
            if(commit.getParentCount()>=1) {
                ca.setCommit(commit);
                ca.analyzeChange();
            }
            commit=rw.next();
        }
    }

    // Reverseで最古から最新へ
    private RevWalk getInitializedRevWalk(Repository repo,
                                          RevSort revSort) throws AmbiguousObjectException,
            IncorrectObjectTypeException, IOException, MissingObjectException {
        RevWalk rw = new RevWalk(repo);
        AnyObjectId headId;

        headId = repo.resolve(Constants.HEAD);
        RevCommit root = rw.parseCommit(headId);
        rw.sort(revSort);
        rw.markStart(root); // この時点ではHeadをさしている．nextで最初のコミットが得られる．
        return rw;
    }
}
