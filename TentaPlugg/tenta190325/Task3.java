package TentaPlugg.tenta190325;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Task3 {
    // Function func;
    List<Integer> list;
    private static ExecutorService executor = ForkJoinPool.commonPool();
    private static List<Integer> result1 = new ArrayList<>();

    private static List<Integer> listFilter(List<Integer> list, Function<Integer, Boolean> pred) {
        List<Integer> result = new ArrayList<>();
        for (Integer ele : list) {
            if (pred.apply(ele)) {
                result.add(ele);

            }

        }
        return result;

    }

    private static List<Integer> parallelListFilter(List<Integer> list, Function<Integer, Boolean> pred) {
        BlockingQueue<Integer> bque = new ArrayBlockingQueue<Integer>(list.size());

        System.out.println(list.size());
        for (int i = 0; i < bque.size(); i++) {
            try {
                bque.put(list.get(i));

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // try {
        //     System.out.println(bque.take());
        // } catch (InterruptedException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        // System.out.println(list + " list" + bque);

        // for (int i = 0; i < list.size(); i++) {
        //     executor.execute(() -> {
        //         try {
        //             Integer integer = bque.take();
        //             doThing(integer, pred, bque);

        //         } catch (Exception e) {
        //             // TODO: handle exception
        //             System.out.println(e);
        //         }

        //     });
    // }
    for (Integer integer : list) {
        executor.submit(() ->{
            boolean bool =pred.apply(integer);
            System.out.println(bool);
            if(bool){
                result1.add(integer);
            }
        });
        
    }
            // for (int i = 0; i < bque.size(); i++) {
            //     executor.execute(() -> {
            //         try {
            //             // Integer integer= list.get();
            //             System.out.println("yo1");
            //             Integer integer =bque.take();
            //             if (pred.apply(integer)) {
            //                 result1.add(integer);
            //                 System.out.println("yo");
            //             }
            //         } catch (Exception e) {
            //             // TODO: handle exception
            //         }
            //     });
            // }



        // for (int i = 0; i < bque.size(); i++) {

        //     try {
        //         result1.add(bque.take());
        //     } catch (InterruptedException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        return result1;
    }



    public static void main(String[] args) {
        List<Integer> result = new ArrayList<Integer>();
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);
        // System.out.println(list1);

        // result = listFilter(list1, (X) -> X > 2);
        result = parallelListFilter(list1, (X) -> X > 2);
        // for (Integer integer : result) {
        //     System.out.println(integer);

        // }
		try {
			while (executor.awaitTermination(30, TimeUnit.SECONDS)) {

			}
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
		}
        System.out.println(result);
        System.out.println( parallelListFilter(list1, (X) -> X > 2));


    }

}