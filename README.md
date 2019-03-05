# metaSort
We explore the features and constraints of Genetic Algorithms by applying this evolutionary strategy to
a problem of optimizing a sorting algorithm. Traits in the sorting algorithm are represented as swap
operations between elements at two distinct positions within a list.
The algorithm features Fitness Proportionate (Roulette Wheel) selection, and single-point crossover.
This implementation exhibits convergences to local optima for some numerical distributions.
Future implementations are to include diversity considerations, using Fitness Uniform Selection,
as well as niching techniques (following DeJong, 1975) to allow for the pursuit of several peaks simultaneously.
