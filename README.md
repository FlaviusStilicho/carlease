# Getting Started

### System Requirements
* At least Java 11 to run the application
* Unused port 8091 for the application
* Postman or equivalent tool for testing
* (Optional) Docker, when running the application with the included docker-compose deployment. This deployment uses 
MySql. This deployment also includes an adminer container, which uses port 8092.


### Setup
* Pull the project to a directory of your choice
* Open your Create a jar file using mvn package
* in order to launch the dockerized deployment:
    * If running on windows, simply open the project directory in cmd and run launch.bat. 
    * If running on a different OS, copy the commands from that file and run them in your favorite shell. 
 * Else, the application can also be run using an H2 database. Using this configuration requires adding the H2 dependency 
     in the pom.xml (currently commented out) and also uncommenting the properties for the h2 database in 
     application.properties. You can then launch the application from your IDE.
 
### Data model
See the ERD.JPG file in the project root dir for visualization.
The business data in the application is modeled using 3 entity classes: Customer, Car and Contract. Both customers and 
cars exist on their own, and can be created through API calls. Contracts each hold a reference to one customer and one
car each. multiple contracts can reference the same customer, but a car can only be referenced by one contract at a time.
After a contract has ended, the car can be reassigned to a new contract.

### Authorization and Role Based Access Control   
The application uses simple username/password authentication. Once a user logs in successfully, the application sends
a JWT token back. For all subsequent calls, the user must include this token in the Authorization header.

There are two roles: Broker and Lease. 

A user with the Broker role may:
* manage (CRUD) customer information
* request the premiums / lease rate for a customer
* reassign a contract to a different customer
* end a contract
* view any data related to customers, contracts and cars, including history of changes to the data.

A user with the Lease role may:
* manage (CRUD) contract and car information information
* view any data  related to contracts and cars, including history of changes to the data.

### API call testing
See below a guide for testing the application using postman, including a list of the endpoints and some data for input.
The DemoData class files ensures that there is already some data in the application on startup, including two accounts.

First, check if the application is running:
GET http://localhost:8091/api/healthcheck

Then, check out the swagger UI using this url:
http://localhost:8091/swagger-ui.html

Then login as either a broker or a lease user with the following login info in the body:
POST http://localhost:8091/api/authenticate
username: test_broker 
password: test1

username: test_lease 
password: test2

For the subsequent calls, copy the JWT from the response and add an authorization header to the calls with value 
"Bearer <Token>"

As broker:
* GET /api/customers
* GET /api/customers/1
* POST /api/customers 
{
    "name": "Darth Vader",
    "emailAddress": "darth.vader@galactic-empire.com",
    "phoneNumber": "1234567890",
    "street": "Vader's Fortress",
    "number": "1",
    "zipcode": "1234AB",
    "city": "Mustafar"
}
* PUT /api/customers/1 
{
        "name": "Luke Skywalker",
        "emailAddress": "l.skywalker@rebelalliance.com",
        "phoneNumber": "1234567890",
        "street": "Rebel Base",
        "number": "2",
        "zipcode": "1234AB",
        "city": "Hoth"
 }
* DELETE /api/customers/4 (deletingrecords 1 or 2 will result in an error)
* GET /api/customers/history/1

As Lease:
GET /api/cars
GET /api/cars/1
POST /api/cars
{
    "carBrand": "Kuat Drive Yards",
    "model": "Imperial Star Destroyer",
    "modelVersion": "3",
    "numberOfDoors": 20000,
    "co2EmissionsPerct": 90.00,
    "grossPrice": 2000000.00,
    "netPrice": 1500000.00,
    "currentlyAssigned": false
}
PUT /api/cars/1 
    {
        "carBrand": "Incom",
        "model": "X-Wing Starfighter",
        "modelVersion": "1",
        "numberOfDoors": 1,
        "co2EmissionsPerct": 50.00,
        "grossPrice": 30000.00,
        "netPrice": 20000.00,
        "currentlyAssigned": false
    }
DELETE /api/cars/4
GET /api/cars/history/1

GET /api/contracts
GET /api/contracts/1
POST /api/contracts
{
    "contract": {
    "currentContractStartDate": [
        2019,
        1
    ],
    "currentContractEndDate": [
        2022,
        1
    ],
    "yearlyMileageKm": 40000,
    "interestRate": 3.5
    },
    "customerId": 3,
    "carId": 4
}

PUT /api/contracts/1 
{
    "currentContractStartDate": [
            2019,
            1
        ],
        "currentContractEndDate": [
            2022,
            1
        ],
        "yearlyMileageKm": 40000,
        "interestRate": 3.5
}

DELETE /api/contracts/1
GET /api/contracts/history/1
GET /api/contracts/leaserate/1
PUT /api/contracts/end/1

### Opportunities for improvement
* Business Logic
The most immediate thing in my opinion that should be improved is the logic for managing the relationships between 
contracts and cars. Currently, every contract is bound to one car, and has a start- and enddate. When a car is assigned 
to a contract, it has a boolean flag 'currently assigned' which is set to 'true', which prevents it from being assigned 
to a different contract at the same time. After the contract is ended, the car is 'released' for re-use. However, there
is currently no way to plan a next contract before a current contract is still active.

* Testing
The application does not have full test coverage, particularly concerning controller methods. 
This can be improved.

* Documentation
While all public methods have been documented with javadocs, this is not the case for all service methods.






