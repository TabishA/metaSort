//Node.java
//Tabish Ashfaq
package metasort;

import java.util.*;
import java.io.*;

  public class Node{
      int i;
      int j;
      Node next;

      public Node(int i, int j){
        this.i = i;
        this.j = j;
        this.next = null;
      }

      public Node(Node node){
        this.i = node.i;
        this.j = node.j;
        this.next = node.next == null ? null : new Node(node.next);
      }

      public boolean compareTo(Node node){
        return (this.i == node.i && this.j == node.j);
      }

    }