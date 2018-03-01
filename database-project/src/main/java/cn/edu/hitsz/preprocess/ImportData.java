package cn.edu.hitsz.preprocess;
/**
 * Created by YorkChu on 2017/10/22.
 */


import cn.edu.hitsz.util.Main;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;



public class ImportData {
    public static void insert() throws Exception {


        //file path
        File db = new File(Main.dbPath);
        File relation = new File("/Users/YorkChu/Documents/data/relationship.csv");
        File activity = new File("/Users/YorkChu/Documents/data/shuffle_activity_record.csv");
        BufferedReader br;
        Map<String, String> config = new HashMap<>();

        config.put("dbms.pagecache.memory", "1g" );
        config.put("dbms.memory.heap.initial_size", "1024m");
        config.put("dbms.memory.heap.max_size", "2048m");
        config.put("cache_type", "none");
        config.put("use_memory_mapped_buffers", "true");

        BatchInserter inserter = BatchInserters.inserter(db, config);
        BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(inserter);

        long sysStartTime = System.currentTimeMillis();
        try{
            //load relationship
            long edgeCounter2=0, vertexCounter2=0;
            Label personLabel = DynamicLabel.label("Person");
            RelationshipType know = DynamicRelationshipType.withName( "Know" );

            String fpersonId, spersonId;
            Long fperson, sperson;
            Map<String, Object> properties;

            String line;
            br = new BufferedReader(new FileReader(relation));
            line = br.readLine();
            line = br.readLine();
            while (line != null) {
                String []split = line.split(",");
                fpersonId = split[0];
                fperson = Long.parseLong(fpersonId);
                if (!inserter.nodeExists(fperson)) {
//                    properties = new HashMap<>();
//                    properties.put("id", fperson);
                    inserter.createNode(fperson, null, personLabel);
//                    index2.add(fperson, properties);
                    vertexCounter2++;
                }
                spersonId = split[1];
                sperson = Long.parseLong(spersonId);
                if (!inserter.nodeExists(sperson)) {
                    inserter.createNode(sperson, null, personLabel);
                    vertexCounter2++;
                }
                inserter.createRelationship(fperson, sperson, know,null);
                edgeCounter2++;
                line = br.readLine();
                System.out.println("the"+vertexCounter2+"th node created");
                System.out.println("the"+edgeCounter2+"th edge created");
                if (edgeCounter2 % 100000 == 0) {
//                    index2.flush();
                    System.out.println(edgeCounter2 + " records");
                }
            }
//            index2.flush();
            System.out.println("relationship inserted success.");

            //load activity_record
            BatchInserterIndex index = indexProvider.nodeIndex("activity-index",
                    MapUtil.stringMap("type", "exact"));
            index.setCacheCapacity("id",1010);

            long edgeCounter=0, vertexCounter=0;
            Label activityLabel = DynamicLabel.label("Activity");
            RelationshipType join = DynamicRelationshipType.withName("Join");

            String ID, locationID, startTime, endTime;
            Long personID, operson;
            br = new BufferedReader(new FileReader(activity));
            line = br.readLine();
            line = br.readLine();
            while (line != null) {
                String []splits = line.split(",");
                ID = splits[0];
                locationID = splits[1];
                startTime = splits[2];
                endTime = splits[3];

                properties = new HashMap<>();
                properties.put("id", ID);
                properties.put("locationID", locationID);
                properties.put("startTime", startTime);
                properties.put("endTime", endTime);
                personID = inserter.createNode(properties, activityLabel);
                index.add(personID, properties);
                    vertexCounter++;

                operson = Long.parseLong(ID);
                inserter.createRelationship(personID, operson, join,null);
                edgeCounter++;
                line = br.readLine();
                System.out.println("Activity: the"+vertexCounter+"th node created");
                System.out.println("Activity: the"+edgeCounter+"th edge created");
                ////make the changes visible for reading, use this sparsely, requires IO!
                if (edgeCounter % 1000000 == 0) {
                    index.flush();
                    System.out.println("Activity: " + edgeCounter + " records");
                }
            }
            index.flush();
            System.out.println("File1 succesfully loaded: " + vertexCounter2 +
                    " vertices and " + edgeCounter + " edges");
            System.out.println("File1 succesfully loaded: " + vertexCounter +
                    " vertices and " + edgeCounter + " edges");
            long sysEndTime = System.currentTimeMillis();
            System.out.println(String.format("Added activities in %.2f seconds", (sysEndTime-sysStartTime) / 1000.0));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
            if ( inserter != null )
            {
                inserter.shutdown();
            }
            indexProvider.shutdown();
        }

    }

    public static void main(String[] args) throws Exception {
        insert();
    }
}
