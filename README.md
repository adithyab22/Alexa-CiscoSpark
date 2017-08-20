# Amazon Alexa-Cisco Spark
This is an amazon alexa skill which integrates voice experience of Amazon Alexa to Cisco Spark. 

# Usage  

## Things this skill can do
* Create Team
* Create Room
* Add member to team
* Add member to room
* Send message to team/room/person

## Sample utterances
* Create team [Teamname]
* Create room [Roomname]
* Send message to [Person|Roomname|Teamname]
* Add member to [Teamname|Roomname]

## How it works?
1) When we command to Alexa, it triggers the Alexa to send the command with the invocation name Sparky.
2) Alexa skills calls a web service running on AWS Lambda.
3) Lambda web service send API requests are sent to cisco Spark server.
4) Based on API response, Lambda returns the speech output to Alexa.
