package exact;

import heuristic.structure.Result;
import scala.util.control.TailCalls;

class Call extends GRBCallback{
    private double lastiter;
    private double lastnode;

    public Call(){
        lastiter = lastnode = -GRB.INFINITY;
    }

    protected void callback(){
        if (where == GRB.CB_POLLING) {
            System.out.println("Polling");
        } else if (where == GRB.CB_PRESOLVE) {
            System.out.println("Presolving");
        }else if(where==GRB.CB_SIMPLEX){
            System.out.println("Simplex");
        }else if(where==GRB.CB_MIP){
            System.out.println("General MIP");
        }else if(where==GRB.CB_MIPSOL){
            System.out.println("MIP solution");
        }else if(where==GRB.CB_MIPNODE){
            System.out.println("MIP node");
        }else if(where==GRB.CB_BARRIER){
            System.out.println("Barrier");
        }
    }

}

public class Procedure {
    private double timeLimit;

    /*private void callback(){
        System.out.println();
    }*/

    public Procedure(double timeLimit){
        this.timeLimit=timeLimit;
    }
    public Result execute(Instance instance) {
        int n = instance.getNumNodes();
        System.out.print(instance.getName()+"\t");
        Result r = new Result(instance.getName());
        try {
            GRBEnv env = new GRBEnv("log_edp.txt"); //en ese fichero se mete todo lo que pase durante la ejecución
            env.set(GRB.DoubleParam.TimeLimit, timeLimit); //tiempo maximo que dejamos ejecutar, en segundos
            env.set(GRB.IntParam.LogToConsole, 0); //este valor a 0 para que no aparezca información por consola
            // Starts writing nodes to disk when reaching XXX M
            //            env.set(GRB.DoubleParam.NodefileStart, 0.05); //para volcar a disco, sirve para instancias muy grandes
            // Reduce the number of threads to reduce memory usage
            //env.set(GRB.IntParam.Threads, 1); //para limitar la ejecución a 1 hilo
            // Presolve 0 off 1 conservative 2 aggresive
            //            env.set(GRB.IntParam.Presolve, 0); //si pones a 0 gurobi no hace calculos previos a la ejecución
            GRBModel model = new GRBModel(env);
            //Call cb   = new Call();
            //model.setCallback(cb);
            // VARIABLES
            GRBVar[] x = new GRBVar[n];
            for (int i = 0; i < n; i++) {
                x[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x["+i+"]"); //primer valor: cota inferior
                // segundo:cota superior, tercer: no se sabe, cuarto: tipo de variable, quinto: nombre unico para la variable
            }

            // OBJECTIVE FUNCTION
            GRBLinExpr objFunc = new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                objFunc.addTerm(1.0, x[i]); //guardamos en objfunc la suma de todos los x[i] multiplicados por el
                //coeficiente 1, si quiero restar se pone -1

            }
            model.setObjective(objFunc, GRB.MINIMIZE); //aquí se dice si queremos minimizar o maximizar el valor de objfunc

            // CONSTRAINTS
            for (int j = 0; j < n; j++) {
                GRBLinExpr r1 = new GRBLinExpr();
                for (int i = 0; i < n; i++){
                    if(instance.getAdyacencyList()[j].contains(i))
                        r1.addTerm(1.0, x[i]);
                }
                r1.addTerm(1.0,x[j]);
                model.addConstr(r1, GRB.GREATER_EQUAL, 1, "r"+j);
            }

            model.update();
            long totalTime=System.currentTimeMillis();
            model.optimize();

            int status = model.get(GRB.IntAttr.Status);
            System.out.print(status+"\t");
            totalTime = System.currentTimeMillis() - totalTime;
            float secs = totalTime / 1000f;
            System.out.print(secs+"\t");
            r.add("Time (s)", secs);
            if (status != GRB.INFEASIBLE) {
                double of = model.get(GRB.DoubleAttr.ObjVal);
                System.out.println(of);
                r.add("OF", (float) of);
            } else {
                System.out.println(-1);
                r.add("OF", -1);
            }
            r.add("Status", status);

            if (status != GRB.INFEASIBLE) {
                Solution sol = new Solution(instance);
                for (int i = 0; i < n; i++) {
                    int active = (int) x[i].get(GRB.DoubleAttr.X);
                    if(active==1) sol.addNode(i);
                }
            }

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }
        return r;
    }
}
