package yousei;

import org.junit.Test;
import weka.core.Instances;
import yousei.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by s-sumi on 2016/05/23.
 */
public class UtilTest {
    @Test
    public void addMapTest(){
        Map<String,Integer> map1= new HashMap<>();
        map1.put("a",1);
        map1.put("c",5);
        map1.put("d",11);

        Map<String,Integer> map2= new HashMap<>();
        map2.put("a",1);
        map2.put("b",5);
        map2.put("c",11);

        Map<String,Integer> res= Util.addMap(map1,map2);
        assertEquals(res.get("a"),Integer.valueOf(2));
        assertEquals(res.get("b"),Integer.valueOf(5));
        assertEquals(res.get("c"),Integer.valueOf(16));
        assertEquals(res.get("d"),Integer.valueOf(11));
        assertEquals(res.get("e"),null);
    }
    @Test
    public void changedDataCounterTest()throws Exception{
        File f=new File("testdata/test-genealogy-handmade.arff");
        BufferedReader br=new BufferedReader(new FileReader(f));
        Instances instances = new Instances(br);
        int[] changedNum=Util.changedDataCounter(instances);
        assertEquals(changedNum.length,3);
        assertEquals(2, changedNum[0]);//actual,expected
        assertEquals(2, changedNum[1]);
        assertEquals(0, changedNum[2]);
    }
    //judgeResultはupdownがtrueなら切り上げ，切り下げした場合にpredictがactualと等しくなるか出力する
    @Test
    public void judgeResultTest(){
        assertFalse(Util.judgeResult(1.0,2.0,true));
        assertFalse(Util.judgeResult(3.0,2.0,true));
        assertTrue(Util.judgeResult(1.5,2.0,true));
        assertTrue(Util.judgeResult(2.5,2.0,true));

        assertTrue(Util.judgeResult(1.5,2.0,false));
        assertTrue(Util.judgeResult(2.4,2.0,false));
        assertFalse(Util.judgeResult(0.0,2.0,false));
        assertFalse(Util.judgeResult(1.4,2.0,false));
        assertFalse(Util.judgeResult(2.6,2.0,false));
    }

    //判定も間違っていないっぽい
    @Test
    public void stdlibTest(){
        assertTrue(1.1==1.1);
        assertTrue(Objects.equals(Double.valueOf(1.2345),Double.valueOf(1.2345)));
        assertFalse(Double.valueOf(1.234)==Double.valueOf(1.234));
        assertEquals(Math.round(2.4),Math.round(1.8));
        assertEquals(Math.round(1.6),Math.round(2.0));
    }

    @Test
    public void isBugfixTest(){
        assertTrue(Util.isBugfix("hogehogeBugfix"));
        assertFalse(Util.isBugfix("ixfugb"));
    }

}
