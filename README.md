Book Shop - Android E-Commerce App
A feature-rich e-commerce bookshop application built for Android using 100% Jetpack Compose and modern development practices. The app provides a complete sales and inventory management experience with distinct roles for administrators and customers.

Demo

<div align="center"> <table> <tr> <td><img src="https://github.com/user-attachments/assets/6b05d8b6-5218-4c79-abd8-1e585bf88632" width="200"/></td> <td><img src="https://github.com/user-attachments/assets/915a6bc3-8c87-4968-944a-2c45518c185d" width="200"/></td> </tr> <tr> <td><img src="https://github.com/user-attachments/assets/2b4657de-992a-48de-8b5e-b827d070c8ff" width="200"/></td> <td><img src="https://github.com/user-attachments/assets/4daabc97-cad6-46c2-b73c-280953451ad7" width="200"/></td> </tr> <tr> <td><img src="https://github.com/user-attachments/assets/a86508e6-f0b0-4866-8aa3-804e4bc4f5d8" width="200"/></td> <td><img src="https://github.com/user-attachments/assets/5fff344a-96b7-46e8-a82f-e1cc955524ec" width="200"/></td> </tr> <tr> <td><img src="https://github.com/user-attachments/assets/bf4af7fb-63de-413e-8e11-99db2245598c" width="200"/></td> </tr> </table> </div>


Key Features
Dual User Roles: Secure login system for separate Admin and Customer functionalities.

Admin Panel:
View and manage current stock levels.
Add or update book inventory.
Auto-fill book details via the Google Books API by providing an ISBN.

Customer Experience:
Browse the book catalog.
Add items to a shopping cart with real-time price updates.
Complete a multi-step checkout process with stock validation and postage calculation.
View and share a persistent order history.

Advanced Sensor Integration:
Scan physical ISBN barcodes using CameraX & ML Kit to instantly add books to the cart.
Pre-fill the shipping address during checkout using the device's GPS location.


Tech Stack & Architecture

| Layer             | Technology                    |
| ----------------- | ----------------------------- |
| UI                | Jetpack Compose               |
| Architecture      | MVVM                          |
| Local Database    | Room                          |
| Networking        | Retrofit                      |
| Async Programming | Coroutines & Flow             |
| Device APIs       | CameraX, ML Kit, Location API |


Setup & Run
Clone the repository:
Bash
git clone https://github.com/Aryan23b/BookShop

Open in Android Studio
Let Gradle sync dependencies
Run the app on an emulator or physical device



Test Accounts
Use the following credentials to test the application:

Admin Account:

Username: admin

Password: p455w0rd

Customer Accounts:

Username: customer1 / customer2

Password: p455w0rd
