package com.sullhouse.horserace;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sullhouse.gambol.HorseRaceGambol;
import com.sullhouse.gambol.PlayerBet;

public class HorseRaceDatabaseAccess {
	private static AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJRHRVAAVVPUZGHLQ", "f0HjTNfDSCGRY+5z4gi7oEDpWX5uKcvvTjdvlAD4");
	private static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(awsCredentials));
	private static String tableName = "HorseRace";
    
	public HorseRaceDatabaseAccess() {
		
	}
	
	public JSONObject getHorseRace(String id) {
		Table table = dynamoDB.getTable(tableName);

        try {

            Item item = table.getItem("horseRaceId", id,  "horseRaceId, playerBets, bettingOpen, horseRaceLength, startTime", null);
            
            System.out.println("Printing item after retrieving it....");
            System.out.println(item.toJSONPretty());
            
            JSONObject h = new JSONObject(item.toJSON());
            return h;

        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }
		return null;	
	}
	
	public JSONArray getAllHorseRaces() {
		Table table = dynamoDB.getTable(tableName);
		
		try {
			ItemCollection<ScanOutcome> items = table.scan();
			
			JSONArray horseRacesJson = new JSONArray();
			
			for (Item i : items) {
				horseRacesJson.put(i);
			}
						
			return horseRacesJson;
        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }
		return null;	
	}
	
	public void putHorseRace(HorseRace horseRace, Date startTime) {
		Table table = dynamoDB.getTable(tableName); 
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sDate = formatter.format(startTime);
		
		Item item =
				new Item()
					.withPrimaryKey("horseRaceId", horseRace.getId())
					.withBoolean("bettingOpen", true)
					.withString("startTime", sDate)
					.withInt("horseRaceLength", horseRace.getLength());

		table.putItem(item);
	}
	
    public void createTable() {

            try {

                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(
                	    new AttributeDefinition()
                	        .withAttributeName("horseRaceId")
                	        .withAttributeType("S"));
                
                ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
                keySchema.add(new KeySchemaElement()
                    .withAttributeName("horseRaceId")
                    .withKeyType(KeyType.HASH)); //Partition key

                CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(5L)
                        .withWriteCapacityUnits(6L));

                System.out.println("Issuing CreateTable request for " + tableName);
                Table table = dynamoDB.createTable(request);

                System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
                table.waitForActive();

                getTableInformation();

            } catch (Exception e) {
                System.err.println("CreateTable request failed for " + tableName);
                System.err.println(e.getMessage());
            }

        }

        static void getTableInformation() {

            System.out.println("Describing " + tableName);

            TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
            System.out.format("Name: %s:\n" + "Status: %s \n"
                    + "Provisioned Throughput (read capacity units/sec): %d \n"
                    + "Provisioned Throughput (write capacity units/sec): %d \n",
            tableDescription.getTableName(), 
            tableDescription.getTableStatus(), 
            tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
            tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
        }

        static void deleteTable() {

            Table table = dynamoDB.getTable(tableName);
            try {
                System.out.println("Issuing DeleteTable request for " + tableName);
                table.delete();

                System.out.println("Waiting for " + tableName
                    + " to be deleted...this may take a while...");

                table.waitForDelete();
            } catch (Exception e) {
                System.err.println("DeleteTable request failed for " + tableName);
                System.err.println(e.getMessage());
            }
        }

		public boolean checkExists(String horseRaceId) {
			Table table = dynamoDB.getTable(tableName); 
			
			Item documentItem =
					table.getItem(new GetItemSpec()
						.withPrimaryKey("horseRaceId", horseRaceId));
			
			if (!documentItem.isNull("horseRaceId")) return true;
			return false;
		}

		public List<PlayerBet> getBets(String horseRaceId) {
			List<PlayerBet> playerBets = new ArrayList<PlayerBet>();
			
			Table table = dynamoDB.getTable(tableName);

	        try {

	            Item item = table.getItem("horseRaceId", horseRaceId,  "playerBets", null);
	            if (item.getJSON("playerBets")!=null) {
	            	playerBets = getPlayerBetListFromJson(item.getString("playerBets"));
	            }
	            System.out.println("Printing item after retrieving it....");
	            System.out.println(item.toJSONPretty());

	        } catch (Exception e) {
	            System.err.println("GetItem failed.");
	            System.err.println(e.getMessage());
	        }  
			return playerBets;
		}

		public void setBets(HorseRaceGambol horseRaceGambol) {
			Table table = dynamoDB.getTable(tableName);

	        try {

	            Map<String, String> expressionAttributeNames = new HashMap<String, String>();
	            expressionAttributeNames.put("#na", "NewAttribute");
	            
	            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
	            		.withPrimaryKey("horseRaceId", horseRaceGambol.getHorseRaceId())
	                    .withUpdateExpression("set #na = :val1")
	                    .withNameMap(new NameMap()
	                        .with("#na", "playerBets"))
	                    .withValueMap(new ValueMap()
	                        .withString(":val1", getPlayerBetListJson(horseRaceGambol.getBets())))
	                    .withReturnValues(ReturnValue.ALL_NEW);

	            UpdateItemOutcome outcome =  table.updateItem(updateItemSpec);

	            // Check the response.
	            System.out.println("Printing item after adding new attribute...");
	            System.out.println(outcome.getItem().toJSONPretty());           

	        }   catch (Exception e) {
	            System.err.println("Failed to add new attribute in " + tableName);
	            System.err.println(e.getMessage());
	        }
		}
		
		public String getPlayerBetListJson(List<PlayerBet> playerBets) throws JsonProcessingException {
			ObjectMapper mapper = new ObjectMapper();
			String s = mapper.writeValueAsString(playerBets);
			return s;
		}
		
		public List<PlayerBet> getPlayerBetListFromJson(String json) throws JsonParseException, JsonMappingException, IOException {
			JSONArray playerBetsArray = new JSONArray(json); 
			List<PlayerBet> playerBets = new ArrayList<PlayerBet>();
			for (int i = 0; i < playerBetsArray.length(); i++) {
		        JSONObject playerBetObject = playerBetsArray.getJSONObject(i);
		        PlayerBet playerBet = new PlayerBet(playerBetObject);
		        playerBets.add(playerBet);
			}
			return playerBets;
			
		}

		public boolean getIsBettingOpen(String horseRaceId) {
			Table table = dynamoDB.getTable(tableName);

	        try {

	            Item item = table.getItem("horseRaceId", horseRaceId,  "bettingOpen", null);
	            if (item.getBoolean("bettingOpen")) {
	            	return true;
	            } else {
	            	return false;
	            }

	        } catch (Exception e) {
	            System.err.println("GetItem failed.");
	            System.err.println(e.getMessage());
	        }  
			return false;
		}

		public void setBettingClosed(String horseRaceId) {
			Table table = dynamoDB.getTable(tableName);

	        try {

	            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
	            .withPrimaryKey("horseRaceId", horseRaceId)
	            .withReturnValues(ReturnValue.ALL_NEW)
	            .withUpdateExpression("set #b = :val1")
	            .withNameMap(new NameMap()
	                .with("#b", "bettingOpen"))
	            .withValueMap(new ValueMap()
	                .withBoolean(":val1", false));

	            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

	            // Check the response.
	            System.out
	            .println("Printing item after conditional update to new attribute...");
	            System.out.println(outcome.getItem().toJSONPretty());

	        } catch (Exception e) {
	            System.err.println("Error updating item in " + tableName);
	            System.err.println(e.getMessage());
	        }
		}

		public String getHorseRaceEmails(String id) {
			Table table = dynamoDB.getTable(tableName);

	        try {

	            Item item = table.getItem("horseRaceId", id,  "horseRaceId, playerBets", null);
	            
	            System.out.println("Printing item after retrieving it....");
	            System.out.println(item.toJSONPretty());
	            
	            JSONObject h = new JSONObject(item.toJSON());
	            JSONArray p = h.getJSONArray("playerBets");
	            
	            List<String> al = new ArrayList<>();
		        
	            for (int i=0;i<p.length();i++) {
	            	JSONObject player = p.getJSONObject(i).getJSONObject("player");
	            	al.add(player.getString("email"));
	            }
	            Set<String> hs = new HashSet<>();
		        hs.addAll(al);
		        al.clear();
		        al.addAll(hs);
		        
		        String s = "";
		        boolean first = true;
		        for (String a : al) {
		        	if (first) {
		        		s = a;
		        		first = false;
		        	} else {
		        		s += ", " + a;
		        	}
		        }
		        
		        return s;
	        } catch (Exception e) {
	            System.err.println("GetItem failed.");
	            System.err.println(e.getMessage());
	        }
			return null;
		}
    }
