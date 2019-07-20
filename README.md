# strava-activity-uploader
> A server reading zip / gps file from email and upload to Strava periodically

[![CircleCI](https://img.shields.io/circleci/build/github/thcathy/strava-activity-uploader/master.svg)](https://circleci.com/gh/thcathy/strava-activity-uploader/tree/master)
[![codecov](https://codecov.io/gh/thcathy/strava-activity-uploader/branch/master/graph/badge.svg)](https://codecov.io/gh/thcathy/strava-activity-uploader)
![GitHub](https://img.shields.io/github/license/thcathy/strava-activity-uploader.svg)

## Features
- support any email server through IMAP
- automatically extract ZIP attachments
- upload file in types: fit, fit.gz, tcx, tcx.gz, gpx, gpx.gz
- login Strava by OAuth2 ([more info](https://developers.strava.com/docs/authentication/))
- start using docker with minimum setup
- low memory footprint (compare to Spring Boot)

## Table of Contents

- [Prerequisites](#Prerequisites)
- [How to use](#How-to-use)
- [Application Parameters](#Application-parameters)
- [Development setup](#Development-setup)
- [Support or Request for new feature](#Support-or-Request-for-new-feature)
- [Contributing](#Contributing)
- [License](#license)

## Prerequisites
### 1. Email account 
Register an email address on provider support IMAP protocol. e.g. Gmail, Yahoo! Mail, Outlook.com  
Suggest to use an email address only for upload activity

### 2. Create an app account in Strava Settings
#### Step 1: Create a Strava account if you do not have one

#### Step 2: Go to Strava developer (https://developers.strava.com) and click "Create & Manage Your App"  
![Strava API](/image/strava-api.png)

#### Step 3: Input following, then click "Create" 
  1. Application Name (Any)
  2. Website (Any)
  3. Authorization Callback Domain  
  If you start your server in your local PC, set to "localhost". otherwise, please input your domain of your server, e.g. myserver.vultr.com
  
![Create App](/image/create-app.png)
   
#### Step 4: In "My API Application" page, remember 1. "Client ID" and 2. "Client Secret" for starting uploader  
![App settings](/image/app-settings.png)

## How to use?
### 1. Start the server. Either use a) docker or b) java jar 
#### a. By docker (recommended)
Run docker image "thcathy/strava-activity-uploader". Replace the required environment variables.  
Add "CALLBACK_HOST=<your server host and port>" if you start the application in your server.
```
docker run --name strava-activity-uploader \
  -e STRAVA_CLIENT_ID=<strava's client id> \
  -e STRAVA_CLIENT_SECRET=<strava's client secret> \
  -e MAIL_HOST=<email's server host> \
  -e MAIL_USERNAME=<email's username> \
  -e MAIL_PASSWORD=<email's password> \
  -p 4567:4567 \ 
  -d thcathy/strava-activity-uploader
```

#### b. Java command line (require Java version 10+ installed)
Download jar file from [release page](https://github.com/thcathy/strava-activity-uploader/releases). 
Then run java command in console. Replace the required properties
```
java \
-Dstrava.client_id=<strava's client id> \
-Dstrava.client_secret=<strava's client secret> \
-Dmail.host=<email's server host> \
-Dmail.username=<email's username> \
-Dmail.password=<email's password> \
-jar strava-activity-uploader.jar
```

### 2. Login Strava in uploader
Step 1: Open "http://localhost:4567/strava/login" (replace 'localhost:4567' if you start application in your server)  
Step 2: Login your Strava account and Authorize the application  
Step 3: If login successfully, browser will show "success". You can close the browser. The application will upload any activity in email periodically. 

### 3. Send email with attachment to email account set in startup parameters
Application will check email for each 3 minutes (can be changed by configuration)  
Then upload any activity files (fit, fit.gz, tcx, tcx.gz, gpx, gpx.gz) to Strava

## Application Parameters
Required | Environment Variable | Property | Example | Description
:--- | :--- |:--- |:--- 
Required | STRAVA_CLIENT_ID | strava.client_id | 12345 | Client ID show in Strava API Application page
Required | STRAVA_CLIENT_SECRET | strava.client_secret | <40 characters string> | Client Secret show in Strava API Application page
Required | MAIL_HOST | mail.host | imap.gmail.com | Email server host
Required | MAIL_USERNAME | mail.username | | Email account username
Required | MAIL_PASSWORD | mail.password | | Email account password
Optional | CALLBACK_HOST | callback.host | http://strava.myserver.com:4567 | host and port of the server which application started
Optional | JOB_INTERVAL_SECOND | job.interval.second | 300 (default) | Interval of checking email

## Development setup
### Build application
1. Install JDK version 10+

2. Clone source from github
```
git clone https://github.com/thcathy/strava-activity-uploader.git
```
3. Build runnable jar using gradle
```
./gradlew assemble shadowJar
```
4. Jar is built in build/libs/strava-activity-uploader.jar

### Running tests
Run all tests using gradle
```
./gradlew check
```

## Support or Request for new feature
Create new issue at [link](https://github.com/thcathy/strava-activity-uploader/issues/new)

## Contributing
1. Fork it (https://github.com/thcathy/strava-activity-uploader/fork)
2. Create your feature branch (git checkout -b feature/fooBar)
3. Commit your changes (git commit -am 'Add some fooBar')
4. Push to the branch (git push origin feature/fooBar)
5. Create a new Pull Request

## License
This project is licensed under the CPL-3.0 License - see the [LICENSE](LICENSE) file for details