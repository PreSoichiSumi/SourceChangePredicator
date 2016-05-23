package yousei;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by s-sumi on 2016/05/23.
 */
public class Util {
    public Map<String, Integer> addMap(Map<String,Integer> map1, Map<String, Integer> map2){
        Map<String, Integer> res= new HashMap<>(map1);
        /*for(Map.Entry<String,Integer> e:map2.entrySet()){
            res.merge(e.getKey(), e.getValue(),
                    (a,b)-> a + b );
        }*/
        map2.entrySet().stream()
                .forEach(e -> res.merge(e.getKey(), e.getValue(), (a,b) -> a+b));
        return res;
    }
}
