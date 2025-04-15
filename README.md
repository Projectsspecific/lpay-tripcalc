# LPay Charge Calculator
## Prerequisites
 - Java 17
 - Maven 3+
 - SpringBoot

## Running the application -
1) Clone the project - lpay-tripcalc
git clone https://github.com/Projectsspecific/lpay-tripcalc.git
cd lpay-tripcalc

Build the project - **mvn clean install**
Test the Unit cases-**mvn test**
Start the application - **mvn spring-boot:run**

Testing the endpoint - 
curl --location --request POST 'http://localhost:8080/trips/process'
![image](https://github.com/user-attachments/assets/bb90f554-3a24-4f94-bc5b-68d1ffd42818)

Assumptions:
1) Trip input file is for 1 day period
2) File is sorted by Date/Time
3) For cancelled assigning duration as '0''
4) For the incomplete trips there will be subsequent commas to replace the data not available.
5) For incomplete trips, showing tap-on and tap-off at the end of the row.
6) Passenger can do multiple trips at various points of time so the same PAN can be repeated in different rows in the taps.csv.
