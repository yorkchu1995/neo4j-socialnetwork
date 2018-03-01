package cn.edu.hitsz.util;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Main {
    public static String dbPath = "/Users/YorkChu/Documents/Neo4j/graph.db";

    public static RelationshipType know = DynamicRelationshipType.withName( "Know" );
    public static RelationshipType join = DynamicRelationshipType.withName("Join");

    public static Label ACTIVITY = DynamicLabel.label("Activity");
    public static Label PERSON = DynamicLabel.label("Person");

//    public static getTime()
}
