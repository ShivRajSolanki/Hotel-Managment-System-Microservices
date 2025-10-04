# Hotel-Managment-System-Microservices
Hotel Management System built with Spring Boot Microservices architecture. Features secure JWT authentication, guest/room management, reservations, Razorpay payments, and automated billing with PDF emails. Scalable, Eureka-based service discovery."
## 🚀 Features
- **Authentication Service**: JWT-based login/signup for staff roles (OWNER, MANAGER, RECEPTIONIST).
- **Guest Service**: CRUD operations for guest profiles with validation.
- **Room Service**: Room management, availability checks, and capacity-based search.
- **Reservation Service**: Book rooms, conflict detection, and availability updates across services.
- **Payment Service**: Secure payments via Razorpay (order creation & verification).
- **Bill Service**: Generate PDF invoices and send via email (JavaMailSender).
- **API Gateway**: Centralized routing, authentication, and role-based access control.
- **Service Discovery**: Netflix Eureka for dynamic service registration.
- **Database**: MySQL per service (JPA/Hibernate).
- **Other**: Swagger for API docs, Lombok for clean code, AOP logging, global exception handling.

## 🛠 Tech Stack
- **Backend**: Spring Boot 3.x, Spring Cloud (Gateway, Eureka, Feign, OpenFeign).
- **Security**: Spring Security, JWT (JJWT).
- **Database**: MySQL, Spring Data JPA.
- **Payments**: Razorpay Java SDK.
- **Other Tools**: Maven, Lombok, iText (PDF), JavaMailSender (Email), Swagger (OpenAPI).
