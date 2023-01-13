package heuristic.constructive;

import heuristic.structure.Instance;
import heuristic.structure.Solution;

public interface IConstructive {
    Solution construct(Instance instance);
    String toString();
}
