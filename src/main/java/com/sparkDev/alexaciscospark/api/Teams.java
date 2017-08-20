package com.sparkDev.alexaciscospark.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Teams {
    //POST http request to create a team on Cisco Spark
    public static String createTeam(String name, String sessionToken) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/teams")
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "3c5f7b4e-6da9-4c68-70ff-f3967bc717ff")
                .body("{\n    \"name\": \"" + name + "\"\n}")
                .asJson();
        if (response.getStatus() == 200) {
            return "Teams successfully created.";
        } else {
            return "Teams cannot be created.";
        }
    }


    //DELETE http request to delete a team on Cisco Spark
    public static String deleteTeam(String teamName, String sessionToken) throws UnirestException {
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

        HttpResponse<JsonNode> response = Unirest.delete("https://api.ciscospark.com/v1/teams/" + teamId)
                .header("authorization", "Bearer " + sessionToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "8511342f-650d-5868-7476-fdc80d64fb59")
                .asJson();

        if (response.getStatus() == 204) {
            return "Teams successfully deleted.";
        } else {
            return "Teams cannot be deleted.";
        }
    }
}
