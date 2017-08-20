package com.sparkDev.alexaciscospark.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Memberships {
    public static String addMemberToRoom(String name, String roomName, String sessionToken) throws UnirestException {
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
            if (item.getString("title").toLowerCase() == roomName.toLowerCase()) {
                roomId = item.getString("id");
                break;
            }
        }

        if (roomId == null) {
            return "Rooms not found.";
        }

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
            if (item.getString("personDisplayName").toLowerCase() == name.toLowerCase()) {
                personId = item.getString("personId");
                personEmail = item.getString("personEmail");
                break;
            }
        }

        if (personId == null || personEmail == null) {
            return "Contact not found. Add the member using email address.";
        }

        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/memberships")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "57a81d81-037f-471b-f02c-84340b87ed02")
                .body("{\r\n  \"roomId\" : \"" + roomId + "\",\r\n  \"personId\" : \"" + personId + "\",\r\n  \"personEmail\" : \"" + personEmail + "\",\r\n  \"isModerator\" : false\r\n}")
                .asJson();

        if (response.getStatus() == 200) {
            return "Member successfully added.";
        } else {
            return "Member cannot be created.";
        }
    }

    public static String addMemberToTeam(String name, String teamName, String sessionToken) throws UnirestException {
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
            if (item.getString("name").toLowerCase() == teamName.toLowerCase()) {
                teamId = item.getString("id");
                break;
            }
        }

        if (teamId == null) {
            return "Teams not found.";
        }

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
            if (item.getString("personDisplayName").toLowerCase() == name.toLowerCase()) {
                personId = item.getString("personId");
                personEmail = item.getString("personEmail");
                break;
            }
        }

        if (personId == null || personEmail == null) {
            return "Contact not found. Add the member using email address.";
        }

        HttpResponse<String> response = Unirest.post("https://api.ciscospark.com/v1/team/memberships")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "8a85ef30-445f-ec87-6dab-825ef17d042c")
                .body("{\r\n  \"teamId\" : \"" + teamId + "\",\r\n  \"personId\" : \"" + personId + "\",\r\n  \"personEmail\" : \"" + personEmail + "\",\r\n  \"isModerator\" : false\r\n}")
                .asString();

        if (response.getStatus() == 200) {
            return "Member successfully added.";
        } else {
            return "Member cannot be created.";
        }
    }

    public static String addMemberToRoomWithEmail(String email, String roomName, String sessionToken) throws UnirestException {
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
            if (item.getString("title").toLowerCase() == roomName.toLowerCase()) {
                roomId = item.getString("id");
                break;
            }
        }

        if (roomId == null) {
            return "Rooms not found.";
        }

        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/memberships")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "57a81d81-037f-471b-f02c-84340b87ed02")
                .body("{\r\n  \"roomId\" : \"" + roomId + "\",\r\n  \"personEmail\": \"" + email + "\",\r\n  \"isModerator\": \"false\"\r\n}")
                .asJson();

        if (response.getStatus() == 200) {
            return "Member successfully added.";
        } else {
            return "Member cannot be created.";
        }
    }

    public static String addMemberToTeamWithEmail(String email, String teamName, String sessionToken) throws UnirestException {
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
            if (item.getString("name").toLowerCase() == teamName.toLowerCase()) {
                teamId = item.getString("id");
                break;
            }
        }

        if (teamId == null) {
            return "Teams not found.";
        }

        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/team/memberships")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "57a81d81-037f-471b-f02c-84340b87ed02")
                .body("{\r\n  \"roomId\" : \"" + teamId + "\",\r\n  \"personEmail\": \"" + email + "\",\r\n  \"isModerator\": \"false\"\r\n}")
                .asJson();

        if (response.getStatus() == 200) {
            return "Member successfully added.";
        } else {
            return "Member cannot be created.";
        }
    }
}
