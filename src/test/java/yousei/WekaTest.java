package yousei;

import org.junit.Test;

import static org.junit.Assert.*;

import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by s-sumi on 2016/06/18.
 */
public class WekaTest {
    private final String testDataPath = "testData/test-genealogy.arff";
    private final File testFile = new File(testDataPath);

    @Test
    public void removeAttributeTest() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(testFile));
        int lastIndex = new Instances(br).numAttributes() / 2 - 1;
        assertAttributeName(0);
        assertAttributeName(lastIndex / 2);
        assertAttributeName(lastIndex);
    }

    //自作メソッドでassertしても有効なのは検証済み
    public void assertAttributeName(int i) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        Instances instances = new Instances(br);
        String attrName = instances.attribute(i).name();
        int num = instances.numAttributes() / 2;
        instances = Util.removeAttrWithoutI(i, instances);
        assertEquals(instances.attribute(num).name(), attrName + "2");
    }

    @Test
    public void attributeFilterTest() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(testDataPath)));
        Instances rawInstances = new Instances(br);
        int numnum=rawInstances.numAttributes()/2;
        br.close();
        for (int i = 0; i < numnum; i++) {
            br = new BufferedReader(new FileReader(new File(testDataPath)));
            Instances instances = new Instances(br);
            int num = instances.numAttributes() / 2;
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances = Util.useFilter(instances);
            assertEquals(rawInstances.attribute(i).name() + "2", instances.attribute(instances.numAttributes() - 1).name());
            assertEquals(instances.numAttributes(), num + 1);//fail
            assertEquals(instances.classAttribute().name(),rawInstances.attribute(i).name()+"2");
            br.close();
        }
    }

    //prediction label test
    @Test
    public void predictionLabelTest() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        Instances instances = new Instances(br);
        int num = instances.numAttributes() / 2;
        br.close();

        for (int i = 0; i < num; i++) {
            br = new BufferedReader(new FileReader(testFile));
            instances = new Instances(br);
            Attribute attr = instances.attribute(i);
            String attrName = attr.name();
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances = Util.useFilter(instances);

            LinearRegression lr = new LinearRegression();
            String[] options = {};
            lr.setOptions(options);
            lr.buildClassifier(instances);

            br.close();
        }
    }

    //filtered data test
    @Test
    public void predictionFromFilter() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(testDataPath)));
        Instances test = new Instances(br);
        br.close();

        for (int i = 0; i < test.numAttributes() / 2; i++) {
            br = new BufferedReader(new FileReader(testFile));
            Instances instances = new Instances(br);
            int num = instances.numAttributes() / 2;
            instances = Util.removeAttrWithoutI(i, instances);
            instances.setClassIndex(num);
            instances = Util.useFilter(instances);
            instances.setClassIndex(instances.numAttributes() - 1);

            assertNotEquals(Long.valueOf(num), Long.valueOf(instances.numAttributes()));

            LinearRegression lr = new LinearRegression();
            String[] options = {};
            lr.setOptions(options);
            lr.buildClassifier(instances);
            assertTrue(lr.toString().contains(instances.classAttribute().name()));
            if (lr.numParameters() != 0 && !Objects.equals(instances.numAttributes() - 1, lr.numParameters()))
                assertEquals(instances.numAttributes() - 1, lr.numParameters());
            br.close();
        }
    }
}
