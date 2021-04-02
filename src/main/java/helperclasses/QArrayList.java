/*
This program has been developed by students from the bachelor Computer Science
at Utrecht University within the Software and Game project course.

©Copyright Utrecht University (Department of Information and Computing Sciences)
*/

package helperclasses;

import java.util.*;
import java.util.function.Function;

/**
 * This class was created to help with list operations and might be deleted
 * later if we find a good library. For now I've added the functions I use most
 * while programming. Last written by Maurin on 07-11-2019
 */

public class QArrayList<T> extends ArrayList<T> {

    /**
     * Instantiate the QArrayList
     */
    public QArrayList() {
        // use the super constructor
        super();
    }

    /**
     * Instantiate the QArrayList using an array
     */
    public QArrayList(T[] array) {
        // use the super constructor
        super(Arrays.asList(array));
    }

    public QArrayList(QArrayList<T> list) {
        super(list);
    }

    public QArrayList(Collection<T> values) {
        super(values);
    }

    /**
     * Instantiate the QArrayList using another list
     */
    public QArrayList(List<T> list) {
        // use the super constructor
        super(list);
    }

    /**
     * Instantiate the QArrayList using another list
     */
    public QArrayList(ArrayList<T> list) {
        // use the super constructor
        super(list);
    }

    /**
     * Instantiate the QArrayList from a primitive integer array
     */
    public static QArrayList<Integer> FromPrimitives(int... array) {
        // use the super constructor
        QArrayList<Integer> list = new QArrayList<>();
        for (int i : array)
            list.add(i);
        return list;
    }

    /**
     * This is a MAP function
     *
     * @param func Lambda expression that should map from E to T
     * @param <A>  This could be any data type
     * @return The mapped version of the list
     */
    public <A> QArrayList<A> select(Function<T, A> func) {

        // create a new list of type T
        QArrayList<A> selectedList = new QArrayList<A>();

        // apply the mapping function on every value in the list and store it in the new
        // list
        for (T value : this)
            selectedList.add(func.apply(value));

        // return the new list
        return selectedList;
    }

    /**
     * This is a FILTER function
     *
     * @param func Lambda expression that evaluates a value in a list
     * @return A filtered list copy
     */
    public QArrayList<T> where(Function<T, Boolean> func) {

        // create a new instance of the list with the same type
        QArrayList<T> filteredList = new QArrayList<T>();

        // copy the value to the new list if the function evaluates it to true
        for (T value : this)
            if (func.apply(value))
                filteredList.add(value);

        // return the new
        return filteredList;
    }

    /**
     * This is an evaluator for a list
     *
     * @param func Lambda expression that evaluates a value
     * @return The evaluation of the whole list
     */
    public boolean allTrue(Function<T, Boolean> func) {

        // if we encounter a value that does not evaluate to true, do an early stop
        for (T value : this)
            if (!func.apply(value))
                return false;

        // everything is true, so return true
        return true;
    }

    /**
     * This is an evaluator for a list
     *
     * @param func Lambda expression that evaluates a value
     * @return The evaluation of the whole list
     */
    public boolean anyTrue(Function<T, Boolean> func) {

        // if we encounter a value that does evaluate to true, do an early stop
        for (T value : this)
            if (func.apply(value))
                return true;

        // everything is false, so return false
        return false;
    }

    /**
     * This function returns the maximum value using an evaluation function
     *
     * @param function The map function to T
     * @param <A>      Any datatype that implements Comparable
     * @return The maximum T value
     */
    public <A extends Comparable<A>> A max(Function<T, A> function) {
        return Collections.max(this.select(function));
    }

    /**
     * This function returns the minimum value using an evaluation function
     *
     * @param function The map function to T
     * @param <A>      Any data type that implements Comparable
     * @return The maximum T value
     */
    public <A extends Comparable<A>> A min(Function<T, A> function) {

        // map everything to T and
        return Collections.min(this.select(function));
    }

    /**
     * @return The value with the highest index
     */
    public T last() throws IndexOutOfBoundsException {

        // check if this is an empty list
        if (size() == 0)
            throw new IndexOutOfBoundsException("The array is empty!");

        // return the last index value
        return this.get(this.size() - 1);
    }

    /**
     * @return The value with the lowest index
     */
    public T first() throws IndexOutOfBoundsException {

        // check if this is an empty list
        if (size() == 0)
            throw new IndexOutOfBoundsException("The array is empty!");

        // return the last index value
        return this.get(0);
    }

    /**
     * Orders a list by T
     *
     * @param function Map function to T
     * @param <A>      Any data type that implements comparable
     * @return A sorted list based on T
     */
    public <A extends Comparable<A>> QArrayList<T> orderBy(Function<T, A> function) {

        // map the list to pairs using the map function
        var tuples = this.select(element -> new SortPair<A, T>(function.apply(element), element));

        // since the tuples implement compare, we can let java do the sorting work
        Collections.sort(tuples);

        // after sorting we can return the original values
        return tuples.select(tuple -> tuple.value);
    }

    /**
     * A Contains function that uses an evaluator
     *
     * @param func lambda function that translates an object to a boolean
     * @return true if any value evaluates to true using the function
     */
    public boolean contains(Function<T, Boolean> func) {
        for (T value : this)
            if (func.apply(value))
                return true; // if any value succeeds, do an early stop
        return false;
    }

    /**
     * Reverses the list
     *
     * @return a reversed COPY of the original list
     */
    public QArrayList<T> reversed() {

        QArrayList<T> reversed = new QArrayList<T>();

        // add in reversed order
        for (int i = this.size() - 1; i >= 0; i--) {
            reversed.add(this.get(i));
        }

        return reversed;
    }

    public boolean isDistinct() {
        return this.size() == this.stream().distinct().toArray().length;
    }

    /**
     * This class helps create a Comparable tuple
     *
     * @param <A> Key: Any data type that implements Comparable
     * @param <E> Value: Any data type
     */
    class SortPair<A extends Comparable<A>, E> implements Comparable<SortPair<A, E>> {

        private A key;
        private E value;

        public SortPair(A key, E value) {
            this.key = key;
            this.value = value;
        }

        /*
         * The compare method just uses the compare method of A.
         */
        @Override
        public int compareTo(SortPair<A, E> pair) {
            return this.key.compareTo(pair.key);
        }
    }
}