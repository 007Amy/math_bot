# MathBot

#### Learn counting to calculus by programming a robot!

Prerequisites
---

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
    
Suggested IDE
---

The project has been built thus far with Intellij
   - [Intellij](https://www.jetbrains.com/idea/) 
     - Plugins
        - Scala Plugin
        - VueJs Plugin
        - ScalaFmt Plugin 

Getting started
---

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
Game Rules to Pass A Step
---
1) Program must be completely finished running
2) Robot must not be carrying anything
3) Robot must be standing on the portal
4) Correct amount of items must be placed on portal (if problem exists)
5) Special parameters must be met (if any)

Implementing New Levels
---
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
[1]	- Unlimited one tools
[10] - Unlimited ten tools
[100] - Unlimited hundred tools
[1g] - Unlimited thousand tools
[10g] - Unlimted ten-thousand tools
[TS][some tools] - Place tools that can be picked up and moved around into second set of brackets. !! THIS FEATURE NOT IMPLEMENTED YET!!
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


