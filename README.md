# strava-activity-uploader
> A server reading zip / GPX file from email and upload to Strava periodically

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

## How to use?

### Prerequisites
#### 1. Email account 
Register an email address on provider with IMAP protocol. e.g. Gmail, Yahoo! Mail, Outlook.com  
Suggest to use a new email address to reduce duplicate messages proceed.

#### 2. Strava API
Open Strava developer https://developers.strava.com

