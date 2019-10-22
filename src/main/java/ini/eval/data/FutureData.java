package ini.eval.data;

import java.util.Map;
import java.util.concurrent.Semaphore;

public class FutureData extends RawData {

	private Semaphore lock = new Semaphore(1);
	private boolean available = false;

	public FutureData() {
		super();
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Data getIfAvailable() {
		sync();
		return this;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	private void sync() {
		if (!available) {
			try {
				lock.acquire();
				lock.release();
				available = true;
				/*System.out.println("UNLOCKING FUTURE: " + this);
				new Exception().printStackTrace(System.out);*/
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public <T> T getValue() {
		sync();
		return super.getValue();
	}

	@Override
	public Data get(Object key) {
		sync();
		return super.get(key);
	}

	@Override
	public Number getNumber() {
		sync();
		return super.getNumber();
	}

	@Override
	public Map<Object, Data> getReferences() {
		sync();
		return super.getReferences();
	}

	@Override
	public Data first() {
		sync();
		return super.first();
	}

	@Override
	public Boolean getBoolean() {
		sync();
		return super.getBoolean();
	}

	@Override
	public int getSize() {
		sync();
		return super.getSize();
	}

	@Override
	public boolean isArray() {
		sync();
		return super.isArray();
	}

	@Override
	public boolean isBoolean() {
		sync();
		return super.isBoolean();
	}

	@Override
	public Data concat(Data data) {
		sync();
		return super.concat(data);
	}

	@Override
	public boolean isNumber() {
		sync();
		return super.isNumber();
	}

	@Override
	public boolean isIndexedSet() {
		sync();
		return super.isIndexedSet();
	}

	@Override
	public boolean isPrimitive() {
		sync();
		return super.isPrimitive();
	}

	@Override
	public boolean isTrueOrDefined() {
		sync();
		return super.isTrueOrDefined();
	}

	@Override
	public Object keyOf(Data data) {
		sync();
		return super.keyOf(data);
	}

	@Override
	public boolean isUndefined() {
		sync();
		return super.isUndefined();
	}

	@Override
	public Object maxIndex() {
		sync();
		return super.maxIndex();
	}

	@Override
	public Object minIndex() {
		sync();
		return super.minIndex();
	}

	@Override
	public Data rest() {
		sync();
		return super.rest();
	}

	@Override
	public void copyData(Data data) {
		super.copyData(data);
		lock.release();
		/*System.out.println("RELEASED1");
		new Exception().printStackTrace(System.out);
		System.out.println(this);
		new Thread() {
			public void run() {
				try {
					sleep(1000);
				} catch (Exception e) {
				}
				System.out.println("=========>" + data);
			}
		}.start();*/
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		lock.release();
		/*System.out.println("RELEASED2");
		new Exception().printStackTrace(System.out);*/
	}

}
