class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	@SuppressWarnings("unchecked")
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	/**
	 * Adds the specified item to the heap.
	 * @param data the item to add
	 */
	@SuppressWarnings("unchecked")
	public void add (T data) {
		if (_numElements == _storage.length) {
			T[] newStorage = (T[]) new Comparable[_storage.length * 2]; // Double the size of the storage array
			System.arraycopy(_storage, 0, newStorage, 0, _storage.length); // Copy old storage to new storage
			_storage = newStorage;
		}
		_storage[_numElements] = data; // Add the new element at the end of the heap
		_numElements++;
		heapifyUp(_numElements - 1); // Recursively heapify up from the last index
	}

	/**
	 * Helper method to heapify up from a given index.
	 * Meaning: if the element at the given index is larger than its parent,
	 * swap them and continue heapifying up from the parent's index.
	 * @param index the index to start heapifying up from
	 */
	private void heapifyUp (int index) {
		if (index == 0) {
			return; // Base case: reached the root of the heap
		}
		int parentIndex = (index - 1) / 2;
		if (_storage[index].compareTo(_storage[parentIndex]) > 0) { // If current is larger than parent
			T temp = _storage[index]; // Swap current with parent
			_storage[index] = _storage[parentIndex];
			_storage[parentIndex] = temp;
			heapifyUp(parentIndex); // Recursively heapify up from the parent's index
		}
	}

	/**
	 * Helper method to heapify down from a given index.
	 * Meaning: if the element at the given index is smaller than either of its children,
	 * swap it with the larger child and continue heapifying down from that child's index.
	 * @param index the index to start heapifying down from
	 */
	private void heapifyDown (int index) {
		int leftChildIndex = 2 * index + 1;
		int rightChildIndex = 2 * index + 2;
		int largestIndex = index;

		if (leftChildIndex < _numElements && _storage[leftChildIndex].compareTo(_storage[largestIndex]) > 0) { // Compare with left child
			largestIndex = leftChildIndex;
		}
		if (rightChildIndex < _numElements && _storage[rightChildIndex].compareTo(_storage[largestIndex]) > 0) { // Compare with the largest so far
			largestIndex = rightChildIndex;
		}
		if (largestIndex != index) { // Swap and continue heapifying down if needed
			T temp = _storage[index]; // Temporarily store the current element
			_storage[index] = _storage[largestIndex];
			_storage[largestIndex] = temp;
			heapifyDown(largestIndex); // Recursively heapify down from the largest index
		}
	}

	/**
	 * Removes and returns the currently "largest" item from the heap (which is always at the top).
	 * @return the top of the heap
	 */
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

	/**
	 * Returns the number of elements currently in the heap.
	 * @return the size of the heap
	 */
	public int size () {
		return _numElements;
	}
}
