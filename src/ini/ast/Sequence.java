package ini.ast;

public class Sequence<E> {

	private E element;
	
	private Sequence<E> next;
	
	public Sequence(E element) {
		this.element=element;
	}

	public E get() {
		return element;
	}
	
	public boolean hasNext() {
		return next!=null;
	}

	public void insertNext(Sequence<E> toInsert) {
		Sequence<E> oldNext=next;
		next=toInsert;
		toInsert.last().next = oldNext;
	}

	public void insertNext(E toInsert) {
		Sequence<E> s = new Sequence<E>(toInsert);
		s.next = next;
		next = s;
	}
	
	public Sequence<E> next() {
		return next;
	}

	public Sequence<E> last() {
		Sequence<E> cur = this;
		while(cur.hasNext()) {
			cur=cur.next();
		}
		return cur;
	}

	public void setNext(E element) {
		next = new Sequence<E>(element);
	}
	
	public int size() {
		int count=0;
		Sequence<E> cur = this;
		while(cur!=null) {
			count++;
			cur=cur.next();
		}
		return count;
	}
	
	public Sequence<E> find(E element) {
		Sequence<E> s = this;
		while(s!=null) {
			if(element.equals(s.get())) {
				return s;
			}
			s=s.next();
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		Sequence<E> s = this;
		while(s!=null) {
			sb.append(""+s.get());
			s=s.next();
			if(s!=null) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
}
