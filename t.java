package org.cloudbus.cloudsim.examples.RLcontainer;

import cern.colt.matrix.DoubleMatrix2D;
import org.cloudbus.cloudsim.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class t {
    public static void main(String[] args) {
          HashMap <ArrayList<Integer>, Double> h = new HashMap<ArrayList<Integer>, Double>();
          ArrayList a = new ArrayList();
          a.add(1);
          a.add(2);
          h.put(a, 1.0);
          Log.printLine(h.get(a));
    }
}
