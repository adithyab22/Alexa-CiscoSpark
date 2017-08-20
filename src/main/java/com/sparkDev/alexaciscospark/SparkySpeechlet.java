/**
 * Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.sparkDev.alexaciscospark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sparkDev.alexaciscospark.api.Memberships;
import com.sparkDev.alexaciscospark.api.Messages;
import com.sparkDev.alexaciscospark.api.Rooms;
import com.sparkDev.alexaciscospark.api.Teams;
import com.sparkDev.alexaciscospark.speechHandler.MemberHandler;
import com.sparkDev.alexaciscospark.speechHandler.MessageHandler;
import com.sparkDev.alexaciscospark.speechHandler.RoomHandler;
import java.util.logging.Level;

/**
 * This sample shows how to create a simple speechlet for handling speechlet
 * requests.
 */
public class SparkySpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(SparkySpeechlet.class);

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("createTeamIntent".equals(intentName)) {
            log.info("inside createTeamIntent");
            return createTeam(intent, session);
        } else if ("dialogueIntent".equals(intentName)) {
            return handleDialogueRequest(intent, session);
        } else if ("createRoomIntent".equals(intentName)) {
            return RoomHandler.createRoom(intent, session);
        }else if ("addMemberToRoomIntent".equals(intentName)) {
            return MemberHandler.addMemberToRoom(intent, session);
        } 
        else if ("sendMessageToRoom".equals(intentName)) {
            return MessageHandler.sendMessageToRoom(intent, session);
        } else if("sendMessageToMember".equals(intentName)){
            return MessageHandler.sendMessageToMember(intent, session);
        }
        else if ("addMemberToTeamIntent".equals(intentName)) {
            return addMemberToTeam(intent, session);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }

    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Cisco Spark");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse createTeam(Intent intent, Session session) {
        String speechText = "Create Teams";
        String teamName = "";
        //if the question does not have a team in it, ask back - What is the team name
        try {
            teamName = getTeamNameFromIntent(intent);
        } catch (Exception e) {
            // invalid city. move to the dialog
            String speechOutput
                    = "What is the team name?";
            String repromptText = "Sorry, I did not get you. What is the team name?";
            // repromptText is the speechOutput
            session.setAttribute("PREVIOUS_INTENT", intent.getName());
            // repromptText is the same as the speechOutput
            return newAskResponse(speechOutput, repromptText);
        }

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Create Teams");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
        if (teamName != null) {
            try {
                response = Teams.createTeam(teamName, session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse handleDialogueRequest(Intent intent, Session session) {
        String inputText = "";
        String previousIntent = "";
        String result = "";
        try {
            previousIntent = (String) session.getAttribute("PREVIOUS_INTENT");
            log.info("Name of previous attribute: "+previousIntent);
            if (previousIntent != null && previousIntent.equals("createTeamIntent")) {
                log.info("Handling dialogue team request..");
                inputText = getInputTextFromIntent(intent);
                try {
                    //if my previous intent was a team intent
                    result = Teams.createTeam(inputText, session.getUser().getAccessToken());
                } catch (UnirestException ex) {
                    java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }else if (previousIntent != null && previousIntent.equals("createRoomIntent")) {
                log.info("Handling dialogue room request..");
                inputText = getInputTextFromIntent(intent);
                try {
                    //if my previous intent was a room intent
                    result = Rooms.createRoom(inputText, session.getUser().getAccessToken());
                } catch (UnirestException ex) {
                    java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }else if (previousIntent != null && previousIntent.equals("sendMessageToRoom")) {
                log.info("Handling message to room request..");
                inputText = getInputTextFromIntent(intent);
                String roomTitle = (String) session.getAttribute("ROOM_TITLE");
                log.info("ROOM TITLE: "+ roomTitle);
                try {
                    //if my previous intent was a message to room intent
                    if(roomTitle != null){
                        result = Messages.sendMessageToRoom(inputText, roomTitle, session.getUser().getAccessToken());
                    }
                    
                } catch (UnirestException ex) {
                    java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }//sendMessageToMember
            else if (previousIntent != null && previousIntent.equals("sendMessageToMember")) {
                log.info("Handling message to member request..");
                inputText = getInputTextFromIntent(intent);
                String memberName = (String) session.getAttribute("MEMBER_NAME");
                 log.info("MEMBER NAME: "+ memberName);
                try {
                    //if my previous intent was a message to room intent
                    if(memberName != null){
                        result = Messages.sendMessageToMember(inputText, memberName, session.getUser().getAccessToken());
                    }
                    
                } catch (UnirestException ex) {
                    java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            else if (previousIntent != null && previousIntent.equals("addMemberToTeamIntent")) {
                log.info("Handling message to team request..");
                inputText = getInputTextFromIntent(intent); //team name
                String memberName = (String) session.getAttribute("MEMBER_NAME");
                log.info("MEMBER NAME: "+ memberName);
                try {
                    //if my previous intent was a message to room intent
                    if(memberName != null){
                        result = Memberships.addMemberToTeam(memberName, inputText, session.getUser().getAccessToken());
                    }
                    
                } catch (UnirestException ex) {
                    java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (Exception e) {
            String speechOutput
                    = "Sorry, I did not get it.";

            return newAskResponse(speechOutput, speechOutput);
        }

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Alexa Spark");
        card.setContent("Alexa Spark");
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(result);
        return SpeechletResponse.newTellResponse(speech, card);
    }

    private String getTeamNameFromIntent(final Intent intent) throws Exception {
        Slot teamSlot = intent.getSlot("TeamName");
        if (teamSlot == null || teamSlot.getValue() == null) {
            throw new Exception("");
        } else {
            return teamSlot.getValue();
        }
    }
    
     private String getInputTextFromIntent(final Intent intent) throws Exception {
        Slot inputText = intent.getSlot("InputText");
        if (inputText == null || inputText.getValue() == null) {
            throw new Exception("");
        } else {
            return inputText.getValue();
        }
    }

    /**
     * Wrapper for creating the Ask response from the input strings with plain
     * text output and reprompt speeches.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is
     * misunderstood.
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        return newAskResponse(stringOutput, false, repromptText, false);
    }

    /**
     * Wrapper for creating the Ask response from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param isOutputSsml whether the output text is of type SSML
     * @param repromptText the reprompt for if the user doesn't reply or is
     * misunderstood.
     * @param isRepromptSsml whether the reprompt text is of type SSML
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
            String repromptText, boolean isRepromptSsml) {
        OutputSpeech outputSpeech, repromptOutputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }

        if (isRepromptSsml) {
            repromptOutputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(stringOutput);
        } else {
            repromptOutputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
        }

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Cisco Spark");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse createRoom(Intent intent, Session session) {
        String speechText = "Cisco Spark";
        Slot roomTitle = intent.getSlot("RoomTitle");

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(speechText);
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
        if (roomTitle != null && roomTitle.getValue() != null) {
            try {
                response = Rooms.createRoom(roomTitle.getValue(), session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse addMemberToTeam(Intent intent, Session session) {
        String speechText = "Cisco Spark";
        Slot teamName = intent.getSlot("TeamName");
        Slot member = intent.getSlot("memberName");

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(speechText);
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
        if (teamName != null && teamName.getValue() != null) {
            try {
                response = Memberships.addMemberToTeam(member.getValue(), teamName.getValue(), session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }
}
