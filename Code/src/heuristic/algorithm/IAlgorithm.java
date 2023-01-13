package heuristic.algorithm;

import heuristic.structure.Instance;
import heuristic.structure.Result;

public interface IAlgorithm {
    public Result execute(Instance instance, boolean draw);
}
