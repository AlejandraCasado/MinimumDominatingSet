package heuristic.algorithm;

import heuristic.Main;
import heuristic.constructive.IConstructive;
import heuristic.localSearch.ILocalSearch;
import heuristic.structure.Instance;
import heuristic.structure.RandomManager;
import heuristic.structure.Result;
import heuristic.structure.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class IG implements IAlgorithm{

    private final IConstructive constructive;
    private final ILocalSearch localSearch;

    private int maxItersWithoutImprove=200;
    private float beta=0.2f;
    private final boolean randomDestruct=true;
    private final boolean randomConstruct=false;

    public IG(IConstructive constructive, ILocalSearch localSearch) {
        this.constructive = constructive;
        this.localSearch=localSearch;
    }

    @Override
    public Result execute(Instance instance, boolean draw) {

        System.out.println(instance.getName());

        long initialTime=System.currentTimeMillis();
        long totalTime = System.currentTimeMillis() - initialTime;
        float secs = totalTime / 1000f;
        Result result=new Result(instance.getName());

        Solution solution=firstSol(instance);
        int numElemsToDestruct=(int)Math.ceil(beta*solution.getSelectedNotSupportNodes().size());

        int numItersWithoutImprove=0;
        int bestOF=solution.evaluate();
        while(numItersWithoutImprove<maxItersWithoutImprove & secs<= 600){
            destruct(solution, numElemsToDestruct);
            construct(solution);
            executeLocalSearch(solution,instance);
            if(solution.evaluate()>=bestOF){
                numItersWithoutImprove++;
            }
            else{
                numItersWithoutImprove=0;
                bestOF=solution.evaluate();
            }
            totalTime = System.currentTimeMillis() - initialTime;
            secs = totalTime / 1000f;
        }

        totalTime = System.currentTimeMillis() - initialTime;
        secs = totalTime / 1000f;

        result.add("Time", secs);
        result.add("OF", bestOF);
        System.out.println(secs+"\t"+bestOF);
        return result;
    }

    private Solution firstSol(Instance instance){
        Solution solution=constructive.construct(instance);
        executeLocalSearch(solution,instance);
        return solution;
    }

    private void executeLocalSearch(Solution solution,Instance instance){
        if(localSearch!=null){
            solution=localSearch.execute(solution,instance);
        }
    }

    private void destruct(Solution solution, int numElemsToDestruct){

        if(randomDestruct) destructRandom(solution,numElemsToDestruct);
        else destructGreedy(solution,numElemsToDestruct);

    }

    private void construct(Solution solution){

        if(randomConstruct) constructRandom(solution);
        else constructGreedy(solution);
    }

    private void destructRandom(Solution solution, int numElemsToDestruct){
        Random rnd=RandomManager.getRandom();
        List<Integer> selectedList=new ArrayList<>(solution.getSelectedNotSupportNodes());
        Collections.shuffle(selectedList,rnd);
        for(int i=0;i<numElemsToDestruct;i++){
            solution.removeNode(selectedList.get(i));
        }
    }

    private void destructGreedy(Solution solution, int numElemsToDestruct){
        int worstNode;
        for(int i=0;i<numElemsToDestruct;i++){
            worstNode=solution.getWorstNodeNew();
            solution.removeNode(worstNode);
        }
    }

    private void constructRandom(Solution solution){
        Random rnd=RandomManager.getRandom();
        while(!solution.isFeasible()){
            solution.addNode(rnd.nextInt(solution.getInstance().getNumNodes()));
        }
    }

    private void constructGreedy(Solution solution){
        int bestNode;
        while(!solution.isFeasible() && !solution.getUnSelectedNodes().isEmpty()){
            bestNode= solution.getBestNextNode();
            solution.addNode(bestNode);
        }
    }

    public String toString() {

        String localSearchName=localSearch!=null? "_" + localSearch.toString():"";
        String typeName= (randomDestruct ? "_Random":"_Greedy")+(randomConstruct ? "Random":"Greedy");

        return this.getClass().getSimpleName()+typeName+"("+beta+", "+maxItersWithoutImprove+")"+localSearchName;
    }

}
