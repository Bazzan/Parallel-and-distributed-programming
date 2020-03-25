package TentaPlugg.tenta190520;




import java.util.function.Consumer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@SuppressWarnings("unchecked")
class Task4 {
	public static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	static class LinkedList<V> {
		private ListEntry<V> first = null;
		
		V getValueAt(int index) {
            ListEntry<V> temp = first;
            V value;
            int i = 0;
            readWriteLock.readLock().lock();

			while (temp != null && i < index) {
				temp = temp.getNext();
				i++;
            }
            readWriteLock.readLock().unlock();

            if (temp != null){
            value =temp.getValue();
            readWriteLock.readLock().unlock();
				return  value;
			}else{
                return null;
            }

		}
		
		boolean insertValueAt(V value, int index) {
			ListEntry<V> newEntry = new ListEntry(value);
			readWriteLock.writeLock().lock();
			if (index == 0) {
				newEntry.next = first;
				first = newEntry;
				return true;
			}
			
			ListEntry<V> temp = first;
			int i = 0;
			while (temp != null && i < index - 1) {
				temp = temp.getNext();
				i++;
			}
            if (temp != null){
            temp.insertAfter(newEntry);
            readWriteLock.writeLock().unlock();
				return true;
            }else{
                readWriteLock.writeLock().unlock();
				return false;
                
            }
		}
		boolean removeValueAt(int index) {
			if (index == 0) {
				if (first != null) {
					first = first.getNext();
					return true;
				}
				else
					return false;		
			}
			
			ListEntry<V> temp = first;
			int i = 0;
			while (temp != null && i < index - 1) {
				temp = temp.getNext();
				i++;
			}
			if (temp != null)
				return temp.removeNext();
			else
				return false;
		}
		
		void forEach(Consumer<V> consumer) {
			ListEntry<V> temp = first;
			while (temp != null) {
				consumer.accept(temp.getValue());
				temp = temp.getNext();
			}
		}
		
		// Inner class.
		class ListEntry<V> {
			private final V value;
			private ListEntry<V> next = null;
			
			ListEntry(V value) {
				this.value = value;
			}
			
			V getValue() {
				return value;
			}
			
			ListEntry<V> getNext() {
				return next;
			}

			void setNext(ListEntry<V> newNext) {
				next = newNext;
			}
			
			boolean insertAfter(ListEntry<V> newEntry) {
				newEntry.setNext(next);
				next = newEntry;
				return true;
			}
			
			boolean removeNext() {
				if (next != null) {
					next = next.getNext();
					return true;
				}
				else
					return false;
			}
		}
	}

	// Entry point.
	public static void main(String[] args) {

	}
}