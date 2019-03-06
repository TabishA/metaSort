//Individual.java
//Tabish Ashfaq
package metasort;

import java.util.*;
import java.io.*;

//Each individual is represented as a linked list of swap operations
//with each node containing the indices of elements to be swapped
//The structure provides methods to add a swap to the end of the list
//or to push a swap to the beginning of the list, depending on the desired application

public class Individual{

    Node first;
    int size;
    double fitness;

    public Individual(){

    }

    public Individual(Individual that){
      this.first = new Node(that.first);
      this.size = that.size;
      this.fitness = that.fitness;
    }


   public String toString () {
      return "Size: " + this.size + ", fitness: " + this.fitness;
    }

    public String deepToString(){
      Node current = this.first;
      String string = " ";
      while(current !=null){
        string = string + (" (" + current.i +", " + current.j + ")");
        current = current.next;
      }
      string = string + ("\n Size: " + this.size);
      return string;
    }

  public void setFitness(double newFitness){
    fitness = newFitness;
  }

  public boolean isEmpty(){
      return size == 0;
  }

  //delete node at given position
  public void delete(int position){
    Node current = first;
    Node previous = null;
    int i = 0;

    if (current == null){
      return;
    }

    if(position == 0){
      first = first.next;
      this.size--;
      return;
    }

    while (current != null && i<position) { 
      previous = current; 
      current = current.next;
      i++;
    }

    if(i==position && current != null){
      previous.next = current.next;
    }else if(i == position && current == null){
      previous.next = null;
    } else {
      return;
    }

    this.size--;
  }

  public void add(int i, int j){
    Node newNode = new Node(i, j);
    Node current = first;

    if(size == 0){
      first = newNode;
      size++;
      return;
    }

    while(current.next != null){
      current = current.next;
    }
    current.next = newNode;
    size++;
  }

  public Node getNode(int position){
    Node current = first;
    int i = 0;
    while (current != null && i<position) {  
      current = current.next;
      i++;
    }
    return current;
  }

  public void push(int i, int j){
    Node newNode = new Node(i, j);
    newNode.next = this.first;
    this.first = newNode;
    size++;
  }

  public void modify(int position, int i, int j){
    Node toModify = first;
    
    if(position == 0){
      toModify.i = i;
      toModify.j = j;
      return;
    }

    for (int k = 0; k < position; k++){
      if(toModify.next != null){
        toModify = toModify.next;
      }else{
        return;
      }
    }

    toModify.i = i;
    toModify.j = j;
    return;

  }

  public void swap(int i, int j, double[] currentList){
    
    if(currentList[i] > currentList[j] && i<j){
      double temp = currentList[i];
      currentList[i] = currentList[j];
      currentList[j] = temp;
    }else if(currentList[j] > currentList[i] && j<i){
      double temp = currentList[j];
      currentList[j] = currentList[i];
      currentList[i] = temp;
    }
  }

  public double[] apply(double[] currentList){
    Node current = first;

    while(current != null){
      swap(current.i, current.j, currentList);
      current = current.next;
    }

    return currentList;
  }

  public boolean contains(Node node){
    Node current = first;

    while(current != null){
      if (current.compareTo(node)) return true;
      current = current.next;
    }
    return false;
  }
}
