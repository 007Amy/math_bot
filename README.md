# MathBot

#### Learn counting to calculus by programming a robot!

### How the project is funded
Mathbot is a mission to become the easiest
and most fun way to learn math.

The way we are going to accomplish this is by:
1. Focusing on problem solving and tool building.
2. Reward students financially for completing levels.

Playing the game is free for anyone,
but if you want to create additional incentives
for the student you can purchase levels.

For example you might buy "Algebra" for $50
for a child or grandchild.
In that case the student will be rewarded with $45,
at maybe $3 per level,
for learning Algebra and passing the levels.

As an open source project many of the contributors
are volunteers and are not primarily interested in compensation.
However, being able to fund advertising 
and minimize the personal sacrifice required 
of contributors
is a smart thing to do.

As such we are going to attempt to financially reward contributors
by creating a utility token called "mathbits."
When someone purchases levels the software
will use 90% of those funds to reward the student
and 10% to purchase and destroy mathbits.

This will cause mathbits to have a market value
and allow us to reward contributors with mathbits.

## Is this another scam coin?
This is a very reasonable question 
since this maybe the first non-scam coin
outside of bitcoin created.

In fact we would prefer to avoid the use of a token
for this reason.
That said we can't think of a better way to reward contributors
of an open source project.
New thing draw the attention of con men,
but that doesn't mean that all new things are cons.
The only way to make that determination is to become an expert
or hire one you can trust.

## Why do you need a stupid token?
Because we don't want to be wasteful.
It is unlikely that teaching the world math
is going to be a highly lucrative endeavour 
even if we are successful.
Spending thousands of dollars to create 
an organization and thousands more to run the bureaucracy 
isn't appealing to any of the existing contributors.

But, buy creating a token that is purchased
when the paid experience is purchased by a user
seems like a reasonable hack.

## Why can't someone else copy the code and disable the token
They absolutely can 
and we would be happy for this to happen.
We believe that there will be enough community goodwill
to keep people from disabling the compensation
of past contributors on many of the running instances of mathbot,
but probably not all.
And since our mission is to teach math,
we are happy if someone does that without us,
even if they use our work and don't give us any rewards.

## Are you going to trademark anything to protect contributors
No, we are not. 
Anyone can copy the mathbot code, copy the mathbot logo,
copy the mathbot messaging, etc.

We are on open source project - we don't exist as a company
and we have no mechanism to enforce legal claims.

## How are the tokens distributed?
Once a month we have a meeting to review
all requests for compensation from contributors.
These could include podcast appearances 
that promoted mathbot,
code written, testing performed, design work completed, etc.

In the near term we will simply update a shared google spreadsheet
with the twitter handle of the owner.
Yes, we know this is very, very low security,
but given the value (near zero) this is fine to get started.
Once a colored coin implementation on bitcoin is ready we will switch 
to that to keep track of ownership.
Issuance will be based of the approval of the project contributors.

## How is any of this enforced?
It isn't. If someone doesn't like anything,
for any reason,
they can copy everything about the project
and change that one thing they don't like.

We think that we will find solutions to 
conflict along the way with some number of contributors
but we have no idea how often the project will fork.

## What about the mathbot.com domain name?
This particular domain name is owned by JW Weatherman.
He has indicated a desire to host a copy of mathbot
and to leave the compensation code in place,
but he has made no commitment to do so.

Domain ownership and hosting is not part
of this open source project and people are free
to do whatever they like with our project - 
though we do think there will be some social 
incentive to keep the compensation code functional.

## How can I get mathbits?
Until we move out of a simple spreadsheet
you will need to contact 
one of the contributors on the spreadsheet on
twitter to work out a trade.

If they tweet out an amount they want to transfer
to your twitter account we will update this
at the next monthly meeting.

## You keep saying "we." Who is that.
Open source projects are very strange
for people that are new to them.
Contributors come and go or simply change
their identities.

Copies of the project are made and use the same name.
To figure out who is the "we" you start with a name like "mathbot."
Then you find any code repositories (often on github) with that name.
You pick the one you like, maybe based on the amount of activity
or the names of contributors you recognize.
Then you can look at their documentation and check-in history.
In our case we will also point to a spreadsheet
with a list of contributors and how they've been compensated.

## How can I trust this absurd system?
You probably shouldn't.
But on the other hand 
you are probably using open source code to read this
so the odd incentives have created unexpectedly great results in the past.

And social incentives and individual reputations 
are among the things that can create enough trust 
to solve some problems.

It is possible that the problem of compensating contributors to 
open source projects in this way will work,
but this is very experimental and likely to fail 
(most open source project do fail).

## What if someone makes a mistake updating the spreadsheet
If you have gotten this far and you plan
to buy mathbits for big money
you are probably an idiot and there is nothing that can be done for you,

We are using twitter and a google doc spreadsheet.
If we fat finger something or your twitter account gets hacked
or we just decide we don't like your face
you could easily lose your mathbits.

But keep in mind you have 100% control of all of the 
value created by the mathbot open source project 
(as is everyone else with access to our open access repo).
You always have the option of taking everything of value
and ditching all of us.

## Running The App

### Prerequisites

  - Java -v 1.8
    - [Java](https://java.com/en/download/)
  - Scala -v 2.11.11
    - [Scala](https://www.scala-lang.org/download/)
  - NodeJS -v 7.10.1
    - Use [N](https://github.com/tj/n)
  - WebPack installed globally
    - ```npm install webpack -g```
  - MongoDB install globally
    - Goto [MongoDb](https://docs.mongodb.com/manual/installation/)
    
### Suggested IDE

The project has been built thus far with Intellij
   - [Intellij](https://www.jetbrains.com/idea/) 
     - Plugins
        - Scala Plugin
        - VueJs Plugin
        - ScalaFmt Plugin 

### Getting started

#### Setting up front end keys

1) Inside `ui/src` add a file named `keys.js`

2) Copy the contents of `ui/src/example-keys.js` into `keys.js`

3) Fill out fields, you will need to setup an [Auth0](https://auth0.com/) account for development. 

* Be sure to leave `example-key.js` in place before pushing. `Keys.js` is being ignored.

* When deployed we will use our Auth0 account using environment variables. 

#### Starting and build app

* This application is not using any of the Scala play views and all the views are served by the [Vue](https://vuejs.org/) code base which is inside the `ui` folder.

* Use any of the sbt commands listed in the below according to the requirement which are working fine with this application.(To see more details of [sbt](http://www.scala-sbt.org/))

``` 
    sbt clean               # Clear existing build files
    
    sbt stage               # Build your application from your project’s source directory 
                     
    sbt run                 # Run both backend and frontend builds in watch mode
    
    sbt dist                # Build both backend and frontend sources into a single distribution
               
    !! sbt test             # Run both backend and frontend unit tests !! *coming soon!  
```

#### Pre-prod Deployment
* Use heroku for testing
    
1) Create a [Heroku](https://heroku.com/) account (if you don't have one) and create a new app.

2) Setup an mLab resource for MongoDb.

3) Change `project/HerokuDeployKeys-example.scala` to `project/HerokuDeployKeys.scala` 
    * Be sure to change the object name to match also
    * Be sure not to remove `project/HerokuDeployKeys-example.scala`
    
4) Fill in all the appropriate fields in `project/HerokuDeployKeys.scala`
    * Fill in the FILL_ME_IN spots

5) Set remote location
    * Add remote to git
    ```
       heroku git:remote -a HEROKU_REMOTE_LOCATION
    ```

* To deploy
    
    ```
       # Prepare app for deployment
       sbt stage 
       
       # Deploy app
       sbt deployHeroku
       
       # Open app (From terminal only)
       heroku open
    ```
### Game Rules to Pass A Step

1) Program must be completely finished running
2) Robot must not be carrying anything
3) Robot must be standing on the portal
4) Correct amount of items must be placed on portal (if problem exists)
5) Special parameters must be met (if any)

### Implementing New Levels

* Rules
    * Levels are located in `conf/assets`
    * Levels and steps are a bi-direction linked list data structure
    * The users stats will be automatically updated in the database when a change to the levels is detected
    * Each level must have at least a single step

#### Adding a level

1) Add a file to the assets directory
    - File name must match the level name
    
2) Copy this json into the file
```
{
  "level": "<{string}LEVEL NAME (MUST MATCH FILE NAME)>",
  "prevLevel": "<{string}PREVIOUS LEVEL NAME (IF NONE TYPE "None">",
  "nextLevel": "<{string}NEXT LEVEL NAME (IF NONE TYPE "None">",
  "show": <{boolean}(If the level's previous is "None" this field must be true)>, 
  "steps": {}
}
```

3) Fill out the json

#### Adding steps to the level

1) Add this json to the `"steps"` field 

```$xslt
 "<NAME OF STEP (must start with a capital and be camel cased)>": {
      "level": "<{string}LEVEL NAME (must match the level name exactly)>",
      "step": "<{string}MUST MACH NAME OF STEP EXACTLY>",
      "gridMap": <{array[string]}SEE `Creating a Grid Map` SECTION>,
      "description": <{string}SEE `Creating a Description` SECTION>, 
      "mainMax": <{int}<ALLOWABLE COMMANDS IN MAIN (if infinite -1)>,
      "robotOrientation": <{int}ROBOTS STARTING ORIENTATION (0, 90, 180, 270 ~ only)>,
      "stagedEnabled": <{boolean}INDICATES IF THE USER IS ABLE TO OPEN THE STAGED FUNCTION POPOVER>,
      "activeEnabled": <{boolean}INDICATES IF THE USER CAN ADD ANYTHING TO ACTIVE FUNCTIONS (almost always true)>,
      "stagedQty": <{int}QTY OF STAGED FUNCTIONS AVAILABLE (if infinite -1)>,
      "assignedStaged": <{object[string, string]}SEE `Adding Assigned Staged` SECTION>,
      "activeQty": <{int}INDICATES HOW MANY STAGED FUNCTIONS CAN BE MOVED TO ACTIVE FUNCTIONS (if infinite -1)>,
      "preBuiltActive": <{object[string, array[string]}SEE `Adding Pre-Built Actives` SECTION>,
      "cmdsAvailable": <{array[string]}ARRAY OF COMMAND NAMES, SEE `Command Names` SECTION FOR COMMAND NAMES>,
      "specialParameters": <{array[string]}LIST OF SPECIAL PARAMETERS, SEE `Special Parameters` SECTION FOR CURRENT SPECIAL PARAMETERS>,
      "problem": <{string}SEE `Building a Problem` SECTION FOR PROPER PROBLEM SYNTAX,
      "clearMain": <{boolean>INDICATES IF MAIN SHOULD BE EMPTY AT THE START OF THE LEVEL>,
      "prevStep": <{String}PREVIOUS STEP NAME (Should be "None" if first step)>,
      "nextStep": <{String}NEXT STEP NAME (Should be "None" if last step)>
    },
```

#### Command Names 
* Command names can also be found in `app/model/DefaultCommands.scala`

```$xslt
  "changeRobotDirection"
  "moveRobotForwardOneSpot"
  "setItemDown"
  "pickUpItem"
```

#### Creating a Grid Map
* Legend
  
```$xslt
(R) - Robot Spot
($) - Portal
|E|	- Robot can go
|W|	- Robot can't go
[1]	- Self replenishing - value 1
[10] - Self replenishing - value 10
[100] - Self replenishing - value 100
[1g] - Self replenishing - value 1,000
[10g] - Self replenishing - value 10,000
[TS(<TOOL>,<TOOL>)] - None self replenishing tools (comma sperated, no spaces) - e.g [TS(1,10,100,1g,10g)]
```
* Grid map creation
    * Use the above legend to fill in the grid map
    * We have only extensively tested a 5 x 10 grid however the grid can be any dimension! 
```$xslt
[
    "[1]   |E| |E| |E| |E| |E| |E| |E| |E| |E|",
    "[10]  |E| |E| |E| |E| |E| |W| |W| |W| |W|",
    "[100] |E| |E| |E| |E| |E| |W| |E| |E| |E|",
    "[1g]  |E| |E| |E| |E| ($) |W| |E| |E| |E|",
    "[10g] |E| |E| |E| |E| (R) |W| |E| |E| |E|"
]
```

#### Creating a Description
* Include the following in the description to be converted 
```$xslt
!!P!! - includes built problem in description
!!L!! - includes level name in description
!!S!! - includes step name in description
!![img]src='<IMG SRC (Must be in single quotes)>'!! - include an image in description, will be same size as font
```

#### Adding Assigned Staged
* Adds specified staged functions to staged functions
1) Add image resource url in `ui/src/assets/assets.js` to `funcImages`
    
2) Create assigned staged object in json
```$xslt
{
    "<NAME OF ASSIGNED STAGED (Must start with a capital letter and be camel cased>": <{string}IMAGE NAME IN ASSESTS>
}
```

#### Special Parameters 
* These are special requirements beyond the regular requirements (see `Game Rules to Pass a Step`) to pass this step

Current Special Parameters
```$xslt
    "functionRequired" - user must use a built function
```

#### Building a Problem
* {int} - becomes an integer with that many digits
* int - becomes self
```$xslt
"{1} + {2} * ({6} - {4}) - 55" == "4 + 65 * (123456 - 4513) - 55"
```


