MathBot
---

Learn counting to calculus by programming a robot!

### Prerequisites
  - NodeJS v- 7.10.1
    - Use [N](https://github.com/tj/n)
  - WebPack installed globally
    - ```npm install webpack -g```
  - MongoDB install globally
    - Goto [MongoDb](https://docs.mongodb.com/manual/installation/)

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





