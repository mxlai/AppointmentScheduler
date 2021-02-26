# Appointment Scheduler
A scheduling application built using Java, MySQL, lambda expressions, streams and filters, and localization and date/time APIs.

## Features
* Functionality for creating, modifying and deleting customers and appointments. 
* Generates notifications for upcoming appointments. 
* Generate reports.
* Localization in English and German.
* Automatically adjusts dates and times for user location and daylight savings time.

## Log-In
* Use the following user credentials:
  - username: test
  - password: test
* Users must log-in to access the main screen and other application functions
* Users cannot be added or edited from within the application. Contact your database administrator for assistance with users.
* Error messages pertaining to log-in are displayed on the log-in window.
* Upon log-in, a notification window will be created for all appointments beginning in the next 15 minutes that are associated with the user who just logged-in.
* Log-ins are recorded to the log.txt file in the root of the project folder.
  - Each log-in is recorded with the user and timestamp of the log-in.
  - New records are appended to the end of the existing file.

## Calendar
* Calendar opens in a monthly view on the current date.
* Calendar can be swapped between monthly and weekly views selecting the appropriate option from the drop-down menu.
* The month or week can be changed by selecting a date from the calendar menu.

## Customers
* Customers can be added via the Add Customer window.
  - The Add Customer window is opened with the Add Customer button on the main screen.
  - Customer information is validated upon submission.
  - Customer information that matches an active customer already in the database will be rejected.
  - Customer information that matches an inactive customer already in the database will prompt an option to set that customer to active.
* Customers can be modified via the Update Customer window.
  - The Update Customer window is opened by selecting a customer from the table and then using the Update button on the main screen.
* Customers can be deleted using the Delete button.
  - A customer must be selected from the table to be deleted.

## Appointments
* Upcoming scheduled appointments can be viewed in the table in the Calendar window.
* Appointments can be created via the Add Appointment window.
  - The Add Appointment window is opened with the Add Appointment button on the main screen.
  - Appointment information is validated upon submission.
  - Appointment times are checked to not be outside business hours (9AM-5PM) and not to overlap with any other appointments.
* Appointments can be modified via the Update Appointment window.
  - The Modify Appointment windows is opened by selecting a customer from the table in the Appointment Summary and then using the Update button.
* Appointments can be deleted via the Delete button in the Appointment Summary window.
  - An appointment must be selected from the table to be deleted.
  - Deleting an appointment also deletes the appointment entry from the database.
	
## Reports
* Reports can be generated from the Reports window.
  - The Reports window is opened with the View Reports button on the main screen.
* Reports that can be generated include the following:
  - Number of Appointment Types by Month
  - Upcoming Schedule for Each Consultant
  - Number of appointments by location
