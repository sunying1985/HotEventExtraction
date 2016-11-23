package com.suny.docu;

import java.util.Arrays;

/**
 * A class representing a mathematical vector. 
 * Supports basic vector operations like add, multiply,
 * divide etc.).
 */
public class IndexVector {
	private final double[] elements;

	/**
	 *  Construct a IndexVector with size elements. 
	 *  */
	public IndexVector(int size) {
		elements = new double[size];
	}

	/** 
	 * Construct a IndexVector by copying the elements of the provided IndexVector. 
	 * */
	public IndexVector(IndexVector vector) {
		elements = Arrays.copyOf(vector.elements, vector.elements.length);
	}

	/** 
	 * Add the provided IndexVector to this IndexVector. 
	 * */
	public IndexVector add(IndexVector operand) {
		IndexVector result = new IndexVector(size());
		
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) + operand.get(i));
		}
		return result;
	}

	/** 
	 * Divide this IndexVector by the provided divisor.
	 *  */
	public IndexVector divide(double divisor) {
		IndexVector result = new IndexVector(size());
		
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) / divisor);
		}
		return result;
	}

	/** 
	 * Get the element of this IndexVector at the specified index. 
	 * */
	public double get(int i) {
		return elements[i];
	}

	/** 
	 * Apply elementwise increment to specified element of this IndexVector. 
	 * */
	public void increment(int i) {
		set(i, get(i) + 1);
	}

	/** 
	 * Calculate the inner product of this IndexVector with the provided IndexVector.
	 *  */
	public double innerProduct(IndexVector vector) {
		double innerProduct = 0;
		for (int i = 0; i < elements.length; i++) {
			innerProduct += get(i) * vector.get(i);
		}
		return innerProduct;
	}

	/** 
	 * Apply elementwise inversion to this IndexVector.
	 *  */
	public IndexVector invert() {
		IndexVector result = new IndexVector(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, 1 / get(i));
		}
		return result;
	}

	/** 
	 * Apply elementwise log to this.
	 *  */
	public IndexVector log() {
		IndexVector result = new IndexVector(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, Math.log(get(i)));
		}
		return result;
	}

	/** 
	 * Return maximal element. 
	 * */
	public double max() {
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < elements.length; i++) {
			maxValue = Math.max(maxValue, get(i));
		}
		return maxValue;
	}

	/**
	 *  Multiply this with the provided scalar multiplier. 
	 *  */
	public IndexVector multiply(double multiplier) {
		IndexVector result = new IndexVector(size());
		for (int i = 0; i < elements.length; i++) {
			result.set(i, get(i) * multiplier);
		}
		return result;
	}

	/** 
	 * Multiply this with the provided vector multiplier. 
	 * */
	public IndexVector multiply(IndexVector multiplier) {
		IndexVector result = new IndexVector(size());
		for (int i = 0; i < elements.length; i++) {
			if (get(i) == 0 || multiplier.get(i) == 0) {
				result.set(i, 0);
			} else {
				result.set(i, multiplier.get(i) * get(i));
			}
		}
		return result;
	}

	/** 
	 * Calculate the L2 norm of this. 
	 * */
	public double norm() {
		double normSquared = 0.0;
		for (int i = 0; i < elements.length; i++) {
			normSquared += get(i) * get(i);
		}
		return Math.sqrt(normSquared);
	}

	/** 
	 * Set the specified element of this. 
	 * */
	public void set(int i, double value) {
		elements[i] = value;
	}

	/** 
	 * Return the number of elements in this. 
	 * */
	public int size() {
		return elements.length;
	}

	@Override
	public String toString() {
		return Arrays.toString(elements);
	}

}