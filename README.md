# Invoice-Management

This document outlines the requirements for an application that manages user accounts, customer information, and invoices.
The application will have features such as user registration, user login with token-based authentication, user profile management, 
role and permission-based authentication, two-factor authentication, customer information management, and invoice management.

----User
1- User Account

* Users can create new accounts with a unique email address.
* Account verification is required to verify the email address.
* Users should provide details such as name, email, position, bio, phone, address, etc.
* Users should be able to update their personal information.

2- User Login

* Users can log in using their email address and password.
* Token-based authentication (JWT Token) should be used for user authentication.
* Role and Permission-based Authentication

3- Authentication and access control should be based on user roles and permissions.
* User roles can be determined based on their phone number.
* 
4- Two-Factor Authentication
* Two-factor authentication should be implemented using phone numbers.
* Customer Information

-----Customer  
The application should allow users to manage customer information, including name, address, etc.
* Customers can be individuals or institutions.
* Customers should have the ability to generate invoices.
* Search Customers
* Pagination functionality should be implemented for large customer lists.

----Invoices
* Users should be able to create new invoices.
* Invoices should be associated with a specific customer.
* The application should provide options to print invoices for mailing.
* Users should be able to view invoices in spreadsheet format.
* Invoices should be downloadable as PDF files.
