package com.sparkDev.alexaciscospark.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Team {
    //POST http request to create a team on Cisco Spark
    public static HttpResponse createTeam(String name) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post("https://api.ciscospark.com/v1/teams")
                .header("authorization", "Bearer " + Globals.CISCO_SPARK_TOKEN)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "3c5f7b4e-6da9-4c68-70ff-f3967bc717ff")
                .body("{\n    \"name\": \"" + name + "\"\n}")
                .asJson();
        return response;
    }
}
