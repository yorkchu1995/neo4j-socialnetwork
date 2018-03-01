package cn.edu.hitsz.test;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.TraversalDescription;

import java.io.File;


public class groupTest {
    public static void main(String[] args){
        File file = new File("/Users/YorkChu/Documents/Neo4j/graphdb");
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file);

        RelationshipType know = DynamicRelationshipType.withName( "Know" );
        try(Transaction tx = graphDB.beginTx()){
            Node startNode = graphDB.getNodeById(7l);

//沿着POINT_TO关系广度优先遍历
            TraversalDescription traversalDescription = graphDB.traversalDescription()
                    .relationships(know, Direction.BOTH)
                    .breadthFirst();

//从节点1开始遍历
            Iterable<Node> nodes = traversalDescription.traverse(startNode).nodes();

            int i=0;
            for(Node n : nodes){
                i++;
                System.out.print(n.getId() +" -> \n");
                if(i == 200){
                    break;
                }
            }
            tx.success();
        }

        graphDB.shutdown();
    }

}
