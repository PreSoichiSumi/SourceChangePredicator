package yousei;

import weka.core.FastVector;
import weka.core.Instances;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by s-sumi on 2016/06/24.
 */
public class ExInstances extends Instances {

    public ExInstances(Reader reader) throws IOException {
        super(reader);  //このコンストラクタが呼ばれればInstancesの一通りの機能が揃う
        init();         //これも実行すると各InstanceがExInstanceになる
    }
    public void init(){
        FastVector newVector =new FastVector();
        for(int i=0;i<numInstances();i++){
            ExInstance ei=new ExInstance(this.instance(i));
            newVector.addElement(ei);
        }
        m_Instances=newVector;
    }

    public ExInstance exInstance(int index){
        return (ExInstance) m_Instances.elementAt(index);
    }


}
