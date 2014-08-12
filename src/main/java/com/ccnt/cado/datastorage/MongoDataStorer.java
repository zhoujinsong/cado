package com.ccnt.cado.datastorage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;






import com.ccnt.cado.datafetch.MetricData;
import com.ccnt.cado.datafetch.MonitorObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;



public class MongoDataStorer implements DataStorer{
	private static final String MONGO_ADDR = "mongo.address";
	private static final String MONGO_PORT = "mongo.port";
	private static final String MONGO_DB = "mongo.db";
	private static final String COLLNAME_MONITOROBJECT = "monitorObject";
	private static final String COLLNAME_METRICDATA = "metricData";
	private static final String COLLNAME_ID = "id";
	
	private MongoClient client;
	private DB db;

	public MongoDataStorer(){
		super();
		try {
			Properties props = new Properties();
			props.load(getClass().getClassLoader().getResourceAsStream("mongodb.properties"));
			String address, port, dbName;
			address = props.getProperty(MONGO_ADDR);
			port = props.getProperty(MONGO_PORT);
			dbName = props.getProperty(MONGO_DB);
			client = new MongoClient(address, Integer.parseInt(port));
			db = client.getDB(dbName);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private synchronized int  getCollId(String collName){
		DBCollection idColl = db.getCollection(COLLNAME_ID);
		DBObject object = idColl.findOne(new BasicDBObject("_id",collName));
		if(object == null){
			BasicDBObject doc = new BasicDBObject().
					append("_id", collName).
					append("currentId", 0);
			idColl.insert(doc);
			return 0;
		}
		int id = (Integer)object.get("currentId") + 1;
		idColl.update(new BasicDBObject("_id",collName),new BasicDBObject("$set",new BasicDBObject("currentId",id)));
		return id;
	}
	
	public void put(MonitorObject object) {
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		BasicDBObject doc = new BasicDBObject();
		int id = getCollId(COLLNAME_MONITOROBJECT);
		doc.put("_id", id);
		object.getAttributes().put("_id", id);
		for(Entry<String,Object> entry : object.getAttributes().entrySet()){
				doc.put(entry.getKey(), entry.getValue());
		}
		for(Entry<String,MonitorObject> entry : object.getFathers().entrySet()){
			doc.put(entry.getKey()+"_Id", entry.getValue().getAttributes().get("_id"));
		}
		monitorObjectColl.insert(doc);
	}
	
	public void put(MetricData metricData) {
		DBCollection metricDataColl = db.getCollection(COLLNAME_METRICDATA);
		BasicDBObject doc = new BasicDBObject();
		int id = getCollId(COLLNAME_METRICDATA);
		doc.put("_id", id);
		doc.put("time", metricData.getTime());
		for(Entry<String,Object> entry : metricData.getDatas().entrySet()){
			doc.put(entry.getKey(), entry.getValue());
		}
		doc.put("monitorObjectId", metricData.getMonitorObject().getAttributes().get("_id"));
		metricDataColl.insert(doc);
	}

	
	public void remove(MonitorObject object) {
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		monitorObjectColl.remove(new BasicDBObject("_id",object.getAttributes().get("_id")));
		for(Entry<String,List<MonitorObject>> entry : object.getSons().entrySet()){
			for(MonitorObject monitorObject : entry.getValue()){
				remove(monitorObject);
			}
		}
	}


	
	public List<Map<String,Object>> getMonitorObjects(Map<String, Object> queryConditions) {
		List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_MONITOROBJECT);
		DBCursor cursor = monitorObjectColl.find(new BasicDBObject(queryConditions));
		while(cursor.hasNext()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			DBObject dbObject = cursor.next();
			for(String key : dbObject.keySet()){
				attributes.put(key, dbObject.get(key));
			}
			attributesArray.add(attributes);
		}
		cursor.close();
		return attributesArray;
	}

	public List<Map<String, Object>> getNewestMetricDatas(
			Map<String, Object> queryConditions, int num) {
		List<Map<String,Object>> metricDatas = new ArrayList<Map<String,Object>>();
		DBCollection monitorObjectColl = db.getCollection(COLLNAME_METRICDATA);
		DBCursor cursor = monitorObjectColl.find(new BasicDBObject(queryConditions)).limit(num).sort(new BasicDBObject("time",-1));
		while(cursor.hasNext()){
			Map<String,Object> attributes = new HashMap<String,Object>();
			DBObject dbObject = cursor.next();
			for(String key : dbObject.keySet()){
				attributes.put(key, dbObject.get(key));
			}
			metricDatas.add(attributes);
		}
		cursor.close();
		return metricDatas;
	}
	
	public void dropAll() {
		db.getCollection(COLLNAME_MONITOROBJECT).drop();
		db.getCollection(COLLNAME_METRICDATA).drop();
	}
}
