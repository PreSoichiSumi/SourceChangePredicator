package yousei;

import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by s-sumi on 2016/06/18.
 */
public class WekaTest {
    private final String testData = "testData/test-genealogy.arff";

    @Test
    public void removeAttributeTest()throws IOException{
        BufferedReader br=new BufferedReader(new FileReader(new File(testData)));
        int lastIndex=new Instances(br).numAttributes()/2-1;
        assertAttributeName(0,br);
        assertAttributeName(lastIndex/2,br);
        assertAttributeName(lastIndex,br);
    }
    public void assertAttributeName(int i,BufferedReader br)throws IOException{
        Instances instances=new Instances(br);
        String attrName=instances.attribute(i).name();
        int num=instances.numAttributes()/2;
        instances=Util.removeAttrWithoutI(i,instances);
        assertEquals(instances.attribute(num).name(),attrName+"2");
    }

    public void hoge()throws IOException{
        BufferedReader br=new BufferedReader(new FileReader(new File(testData)));
        Instances instances=new Instances(br);


    }
}
