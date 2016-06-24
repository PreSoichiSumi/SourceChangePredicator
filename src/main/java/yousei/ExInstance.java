package yousei;

import weka.core.Instance;

/**
 * Created by s-sumi on 2016/06/24.
 */
public class ExInstance extends Instance{
    public int dist=0;

    public ExInstance(Instance data){
        this.m_AttValues=data.toDoubleArray();
        this.m_Weight=data.weight();
        this.m_Dataset=data.dataset();
        setDist();
    }

    public void setDist(){
        int num=m_AttValues.length/2;
        int tmp=0;
        for(int i=0;i<num;i++){
            tmp+=Math.abs(Math.round(m_AttValues[i])-Math.round(m_AttValues[i+num]));
        }
        this.dist=tmp;
    }
}
