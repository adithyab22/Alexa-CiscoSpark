package com.sparkDev.alexaciscospark.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Messages {
    public static String sendMessageToRoom(String message, String roomName, String sessionToken) throws UnirestException {
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
            if (item.getString("title").toLowerCase().equals(roomName.toLowerCase())) {
                roomId = item.getString("id");
                break;
            }
        }

        if (roomId == null) {
            return "Rooms not found.";
        }

        HttpResponse<String> response = Unirest.post("https://api.ciscospark.com/v1/messages")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "00132cbc-ab8c-546c-36a0-530ab16c0066")
                .body("{\r\n  \"roomId\" : \"" + roomId + "\",\r\n  \"text\" : \"" + message + "\"\r\n}")
                .asString();

        if (response.getStatus() == 200) {
            return "Message sent.";
        } else {
            return "Message cannot be sent.";
        }
    }

    public static String sendMessageToMember(String message, String name, String sessionToken) throws UnirestException {
        HttpResponse<JsonNode> membersResponse = Unirest.get("https://api.ciscospark.com/v1/memberships")
                .header("authorization", "Bearer " + sessionToken)
                .header("cache-control", "no-cache")
                .header("postman-token", "10a775c7-ed31-3386-6d46-f7d610539350")
                .asJson();

        JSONObject members = membersResponse.getBody().getObject();
        JSONArray memberItems = (JSONArray) members.get("items");
        String personId = null;
        String personEmail = null;
        for (int i = 0; i < memberItems.length(); i++) {
            JSONObject item = memberItems.getJSONObject(i);
            if (item.getString("personDisplayName").toLowerCase().equals(name.toLowerCase())) {
                personId = item.getString("personId");
                personEmail = item.getString("personEmail");
                break;
            }
        }

        if (personId == null || personEmail == null) {
            return "Contact not found. Add the member using email address.";
        }

        HttpResponse<String> response = Unirest.post("https://api.ciscospark.com/v1/messages")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "00132cbc-ab8c-546c-36a0-530ab16c0066")
                .body("{\r\n  \"toPersonId\" : \"" + personId + "\",\r\n  \"toPersonEmail\" : \"" + personEmail + "\",\r\n  \"text\" : \"" + message + "\",\r\n}")
                .asString();

        if (response.getStatus() == 200) {
            return "Message sent.";
        } else {
            return "Message cannot be sent.";
        }
    }
}
