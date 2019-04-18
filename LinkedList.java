/* 
 * Name: Rynel Luo, Vidula Kopli
 * PennKey: rynelluo, vkopli
 * Recitation: 215, 216
 *
 * Description: Creates linked list that can be altered using various methods
 */

public class LinkedList<T> implements List<T> {
    
    private Node head = null;  
    private Node tail = null;
    private int N = 0;
    
    Node newNode;
    Node curr;
    
    //inner Node class (each representing a note)
    private class Node {
        
        private Node next;
        private T element;
        
        public Node(T element) {
            next = null;
            this.element = element;
        }
        
        public Node(T element, Node next) {
            this.next = next;
            this.element = element;
        }
    }
    
    //list iterator class
    public class LinkedListIterator implements ListIterator<T> {
        
        private Node copyCurr;
        
        //constructor: makes new iterator for linked list, starting at head
        public LinkedListIterator() {
            
            if (head == null) {
                copyCurr = null;
            }
            else {
                copyCurr = new Node(head.element);
                Node prevCopyCurr = copyCurr;
                
                for (Node curr = head.next; curr != null; curr = curr.next) {
                    Node newCurr = new Node(curr.element);
                    prevCopyCurr.next = newCurr;
                    prevCopyCurr = newCurr;
                }
            }
        }

        /**
         * Checks if iterator has a next (node of linked list)
         * @param x the element to be added to this list
         * @return whether another node is left in list
         */
        public boolean hasNext() {
            
            return copyCurr != null; 
        }
         
        /**
         * Returns current node and moves current node to next node in list
         * @return current node in linked list
         */
        public T next() {
            
            T e = copyCurr.element;
            copyCurr = copyCurr.next;
            return e;
        }
    }
    
    /** 
     * Constructor: makes new empty linked list
     */
    public LinkedList() {}
    
    /**
     * Constructs a new iterator for linked list
     * @return iterator object, starting at head of linked list
     */
    public LinkedListIterator listIterator() {
        
        LinkedListIterator newIterator = new LinkedListIterator();
        return newIterator;
    }
    
    /**
     * Adds the object x to the end of the list.
     * @param x the element to be added to this list
     * @return true
     */
    public boolean add(T x) {
    
        isNullElement(x);
        
        if (head == null) {
            head = new Node(x);
            tail = head;
        }
        else {
            newNode = new Node(x);
            tail.next = newNode;
            tail = newNode;       
        }
        
        N++;
        return true;
    }
    
    /**
     * Adds the object x at the specified position
     * @param index the position to add the element
     * @param x the element to be added to the list
     * @return true if the operation succeeded, false otherwise
     * @throws IllegalArgumentException - if index is longer
     * than the currentlength of the list
     */
    public boolean add(int index, T x) {
    
        if (index > N || index < 0) {
            throw new IllegalArgumentException("Error: added " + 
                                               "index is out of bounds");
        }
        
        //if adding to end of list, add
        if (index == N) {
            //if empty list (index = N = 0), add() will take care of it
            //throws error if element is null
            return add(x); 
        }
        
        isNullElement(x); //throw error if element is null 
         
        //know at this point that list is not empty
        if (index == 0) {
            newNode = new Node(x, head);
            head = newNode;
            N++;
            return true;
        }
        
        curr = head; 
        for (int i = 0; i < (index - 1); i++) {
            curr = curr.next;
        }
        
        newNode = new Node(x, curr.next);
        curr.next = newNode;
                
        N++;
        return true;
    }
    
    /**
     * Returns the number of elements in this list
     * @return the number of elements in this list
     */
    public int size() {
    
        return N;
    }
    
    /**
     * Returns the element with the specified position in this list
     * @param index the position of the element
     * @return the element at the specified position in this list
     * @throws IllegalArugmentException if index is longer than the
     * number of elements in the list
     */
    public T get(int index) {
    
        isIndex(index); 
        if (isEmpty()) {
            throw new RuntimeException("Error: " +
                                           "trying to get from empty list");
                
        }
        
        curr = head; 
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        
        return curr.element;
    }
    
    /**
     * Replaces the object at the specified position
     * @param index the position to replace
     * @param x the element to be stored
     * @return the previous value of the element at index
     * @throws IllegalArugmentException if index is longer than the
     * number of elements in the list
     */
    public T set(int index, T x) {
    
        isIndex(index);
        isNullElement(x);
        if (isEmpty()) {
            throw new RuntimeException("Error: " +
                                           "trying to set to empty list");
                
        }
        
        curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
            
        T oldElement = curr.element;
        curr.element = x;
        
        return oldElement;
    }
    
    /**
     * Removes the object at the specified position
     * @param index the position to remove
     * @return the object that was removed
     * @throws IllegalArugmentException if index is more than
     * the number of elements in the list
     */
    public T remove(int index) {
    
        isIndex(index);
        if (isEmpty()) {
            throw new RuntimeException("Error: trying" +
                                           "to remove from empty list");        
        }
        
        if (index == 0) {
            T removedElement = head.element;
            head = head.next;
            N--;
            return removedElement;
        }   
        
        curr = head;
        for (int n = 0; n < (index - 1); n++) {
            curr = curr.next;
        }

        T removedElement = curr.next.element;
        curr.next = curr.next.next;
        
        N--;
        return removedElement;
    }
    
    /**
     * Tests if this list has no elements.
     * @return  <tt>true</tt> if this list has no elements;
     *          <tt>false</tt> otherwise.
     */
    public boolean isEmpty() {
    
        return head == null;
    }
    
    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     *
     * @param element element whose presence in this List is to be tested.
     * @return  <code>true</code> if the specified element is present;
     *  <code>false</code> otherwise.
     */
    public boolean contains(T element) {
    
        for (curr = head; curr != null; curr = curr.next) {
            if (curr.element.equals(element)) {
                return true;
            }
        }
        return false;
    }
    
    /** 
     * Returns the index of the specified element
     *
     * @param element the element we're looking for
     * @return the index of the element in the list, or -1 if it is not contained within the list
     */
    public int indexOf(T element) {
            
        int i = 0;
        for (curr = head; curr != null; curr = curr.next) {
            if (curr.element.equals(element)) {
                return i;
            }
            i++;
        }
        return -1; 
    }
    
    /** 
     * Throws error if element is null
     *
     * @param element to be checked
     * @throws IllegalArgumentException - if element is null
     */
    private void isNullElement(T x) {
    
        if (x == null) {
            throw new IllegalArgumentException("Error: " + 
                                               "trying to add null element");
        }
    }
    
    /** 
     * Throws error if index is not in current list
     *
     * @param element to be checked
     * @throws IllegalArgumentException - if index is not in current list
     */
    private void isIndex(int index) {
        
        if (index >= N || index < 0) {
            throw new IllegalArgumentException("Error: existing " + 
                                               "index is out of bounds");
        }
    }
    
    /** 
     * Prints all elements in list
     */
    public void printElements() {
         
        for (curr = head; curr != null; curr = curr.next) {
            System.out.print(curr.element + " ");
        }
        System.out.println();
    }
        
    /** 
     * Code to test LinkedList Implementation
     */
    public static void main(String[] args) {
        
        LinkedList<String> myList = new LinkedList<String>();  
        LinkedList<Integer> myInts = new LinkedList<Integer>();
        myInts.add(1);
        myInts.add(1, 2);
        myInts.set(0,3);
        myInts.printElements();
        System.out.println(myList.isEmpty());
        myList.add("a");
        myList.add("b");
        myList.add("c");
        myList.printElements();
        System.out.println(myList.size());
        myList.add(2, "d");
        myList.printElements();
        myList.remove(0);
        myList.printElements();
        System.out.println(myList.get(1));
        System.out.println(myList.remove(1));
        myList.printElements();
        System.out.println(myList.set(1, "x"));
        myList.printElements();
        System.out.println(myList.contains("x"));
        System.out.println(myList.indexOf("c"));
    }
}
