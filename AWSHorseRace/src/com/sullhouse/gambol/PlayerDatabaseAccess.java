package com.sullhouse.gambol;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class PlayerDatabaseAccess {
	private static AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJRHRVAAVVPUZGHLQ", "f0HjTNfDSCGRY+5z4gi7oEDpWX5uKcvvTjdvlAD4");
	private static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(awsCredentials));
	private static String tableName = "Player";
    
	public PlayerDatabaseAccess() {
		
	}
	
	public Player getPlayer(String code) {
		Table table = dynamoDB.getTable(tableName);

        try {

            Item item = table.getItem("code", code,  "code, playerName, email", null);
            
            System.out.println("Printing item after retrieving it....");
            System.out.println(item.toJSONPretty());
            
            return new Player(item.getString("playerName"), item.getString("email"), item.getString("code"));

        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }
		return null;  	
	}
	
	public void putPlayer(Player player) {
		Table table = dynamoDB.getTable(tableName); 

		try {

            Item item = new Item()
                .withPrimaryKey("code", player.getCode())
                .withString("playerName", player.getPlayerName())
                .withString("email", player.getEmail());
            table.putItem(item);

        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());

        }
	}
	
    public void createTable() {

            try {

                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(
                	    new AttributeDefinition()
                	        .withAttributeName("code")
                	        .withAttributeType("S"));
                
                ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
                keySchema.add(new KeySchemaElement()
                    .withAttributeName("code")
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
						.withPrimaryKey("code", horseRaceId));
			
			if (!documentItem.isNull("code")) return true;
			return false;
		}

		public Player getPlayerByEmail(String email) {
			Table table = dynamoDB.getTable(tableName);

			ScanSpec scanSpec = new ScanSpec()
					.withProjectionExpression("#em, playerName, code")
					.withFilterExpression("#em = :email")
					.withNameMap(new NameMap().with("#em",  "email"))
					.withValueMap(new ValueMap().withString(":email", email));
			
			try {
				ItemCollection<ScanOutcome> items = table.scan(scanSpec);
				
				Iterator<Item> iter = items.iterator();
				while (iter.hasNext()) {
					Item item = iter.next();
					JSONObject itemJson = new JSONObject(item.toJSON());
					System.out.println(item.toString());
					Player player = new Player(itemJson);
					return player;
				}
			} catch (Exception e) {
				System.err.println("Unable to scan the table:");
				System.err.println(e.getMessage());
			}
			return null; 
		}
    }
