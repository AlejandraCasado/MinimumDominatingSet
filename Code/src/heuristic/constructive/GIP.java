package heuristic.constructive;


import heuristic.structure.Instance;
import heuristic.structure.Solution;

public class GIP implements IConstructive {

    @Override
    public Solution construct(Instance instance) {

        Solution solution=new Solution(instance);

        for (int supportNode:instance.getSupportNodes()){
            solution.addNode(supportNode);
        }

        while(!solution.isFeasible()){
            int selectedNode=solution.getBestNextNode();
            solution.addNode(selectedNode);
        }

        return solution;
    }


    public String toString() {
        return this.getClass().getSimpleName();
    }
}
