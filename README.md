# markdown2slides

**markdown2slides** is a web application that allows users to create, edit, and export slide presentations using simple, flexible Markdown syntax. The backend is built in **Kotlin** with **Spring**, providing RESTful APIs for project management and content storage, while leveraging **Pandoc** for Markdown-to-slide conversion.

## 🚀 Getting Started

### 1. 📦 Clone the repository

```bash
git clone https://github.com/adriano9358/markdown2slides.git
cd markdown2slides
```

### 2. 🔑 Set Up Google OAuth2 Client
   
This application uses Google OAuth2 for authentication. To set it up, follow these steps:

1. Go to the Google Cloud Console: https://console.cloud.google.com/auth/clients

2. Create a new project (or select an existing one).

3. Navigate to APIs & Services > Credentials.

4. Click Create Credentials > OAuth client ID. If prompted, configure the consent screen with basic information (application name, support email, etc.).

5. Create a client, choose Web application.

6. Set Authorized redirect URIs to:

```bash
http://localhost:8080/login/oauth2/code/google
```

7. After creating, you’ll receive a Client ID and Client Secret.

### 3. ✏ Configure the Backend
   
Edit the following file:

```bash
host/src/main/resources/application.yaml
```

Update the client-id and client-secret with your Google credentials:

client-id: YOUR_GOOGLE_CLIENT_ID

client-secret: YOUR_GOOGLE_CLIENT_SECRET

### 4. ▶️ Run the App
   
Make sure Docker is installed and running.

Then run:

```bash
docker-compose up --build
```

### 🌐 Accessing the App

Frontend: http://localhost:8000

Backend: http://localhost:8080

### 📁 Data Persistence
Markdown and slide files are stored in the data/ folder in the backend container (mapped to a local volume).

PostgreSQL data is persisted via a named Docker volume.

### 🛑 Stopping the App

To stop everything:

```bash
docker-compose down
```
To stop and remove volumes (⚠️ this will delete your database data):

```bash
docker-compose down -v
```


### 🐞 Troubleshooting

❌ Port already in use? 

Make sure ports 8000 and 8080 are free before running.

❌ OAuth login not working?

Double-check redirect URI in Google Console. Make sure the credentials are correct in application.yaml