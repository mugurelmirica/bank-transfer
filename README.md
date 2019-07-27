# Bank Transfer
Sample application to demonstrate transfer between two bank accounts.

Application starts on the port given as first argument on command line, or defaults to 7000.

---
This app uses [Maven](https://maven.apache.org/download.cgi), so make sure you have it installed.

Navigate to project dir if you want to run the following:

Package:
`mvn package`

Run jar:
`java -jar bank-transfer-1-jar-with-dependencies.jar`

Run tests:
`mvn clean test`

---
## Endpoints:

* `POST /api/holder` - create an account holder
* `GET /api/holder/` - list all holders
* `GET /api/holder/:id` list specific holder
* `POST /api/account` - create an account
* `GET /api/account` - list all accounts
* `GET /api/account/:id` - get specific account
* `GET /api/account/:id/transfers` - list all transfers for an account
* `POST /api/transfer` - create and process a transfer
* `GET /api/transfer/:id` - get info about a transfer

---
## Requests examples:

POST /api/holder
`{
	"name": "Holder name"
}`

POST /api/account
`{
	"holderId": 1,
	"iban": "NL56ABNA5353131762",
	"currency": "EUR",
	"initialAmount": 100
}`

POST /api/transfer
`{
	"sourceAccountId": 1,
	"targetAccountId": 2,
	"currency": "EUR",
	"amount": 100
}`