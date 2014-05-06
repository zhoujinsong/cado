package com.ccnt.cado.datastorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ccnt.cado.bean.Application;
import com.ccnt.cado.bean.Host;
import com.ccnt.cado.bean.MetricData;
import com.ccnt.cado.bean.MonitorData;
import com.ccnt.cado.bean.Service;
import com.ccnt.cado.bean.ServiceInstance;
import com.ccnt.cado.exception.AuthenticationException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import static com.ccnt.cado.bean.MonitorObject.*;


public class MongoDataStorer implements DataStorer{
	private static final String MONGO_ADDR = "mongo.address";
	private static final String MONGO_PORT = "mongo.port";
	private static final String MONGO_DB = "mongo.db";
	private static final String MONGO_USERNAME = "mongo.username";
	private static final String MONGO_PASSWORD = "mongo.password";
	private static final String COLLNAME_HOST = "host";
	private static final String COLLNAME_INSTANCE = "instance";
	private static final String COLLNAME_METRIC = "metric";
	private static final String COLLNAME_ID = "id";
	private static final String COLLNAME_APP = "application";
	private static final String COLLNAME_SERVICE = "service";
	
	private MongoClient client;
	private DB db;

	@SuppressWarnings("deprecation")
	public MongoDataStorer() throws IOException, AuthenticationException {
		super();
		Properties props = new Properties();
		props.load(getClass().getClassLoader().getResourceAsStream("mongodb.properties"));
		String address, port, dbName ,username ,password;
		address = props.getProperty(MONGO_ADDR);
		port = props.getProperty(MONGO_PORT);
		dbName = props.getProperty(MONGO_DB);
		username = props.getProperty(MONGO_USERNAME);
		password = props.getProperty(MONGO_PASSWORD);
		client = new MongoClient(address, Integer.parseInt(port));
		db = client.getDB(dbName);
		if(!db.authenticate(username, password.toCharArray())){
			throw new AuthenticationException();
		}
	}
	public void storeData(MonitorData data) {
		storeHostData(data.getHosts());
		storeApplicationData(data.getApplications());
		storeServiceData(data.getServices());
		storeInstanceData(data.getInstances());
		
	}
	public MonitorData getData() {
		MonitorData data = new MonitorData();
		data.setHosts(getHostData());
		data.setApplications(getApplicationData());
		data.setServices(getServiceData(data.getApplications()));
		data.setInstances(getInstanceData(data.getServices(),data.getHosts()));
		return data;
	}
	public void storeHostData(List<Host> hosts){
		DBCollection hostColl = db.getCollection(COLLNAME_HOST);
		for(Host host : hosts){
			switch(host.getState()){
			case MONSTATE_NEWONE:
				host.setId(getCollId(COLLNAME_HOST));
				DBObject doc = Host2DBObject(host);
				hostColl.insert(doc);
				break;
			case MONSTATE_SAME:
				break;
			case MONSTATE_CHANGED:
				break;
			case MONSTATE_DELETE:
				hostColl.remove(new BasicDBObject("_id",host.getId()));
				break;
			default:
			}
			host.setState(MONSTATE_DELETE);
		}
	}
	public List<Host> getHostData() {
		List<Host> result = new ArrayList<Host>();
		DBCollection hostColl = db.getCollection(COLLNAME_HOST);
		DBCursor cursor = hostColl.find();
		while(cursor.hasNext()){
			Host host = DBObject2Host(cursor.next());
			host.setState(MONSTATE_DELETE);
			result.add(host);
		}
		cursor.close();
		return result;
	}
	public void storeApplicationData(List<Application> applications){
		DBCollection appColl = db.getCollection(COLLNAME_APP);
		for(Application application : applications){
			switch(application.getState()){
			case MONSTATE_NEWONE:
				application.setId(getCollId(COLLNAME_APP));
				DBObject doc = Application2DBObject(application);
				appColl.insert(doc);
				break;
			case MONSTATE_SAME:
				break;
			case MONSTATE_CHANGED:
				appColl.update(new BasicDBObject("_id",application.getId()), 
						new BasicDBObject("$set",new BasicDBObject("state",application.getRuningState())));
				break;
			case MONSTATE_DELETE:
				appColl.remove(new BasicDBObject("_id",application.getId()));
				break;
			default:		
			}
			application.setState(MONSTATE_DELETE);
		}
	}
	public List<Application> getApplicationData() {
		List<Application> result = new ArrayList<Application>();
		DBCollection appColl = db.getCollection(COLLNAME_APP);
		DBCursor cursor = appColl.find();
		while(cursor.hasNext()){
			Application app = DBObject2Application(cursor.next());
			app.setState(MONSTATE_DELETE);
			result.add(app);
		}
		cursor.close();
		return result;
	}
	public void storeServiceData(List<Service> services){
		DBCollection serviceColl = db.getCollection(COLLNAME_SERVICE);
		for(Service service : services){
			switch(service.getState()){
			case MONSTATE_NEWONE:
				service.setId(getCollId(COLLNAME_SERVICE));
				DBObject doc = Service2DBObject(service);
				serviceColl.insert(doc);
				break;
			case MONSTATE_SAME:
				break;
			case MONSTATE_CHANGED:
				serviceColl.update(new BasicDBObject("_id",service.getId()), 
						new BasicDBObject("$set",new BasicDBObject("state",service.getRuningState())));
				break;
			case MONSTATE_DELETE:
				serviceColl.remove(new BasicDBObject("_id",service.getId()));
				break;
			default:		
			}
			service.setState(MONSTATE_DELETE);
		}
	}
	public List<Service> getServiceData(List<Application> applications) {
		List<Service> result = new ArrayList<Service>();
		DBCollection serviceColl = db.getCollection(COLLNAME_SERVICE);
		DBCollection appColl = db.getCollection(COLLNAME_APP);
		DBCursor cursor = serviceColl.find();
		while(cursor.hasNext()){
			DBObject object = cursor.next();
			Service service = DBObject2Service(object);
			int appId = (Integer) object.get("appId");
			Application app = null;
			for(Application application : applications){
				if(application.getId() == appId){
					app = application;
					break;
				}
			}
			if(app == null){
				app = DBObject2Application(appColl.findOne(new BasicDBObject("_id",appId)));
				applications.add(app);
			}
			app.getServices().add(service);
			service.setApplication(app);
			service.setState(MONSTATE_DELETE);
			result.add(service);
		}
		cursor.close();
		return result;
	}
	public void storeInstanceData(List<ServiceInstance> instances){
		DBCollection instanceColl = db.getCollection(COLLNAME_INSTANCE);
		for(ServiceInstance instance : instances){
			switch(instance.getState()){
			case MONSTATE_NEWONE:
				instance.setId(getCollId(COLLNAME_INSTANCE));
				DBObject doc = Instance2DBObject(instance);
				instanceColl.insert(doc);
				storeAppMetric(instance);
				break;
			case MONSTATE_SAME:
				storeAppMetric(instance);
				break;
			case MONSTATE_CHANGED:
				instanceColl.update(new BasicDBObject("_id",instance.getId()),
						new BasicDBObject("$set",new BasicDBObject("state",instance.getState())));
				storeAppMetric(instance);
				break;
			case MONSTATE_DELETE:
				instanceColl.remove(new BasicDBObject("_id",instance.getId()));
				break;
			default:		
			}
			instance.setState(MONSTATE_DELETE);
		}
	}
	public List<ServiceInstance> getInstanceData(List<Service> services,List<Host> hosts) {
		List<ServiceInstance> result = new ArrayList<ServiceInstance>();
		DBCollection instanceColl = db.getCollection(COLLNAME_INSTANCE);
		DBCollection serviceColl = db.getCollection(COLLNAME_SERVICE);
		DBCollection hostColl = db.getCollection(COLLNAME_HOST);
		DBCursor cursor = instanceColl.find();
		while(cursor.hasNext()){
			DBObject object = cursor.next();
			int serviceId = (Integer) object.get("serviceId");
			int hostId = (Integer) object.get("hostId");
			ServiceInstance instance = DBObject2Instance(object);
			Service service = null;
			Host host = null;
			for(Service s : services){
				if(s.getId() == serviceId){
					service = s;
					break;
				}
			}
			if(service == null){
				service = DBObject2Service(serviceColl.findOne(new BasicDBObject("_id",serviceId)));
				services.add(service);
			}
			service.getInstances().add(instance);
			instance.setService(service);
			for(Host h : hosts){
				if(h.getId() == hostId){
					host = h;
					break;
				}
			}
			if(host == null){
				host = DBObject2Host(hostColl.findOne(new BasicDBObject("_id",hostId)));
				hosts.add(host);
			}
			host.getInstances().add(instance);
			instance.setHost(host);
			instance.setState(MONSTATE_DELETE);
			result.add(instance);
		}
		cursor.close();
		return result;
	}
	private int getCollId(String collName){
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
	private void storeAppMetric(ServiceInstance instance){
		DBCollection metricColl = db.getCollection(COLLNAME_METRIC);
		BasicDBObject doc = new BasicDBObject().
				append("_id", getCollId(COLLNAME_METRIC)).
				append("instanceId", instance.getId()).
				append("date", instance.getMetricDate());
		List<DBObject> metrics = new ArrayList<DBObject>();
		for(MetricData md :instance.getMetrics()){
			DBObject metric = new BasicDBObject().
					append(md.getName(), md.getValue().toString());
			metrics.add(metric);
		}
		doc.append("metrics", metrics);
		metricColl.save(doc);
	}
	
	private static Host DBObject2Host(DBObject object){
		Host host = new Host();
		host.setId((Integer) object.get("_id"));
		host.setName((String) object.get("name"));
		host.setAddress((String) object.get("address"));
		DBObject metricObj = (DBObject) object.get("metrics");
		Set<String> keys = metricObj.keySet();
		List<MetricData> metrics = new ArrayList<MetricData>();
		for(String key : keys){
			metrics.add(new MetricData(key,metricObj.get(key)));
		}
		host.setProps(metrics);
		return host;
	}
	private static DBObject Host2DBObject(Host host){
		BasicDBObject doc = new BasicDBObject().
				append("_id", host.getId()).
				append("address", host.getAddress()).
				append("name", host.getName());
		BasicDBObject metrics = new BasicDBObject();
		for(MetricData md :host.getProps()){
			metrics.append(md.getName(),md.getValue());
		}
		doc.append("metrics", metrics);
		return doc;
	}
	private static Application DBObject2Application(DBObject object){
		Application app = new Application();
		app.setId((Integer) object.get("_id"));
		app.setName((String) object.get("name"));
		app.setRuningState((String)object.get("state"));
		return app;
	}
	private static DBObject Application2DBObject(Application application){
		DBObject doc = new BasicDBObject().
				append("_id", application.getId()).
				append("name", application.getName()).
				append("state", application.getRuningState());
		return doc;
	}
	private static Service DBObject2Service(DBObject object){
		Service service = new Service();
		service.setId((Integer) object.get("_id"));
		service.setName((String) object.get("name"));
		service.setRuningState((String) object.get("state"));
		return service;
	}
	private static DBObject Service2DBObject(Service service){
		DBObject doc = new BasicDBObject().
				append("_id", service.getId()).
				append("name", service.getName()).
				append("state", service.getRuningState()).
				append("appId", service.getApplication().getId());
		return doc;
	}
	private static ServiceInstance DBObject2Instance(DBObject object){
		ServiceInstance instance = new ServiceInstance();
		instance.setId((Integer) object.get("_id"));
		instance.setName((String) object.get("name"));
		instance.setRuningState((String) object.get("state"));
		return instance;
	}
	private static DBObject Instance2DBObject(ServiceInstance instance){
		BasicDBObject doc = new BasicDBObject().
				append("_id", instance.getId()).
				append("name", instance.getName()).
				append("state", instance.getRuningState()).
				append("hostId", instance.getHost().getId()).
				append("serviceId", instance.getService().getId());
		return doc;
	}

}
