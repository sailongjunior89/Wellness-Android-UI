# 🌿 Wellness AI Mobile Application

> A full-stack AI-powered wellness tracking application built with
> **Android (Kotlin)**, **Spring Boot**, **Python (FastAPI)**,
> **OpenAI**, and **MySQL**.

## 📖 Overview

The Wellness AI Mobile Application helps users monitor their daily
wellness habits while receiving AI-powered recommendations and wellness
guidance.

This project demonstrates full-stack software engineering skills
including Android development, backend API design, database design,
authentication, and AI integration.

------------------------------------------------------------------------

## ✨ Features

### 🔐 Authentication

-   User registration
-   Secure login
-   JWT authentication
-   BCrypt password encryption
-   Logout support

### 📊 Dashboard

-   Personal wellness summary
-   Recent wellness records
-   AI recommendations
-   Quick navigation

### 📝 Wellness Tracking

Users can record: - Sleep hours - Exercise - Water intake - Mood -
Stress level - Weight - Calories

Supports Create, Read, Update and Delete (CRUD).

### 🤖 AI Wellness Assistant

-   AI chatbot
-   Wellness Q&A
-   Healthy lifestyle guidance
-   Personalized responses

### 🧠 AI Recommendation Engine

-   Reviews wellness records
-   Detects trends
-   Generates personalized recommendations

### 👤 User Profile

-   Personal information
-   Height & weight
-   Date of birth
-   Fitness goals

------------------------------------------------------------------------

# 🏗 Architecture

``` text
Android (Kotlin)
      │
 Retrofit REST API
      │
Spring Boot Backend
      │
 ├── JWT Security
 ├── Business Logic
 ├── MySQL
 └── Python FastAPI
          │
      OpenAI API
```

------------------------------------------------------------------------

# 🛠 Technology Stack

## Mobile

-   Kotlin
-   Android Studio
-   Material Design
-   Retrofit
-   RecyclerView
-   ViewBinding

## Backend

-   Java
-   Spring Boot
-   Spring Security
-   Spring Data JPA
-   JWT
-   Maven

## AI

-   Python
-   FastAPI
-   OpenAI API

## Database

-   MySQL

## Tools

-   Git
-   GitHub
-   Postman
-   IntelliJ IDEA
-   Android Studio
-   VS Code

------------------------------------------------------------------------

# 📂 Project Structure

``` text
Android
 ├── Activities
 ├── Fragments
 ├── API
 ├── Adapter
 ├── Models
 └── Utilities

Spring Boot
 ├── Controller
 ├── Service
 ├── Repository
 ├── Entity
 ├── DTO
 └── Security

Python AI
 ├── FastAPI
 ├── Chat Service
 └── Recommendation Service
```

------------------------------------------------------------------------

# 🔒 Security

-   JWT Authentication
-   BCrypt Password Hashing
-   Role-based Authorization
-   Secure REST APIs

------------------------------------------------------------------------

# 📡 REST API

  Method   Endpoint
  -------- -----------------------
  POST     `/api/auth/register`
  POST     `/api/auth/login`
  GET      `/api/dashboard`
  GET      `/api/profile`
  PUT      `/api/profile`
  GET      `/api/records`
  POST     `/api/records`
  PUT      `/api/records/{id}`
  DELETE   `/api/records/{id}`
  POST     `/api/chat`
  GET      `/api/recommendation`

------------------------------------------------------------------------

# 🚀 Running the Project

## Backend

``` bash
mvn spring-boot:run
```

## Python AI

``` bash
pip install -r requirements.txt
python main.py
```

## Android

Open the Android project in Android Studio and configure:

``` properties
BASE_URL=http://10.0.2.2:8080/
```

------------------------------------------------------------------------

# 💼 Skills Demonstrated

-   Android Development
-   Kotlin
-   Java
-   Spring Boot
-   REST API Development
-   JWT Authentication
-   MySQL Database Design
-   AI Integration
-   FastAPI
-   Client-Server Architecture
-   Git Version Control

------------------------------------------------------------------------

# 🔮 Future Improvements

-   Google Sign-In
-   Food Image Recognition
-   Barcode Nutrition Scanner
-   Wearable Device Integration
-   Push Notifications
-   AI Meal Planning

------------------------------------------------------------------------

# 👨‍💻 About This Project

This project showcases my ability to design and develop a modern
full-stack mobile application from frontend to backend, including secure
authentication, RESTful APIs, AI-powered features, and database
integration.

It demonstrates practical software engineering skills applicable to
enterprise Android and Java development roles.

------------------------------------------------------------------------

## 📜 License

This project is provided for educational and portfolio purposes.
