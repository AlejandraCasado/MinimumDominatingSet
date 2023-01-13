package exact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {

    private final Set<Integer> selectedNodes=new HashSet<>();
    private final Set<Integer> selectedNotSupportNodes =new HashSet<>();

    private int numCovered=0;
    private Set<Integer>[] watchers;
    private Instance instance;


    public Solution(Instance instance) {
        this.watchers=new HashSet[instance.getNumNodes()];

        for (int i=0;i<instance.getNumNodes();i++){
            this.watchers[i]=new HashSet<>();
        }
        this.instance=instance;
    }

    public boolean isFeasible(){
        return numCovered==instance.getNumNodes();
    }

    public boolean checking(){

        boolean removed=false;
        List<Integer> selectedList=new ArrayList<>(selectedNotSupportNodes);
        for(int select : selectedList){

            boolean remove=true;
            if(watchers[select].size()==1){
                continue;
            }

            for(int elem : instance.getAdyacencyList()[select]){
                if(watchers[elem].size()==1){
                    remove=false;
                    break;
                }
            }
            if(remove){
                removed=true;
                removeNode(select);
            }
        }
        return removed;
    }

    public void addNode(int node){
        selectedNodes.add(node);
        instance.getUnSelectedNodes().remove(node);
        addWatcher(node);
        if(!instance.getSupportNodes().contains(node)){
            selectedNotSupportNodes.add(node);
        }

    }

    private void addWatcher(int selectedNode){
        if(watchers[selectedNode].isEmpty()){
            numCovered++;
        }
        watchers[selectedNode].add(selectedNode);

        for (int neighbour: instance.getAdyacencyList()[selectedNode]){
            if(watchers[neighbour].isEmpty()){
                numCovered++;
            }
            watchers[neighbour].add(selectedNode);
        }

    }

    public void removeNode(int node){ //hay que asegurarse de que el nodo no esta en el set de support antes de hacer esto
        selectedNodes.remove(node);
        instance.getUnSelectedNodes().add(node);
        removeWatcher(node);
        selectedNotSupportNodes.remove(node);
    }

    private void removeWatcher(int selectedNode){
        watchers[selectedNode].remove(selectedNode);
        if(watchers[selectedNode].isEmpty()){
            numCovered--;
        }

        for (int neighbour: instance.getAdyacencyList()[selectedNode]){
            watchers[neighbour].remove(selectedNode);
            if(watchers[neighbour].isEmpty()){
                numCovered--;
            }
        }
    }

    public void copy(Solution sol){

        this.instance=sol.getInstance();
        this.selectedNotSupportNodes.addAll(sol.getSelectedNotSupportNodes());
        this.selectedNodes.addAll(sol.getSelectedNodes());
        this.watchers=sol.watchers.clone();
        this.numCovered=sol.getNumCovered();

    }

    public int getBestNextNode(){
        int bestCount=-1;
        int bestNode=-1;

        for (int i=0; i<instance.getNumNodes(); i++){
            if(!getSelectedNodes().contains(i)){
                int count=0;
                for (int neighbour: instance.getAdyacencyList()[i]){
                    if(getWatchers()[neighbour].size()==0){
                        count++;
                    }
                }
                if(bestCount<count && !instance.getLeavesNodes().contains(i)){
                    bestCount=count;
                    bestNode=i;
                }
            }
        }

        return bestNode;
    }

    public int getWorstNode(){
        int worstNode=-1;
        int totalMinWatchers=0x3f3f3f;

        for (int i: getSelectedNotSupportNodes()){
            int minWatchers=0x3f3f3f;
            for (int neighbour: instance.getAdyacencyList()[i]){
                if(minWatchers>getWatchers()[neighbour].size()){
                    minWatchers=getWatchers()[neighbour].size();
                }
            }
            if(totalMinWatchers>minWatchers){
                worstNode=i;
                totalMinWatchers=minWatchers;
            }
        }

        return worstNode;
    }

    public int getWorstNodeNew(){
        int worstNode=-1;
        int totalMaxWatchers=0;

        for (int i: getSelectedNotSupportNodes()){
            int minWatchers=0x3f3f3f;
            for (int neighbour: instance.getAdyacencyList()[i]){
                if(minWatchers>getWatchers()[neighbour].size()){
                    minWatchers=getWatchers()[neighbour].size();
                }
            }
            if(totalMaxWatchers<minWatchers){
                worstNode=i;
                totalMaxWatchers=minWatchers;
            }
        }

        return worstNode;
    }

    public int evaluate(){
        return selectedNodes.size();
    }

    public int getNumCovered() {
        return numCovered;
    }

    public Set<Integer>[] getWatchers() {
        return watchers;
    }

    public Set<Integer> getSelectedNodes() {
        return selectedNodes;
    }

    public Set<Integer> getSelectedNotSupportNodes() {
        return selectedNotSupportNodes;
    }

    public Instance getInstance() {
        return instance;
    }
}
