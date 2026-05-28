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
  "messages",
  "admin-console",
  "file-upload-preview"
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
  "/cc001/admin/whoami",
  "/cc001/admin/organizations/dashboard",
  "/cc001/admin/users/ban",
  "/cc001/admin/questions/list",
  "/cc001/admin/content/list",
  "/cc001/admin/broadcast",
  "/cc001/admin/audit-log/list",
  "/cc001/files/upload",
  "/cc001/files/preview-url",
  "/cc001/files/download",
  "/cc001/files/delete",
  "/cc001/files/extract-text",
  "/cc001/career-profile/snapshot/get",
  "/cc001/career-agent/today/get"
];

function fail(message) {
  console.error(message);
  process.exit(1);
}

for (const key of requiredRoutes) {
  if (!routeMap.routes.some((route) => route.key === key && (route.status === "available" || route.status === "entry-only"))) {
    fail(`Missing available or entry-only route: ${key}`);
  }
  if (!app.includes(key)) {
    fail(`Static app does not reference route: ${key}`);
  }
}

const serialized = JSON.stringify(routeMap);
const routeKeys = new Set(routeMap.routes.map((route) => route.key));
const mountKeys = new Set();

if (!routeMap.identity || !routeMap.identity.production || !routeMap.identity.development) {
  fail("Route map missing production/development identity metadata");
}
if (routeMap.identity.production.mode !== "cosmic-platform-context") {
  fail("Production identity mode must be cosmic-platform-context");
}
if (!routeMap.identity.production.backendAdapter) {
  fail("Production identity metadata must name backend adapter");
}
if (!routeMap.identity.production.adapterEnablement || routeMap.identity.production.adapterEnablement.property !== "cc001.identity.adapter.enabled") {
  fail("Production identity metadata must document adapter enablement property");
}
if (routeMap.identity.production.adapterEnablement.enabledValue !== "true") {
  fail("Production identity adapter enabled value must be true");
}
if (routeMap.identity.production.adapterEnablement.diagnosticsProperty !== "cc001.identity.adapter.diagnostics.enabled") {
  fail("Production identity metadata must document adapter diagnostics property");
}
if (routeMap.identity.production.adapterEnablement.defaultBehavior !== "disabled-safe-identity-required") {
  fail("Production identity adapter default behavior must remain disabled-safe-identity-required");
}
if (!routeMap.identity.production.disabledBehavior || !routeMap.identity.production.disabledBehavior.includes("identity-required")) {
  fail("Production identity metadata must document disabled identity-required behavior");
}
for (const group of ["userId", "adminId", "orgId", "roles", "ip", "userAgent"]) {
  if (!routeMap.identity.production.candidateFields || !Array.isArray(routeMap.identity.production.candidateFields[group]) || routeMap.identity.production.candidateFields[group].length === 0) {
    fail(`Production identity metadata missing candidate fields: ${group}`);
  }
}
for (const alias of ["ADMIN", "COSMIC_ADMIN", "PLATFORM_ADMIN"]) {
  if (!routeMap.identity.production.adminAliases || !routeMap.identity.production.adminAliases.includes(alias)) {
    fail(`Production identity metadata missing admin alias: ${alias}`);
  }
}
if (routeMap.identity.development.mode !== "explicit-fallback") {
  fail("Development identity mode must be explicit-fallback");
}
for (const status of ["OK", "IDENTITY_REQUIRED", "FORBIDDEN", "IDENTITY_MISMATCH"]) {
  if (!routeMap.identity.backendStatuses || !routeMap.identity.backendStatuses.includes(status)) {
    fail(`Route map missing backend identity status: ${status}`);
  }
}
if (!routeMap.identity.mismatchRule || !routeMap.identity.mismatchRule.includes("conflicts")) {
  fail("Route map missing backend identity mismatch rule");
}
if (!routeMap.sourceEvidence || !routeMap.sourceEvidence.runtimeRule) {
  fail("Route map missing IPD source evidence or runtime rule");
}
if (!Array.isArray(routeMap.platformMounts) || routeMap.platformMounts.length === 0) {
  fail("Route map missing platformMounts");
}

for (const mount of routeMap.platformMounts) {
  if (!mount.mountKey || mountKeys.has(mount.mountKey)) {
    fail(`Invalid or duplicate platform mount key: ${mount.mountKey}`);
  }
  mountKeys.add(mount.mountKey);
  if (!routeKeys.has(mount.routeKey)) {
    fail(`Platform mount references unknown route: ${mount.mountKey} -> ${mount.routeKey}`);
  }
  if (!mount.title || !mount.target || !mount.audience || !mount.requiredRole || !mount.publishability || !mount.identityMode || !mount.fallback || !mount.deploymentNotes) {
    fail(`Platform mount missing required metadata: ${mount.mountKey}`);
  }
  if (mount.publishability === "admin-only" && mount.requiredRole !== "ADMIN") {
    fail(`Admin platform mount must require ADMIN: ${mount.mountKey}`);
  }
  if (mount.publishability !== "admin-only" && mount.requiredRole === "ADMIN") {
    fail(`User platform mount cannot require ADMIN: ${mount.mountKey}`);
  }
}

for (const key of ["workbench", "onboarding", "today-action", "assessment", "resume", "resume-diagnosis", "interview", "career-plan", "assistant", "messages", "file-upload-preview", "employment-insight", "career-resources", "admin-console"]) {
  if (!routeMap.platformMounts.some((mount) => mount.routeKey === key)) {
    fail(`Missing platform mount for route: ${key}`);
  }
}

for (const api of requiredApis) {
  if (!serialized.includes(api)) {
    fail(`Route map missing API: ${api}`);
  }
}

console.log("careerloop route map ok");
