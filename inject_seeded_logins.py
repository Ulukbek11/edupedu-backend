import json

with open("edupedu_collection.json", "r") as f:
    collection = json.load(f)

logins = [
    {"name": "Login - Super Admin", "email": "superadmin@edupage.com", "password": "super123"},
    {"name": "Login - University Admin", "email": "admin@edupage.com", "password": "admin123"},
    {"name": "Login - Accountant", "email": "finance@edupage.com", "password": "finance123"},
    {"name": "Login - Teacher (John)", "email": "instructor@edupage.com", "password": "instructor123"},
    {"name": "Login - Teacher (Jane)", "email": "teacher1@edupage.com", "password": "teacher123"},
    {"name": "Login - Student (Alice)", "email": "student1@edupage.com", "password": "student123"}
]

auth_folder = {
    "name": "0. Seeded Logins (Run to set token)",
    "description": "Run any of these to automatically set the `accessToken` environment variable for your subsequent requests.",
    "item": []
}

for login in logins:
    req = {
        "name": login["name"],
        "event": [
            {
                "listen": "test",
                "script": {
                    "exec": [
                        "var jsonData = pm.response.json();",
                        "if (jsonData.access_token) {",
                        "    pm.environment.set(\"accessToken\", jsonData.access_token);",
                        "    console.log(\"Token automatically set in environment.\");",
                        "}"
                    ],
                    "type": "text/javascript"
                }
            }
        ],
        "request": {
            "method": "POST",
            "header": [
                {
                    "key": "Content-Type",
                    "value": "application/json"
                }
            ],
            "body": {
                "mode": "raw",
                "raw": json.dumps({"email": login["email"], "password": login["password"]}, indent=2)
            },
            "url": {
                "raw": "{{baseUrl}}/api/v1/auth/login",
                "host": ["{{baseUrl}}"],
                "path": ["api", "v1", "auth", "login"]
            }
        },
        "response": []
    }
    auth_folder["item"].append(req)

# Insert the logins folder as the first item in the collection
collection["item"].insert(0, auth_folder)

with open("edupedu_collection.json", "w") as f:
    json.dump(collection, f, indent=2)

print("Seeded logins injected into edupedu_collection.json")
