/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sparkDev.alexaciscospark.speechHandler;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sparkDev.alexaciscospark.SparkySpeechlet;
import com.sparkDev.alexaciscospark.api.Rooms;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Adithya
 */
public class MessageHandler {
     private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    public static SpeechletResponse sendMessageToRoom(Intent intent, Session session) {
        String speechText = "send message";
        String message = "";
        String roomTitle = "";
        //if the question does not have a room in it, ask back - What is the room name
        try {
            roomTitle = getRoomTitleFromIntent(intent);
            session.setAttribute("ROOM_TITLE", roomTitle);
            message = getMessageFromIntent(intent);
        } catch (Exception e) {
            // invalid room. move to the dialog
            String speechOutput
                    = "What message would you like to send?";
            String repromptText = "Sorry, I did not get you. What message would you like to send?";
            // repromptText is the speechOutput
            session.setAttribute("PREVIOUS_INTENT", intent.getName());
            // repromptText is the same as the speechOutput
            return newAskResponse(speechOutput, repromptText);
        }
        
        //THIS PART WILL NEVER BE REACHED:
        
        
        
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Spark Alexa");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
        if (message != null) {
            try {
                response = Rooms.createRoom(message, session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }
    
    public static SpeechletResponse sendMessageToMember(Intent intent, Session session) {
        String speechText = "send message to member";
        String message = "";
        String memberName = "";
        //if the question does not have a room in it, ask back - What is the room name
        try {
            memberName = getMemberFromIntent(intent);
            session.setAttribute("MEMBER_NAME", memberName);
            message = getMessageFromIntent(intent);
        } catch (Exception e) {
            // invalid room. move to the dialog
            String speechOutput
                    = "What message would you like to send?";
            String repromptText = "Sorry, I did not get you. What message would you like to send?";
            // repromptText is the speechOutput
            session.setAttribute("PREVIOUS_INTENT", intent.getName());
            // repromptText is the same as the speechOutput
            return newAskResponse(speechOutput, repromptText);
        }
        
        //THIS PART WILL NEVER BE REACHED:
        
        
        
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Spark Alexa");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        String response = "";
        if (message != null) {
            try {
                response = Rooms.createRoom(message, session.getUser().getAccessToken());
            } catch (UnirestException ex) {
                java.util.logging.Logger.getLogger(SparkySpeechlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        speech.setText(response);

        return SpeechletResponse.newTellResponse(speech, card);
    }


    private static String getMemberFromIntent(final Intent intent) throws Exception {
        Slot memberName = intent.getSlot("memberName");
        if (memberName == null || memberName.getValue() == null) {
            throw new Exception("");
        } else {
            return memberName.getValue();
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
    private static SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
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
    private static SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
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
    
    private static String getRoomTitleFromIntent(final Intent intent) throws Exception {
        Slot roomTitle = intent.getSlot("RoomTitle");
        if (roomTitle == null || roomTitle.getValue() == null) {
            throw new Exception("");
        } else {
            return roomTitle.getValue();
        }
    }
    
    
    private static String getMessageFromIntent(final Intent intent) throws Exception {
        Slot roomTitle = intent.getSlot("message");
        if (roomTitle == null || roomTitle.getValue() == null) {
            throw new Exception("");
        } else {
            return roomTitle.getValue();
        }
    }

    
    
}
