package cn.edu.hitsz.servlet;

import cn.edu.hitsz.util.Main;
import org.apache.commons.lang3.ArrayUtils;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@WebServlet(name="FirstQuery",urlPatterns = "/FirstQuery")
public class QueryServlet extends HttpServlet {
    public static String[] identifyGroup(String[] split, GraphDatabaseService graphDB) {
        for(int i = 0; i < split.length-1; i++){
            if(split[i].length() > 10)
                continue;
            try(Transaction tx = graphDB.beginTx()) {
                Node startNode = graphDB.getNodeById(Long.parseLong(split[i]));
                TraversalDescription traversalDescription = graphDB.traversalDescription()
                        .depthFirst()
                        .relationships(Main.know, Direction.BOTH);
                ResourceIterable<Node> nodes = traversalDescription.traverse(startNode).nodes();
                for(int j = i + 1; j < split.length; j++){
                    for(Node n : nodes){
                        if(Long.parseLong(split[j]) == n.getId()) {
//                            System.out.println("same group!");
                            split = ArrayUtils.remove(split, i);
                            break;
                        }
                    }
                }
                tx.success();
            }
        }
        return split;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html,charset=utf-8");

        File file = new File("/Users/YorkChu/Documents/Neo4j/graph.db");
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file);

        Long stime = System.currentTimeMillis();

        Map<String,List<String[]>> resultMap = new HashMap<>();
        Map<String,List<String[]>> tempMap = new HashMap<>();
        int flag = 0;

        String parameter = request.getParameter("personId");
        String[] split = parameter.split(",");
        split = identifyGroup(split, graphDB);
        for(String i : split){
            try(Transaction tx = graphDB.beginTx()) {
                Node startNode = graphDB.getNodeById(Long.parseLong(i));
                //find nodes in the join
                TraversalDescription traversalDescription = graphDB.traversalDescription()
                        .depthFirst()
                        .relationships(Main.know,Direction.BOTH);

                ResourceIterable<Node> nodes = traversalDescription.traverse(startNode).nodes();

                //find the activity for the node in join
                for(Node n : nodes){
                    TraversalDescription traversalDescription1 = graphDB.traversalDescription()
                            .depthFirst()
                            .relationships(Main.join, Direction.BOTH)
                            .evaluator(Evaluators.excludeStartPosition());
                    ResourceIterable<Node> nodes2 = traversalDescription1.traverse(n).nodes();

                    for(Node n2 : nodes2){
                        String s1 = (String) n2.getProperties("locationID").get("locationID");
                        String s2 = (String) n2.getProperties("startTime").get("startTime");
                        String s3 = (String) n2.getProperties("endTime").get("endTime");
//                        Map<String,String> map = new HashMap<>();
                        String[] arr = new String[2];
                        List<String[]> list = new ArrayList<>();
                        if(flag == 0){
                            if(!resultMap.containsKey(s1)){
                                arr[0] = s2;
                                arr[1] = s3;
                                list.add(arr);
                                resultMap.put(s1,list);
                            }
                            else{
                                arr[0] = s2;
                                arr[1] = s3;
                                list = resultMap.get(s1);
                                list.add(arr);
                            }
                        }
                        else{
                            /**
                             * compute the overlapping period
                             */
                            if(resultMap.containsKey(s1)){
                                list = resultMap.get(s1);
                                for(int j = 0; j < list.size(); j++){
                                    arr = list.get(j);
                                    String start = arr[0];
                                    String end = arr[1];
                                    String[] newarr = new String[2];
//                                    if ((s2.compareTo(start) > 0) && (s3.compareTo(end) < 0)){
//                                        newarr[0] = s2;
//                                        newarr[1] = s3;
//                                        list.remove(arr);
//                                        list.add(newarr);
//                                    }
//                                    else if (s2.compareTo(start)>0 && s3.compareTo(end)>0){
//                                        newarr[0] = s2;
//                                        newarr[1] = end;
//                                        list.remove(arr);
//                                        list.add(newarr);
//                                    }
//                                    else if (s2.compareTo(start)<0 && s3.compareTo(end)<0){
//                                        newarr[0] = start;
//                                        newarr[1] = end;
//                                        list.remove(arr);
//                                        list.add(newarr);
//                                    }
                                    String max, min;
                                    if((s3.compareTo(start) > 0) && (end.compareTo(s2) > 0)){
                                        if(s2.compareTo(start) > 0)
                                            max = s2;
                                        else
                                            max = start;
                                        if(s3.compareTo(end) > 0)
                                            min = end;
                                        else
                                            min = s3;
                                        newarr[0] = max;
                                        newarr[1] = min;
                                        list.remove(j);
                                        List<String[]> list2  = new ArrayList<>();
                                        if(tempMap.containsKey(s1)){
                                            list2 = tempMap.get(s1);
                                            list2.add(newarr);
                                        }else{
                                            list2.add(newarr);
                                            tempMap.put(s1,list2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tx.success();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(flag > 0){
                resultMap.clear();
                Iterator it = tempMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    resultMap.put(key, tempMap.get(key));
                }
                tempMap.clear();
            }
            flag++;
        }
        Long etime = System.currentTimeMillis();
        System.out.println((etime-stime)+"ms");
        System.out.println("query completed");
        System.out.println(resultMap.size());
//        for (String key: resultMap.keySet()){
//            List<String[]> list = resultMap.get(key);
//            System.out.println(list.size());
//            for (String[] arr : list){
//                System.out.println(key + "\t" + arr[0] + "\t" + arr[1]);
//            }
//            break;
//        }

        graphDB.shutdown();
        request.setAttribute("result", resultMap);
        RequestDispatcher dispatcher = request.getRequestDispatcher("success1.jsp");
        dispatcher.forward(request,response);
    }
}
