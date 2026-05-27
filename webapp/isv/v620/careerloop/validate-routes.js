const fs = require("fs");
const path = require("path");

const routePath = path.join(__dirname, "careerloop-routes.json");
const appPath = path.join(__dirname, "assets", "app.js");
const routeMap = JSON.parse(fs.readFileSync(routePath, "utf8"));
const app = fs.readFileSync(appPath, "utf8");

const requiredRoutes = [
  "workbench",
  "employment-insight",
  "career-resources",
  "messages"
];

const requiredApis = [
  "/cc001/career-employment/insight/get",
  "/cc001/career-employment/resources/list",
  "/cc001/notifications/list",
  "/cc001/notifications/unread-count",
  "/cc001/notifications/read",
  "/cc001/notifications/subscription/grant",
  "/cc001/notifications/subscription/quota",
  "/cc001/notifications/weekly-report/run",
  "/cc001/career-profile/snapshot/get",
  "/cc001/career-agent/today/get"
];

function fail(message) {
  console.error(message);
  process.exit(1);
}

for (const key of requiredRoutes) {
  if (!routeMap.routes.some((route) => route.key === key && route.status === "available")) {
    fail(`Missing available route: ${key}`);
  }
  if (!app.includes(key)) {
    fail(`Static app does not reference route: ${key}`);
  }
}

const serialized = JSON.stringify(routeMap);
for (const api of requiredApis) {
  if (!serialized.includes(api)) {
    fail(`Route map missing API: ${api}`);
  }
}

console.log("careerloop route map ok");
