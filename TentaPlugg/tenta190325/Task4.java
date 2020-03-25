// Peter Idestam-Almquist, 2019-03-20.

package TentaPlugg.tenta190325;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
class Task4 {
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	static class Array<T> {
		private int size = -1;
		private Object[] data = null;
		
		// Constructor.
		Array(int size) {
			this.size = size;
			data = new Object[size];
		}
		
		int size() {
			return size;
		}
		
		void set(int i, T value) {

			if (i < 0 || i >= size)
                throw new IndexOutOfBoundsException();
                

            lock.writeLock().lock();

			try {

                data[i] = value;
                                
            } finally {
            lock.writeLock().unlock();
            }

		}
		
		T get(int i) {
			if (i < 0 || i >= size)
				throw new IndexOutOfBoundsException();
            lock.readLock().lock();
            T value;
            try{
                 value = (T)data[i];

            } finally{
                lock.readLock().unlock();
            }
            return value;

		}
		
		void forEach(Consumer<T> consumer) {
            lock.writeLock().lock();
            try{
            for (int i = 0; i < size; i++) {
                
                if (data[i] != null)
					consumer.accept((T)data[i]);
            }
        }finally{ 
            lock.writeLock().unlock();
        }
		}
	}
	
	// You can modify this test method in any way you like.
	public static void main(String[] args) {
		Array<Integer> data = new Array<Integer>(10);
		data.set(1, 1);
		data.set(2, 6);
		data.set(3, 1);
		data.set(4, 5);
		data.set(6, 3);
		data.set(8, 2);
		data.forEach(System.out::println);
		System.out.println(data.get(2));
		data.set(4, null);
		System.out.println(data.get(4));
		data.forEach(System.out::println);
	}
}
