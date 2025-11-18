//import heap

class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	@SuppressWarnings("unchecked")
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	@SuppressWarnings("unchecked")
	public void add (T data) {
		if (_numElements == _storage.length) {
			T[] newStorage = (T[]) new Comparable[_storage.length * 2];
			System.arraycopy(_storage, 0, newStorage, 0, _storage.length);
			_storage = newStorage;
		}
		_storage[_numElements] = data;
		_numElements++;
		heapifyUp(_numElements - 1);
	}

	private void heapifyUp (int index) {
		if (index == 0) {
			return;
		}
		int parentIndex = (index - 1) / 2;
		if (_storage[index].compareTo(_storage[parentIndex]) > 0) {
			T temp = _storage[index];
			_storage[index] = _storage[parentIndex];
			_storage[parentIndex] = temp;
			heapifyUp(parentIndex);
		}
	}
	private void heapifyDown (int index) {
		int leftChildIndex = 2 * index + 1;
		int rightChildIndex = 2 * index + 2;
		int largestIndex = index;

		if (leftChildIndex < _numElements && _storage[leftChildIndex].compareTo(_storage[largestIndex]) > 0) {
			largestIndex = leftChildIndex;
		}
		if (rightChildIndex < _numElements && _storage[rightChildIndex].compareTo(_storage[largestIndex]) > 0) {
			largestIndex = rightChildIndex;
		}
		if (largestIndex != index) {
			T temp = _storage[index];
			_storage[index] = _storage[largestIndex];
			_storage[largestIndex] = temp;
			heapifyDown(largestIndex);
		}
	}

	public T removeFirst () {
		if (_numElements == 0) {
			return null;
		}
		T result = _storage[0];
		_storage[0] = _storage[_numElements - 1];
		_numElements--;
		heapifyDown(0);
		return result;
	}

	public int size () {
		return _numElements;
	}
}
