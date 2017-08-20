/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sparkDev.alexaciscospark.api.Membership;
import com.sparkDev.alexaciscospark.api.Room;
import com.sparkDev.alexaciscospark.api.Team;
import java.util.logging.Level;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
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

        if("createTeamIntent".equals(intentName)){
            return createTeam(intent, session);
        } else if("createRoomIntent".equals(intentName)){
            return createRoom(intent, session);
        }else if("addMemberToTeamIntent".equals(intentName)){
            return addMemberToTeam(intent, session);
        }
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        }else {
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
    
     private SpeechletResponse createTeam(Intent intent, Session session)  {
        String speechText = "Create Team";
        Slot teamNameSlot = intent.getSlot("TeamName");

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Create Team");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
         if (teamNameSlot != null && teamNameSlot.getValue() != null) {
            try {
                response = Team.createTeam(teamNameSlot.getValue(), session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }
             
         }
            speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
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
                response = Room.createRoom(roomTitle.getValue(), session.getUser().getAccessToken());
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
                response = Membership.addMemberToTeam(member.getValue(), teamName.getValue(), session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }
             
         }
            speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }
}
