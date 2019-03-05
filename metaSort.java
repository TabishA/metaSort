//metaSort.java
//Tabish Ashfaq



import java.util.*;
import java.io.*;


public class metaSort{

  public static void main(String args[]) throws FileNotFoundException, IOException{
    double[] avgFitness = new double[100];

    metaSort demo = new metaSort();
    Population population = demo.new Population();
    double maxFitness = 0;

    Population optimalPopulation = demo.new Population();

    Random rand = new Random();

    String inFile = args[0];

    //Data pre-processing: Read csv file into 2D array
    Scanner input = new Scanner(new BufferedReader(new FileReader(inFile)));
    double [][] csvArray = new double[100][100];
    while(input.hasNextLine()) {
      for (int i=0; i<csvArray.length; i++) {
        String[] line = input.nextLine().split(",");
          for (int j=0; j<line.length; j++) {
              csvArray[i][j] = Double.parseDouble(line[j]);
          }
        }
    }
    input.close();

    //System.out.println(Arrays.deepToString(csvArray));

    //initialize random population of 1000 individuals - Generation 0
    double[] test = new double[100];

    for(int i = 0; i < 100; i++){
      test[i] = csvArray[99][i];
    }

    for (int j = 0; j < 1000; j++){
      //initialize random individual
      double[]testTemp = Arrays.copyOf(test,test.length);
      int randSize = rand.nextInt(500);
      if(randSize == 0) continue;
      Individual individual = demo.new Individual();

      //generate indices for successive swap operations
      for(int i = 0; i < randSize; i++){
        int randI = rand.nextInt(100);
        int randJ = rand.nextInt(100);
        while(randI == randJ){
          randJ = rand.nextInt(100);  //avoid swapping an element with itself
        }

        individual.push(randI, randJ);

        }

        individual.apply(testTemp);
        calculateFitness(individual, testTemp, 1000); //temperature initially at 1000

        //save fittest individual from generation 0
        if (individual.fitness > maxFitness){
          maxFitness = individual.fitness;
          population.fittest = individual;
        }

        population.addIndividual(individual);
    }
 
  int temperature;
  //generate successive generations per row of the input file
  for (int i = 0; i <100; i++){
    temperature = 991 - i*(111/11);  //temperature computed to decrease from 991 to 1
    for(int j = 0; j < 100; j++){
      test[j] = csvArray[i][j];
    }
    double[] temp = Arrays.copyOf(test, test.length);

    avgFitness[i] = population.averageFitness();

    population = demo.selection(population, test, temperature);
    System.out.println("next generation " + i);
    
  }

  //fittest individual of final generation is saved in selection()
  Individual fittest = population.fittest;

  System.out.println(fittest.deepToString());

  double[][] csvSorted = new double[100][100];

  //apply fittest individual from generation 99 to the input dataset
  for(int i = 0; i < 100; i++){
    for(int j = 0; j < 100; j++){
      test[j] = csvArray[i][j];
    }
    fittest.apply(test);
    for(int k  = 0; k < 100; k++){
      csvSorted[i][k] = test[k];
    }
  }

  StringBuilder out = new StringBuilder();
  
  for(int i = 0; i < csvSorted.length; i++){
      for(int j = 0; j < csvSorted.length; j++){
        out.append(csvSorted[i][j]+"");
      if(j < csvSorted.length - 1)
         out.append(",");
      }
      out.append("\n");
   }
   
  BufferedWriter outwrite = new BufferedWriter(new FileWriter(inFile.substring(0, inFile.lastIndexOf('.')) + "-output.csv"));
  outwrite.write(out.toString());
  outwrite.close();

}

  //fitness function
  //parameter weights change based on temperature values
  public static double calculateFitness(Individual individual, double[] sublist, int temperature){
    if(individual.size == 0){
      individual.setFitness(0.0);
      return 0.0;
    }

    int ascendingRuns = numAsc(sublist);
    int inversions = countInversions(sublist);
    double efficiency =  (double) 1/(individual.size);
    double effectiveness;
    double fitness;
    
    if(inversions == 0){
      effectiveness = 1;
    }else{
      effectiveness = (double) 1/inversions + 1/ascendingRuns;
    }

    if (temperature == 1000){
      fitness = (double) effectiveness*temperature + efficiency/temperature;
    }else if(temperature > 500){
      fitness = (double) effectiveness*temperature;
    }else if(temperature < 500 && temperature > 250){
      fitness = (double) effectiveness + efficiency/temperature;
    }else{
      fitness = (double) effectiveness + efficiency;
    }

    //System.out.println("inversions: " + inversions + " ");

    individual.setFitness(fitness);
    System.out.println("fitness: " + fitness);
    return fitness;


  }

  //mutations are implemented:
  //for rand(3):
  //case 1: add random swap
  //case 2: remove random swap
  //case 3: modify random swap with random indices
  public void mutate(Individual individual){

    Random rand = new Random();

    int mutation = rand.nextInt(3);
    if(individual.size == 0) return;
    int position = rand.nextInt(individual.size);

    int randI = rand.nextInt(100);
    int randJ = rand.nextInt(100);
    while(randI == randJ){
      randJ = rand.nextInt(100);  //avoid swapping an element with itself
    }

    switch(mutation){
      case 0: individual.push(randI, randJ);
              break;
      case 1: individual.delete(position);
              break;
      case 2: individual.modify(position, randI, randJ);
              break;
    }
  }

//Fitness proportionate selection
//individuals selected for crossover and placed in queue
public Population selection(Population oldGeneration, double[] sublist, int temperature){


  Random rand = new Random();
  
  Population nextGeneration = oldGeneration;

  //create a queue for individuals selected for crossover
  Queue<Individual> selected = new LinkedList<>();

  for(int i = 0; i<nextGeneration.getSize(); i++){
    double select = rand.nextDouble();
    Individual nextIndividual = nextGeneration.getIndividual(i);
    double rankFitness = 100*(nextIndividual.fitness)/nextGeneration.totalFitness;  //normalize to 1, scale up probablity of selection
    


    if(nextIndividual.isEmpty()) nextGeneration.removeIndividual(nextIndividual);

    if(rankFitness > select){
      selected.add(nextIndividual);
    }
    // else if(!selected.isEmpty() && nextIndividual.diverse(selected) > rand.nextDouble()){
    //   selected.add(nextIndividual);
    //   System.out.println("adding diverse");
    // }
    //diversity considerations
  }
 
  Queue<Individual> children = new LinkedList<Individual>(selected);
  if(children.size() != 0){
    children = crossover(children);
  }

  while(!children.isEmpty() && !selected.isEmpty() && !children.peek().isEmpty() && !selected.peek().isEmpty()){
    double[] testList = Arrays.copyOf(sublist,sublist.length);
    double[] testListCopy = Arrays.copyOf(sublist, sublist.length);
    Individual child = children.poll();
    Individual parent = selected.poll();

    //small random probability of mutation
    if (rand.nextInt()%8 < 5) {
      mutate(child);
    }

    parent.apply(testListCopy);
    calculateFitness(parent, testListCopy, temperature);

    child.apply(testList);
    calculateFitness(child, testList, temperature);

    if (child.compareTo(nextGeneration.fittest) > 0){
      nextGeneration.fittest = child;
    }
    
    //Simulated Annealing
    double deltaFitness = child.fitness - parent.fitness;

    if(deltaFitness > 0){
      nextGeneration.replace(parent, child);
    }else if(Math.exp(deltaFitness/temperature) > rand.nextInt(5)){
      nextGeneration.replace(parent, child);
     }
  }
    
    // System.out.println("returning next generation");
    return nextGeneration;
  }

  //crossover point between two parents is chosen at random
  //the current implementation is chosen to avoid references to parent chromosomes
public Queue<Individual> crossover(Queue<Individual> parents){   
    
    System.out.println("crossover, queue size: " + parents.size());

    Random rand = new Random();

    Queue<Individual> children = new LinkedList<>();

    while(parents.size() != 1 && !parents.isEmpty()){

      if(parents.peek().isEmpty()) {  //remove empty individuals
        parents.remove();
        continue;
      }

      Individual child1 = new Individual(parents.poll());
      
      if(parents.peek().isEmpty()) {  //if parent2 is empty, remove it from queue, and return parent1 to queue
        parents.remove();
        parents.add(child1);
        continue;
      }

      Individual child2 = new Individual(parents.poll());

      //random crosspoints
      int cross1 = rand.nextInt(child1.size);
      int cross2 = rand.nextInt(child2.size);

      //diff used to compute new size
      int diff1 = child1.size - cross1;
      int diff2 = child2.size - cross2;

      //exchange next node references at crosspoints between parents
      Node temp1 = child1.first; 
      for (int i = 0; i < cross1; i++) {
        temp1 = temp1.next;
      }

      Node temp2 = child2.first;
      for (int i = 0; i < cross2; i++) {
        temp2 = temp2.next;
      }

      if (temp1 == null || temp2 == null) continue;

      //save reference to detached node to avoid floating
      Node savedTemp1Next = temp1.next == null ? null : new Node(temp1.next);
      temp1.next = temp2.next;
      temp2.next = savedTemp1Next;

      //update sizes of children
      child1.size = cross1 + diff2;
      child2.size = cross2 + diff1;

      //add to offspring
      children.add(child1);
      children.add(child2);
    
    }

    if (parents.peek() != null && !parents.peek().isEmpty()) {
      Individual last = parents.poll();
      mutate(last);
      children.add(last);  
    }
    
    return children;
}

//Count inversions and ascending runs
/*--------------------------------------------------------------------------------------------*/


private static int merge(double[] sublist, double[] left, double[] right) {
    int i = 0, j = 0, count = 0;
    while (i < left.length || j < right.length) {
        if (i == left.length) {
            sublist[i+j] = right[j];
            j++;
        } else if (j == right.length) {
            sublist[i+j] = left[i];
            i++;
        } else if (left[i] <= right[j]) {
            sublist[i+j] = left[i];
            i++;                
        } else {
            sublist[i+j] = right[j];
            count += left.length-i;
            j++;
        }
    }
    return count;
}

private static int countInversions(double[] sublist) {
    if (sublist.length < 2)
        return 0;

    int m = (sublist.length + 1) / 2;
    double left[] = Arrays.copyOfRange(sublist, 0, m);
    double right[] = Arrays.copyOfRange(sublist, m, sublist.length);
    double arrayCopy[] = Arrays.copyOfRange(sublist, 0, sublist.length);

    return countInversions(left) + countInversions(right) + merge(arrayCopy, left, right);
}


private static int numAsc(double[] sublist){
  int numSubgraphs = 0;
  for (int i = 0; i < 100; i++){
    while((i<99) && sublist[i] <= sublist[i + 1]){
      i++;
    }
    numSubgraphs++;
  }
  return numSubgraphs;
}

}
