package com.sparkDev.alexaciscospark.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Room {
    //POST http request to create an independent room on Cisco Spark
    public static String createRoom(String name, String sessionToken) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/rooms")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "71f8d06a-c440-63a7-542a-552907d5b5b3")
                .body("{\n    \"title\": \"" + name + "\"\n}")
                .asJson();
        if (response.getStatus() == 200) {
            return "Room successfully created.";
        } else {
            return "Room cannot be created.";
        }
    }

    //POST http request to create a room under a team on Cisco Spark
    public static String createRoom(String title, String teamName, String sessionToken) throws UnirestException {
        HttpResponse<JsonNode> teamsResponse = Unirest.get("https://api.ciscospark.com/v1/teams")
                .header("authorization", "Bearer " + sessionToken)
                .header("cache-control", "no-cache")
                .header("postman-token", "2a79d2f7-e472-5985-9f05-a315bcb50974")
                .asJson();

        JSONObject teams = teamsResponse.getBody().getObject();
        JSONArray items = (JSONArray) teams.get("items");
        String teamId = null;
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.getString("name") == teamName) {
                teamId = item.getString("id");
                break;
            }
        }

        if (teamId == null) {
            return "Team not found.";
        }

        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/rooms")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "71f8d06a-c440-63a7-542a-552907d5b5b3")
                .body("{\r\n  \"title\" : \"" + title + "\",\r\n  \"teamId\" : \"" + teamId + "\"\r\n}")
                .asJson();
        if (response.getStatus() == 200) {
            return "Room successfully created.";
        } else {
            return "Room cannot be created.";
        }
    }

    //DELETE http request to delete a room on Cisco Spark
    public static String deleteRoom(String roomName, String sessionToken) throws UnirestException {
        HttpResponse<JsonNode> roomsResponse = Unirest.get("https://api.ciscospark.com/v1/rooms")
                .header("authorization", "Bearer " + sessionToken)
                .header("cache-control", "no-cache")
                .header("postman-token", "2a79d2f7-e472-5985-9f05-a315bcb50974")
                .asJson();

        JSONObject rooms = roomsResponse.getBody().getObject();
        JSONArray items = (JSONArray) rooms.get("items");
        String roomId = null;
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.getString("title") == roomName) {
                roomId = item.getString("id");
                break;
            }
        }

        if (roomId == null) {
            return "Room not found.";
        }

        HttpResponse<JsonNode> response = Unirest.delete("https://api.ciscospark.com/v1/rooms/" + roomId)
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "ad0827fb-568f-8f0d-d4ef-3e2775681a11")
                .asJson();

        if (response.getStatus() == 204) {
            return "Room successfully deleted.";
        } else {
            return "Room cannot be deleted.";
        }
    }
}

