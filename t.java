package org.cloudbus.cloudsim.examples.RLcontainer;

import cern.colt.matrix.DoubleMatrix2D;
import org.cloudbus.cloudsim.Log;

import java.lang.reflect.Array;
import java.util.*;

public class t {
    public static void main(String[] args) {
          List<Integer> a = new ArrayList<>();
          a.add(1);
          a.add(2);
          List<Integer> b=new ArrayList<>();
          Collections.copy(a,b);

          List<Integer> srcList = new ArrayList<Integer>();
          srcList.add(1);
          srcList.add(2);
          List<Integer> destList1= new ArrayList<Integer>(srcList);

          List<Integer> destList2= Arrays.asList(new Integer[srcList .size()]);
          Collections.copy(destList2, srcList);

          List<Integer> destList3= new ArrayList<Integer>();
          destList3.addAll(srcList);

          List<Integer> destList4= new ArrayList<Integer>();
          for (int i = 0; i < srcList.size(); i++) {
              destList4.add(srcList.get(i));
          }
          //测试：
          System.out.println("源list"+srcList);//输出[1, 2]
          System.out.println("1copyList："+destList1);//输出[1, 2]
          System.out.println("2copyList："+destList2);//输出[1, 2]
            System.out.println("3copyList："+destList3);//输出[1, 2]
            System.out.println("4copyList："+destList4);//输出[1, 2]
            System.out.println("---删除源数据第一个元素");
            srcList.set(0, 2);
            System.out.println("---删除源数据第一个元素之后");
            System.out.println("源list"+srcList);//输出[2]
            System.out.println("1copyList："+destList1);//输出[1, 2]
            System.out.println("2copyList："+destList2);//输出[1, 2]
            System.out.println("3copyList："+destList3);//输出[1, 2]
    }
}
