### Lunch-Mator
The lunch meetup app. 

![lunch-mator](/ui/src/assets/images/lunchmator.jpg)


## Dev setup
### DB
0. Install postgress (for mac try [this](http://postgresapp.com/))
0. once you have done the install go to terminal and execute the following commands
0. create a database called lunch_mator 
  * `createdb lunch_mator`
0. create user app
  * `createuser -P -s -e app`
  * When prompted, provide the password as `root` 

# NPM and UI
0. You will need NPM 8.11.3 
0. [nvm](https://github.com/creationix/nvm) is an easy to use Node Env Manager
0. navigate to `lunch-mator/ui`
0. execute `npm install`
0. next up execute `npm run build` this will build the UI (you should not see any errors)
0. if you are developing the UI you can run `npm run build-watch`

# Scala Play application
The backend is a scala play 2.6 application inorder to run this you will need to install sbt
0. try `brew install sbt@1`
0. once you have sbt you can execute 
`sbt run -Dconfig.resource=application.test.conf -Dlogger.resource=test-logback.xml`
in the project root. This is start up the play application server(**NOTE:** This will not build the UI that you will need
to do manually, follow steps above)
0. Once you have the application running, you can navigate to `http://localhost:9000` to see the application in action

### Run 
#### For dev
```bash 
sbt run -Dconfig.resource=application.test.conf -Dlogger.resource=test-logback.xml
```
#### For prod
```bash
sbt dist
``` 
and then 
```bash
unzip DIST_FILE
sudo DIST_FILE/bin/lunch-mator -Dhttp.port=80
```
