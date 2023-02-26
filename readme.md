Assumptions:

- if primary key is already present for any entry in db, this entry is being updated (it was not specified in requirements if it should be updated, or it should be invalid scenario that should be blocked by service)
- comma was used as data separator in csv file 

Setup for local development:
- docker with mysql database can be setup with command(it has to be started from resource directory for relative paths to work): 

**docker-compose up -d**

Running:

- app can be run and packed to jar using standard maven commands

