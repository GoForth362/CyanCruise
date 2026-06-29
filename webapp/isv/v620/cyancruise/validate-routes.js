const fs = require("fs");
const path = require("path");

const routePath = path.join(__dirname, "cyancruise-routes.json");
const appPath = path.join(__dirname, "assets", "app-runtime.js");
const assetsPath = path.join(__dirname, "assets");
const stylesPath = path.join(__dirname, "assets", "styles.css");
const panoramaImagePath = path.join(__dirname, "assets", "images", "panorama-interview-room-v1.png");
const panoramaInterviewerImagePath = path.join(__dirname, "assets", "images", "ai-interviewer-human-v1.png");
const routeMap = JSON.parse(fs.readFileSync(routePath, "utf8"));
const app = fs.readFileSync(appPath, "utf8");
const appSources = readJsSources(assetsPath);
const styles = fs.readFileSync(stylesPath, "utf8");

const requiredSplitFiles = [
  "app.js",
  "app-config.js",
  "app-runtime.js",
  "routes.js",
  "api.js",
  "state.js",
  "identity.js",
  "navigation.js",
  "pages/workbench.js",
  "pages/employment-home.js",
  "pages/resume-home.js",
  "pages/resume.js",
  "pages/resume-diagnosis.js",
  "pages/interview-home.js",
  "pages/interview.js",
  "pages/interview-panorama.js",
  "pages/postgraduate.js",
  "pages/recommendation.js",
  "pages/study-abroad.js",
  "pages/assessment.js",
  "pages/career-plan.js",
  "pages/assistant.js",
  "pages/messages.js",
  "pages/admin-console.js",
  "components/feature-card.js",
  "components/message.js",
  "components/form.js",
  "components/dialog.js",
  "components/page-shell.js",
  "components/status-panel.js",
  "components/pager.js",
  "services/resume-service.js",
  "services/interview-service.js",
  "services/profile-service.js",
  "services/further-study-service.js",
  "services/resource-service.js",
  "services/file-service.js",
  "services/admin-service.js"
];

const requiredPageRendererRoutes = [
  "workbench",
  "employment-home",
  "resume-home",
  "resume",
  "resume-diagnosis",
  "interview-home",
  "interview",
  "interview-panorama",
  "postgraduate",
  "postgraduate-recommendation",
  "study-abroad",
  "assessment",
  "career-plan",
  "assistant",
  "messages",
  "admin-console"
];

function readJsSources(dir) {
  let out = "";
  for (const item of fs.readdirSync(dir, { withFileTypes: true })) {
    const itemPath = path.join(dir, item.name);
    if (item.isDirectory()) {
      out += "\n" + readJsSources(itemPath);
    } else if (item.isFile() && item.name.endsWith(".js")) {
      out += "\n" + fs.readFileSync(itemPath, "utf8");
    }
  }
  return out;
}

if (!fs.existsSync(panoramaImagePath)) {
  throw new Error("Missing panoramic interview room image asset");
}
if (!fs.existsSync(panoramaInterviewerImagePath)) {
  throw new Error("Missing AI interviewer human image asset");
}
for (const marker of ["panoramaDeadlineAt", "Date.now()", "ai-interviewer-human-v1.png"]) {
  if (!app.includes(marker)) throw new Error(`Missing persistent panoramic timer or interviewer marker: ${marker}`);
}
for (const marker of ["speechSynthesis", "SpeechSynthesisUtterance", "speakPanoramaQuestion", "panoramaLastSpokenQuestion", "播放题目"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic interviewer speech marker: ${marker}`);
}
for (const marker of [".panorama-ai-caption", "min-height: 430px", "gap: 2px", "max-height: 92px", "min-height: 600px"]) {
  if (!styles.includes(marker)) throw new Error(`Missing panoramic room proportion marker: ${marker}`);
}
for (const marker of [".panorama-question-audio", ".panorama-ai-presence.speaking", "@keyframes panorama-interviewer-speaking", "panorama-speaking-rings"]) {
  if (!styles.includes(marker)) throw new Error(`Missing panoramic interviewer speaking style: ${marker}`);
}
for (const marker of [".panorama-ai-presence img", ".panorama-ai-presence.speaking img", "@keyframes panorama-interviewer-speaking"]) {
  if (!styles.includes(marker)) throw new Error(`Missing panoramic human interviewer style: ${marker}`);
}
for (const marker of ["mediaDevices.getUserMedia", "panoramaCamera", "SpeechRecognition", "mode: \"VOICE\""]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic interview implementation marker: ${marker}`);
}
for (const marker of ["panoramaMediaCandidates", "无摄像头继续", "window.isSecureContext"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic camera compatibility marker: ${marker}`);
}
for (const marker of ["panoramaMediaDiagnosticTips", "panoramaMediaDiagnostics", "allow=\\\"camera; microphone\\\"", "Permissions-Policy"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic media diagnostic marker: ${marker}`);
}
for (const marker of ["window.isSecureContext !== false", "摄像头和麦克风接口"]) {
  if (!app.includes(marker)) throw new Error(`Missing precise panoramic media diagnostic marker: ${marker}`);
}
for (const marker of [".panorama-diagnostic", ".panorama-diagnostic li"]) {
  if (!styles.includes(marker)) throw new Error(`Missing panoramic media diagnostic style: ${marker}`);
}
for (const marker of ["panoramaAnswerLimit", "3 * 60", "5 * 60", "8 * 60", "本题在规定时间内未完成回答"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic answer timing marker: ${marker}`);
}
for (const marker of ["interviewAnswerCount", "提交并生成复盘", "score-dimensions"]) {
  if (!app.includes(marker)) throw new Error(`Missing seven-question AI interview marker: ${marker}`);
}
for (const marker of ["interviewDelete", "删除面试记录", "删除记录"]) {
  if (!app.includes(marker)) throw new Error(`Missing interview deletion marker: ${marker}`);
}
for (const marker of ["interviewPage", "interview-history", "interviewHistoryPageNumber", "每页查看 10 条记录", "interview-history-pager full", "formatInterviewDateTime", "结束时间：尚未结束"]) {
  if (!app.includes(marker)) throw new Error(`Missing paged interview history marker: ${marker}`);
}
for (const marker of ["interviewViewMode", "查看问答", "interviewQuestionAnswerPairs", "面试官问题", "我的回答"]) {
  if (!app.includes(marker)) throw new Error(`Missing interview transcript marker: ${marker}`);
}
for (const marker of ['data-interview-action="leave"', "resetActiveInterviewView", "返回 AI 面试中心"]) {
  if (!app.includes(marker)) throw new Error(`Missing AI interview center return marker: ${marker}`);
}
for (const marker of [".voice-answer-button:focus", ".voice-answer-button:focus-visible", ".voice-answer-button:active span", "background: #43bca7", "background: transparent"]) {
  if (!styles.includes(marker)) throw new Error(`Missing voice answer interaction style: ${marker}`);
}
for (const marker of ['feature("面试中心", "面", "选择全景仿真面试或 AI 模拟面试并查看记录", "interview-home"']) {
  if (!app.includes(marker)) throw new Error(`Missing home interview center recommendation marker: ${marker}`);
}
for (const marker of ["interviewSetupDifficulty", "syncInterviewSetupDraftFromPage", "normalizeInterviewDifficulty", 'option value="Easy"']) {
  if (!app.includes(marker)) throw new Error(`Missing AI interview setup state marker: ${marker}`);
}
for (const marker of [".interview-type-grid .feature-card > p", ".interview-type-grid .feature-card > button", "grid-column: 1 / -1"]) {
  if (!styles.includes(marker)) throw new Error(`Missing interview center card layout marker: ${marker}`);
}
for (const marker of ["interview-record-entry-grid", "查看 AI 模拟面试记录", "查看全景仿真面试记录"]) {
  if (!app.includes(marker) && !styles.includes(marker)) throw new Error(`Missing interview record entry marker: ${marker}`);
}
for (const marker of ["interview-panorama-history", "panoramaHistoryPageNumber", "loadPanoramaHistoryPage", 'mode: "VOICE"', "renderPanoramaTranscript", "删除记录"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic interview history marker: ${marker}`);
}
for (const removedMarker of ['<h3>面试记录</h3><p class="panel-note">历史记录在独立页面中保存和分页展示。</p>', 'statePanel("暂时没有完成操作"']) {
  if (app.includes(removedMarker)) throw new Error(`Obsolete AI interview prompt card remains: ${removedMarker}`);
}

const requiredRoutes = [
  "workbench",
  "employment-home",
  "further-study-home",
  "postgraduate-recommendation",
  "study-abroad",
  "onboarding",
  "today-action",
  "assessment",
  "resume",
  "resume-diagnosis",
  "interview-home",
  "interview",
  "interview-history",
  "interview-panorama-history",
  "interview-panorama",
  "career-plan",
  "assistant",
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
  "/cc001/admin/users/list",
  "/cc001/admin/users/ban",
  "/cc001/admin/users/unban",
  "/cc001/admin/questions/list",
  "/cc001/admin/questions/approve",
  "/cc001/admin/questions/reject",
  "/cc001/admin/content/list",
  "/cc001/admin/content/pin",
  "/cc001/admin/content/hide",
  "/cc001/admin/broadcast",
  "/cc001/admin/analytics/summary",
  "/cc001/admin/audit-log/list",
  "/cc001/files/upload",
  "/cc001/files/preview-url",
  "/cc001/files/download",
  "/cc001/files/delete",
  "/cc001/files/extract-text",
  "/cc001/career-profile/snapshot/get",
  "/cc001/career-agent/today/get",
  "/cc001/career-profile/onboarding/save",
  "/cc001/assessment/scales",
  "/cc001/assessment/questions",
  "/cc001/assessment/submit",
  "/cc001/assessment/records",
  "/cc001/assessment/record/get",
  "/cc001/resume/list",
  "/cc001/resume/create",
  "/cc001/resume-diagnosis/analyze",
  "/cc001/resume-diagnosis/keywords/status",
  "/cc001/career-plan/summary",
  "/cc001/career-plan/ensure",
  "/cc001/interview/list",
  "/cc001/interview/page",
  "/cc001/interview/start",
  "/cc001/interview/guided/start",
  "/cc001/interview/guided/answer",
  "/cc001/interview/guided/finish",
  "/cc001/interview/messages",
  "/cc001/interview/delete",
  "/cc001/assistant-chat/send",
  "/cc001/assistant-chat/session/list"
];

const requiredPageShellRoutes = [
  "workbench",
  "employment-home",
  "further-study-home",
  "postgraduate-recommendation",
  "study-abroad",
  "onboarding",
  "today-action",
  "assessment",
  "resume",
  "file-upload-preview",
  "resume-diagnosis",
  "career-plan",
  "interview-home",
  "interview",
  "interview-history",
  "interview-panorama-history",
  "interview-panorama",
  "assistant",
  "messages",
  "employment-insight",
  "career-resources"
];

const requiredDefaultUserRoutes = [
  "workbench",
  "employment-home",
  "further-study-home",
  "onboarding",
  "today-action",
  "assessment",
  "resume",
  "resume-diagnosis",
  "career-plan",
  "interview-home",
  "interview",
  "interview-history",
  "interview-panorama-history",
  "interview-panorama",
  "assistant",
  "messages",
  "employment-insight"
];

const requiredDebugRoutes = [
  "file-upload-preview",
  "career-resources",
  "admin-console"
];

const requiredStateModel = [
  "loading",
  "empty",
  "success",
  "identity-required",
  "forbidden",
  "unavailable",
  "backend-error",
  "pending"
];

function fail(message) {
  console.error(message);
  process.exit(1);
}

for (const file of requiredSplitFiles) {
  if (!fs.existsSync(path.join(assetsPath, file))) {
    fail(`Missing split frontend module: ${file}`);
  }
}

for (const route of requiredPageRendererRoutes) {
  if (!appSources.includes(`attachRenderer("${route}"`)) {
    fail(`Missing page module renderer registration: ${route}`);
  }
}

for (const key of requiredRoutes) {
  if (!routeMap.routes.some((route) => route.key === key && (route.status === "available" || route.status === "entry-only"))) {
    fail(`Missing available or entry-only route: ${key}`);
  }
  if (!appSources.includes(key)) {
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
if (!routeMap.pageShell || routeMap.pageShell.change !== "migrate-webapp-cyancruise-pages") {
  fail("Route map missing migrate-webapp-cyancruise-pages pageShell metadata");
}
if (routeMap.pageShell.implementation !== "static-hash-route-states") {
  fail("Page shell implementation must be static-hash-route-states");
}
for (const key of requiredPageShellRoutes) {
  if (!routeMap.pageShell.visibleUserRoutes || !routeMap.pageShell.visibleUserRoutes.includes(key)) {
    fail(`Page shell missing visible route: ${key}`);
  }
  if (!routeKeys.has(key)) {
    fail(`Page shell references unknown route: ${key}`);
  }
  if (!appSources.includes(`"${key}"`) && !appSources.includes(`'${key}'`)) {
    fail(`Static app page registry missing route: ${key}`);
  }
}
for (const key of requiredDefaultUserRoutes) {
  if (!routeMap.pageShell.defaultUserRoutes || !routeMap.pageShell.defaultUserRoutes.includes(key)) {
    fail(`Page shell missing default user route: ${key}`);
  }
}
for (const key of requiredDebugRoutes) {
  if (!routeMap.pageShell.debugRoutes || !routeMap.pageShell.debugRoutes.includes(key)) {
    fail(`Page shell missing debug route: ${key}`);
  }
}
for (const key of ["file-upload-preview", "career-resources"]) {
  const route = routeMap.routes.find((item) => item.key === key);
  if (!route || !route.visibility || route.visibility.defaultNav !== false || route.visibility.debugNav !== true || route.visibility.hashDirect !== true) {
    fail(`Route visibility metadata must hide by default and show in debug: ${key}`);
  }
}
for (const key of ["admin-console"]) {
  if (!routeMap.pageShell.adminRoutes || !routeMap.pageShell.adminRoutes.includes(key)) {
    fail(`Page shell missing admin route: ${key}`);
  }
}
for (const stateName of requiredStateModel) {
  if (!routeMap.pageShell.stateModel || !routeMap.pageShell.stateModel.includes(stateName)) {
    fail(`Page shell missing state model: ${stateName}`);
  }
}
if (!routeMap.pageShell.runtimeRule || !routeMap.pageShell.runtimeRule.includes("Vue") || !routeMap.pageShell.runtimeRule.includes("Flyway")) {
  fail("Page shell runtime rule must document excluded IPD runtimes");
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

for (const key of ["workbench", "employment-home", "further-study-home", "postgraduate-recommendation", "study-abroad", "onboarding", "today-action", "assessment", "resume", "resume-diagnosis", "interview", "career-plan", "assistant", "messages", "file-upload-preview", "employment-insight", "career-resources", "admin-console"]) {
  if (!routeMap.platformMounts.some((mount) => mount.routeKey === key)) {
    fail(`Missing platform mount for route: ${key}`);
  }
}

for (const api of requiredApis) {
  if (!serialized.includes(api)) {
    fail(`Route map missing API: ${api}`);
  }
}

console.log("cyancruise route map ok");
