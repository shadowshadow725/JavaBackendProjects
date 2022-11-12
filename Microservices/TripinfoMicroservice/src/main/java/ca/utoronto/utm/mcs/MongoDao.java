package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

public class MongoDao {
	
	public MongoCollection<Document> collection;
	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection. 
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = "mongodb";
//		String username = dotenv.get("MONGO_INITDB_ROOT_USERNAME");
//		String dbName = dotenv.get("MONGO_INITDB_DATABASE");
		String dbName = "trip";
		String username = "root";
		String password = "123456";
//		String password = dotenv.get("MONGO_INITDB_ROOT_PASSWORD");
		String uriDb = String.format("%s://%s:%s@mongodb:27017", addr, username, password);
		MongoClient mongoClient = MongoClients.create(uriDb);
		MongoDatabase database = mongoClient.getDatabase(dbName);
		this.collection = database.getCollection(dbName);
	}

	public String tripConfirm(String passenger, String driver, int time) {

		Document doc = new Document();
		doc.put("passenger", passenger);
		doc.put("driver", driver);
		doc.put("startTime", time);

		try {
			this.collection.insertOne(doc);
			ObjectId id = (ObjectId)doc.get( "_id" );
			return id.toString();
		} catch (Exception e) {
			return null;
		}

	}
//○ distance (Integer) - The distance of the trip.
//○ totalCost (String) - The total cost of the trip
//○ startTime (Integer) - The start time of the trip (unix time in seconds)
//○ endTime (Integer) - The end time of the trip (unix time in seconds)
//○ timeElapsed (Integer) - The time spend for the entire trip (endTime - startTime)
//○ driver (String) - The uid of the driver in this trip
//○ passenger (String) - The uid of the passenger in this trip
	public boolean updateTrip(int endtime, int distance, int timeElapsed, double totalCost, String _id){
		try{
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", new ObjectId(_id));
			Document doc = this.collection.find(whereQuery).first();

			doc.put("endTime", endtime);
			doc.put("totalCost", Double.toString(totalCost));
			doc.put("distance", distance);
			doc.put("timeElapsed", timeElapsed);
			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", doc);
			UpdateResult result = this.collection.updateOne(whereQuery, updateObject);


			return true;
		}
		catch (Exception e){
			return false;
		}
	}

	public JSONArray getPassengerTrips(String passengerid){
		try{
			FindIterable<Document> cursor = this.collection.find();
			JSONArray js = Utils.findIterableToJSONArray(cursor);
			JSONArray trip = new JSONArray();
			for (Document doc : cursor) {
				if (doc.getString("passenger").equals(passengerid)) {
					JSONObject i = new JSONObject();
					i.put("distance", doc.getInteger("distance"));
					i.put("_id", doc.getObjectId("_id").toString());
					i.put("totalCost", doc.getString("totalCost"));
					i.put("startTime", doc.getInteger("startTime"));
					i.put("endTime", doc.getInteger("endTime"));
					i.put("driver", doc.getString("driver"));
					i.put("timeElapsed", doc.getInteger("timeElapsed"));
					trip.put(i);
				}
        	}
			return trip;

		}
		catch (Exception e){
			return null;
		}
	}

	public JSONArray getDriverTrips(String driverid){
		try{

			JSONArray trips = new JSONArray();
			FindIterable<Document> cursor = this.collection.find();
			for (Document doc : cursor) {
				if (doc.getString("driver").equals(driverid)){
					JSONObject i = new JSONObject();
					i.put("distance", doc.getInteger("distance"));
					i.put("_id", doc.getObjectId("_id").toString());
					i.put("totalCost", doc.getString("totalCost"));
					i.put("startTime", doc.getInteger("startTime"));
					i.put("endTime", doc.getInteger("endTime"));
					i.put("timeElapsed", doc.getInteger("timeElapsed"));
					i.put("passenger", doc.getString("passenger"));
					trips.put(i);
				}
			}
			return trips;
		}
		catch (Exception e){
			return null;
		}
	}

	public Document getTripRow(String tripid){
		try{
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("_id", new ObjectId(tripid));
			Document doc = this.collection.find(whereQuery).first();
			return doc;
		}
		catch (Exception e){
			return null;
		}

	}

	// *** implement database operations here *** //

}
