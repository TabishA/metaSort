//Population.java
//Tabish Ashfaq
package metasort;


  //Population is represented as an ArrayList of all individuals
  public class Population{

    Individual fittest = new Individual();

    List<Individual> allIndividuals = new ArrayList<>();

    double totalFitness;

    int getSize(){
      return allIndividuals.size();
    }

    void addIndividual(Individual individual){
      allIndividuals.add(individual);
      totalFitness = totalFitness + individual.fitness;
    }

    void replace(Individual previous, Individual next){
      totalFitness = totalFitness - previous.fitness;
      allIndividuals.remove(previous);
      this.addIndividual(next);
    }

    Individual getIndividual(int i){
      return allIndividuals.get(i);
    }

    void removeIndividual(Individual individual){
      allIndividuals.remove(individual);
      totalFitness = totalFitness - individual.fitness;
    }

    void removeIndividual(int i){
      allIndividuals.remove(i);
    }

    double averageFitness(){
      return (double) totalFitness/allIndividuals.size();
    }

  }