package heuristic;

import heuristic.algorithm.IG;
import heuristic.algorithm.IAlgorithm;
import heuristic.constructive.GIP;
import heuristic.localSearch.*;
import heuristic.structure.Instance;
import heuristic.structure.RandomManager;
import heuristic.structure.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    final static String pathFolder = "./Instances";

    final static boolean readAllFolders = false;
    final static boolean readAllInstances = true;

    final static String folderIndex = "LiteratureInstances";
    final static String instanceIndex = "astro-ph.txt";

    static List<String> foldersNames;
    static List<String> instancesNames;
    static String instanceFolderPath;

    static IAlgorithm algorithm;
    static GIP constructive=new GIP();
    static LocalSearchEfficient_1_1 localSearchEfficient_1_1=new LocalSearchEfficient_1_1();

    public static void main(String[] args) {
        algorithm=new IG(constructive, localSearchEfficient_1_1);
        execute();
    }

    private static void execute()  {
        File file=new File(pathFolder);
        instanceFolderPath = file.getPath() + "/";
        printHeaders("./results/"+algorithm.toString()+".csv");
        readData();
    }

    private static void printHeaders(String path) {
        try (PrintWriter pw = new PrintWriter(path)) {
            pw.print("Instance;Time;OF");
            pw.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void printResults(String path, Result result, String name) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path,true))) {

            pw.print(name+";");
            int nElems=result.getKeys().size();
            for (int i = 0; i < nElems; i++) {
                pw.print(result.get(i));
                if (i < nElems-1) pw.print(";");
            }
            pw.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void readData(){
        foldersNames = Arrays.asList(new File(pathFolder).list());

        if(readAllFolders) readAllFolders();
        else if (foldersNames.contains(folderIndex)) readFolder(folderIndex);
        else System.out.println("Folder index exceeds the bounds of the array");

    }

    private static void readAllFolders(){
        String [] folders =new File(pathFolder).list();

        for(String fileName : folders){
            readFolder(fileName);
        }
    }

    private static void readFolder(String fileName){
        File file;
        file=new File(pathFolder+"/"+fileName);
        if(!fileName.startsWith(".") && !fileName.startsWith("..") && file.isDirectory()){
            instancesNames = Arrays.asList(file.list());
            instanceFolderPath = file.getPath() + "/";
            if(readAllInstances) readAllInstances();
            else if (instancesNames.contains(instanceIndex)) readInstance(instanceIndex);
            else System.out.println("Instance index exceeds the bounds of the array");
        }
    }

    private static void readAllInstances(){
        for(String instanceName : instancesNames){
            if(!instanceName.startsWith(".") && !instanceName.startsWith(".."))
                readInstance(instanceName);
        }
    }

    private static void readInstance(String instanceName){
        System.out.println(instanceName);
        Instance instance=new Instance(instanceFolderPath +instanceName);
        RandomManager.setSeed(13);
        Result result= algorithm.execute(instance,false);
        printResults("./results/"+algorithm.toString()+".csv", result, instanceName);
    }

}
