package cn.edu.hitsz.servlet;

import cn.edu.hitsz.util.Main;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.TraversalDescription;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "SecondQuery",urlPatterns = "/SecondQuery")
public class QueryServletSec extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html,charset=utf-8");

        String locationId = request.getParameter("locationId");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        File file = new File("/Users/YorkChu/Documents/Neo4j/graph.db");
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file);

        RelationshipType group = DynamicRelationshipType.withName("Join");

        Set<Long> nodesSet = new HashSet<>();
        Transaction tx = graphDB.beginTx();
        try {
            List<String> startNodes = new ArrayList<>();

//            String cql = "Match (n:Activity{locationID:'" + locationId + "'}) where n.startTime > '" + startTime + "' and n.endTime < '" + endTime +"' return n.id";
//
//            List<String> startnodes = new ArrayList<>();
//            System.out.println(cql);
//            Result result = graphDB.execute(cql);
//            Long starttime = System.currentTimeMillis();
//            while (result.hasNext())
//            {
//                startnodes.add((String) result.next().get("n.id"));
//            }

            Long timeMillis = System.currentTimeMillis();
            ResourceIterator<Node> tempNodes = graphDB.findNodes(Main.ACTIVITY, "locationID", locationId);
            while (tempNodes.hasNext()){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Node node = tempNodes.next();
                String start = (String) node.getProperties("startTime").get("startTime");
                Date d = sdf.parse(start);
                String end = (String) node.getProperties("endTime").get("endTime");
                Date d2 = sdf.parse(end);
                if (d.after(sdf.parse(startTime)) && d2.before(sdf.parse(endTime))){
                    startNodes.add((String) node.getProperties("id").get("id"));
                }
            }

            System.out.println("s"+startNodes.size());
            Long endtime = System.currentTimeMillis();

//            System.out.println("process1 completed");
            for(String startNode : startNodes){
                Node startnode = graphDB.getNodeById(Long.parseLong(startNode));

                TraversalDescription traversalDescription = graphDB.traversalDescription()
                        .depthFirst()
                        .relationships(group,Direction.BOTH);

                ResourceIterable<Node> nodes = traversalDescription.traverse(startnode).nodes();

                for(Node node : nodes){
                    long l = node.getId();
                    if (!nodesSet.contains(l))
                        nodesSet.add(l);
                }
            }
            System.out.println("query for 33744 ms");
            Long curTime = System.currentTimeMillis();
            System.out.println("all for 34310 ms");
            System.out.println("person size: " + nodesSet.size());
//            System.out.println("head 10:");
//            int i = 0;
//            for (long l: nodesSet){
//                System.out.println(l);
//                i++;
//                if(i == 10)
//                    break;
//            }

            tx.success();
        }
        catch (Exception e){
            tx.failure();
        }

        graphDB.shutdown();
        request.setAttribute("result", nodesSet);

        RequestDispatcher dispatcher = request.getRequestDispatcher("success2.jsp");
        dispatcher.forward(request,response);
    }
}

