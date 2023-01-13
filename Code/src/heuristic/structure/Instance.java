package heuristic.structure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Instance {

    private String path;
    private String name;

    private int numNodes;
    private List<Integer>[] adyacencyList;
    private Set<Integer> leavesNodes;

    private Set<Integer> unSelectedNodes;

    private Set<Integer> supportNodes;
    private int optimum;
    private final boolean supportAndLeafNodes=true;

    public Instance(String path){
        this.path = path;
        this.name = path.substring(Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"))+1).replace(".txt", "");

        readInstance();
    }

    public Instance(int numNodes, List<Integer>[] adyacencyList, Set<Integer> unSelectedNodes){
        this.numNodes=numNodes;
        this.adyacencyList=adyacencyList;
        this.unSelectedNodes=new HashSet<>(unSelectedNodes);
        setSupportNodes();
    }

    private void readInstance(){
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            String[] lineContent;
            line = br.readLine();
            lineContent = line.split(" ");

            numNodes = Integer.parseInt(lineContent[0]);
            unSelectedNodes=new HashSet<>(numNodes);
            br.readLine(); //para leer la linea vacia

            adyacencyList=new ArrayList [numNodes];

            //String line;
            //boolean empty=false;
            //int count=0;
            //while(!empty){
            //    line=br.readLine();
            //    empty=line==null;
            //    count+=1;
            //}
            //System.out.println(count);

            for (int i = 0; i< numNodes; i++){
                adyacencyList[i]=new ArrayList<>(numNodes);
                unSelectedNodes.add(i);
                line = br.readLine();
                lineContent = line.split(" ");
                for (int j=0; j<numNodes;j++){
                    if (Integer.parseInt(lineContent[j])==1) {
                        adyacencyList[i].add(j);
                    }
                }
            }
            br.readLine();
            optimum = Integer.parseInt(br.readLine());

        } catch (FileNotFoundException e){
            System.out.println(("File not found " + path));
        } catch (IOException e){
            System.out.println("Error reading line");
        }

        setSupportNodes();
    }


    private void readInstanceNew(){
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            String[] lineContent;

            br.readLine();
            line= br.readLine();
            lineContent = line.split("\\s");

            numNodes = Integer.parseInt(lineContent[0]);
            int numEdges = Integer.parseInt(lineContent[2]);
            unSelectedNodes=new HashSet<>(numNodes);
            adyacencyList = new ArrayList[numNodes];

            for(int i=0; i<numNodes;i++){
                adyacencyList[i]=new ArrayList<>();
                unSelectedNodes.add(i);
            }

            for (int i = 0; i< numEdges; i++){
                line = br.readLine();
                lineContent=line.split("\\s");

                int node1=Integer.parseInt(lineContent[0]);
                int node2=Integer.parseInt(lineContent[1]);

                adyacencyList[node1-1].add(node2-1);
                adyacencyList[node2-1].add(node1-1);
            }

        } catch (FileNotFoundException e){
            System.out.println(("File not found " + path));
        } catch (IOException e){
            System.out.println("Error reading line");
        }
        setSupportNodes();
    }

    private void setSupportNodes(){
        supportNodes=new HashSet<>();
        leavesNodes=new HashSet<>();
        for (int i =0;i< numNodes;i++){
            if(adyacencyList[i].size()==1 && supportAndLeafNodes){
                int neighbour=adyacencyList[i].get(0);
                if(!leavesNodes.contains(neighbour)) {
                    leavesNodes.add(i);
                    supportNodes.add(neighbour);
                }
                unSelectedNodes.remove(neighbour);
                unSelectedNodes.remove(i);
            }
            else if(adyacencyList[i].size()==0 && supportAndLeafNodes){
                supportNodes.add(i);
            }
        }
    }

    public Set<Integer> getSupportNodes() {
        return supportNodes;
    }

    public String getName(){
        return name;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public List<Integer>[] getAdyacencyList() {
        return adyacencyList;
    }

    public Set<Integer> getLeavesNodes() {
        return leavesNodes;
    }

    public Set<Integer> getUnSelectedNodes() {
        return unSelectedNodes;
    }

}
