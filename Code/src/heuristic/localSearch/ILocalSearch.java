package heuristic.localSearch;

import heuristic.structure.Instance;
import heuristic.structure.Solution;

public interface ILocalSearch {

    Solution execute(Solution sol, Instance instance);
    String toString();

}
