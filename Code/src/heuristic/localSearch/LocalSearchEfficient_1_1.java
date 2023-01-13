package heuristic.localSearch;

import heuristic.Main;
import heuristic.structure.Instance;
import heuristic.structure.RandomManager;
import heuristic.structure.Solution;

import java.util.*;

public class LocalSearchEfficient_1_1 implements ILocalSearch{

    private boolean allowEqualSolutions=false;
    @Override
    public Solution execute(Solution sol, Instance instance) {

        boolean improve = true;

        while(improve){
            improve=checkImprove(sol,instance);
            if(sol.evaluate()<= Main.best){
                break;
            }
        }

        return sol;
    }

    private boolean checkImprove(Solution sol, Instance instance){

        List<Integer> copySelected=new ArrayList<>(sol.getSelectedNotSupportNodes());
        Collections.shuffle(copySelected, RandomManager.getRandom());
        Solution lastSol=new Solution(instance);
        for (int i=0;i<copySelected.size();i++){

            int nodeRem= copySelected.get(i);
            int nodeNew=selectElemToAdd(nodeRem,instance, sol);

            if(nodeNew==-1){
                continue;
            }
            else{
                int of=sol.evaluate();
                //lastSol.copy(sol); //nuevo
                sol.removeNode(nodeRem);
                sol.addNode(nodeNew);
                sol.checking();
                if(sol.evaluate()<of){
                    return true;
                }
                //sol.copy(lastSol);//nuevo
            }
        }
        return false;
    }

    private int selectElemToAdd(int node,Instance instance, Solution solution){

        Set<Integer> neighbours = null;
        if(solution.getWatchers()[node].size()==1){
            neighbours=new HashSet<>(instance.getAdyacencyList()[node]);
        }

        for(int neighbour : instance.getAdyacencyList()[node]){
            if(solution.getWatchers()[neighbour].size()==1){
                if (neighbours == null) {
                    neighbours = new HashSet<>(instance.getAdyacencyList()[neighbour]);
                } else {
                    neighbours.retainAll(instance.getAdyacencyList()[neighbour]);
                }
            }
        }
        if(neighbours!=null) neighbours.remove(node);

        return neighbours==null || neighbours.isEmpty()  ? -1 :neighbours.iterator().next();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
