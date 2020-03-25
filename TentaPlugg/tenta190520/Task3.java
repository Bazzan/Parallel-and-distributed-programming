package TentaPlugg.tenta190520;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.*;

class Task3 {
    static List<Pair> pairs = new ArrayList<Pair>();
    static Thread[] threads;
    // static List<Integer> result;

    private static List<Integer> zipApply(BiFunction<Integer, Integer, Integer> func, List<Integer> List1,
            List<Integer> List2) {
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < List1.size(); i++) {
            list.add(func.apply(List1.get(i), List2.get(i)));
        }
        System.out.println(list.toString());
        return list;
    }
    private static List<Integer> parallelzipApply(BiFunction<Integer, Integer, Integer> func, List<Integer> list1, List<Integer> list2) {
		for (int i = 0; i < list1.size(); i++) {
            pairs.add(new Pair(list1.get(i), list2.get(i)));
        }
        List<Integer> result = pairs.parallelStream().map(p -> func.apply(p.e1, p.e2)).collect(Collectors.toList());
        return result;
    }
    public static void main(String[] args) {

        // List<Integer> list1 = Arrays.asList(1, 2, 3);
        // List<Integer> list2 = Arrays.asList(3, 2, 1);
        // List<Integer> finalresult1 = zipApply((A, B) -> A + B, list1, list2);
        // System.out.println(finalresult1.toString());
        List<Integer> list3 = new ArrayList<Integer>();

        list3.add(1);
        list3.add(3);
        list3.add(2);
        // List<Integer> list3 = Arrays.asList(2, 2, 3);
        List<Integer> list4 = new ArrayList<Integer>();
        list4.add(2);
        list4.add(22);
        list4.add(223);
        List<Integer> finalresult2 = parallelzipApply((A, B) -> A + B, list3, list4);

        System.out.println(finalresult2.toString());
        // + " " +finalresult2.get(0)+ " " + finalresult2.get(1)+ " " +
        // finalresult2.get(2)
    }
}

class Pair {
    Integer e1;
    Integer e2;
    // int index;
    // BiFunction<Integer, Integer, Integer> func;

    Pair(Integer e1, Integer e2) {
        this.e1 = e1;
        this.e2 = e2;
        // this.index = index;
        // this.func = func;
    }

}
