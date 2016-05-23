package yousei;

import org.eclipse.jgit.diff.DiffEntry;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
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

        Map<String,Integer> res=Util.addMap(map1,map2);
        assertEquals(res.get("a"),Integer.valueOf(2));
        assertEquals(res.get("b"),Integer.valueOf(5));
        assertEquals(res.get("c"),Integer.valueOf(16));
        assertEquals(res.get("d"),Integer.valueOf(11));
        assertEquals(res.get("e"),null);
    }

}
