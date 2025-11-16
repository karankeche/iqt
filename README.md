# Interview Question Tracker

> A simple JavaFX desktop application to track interview questions, answers, and requests — backed by a MySQL database.

---

## Demo / Screenshots

> Replace the example paths below with your actual screenshot files. Put screenshots inside an `assets/` folder in the project root (e.g. `assets/screenshot-1.png`).

![Home screen]([assets/screenshot-1.png](https://github.com/karankeche/Intervivew_Question_Tracker/blob/main/src/application/home.png?raw=true))

![Add Question form](assets/screenshot-2.png)



---

## About

This project is a lightweight desktop application built with Java and JavaFX that allows users to:

* Add, edit, and delete interview questions
* Search and filter questions
* Store questions and related metadata in a MySQL database
* Track requests and user interactions (admin panel)

The README below documents what I used to build the app, how the project is organized, and how to run it locally.

---

## What I used to build this project

**Languages & frameworks**

* Java (JDK 11+ recommended)
* JavaFX for the UI (FXML + controllers)

**Database**

* MySQL (8.x recommended) as the backend database
* MySQL Connector/J (JDBC driver)

**Tools & Extensions**

* Visual Studio Code with *Java Extension Pack* (for editing, building and running Java projects)
* Scene Builder (optional; used to design FXML layouts visually)
* Git (version control)

**Libraries / Dependencies**

* `mysql-connector-java` (JDBC driver)
* Any additional libraries you used (e.g., logging library). Add them to `lib/` or your build tool configuration.

---

## Project structure

```
project-root/
├─ src/                     # Java source files (controllers, models, main)
│  ├─ com/yourorg/app/
│  │  ├─ Main.java
│  │  ├─ controller/
│  │  ├─ model/
│  │  └─ util/
├─ lib/                     # External .jar libraries (e.g. mysql-connector-java.jar)
├─ assets/                  # Put screenshots here (e.g. screenshot-1.png)
├─ config/                  # Optional: database schema or SQL scripts
│  └─ schema.sql
├─ bin/                     # Compiled output (auto-generated)
├─ .vscode/
│  └─ settings.json         # VS Code Java settings (if customized)
├─ README.md                # <-- this file
```

---

## Database (example schema)

Below is a simple SQL schema you can use as a starting point. Save this as `config/schema.sql` and run it in your MySQL instance.

```sql
CREATE DATABASE IF NOT EXISTS interview_tracker;
USE interview_tracker;

CREATE TABLE IF NOT EXISTS questions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  question TEXT NOT NULL,
  answer TEXT,
  tags VARCHAR(255),
  difficulty ENUM('Easy','Medium','Hard') DEFAULT 'Medium',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS requests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  requester VARCHAR(255),
  description TEXT,
  status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Note:** update your connection credentials in the application configuration before running.

---

## How to run locally (basic steps)

1. **Install JDK** — Install Java 11 or newer and set `JAVA_HOME`.
2. **Install MySQL** — Start MySQL server and create the DB using the `config/schema.sql` script above.
3. **Add JDBC driver** — Put `mysql-connector-java-x.x.x.jar` inside the `lib/` folder or add it to your classpath / build tool.
4. **Configure DB connection** — Edit the DB config file or `DBConfig`/`DatabaseUtil` class in `src` to set your DB URL, username, and password. Example:

```java
String url = "jdbc:mysql://localhost:3306/interview_tracker?useSSL=false&serverTimezone=UTC";
String user = "root";
String pass = "your_password";
```

5. **Open the project** in VS Code (File → Open Folder).
6. **Run the application** — Run `Main.java` from the editor or use the Run command provided by the Java Extension Pack. If you compile manually, ensure the JavaFX runtime modules are available on the module path (if you use modular setup).

---

## VS Code tips

* If VS Code suggests creating `.vscode/settings.json` for Java compilation output or JavaFX VM arguments, follow those hints.
* If your JavaFX runtime is not found at runtime, add the JavaFX SDK VM arguments in the `launch.json` or run configuration.

---

## Screenshots — how to add them here

1. Create an `assets` folder at project root.
2. Save your screenshots as `screenshot-1.png`, `screenshot-2.png`, etc.
3. The markdown at the top of this README includes the image references. When viewing the repository on GitHub or VS Code with markdown preview, the screenshots will appear.

---

## What to add / customize

* Replace the placeholder screenshots with real images taken from the app.
* Add a section listing the third-party libraries you included, with exact versions.
* If you used a build tool (Maven/Gradle), add the relevant `pom.xml` or `build.gradle` and instructions to build.
* Add a license or contribution guidelines if you plan to share.

---

## License & Credits

This README and the project skeleton were created by the project author. Include a license file (e.g., MIT) if you plan to publish the repo.

---

If you want, I can:

* Convert this README into a real `README.md` file in the project and add sample screenshots provided by you.
* Add exact dependency lines (Maven/Gradle) if you tell me which build tool you used (Maven or Gradle).

Tell me which of these you'd like next.

