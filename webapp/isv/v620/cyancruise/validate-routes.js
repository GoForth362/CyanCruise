const fs = require("fs");
const path = require("path");

const routePath = path.join(__dirname, "cyancruise-routes.json");
const indexPath = path.join(__dirname, "index.html");
const appPath = path.join(__dirname, "assets", "app-runtime.js");
const assetsPath = path.join(__dirname, "assets");
const stylesPath = path.join(__dirname, "assets", "styles.css");
const panoramaImagePath = path.join(__dirname, "assets", "images", "panorama-interview-room-v1.png");
const panoramaInterviewerImagePath = path.join(__dirname, "assets", "images", "ai-interviewer-human-v1.png");
const routeMap = JSON.parse(fs.readFileSync(routePath, "utf8"));
const indexHtml = fs.readFileSync(indexPath, "utf8");
const app = fs.readFileSync(appPath, "utf8");
const appSources = readJsSources(assetsPath);
const styles = fs.readFileSync(stylesPath, "utf8");
const adminServiceSource = fs.readFileSync(path.join(assetsPath, "services", "admin-service.js"), "utf8");
const adminConsoleSource = fs.readFileSync(path.join(assetsPath, "pages", "admin-console.js"), "utf8");
const pageShellSource = fs.readFileSync(path.join(assetsPath, "components", "page-shell.js"), "utf8");
const messageComponentSource = fs.readFileSync(path.join(assetsPath, "components", "message.js"), "utf8");
const messagesPageSource = fs.readFileSync(path.join(assetsPath, "pages", "messages.js"), "utf8");
const routesSource = fs.readFileSync(path.join(assetsPath, "routes.js"), "utf8");
const navigationSource = fs.readFileSync(path.join(assetsPath, "navigation.js"), "utf8");
const postgraduatePageSource = fs.readFileSync(path.join(assetsPath, "pages", "postgraduate.js"), "utf8");
const repositoryRoot = path.resolve(__dirname, "..", "..", "..", "..");
const realDataBackendSources = [
  "code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career/CareerPlanSummaryService.java",
  "code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career/InterviewAiHelper.java",
  "code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career/ResumeDiagnosisService.java",
  "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/ai/impl/DefaultResumeDiagnosisAnalyzer.java",
  "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/storage/impl/InMemoryCareerResourceStorage.java",
  "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/storage/impl/InMemoryEmploymentInsightStorage.java",
  "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/storage/impl/InMemoryStudyCenterStorage.java"
].map((file) => fs.readFileSync(path.join(repositoryRoot, file), "utf8")).join("\n");

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
  "pages/resume.js",
  "pages/resume-diagnosis.js",
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
  "resume",
  "resume-diagnosis",
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

for (const marker of [
  '<header class="app-header chrome-hidden">',
  '<nav class="route-nav chrome-hidden" id="routeNav"',
  '<section class="status-grid chrome-hidden" aria-label="调试状态">'
]) {
  if (!indexHtml.includes(marker)) {
    throw new Error(`Debug chrome must be hidden before the first paint: ${marker}`);
  }
}
for (const legacyMarker of ["求职主循环", "主循环状态"]) {
  if (indexHtml.includes(legacyMarker) || app.includes(legacyMarker)) {
    throw new Error(`Legacy main-loop shell copy remains visible: ${legacyMarker}`);
  }
}
for (const id of [
  "routeNav",
  "pageHost",
  "userIdInput",
  "saveUserIdButton",
  "identityTitle",
  "identityHint",
  "targetRole",
  "profileStatus",
  "readinessScore",
  "readinessHint",
  "resumeState",
  "resumeHint",
  "interviewState",
  "interviewHint"
]) {
  if (!indexHtml.includes(`id="${id}"`)) {
    throw new Error(`Legacy shell cleanup removed a required runtime mount: ${id}`);
  }
}
for (const marker of [
  "toggleHidden(els.appHeader, !debug)",
  "toggleHidden(els.routeNav, !debug)",
  "toggleHidden(els.statusGrid, !debug)"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Explicit debug mode can no longer restore debug chrome: ${marker}`);
  }
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
for (const marker of ["function renderFurtherStudyHome", "function furtherStudyRoadmapPanel", "明确升学方向", "评估基础与差距", "制定备考或申请计划", "推进材料与关键节点", "studyCenterDirection"]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing study route map marker: ${marker}`);
  }
}
for (const marker of ["studyGeneratePlan", "studyDailyTaskUpdate", "data-ensure-study-plan", "currentRouteGoal", "syncCurrentRoutePlanningState"]) {
  if (!app.includes(marker)) throw new Error(`missing independent study planning marker: ${marker}`);
}
const saveStudyDirectionStart = app.indexOf("function saveStudyCenterDirection()");
const saveStudyDirectionEnd = app.indexOf("function setStudyCenterSaveButtonBusy", saveStudyDirectionStart);
if (saveStudyDirectionStart < 0 || saveStudyDirectionEnd < 0) {
  throw new Error("Missing study direction save handler");
}
const saveStudyDirectionSource = app.slice(saveStudyDirectionStart, saveStudyDirectionEnd);
for (const marker of ["state.studyDailyPlan = null", "post(endpoints.studyDailyPlan", "state.dailyPlan = state.studyDailyPlan"]) {
  if (!saveStudyDirectionSource.includes(marker)) {
    throw new Error(`Study direction switching must refresh today actions: ${marker}`);
  }
}
for (const marker of ["function hasVerifiedStudyPlan", "function studyPlanRefreshState", 'plan.hasPlan !== true', 'phases.length >= 3', '"生成考研规划"']) {
  if (!app.includes(marker)) throw new Error(`Study planning controls must require a verified real plan: ${marker}`);
}
for (const marker of ["function furtherStudyMessageHtml(", "message.route !== state.route",
  "data-dismiss-further-study-message", "function dismissFurtherStudyMessage(", "function setFurtherStudyMessage("]) {
  if (!app.includes(marker)) throw new Error(`Further-study messages must remain dismissible and route-scoped: ${marker}`);
}
for (const marker of [
  "function startedStudyPhaseIds",
  "startedPhaseIds: startedStudyPhaseIds(previous)",
  "isPhaseStartedByProgress(phases[phaseIndex], phaseIndex, progressState)",
  "if (counters.completed > 0)",
  'return { code: "idle", label: "未开始" }'
]) {
  if (!app.includes(marker)) throw new Error(`Missing evidence-based study phase status marker: ${marker}`);
}
const studyGenerateApi = routeMap.routes.flatMap((route) => route.webApis || [])
  .find((api) => api.path === "/cc001/study-center/plan/generate");
if (!studyGenerateApi || !Array.isArray(studyGenerateApi.body)
    || !studyGenerateApi.body.includes("startedPhaseIds")) {
  throw new Error("Study plan generation must submit the stages that contain real checked progress");
}
for (const marker of ["function filterPlanPhasesByYears", "if (isStudyRoute()) return list;"]) {
  if (!app.includes(marker)) throw new Error(`Verified study phases must bypass employment horizon filtering: ${marker}`);
}
if (app.includes("hasTwelveMonthStudyCoverage")) {
  throw new Error("The browser must trust the server-verified study plan instead of parsing horizon text again");
}
for (const marker of ["setStudyCenterSaveButtonBusy(true)", "保存中...", 'aria-busy="true"']) {
  if (!app.includes(marker)) throw new Error(`Study-center save button must expose immediate busy feedback: ${marker}`);
}
for (const marker of ["isSuccessTitle", "\"success\"", "setTimeout(hide, 3000)"]) {
  if (!messageComponentSource.includes(marker)) throw new Error(`Success message theme or auto-dismiss contract is missing: ${marker}`);
}
if (!styles.includes(".message-panel.success") || !styles.includes("var(--blue-bg)")) {
  throw new Error("Success messages must use the application light-blue theme");
}
if (app.includes("返回升学中心")) {
  throw new Error("Study companion homes must remain independent platform entries without an in-page return action");
}
for (const route of ["postgraduate", "postgraduate-recommendation", "study-abroad"]) {
  const pattern = new RegExp(`["']?${route}["']?\\s*:\\s*["']{2}`);
  if (!pattern.test(navigationSource)) throw new Error(`Study companion home must not have a page-level back route: ${route}`);
}
for (const removedMarker of ["further-study-tools", "<h3>选择具体升学方向</h3>", "路线明确后，可继续使用对应方向的择校、备考、材料和申请工具。"] ) {
  if (app.includes(removedMarker)) {
    throw new Error(`Study center must not restore duplicate companion cards: ${removedMarker}`);
  }
}
for (const marker of ["function homeTargetField", "function syncHomeTargetField", "目标院校", "data-school-value", "targetSchool: intent.targetSchool"]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing route-aware self-profile target marker: ${marker}`);
  }
}
if (!postgraduatePageSource.includes("context.renderers.renderFurtherStudyHome(item)")) {
  throw new Error("Further-study page module must render the dedicated study route map");
}
if (postgraduatePageSource.includes("item.title + '工具'")) {
  throw new Error("Further-study page module must not restore the old overview tool heading");
}
for (const marker of ["function required", "required(post, endpoints.adminUsers", "required(context.post, endpoints.adminWhoami"]) {
  if (!adminServiceSource.includes(marker)) {
    throw new Error(`Admin service must preserve management data request failures: ${marker}`);
  }
}
for (const marker of ["管理数据暂时无法加载", "admin-retry-load", "重新加载"]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Admin console must show a recoverable load failure state: ${marker}`);
  }
}
for (const marker of ["adminUserSearch", "bindUserSearch", "service.listUsers(adminContext(context), keyword, 20)", "管理员", "普通用户"]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Admin user search and account type rendering must remain connected: ${marker}`);
  }
}
for (const marker of ["QUESTION_PAGE_SIZE = 10", "renderInterviewQuestionList", "renderAssessmentQuestionList", "assessmentQuestionPageByScale", "bindQuestionPager", "每页 ' + QUESTION_PAGE_SIZE + ' 道"]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Admin question banks must paginate every 10 questions: ${marker}`);
  }
}
for (const marker of [".admin-question-pager", ".admin-question-page"]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing admin question pager style: ${marker}`);
  }
}
for (const marker of ["function renderUnauthorized", "admin-access-denied", "管理员治理入口，仅对 ADMIN 或平台管理员开放。"]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Admin console must render the centered unauthorized state: ${marker}`);
  }
}
for (const marker of [".admin-access-denied", "place-items: center", "text-align: center"]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing centered admin access-denied style: ${marker}`);
  }
}
if (adminConsoleSource.includes('renderStandaloneState(context, "无管理员权限"')) {
  throw new Error("Unauthorized administrators must not receive the technical standalone warning panel");
}
for (const marker of [
  "admin-dashboard-hero",
  "admin-dashboard-metrics",
  "admin-dashboard-health-groups",
  "admin-dashboard-attention",
  "function dashboardMetric(",
  "function quickEntry(",
  'data-admin-jump="audit"',
  'root.querySelectorAll("[data-admin-jump]")',
  "activateTab(root, this.getAttribute(\"data-admin-jump\"))"
]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Missing redesigned admin dashboard contract: ${marker}`);
  }
}
for (const marker of [
  ".admin-dashboard-hero",
  ".admin-dashboard-metrics",
  ".admin-dashboard-body",
  ".admin-dashboard-quick-entry:focus-visible",
  "@media (max-width: 560px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned admin dashboard style: ${marker}`);
  }
}
for (const forbidden of ["function renderOverviewLegacy(", "totalUsers + 1", "totalInterviews + 1"]) {
  if (adminConsoleSource.includes(forbidden)) {
    throw new Error(`Admin dashboard must not retain legacy or fabricated overview data: ${forbidden}`);
  }
}
for (const marker of ["destructiveActionConfirmation", "context.showConfirmDialog", '{ danger: true }', "确认删除"]) {
  if (!adminConsoleSource.includes(marker)) {
    throw new Error(`Admin destructive actions must use the in-app confirmation dialog: ${marker}`);
  }
}
if (/\b(?:window\.)?confirm\s*\(/.test(adminConsoleSource)) {
  throw new Error("Admin console must not use browser-native confirm dialogs");
}
const homeIntentStart = app.indexOf("function renderHomeIntentPage(");
const homeIntentEnd = app.indexOf("function isHomeIntentCollapsed(", homeIntentStart);
if (homeIntentStart < 0 || homeIntentEnd < 0) {
  throw new Error("Missing home intent renderer for redesigned workbench");
}
const homeIntentSource = app.slice(homeIntentStart, homeIntentEnd);
for (const marker of [
  'classList.toggle("home-dashboard-mode", item.key === "workbench" && !isDebugMode())',
  "function homeWelcomeSummary(",
  "function homeDashboardOverview(",
  "function homeDashboardRouteCards(",
  "overviewRows(goal)",
  "todayOverviewStatus()",
  'data-link="today-action"',
  "home-profile-card",
  "toggleHomeProfileButton",
  "editHomeIntentButton",
  "homeDirectionButtons(selectedGoal)"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned workbench contract: ${marker}`);
  }
}
for (const forbidden of ["overviewStrip(selectedGoal)", 'class="feature-grid"']) {
  if (homeIntentSource.includes(forbidden)) {
    throw new Error(`Workbench must not restore the old equal-weight dashboard structure: ${forbidden}`);
  }
}
for (const marker of [
  "body.home-dashboard-mode .feature-page-header",
  ".home-dashboard-overview",
  ".home-today-card",
  "-webkit-line-clamp: 3",
  ".home-dashboard-status-grid",
  ".home-profile-grid",
  ".home-dashboard-route-grid",
  ".home-dashboard-route-card:focus-visible",
  "width: min(calc(100vw - 20px), 560px)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned workbench style: ${marker}`);
  }
}
const assessmentListStart = app.indexOf("function renderAssessmentList(");
const assessmentListEnd = app.indexOf("function deepProfileActionPanel(", assessmentListStart);
if (assessmentListStart < 0 || assessmentListEnd < 0) {
  throw new Error("Missing assessment profile completion dashboard renderer");
}
const assessmentListSource = app.slice(assessmentListStart, assessmentListEnd);
for (const marker of [
  'classList.toggle("assessment-dashboard-mode", item.key === "assessment" && !state.assessmentSelectedScaleId)',
  "Math.min(Object.keys(completed).length, scales.length)",
  "assessment-dashboard-scales",
  "assessment-dashboard-list",
  "assessment-dashboard-card--tone-",
  "assessment-dashboard-state",
  "assessment-dashboard-action",
  'data-assessment-scale="',
  "deepProfileActionPanel(scales.length, completedCount, nextScale)",
  'data-deep-profile-generate="true"',
  'data-link="deep-profile-detail"'
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned assessment dashboard contract: ${marker}`);
  }
}
for (const forbidden of ["assessment-card app-card-soft", "app-icon-tile--candy"]) {
  if (assessmentListSource.includes(forbidden)) {
    throw new Error(`Assessment dashboard must not restore the old horizontal or pink card treatment: ${forbidden}`);
  }
}
for (const marker of [
  "body.assessment-dashboard-mode .page-header",
  ".assessment-profile-overview",
  ".assessment-profile-ring",
  ".assessment-dashboard-section-head",
  ".assessment-dashboard-list",
  ".assessment-dashboard-card.is-recommended",
  ".assessment-dashboard-action:focus-visible",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned assessment dashboard style: ${marker}`);
  }
}
const employmentCenterStart = app.indexOf("function employmentRoadmapPanel(");
const employmentCenterEnd = app.indexOf("function planPhaseStepCard(", employmentCenterStart);
const studyCenterStart = app.indexOf("function furtherStudyRoadmapPanel(");
const studyCenterEnd = app.indexOf("function studyPlanningMaterialsPanel(", studyCenterStart);
if (employmentCenterStart < 0 || employmentCenterEnd < 0 || studyCenterStart < 0 || studyCenterEnd < 0) {
  throw new Error("Missing employment or study center overview renderer");
}
const employmentCenterSource = app.slice(employmentCenterStart, employmentCenterEnd);
const studyCenterSource = app.slice(studyCenterStart, studyCenterEnd);
for (const marker of [
  'classList.toggle("employment-center-dashboard-mode", item.key === "employment-home")',
  'classList.toggle("study-center-dashboard-mode", item.key === "further-study-home")',
  "path-center-overview--employment",
  "path-center-overview--study",
  "path-center-hero",
  "path-center-meta",
  "path-center-control",
  "path-center-focus-card",
  "path-center-steps",
  'data-link="career-plan"',
  "data-ensure-plan",
  'data-ensure-study-plan="true"',
  'data-save-study-direction="true"',
  'id="studyCenterDirection"'
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned path center contract: ${marker}`);
  }
}
for (const marker of [
  "state.resumes",
  "state.interviews",
  "employmentTargetRole(plan)",
  "weeklyFocus",
  "phases.slice(0, 4)"
]) {
  if (!employmentCenterSource.includes(marker)) {
    throw new Error(`Employment center must use real route data: ${marker}`);
  }
}
for (const marker of [
  "profile.direction",
  "profile.targetSchool",
  "profile.hasDirection",
  "hasVerifiedStudyPlan(studyPlan, profile.directionCode)"
]) {
  if (!studyCenterSource.includes(marker)) {
    throw new Error(`Study center must use real route data: ${marker}`);
  }
}
for (const marker of [
  "function employmentInsightPanel(",
  "function employmentResourcePanels(",
  "function studyPlanningMaterialsPanel(",
  "function studyCenterInsightPanel(",
  "function studyCenterResourcePanels(",
  'data-link="career-resources"',
  'data-link="study-resources"',
  "data-upload-study-material",
  "data-delete-study-material"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Path center must preserve existing insight, material, or resource contract: ${marker}`);
  }
}
for (const marker of [
  "body.employment-center-dashboard-mode .feature-page-header",
  "body.study-center-dashboard-mode .feature-page-header",
  ".path-center-hero",
  ".path-center-control",
  ".path-center-roadmap",
  ".path-center-step:focus-visible",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned path center style: ${marker}`);
  }
}
const todayActionStart = app.indexOf("function renderTodayPage(");
const todayActionEnd = app.indexOf("function renderRouteControlProgress(", todayActionStart);
if (todayActionStart < 0 || todayActionEnd < 0) {
  throw new Error("Missing today action dashboard renderer");
}
const todayActionSource = app.slice(todayActionStart, todayActionEnd);
for (const marker of [
  'classList.toggle("today-action-dashboard-mode", item.key === "today-action")',
  "function renderTodayActionOverview(",
  "function dailyTaskCompletionSummary(",
  "function isDailyTaskCompleted(",
  "today-action-overview",
  "today-action-main",
  "today-action-source",
  "summary.carriedOver",
  "view.progressSummary.activePhaseTitle",
  'data-link="career-plan"'
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned today action contract: ${marker}`);
  }
}
for (const marker of [
  "view.dailyPlan.items",
  "item.persisted",
  "item.completed",
  "progressState.checked",
  "Math.round(completed * 100 / list.length)"
]) {
  if (!todayActionSource.includes(marker)) {
    throw new Error(`Today action overview must use real task state: ${marker}`);
  }
}
for (const marker of [
  'data-daily-task',
  'data-plan-task',
  'data-source-task',
  "currentDailyTaskUpdateEndpoint()",
  "updateDailyTask(dailyTaskId"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Today action must preserve task update contract: ${marker}`);
  }
}
for (const marker of [
  "body.today-action-dashboard-mode .feature-page-header",
  ".today-action-overview",
  ".today-action-progress-ring",
  ".today-action-main",
  ".today-action-daily-card",
  ".today-action-source",
  ".route-task-toggle input:focus-visible + .route-checkmark",
  "overflow-x: hidden",
  "overflow-wrap: anywhere",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned today action style: ${marker}`);
  }
}
const careerPlanStart = app.indexOf("function renderCareerPlanPage(");
const careerPlanEnd = app.indexOf("function renderPlanSummaryCard(", careerPlanStart);
if (careerPlanStart < 0 || careerPlanEnd < 0) {
  throw new Error("Missing career plan dashboard renderer");
}
const careerPlanSource = app.slice(careerPlanStart, careerPlanEnd);
for (const marker of [
  'classList.toggle("career-plan-dashboard-mode", item.key === "career-plan")',
  "function renderCareerPlanOverview(",
  "career-plan-overview",
  "career-plan-execution",
  "career-plan-stages",
  "renderPlanTimeline(view.visiblePhases",
  "function planTimelineMilestoneLabel(",
  "renderPlanFlow(view.visiblePhases",
  "renderPlanPhaseCard(phase",
  "view.progressSummary",
  "view.visiblePhases.length",
  "view.selectedYears",
  'data-ensure-plan'
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned career plan contract: ${marker}`);
  }
}
for (const marker of [
  "progress.totalTasks",
  "progress.completedTasks",
  "progress.activePhaseTitle",
  "view.targetRole",
  "Math.round(completed * 100 / total)"
]) {
  if (!careerPlanSource.includes(marker)) {
    throw new Error(`Career plan overview must use real route data: ${marker}`);
  }
}
for (const marker of [
  "function currentPlanningTarget(",
  "intent.targetSchool",
  "每日计划暂时无法加载，请稍后重试；系统不会用无关任务替代。",
  "尚无真实路线阶段，生成路线后会显示服务端返回的内容。"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Study career plan must preserve study target semantics: ${marker}`);
  }
}
for (const marker of [
  'data-plan-focus',
  'data-plan-task',
  'data-daily-task',
  "focusPlanPhase(",
  "setPlanTaskChecked(",
  "requestAgentCareerPlanGeneration()"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Career plan must preserve route interaction contract: ${marker}`);
  }
}
for (const marker of [
  "body.career-plan-dashboard-mode .feature-page-header",
  ".career-plan-overview",
  ".career-plan-progress-ring",
  ".career-plan-evidence",
  ".career-plan-execution",
  ".career-plan-flow",
  ".career-plan-phase-card.active",
  ".route-empty-copy",
  ".route-task-toggle input:focus-visible + .route-checkmark",
  "overflow-x: hidden",
  "overflow-wrap: anywhere",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned career plan style: ${marker}`);
  }
}
const resumeWorkspaceStart = app.indexOf("function renderResumePage(");
const resumeWorkspaceEnd = app.indexOf("function bindResumeEvents(", resumeWorkspaceStart);
if (resumeWorkspaceStart < 0 || resumeWorkspaceEnd < 0) {
  throw new Error("Missing resume workspace renderer");
}
const resumeWorkspaceSource = app.slice(resumeWorkspaceStart, resumeWorkspaceEnd);
for (const marker of [
  'classList.toggle("resume-workspace-mode", item.key === "resume")',
  "function resumeWorkspaceOverview(",
  "function resumeHasLinkedFile(",
  "function resumeHasDiagnosis(",
  "resume-workspace-overview",
  "resume-create-card",
  "resume-form-step",
  "resume-list-card",
  "resume-record-card",
  "resume-record-meta",
  "resume-record-actions"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned resume workspace contract: ${marker}`);
  }
}
for (const marker of [
  "normalizeArray(state.resumes)",
  "resumes.filter(resumeHasLinkedFile).length",
  "resumes.filter(resumeHasDiagnosis).length",
  'id="resumeForm"',
  'id="resumeFileInput"',
  'id="uploadResumeFileButton"',
  'id="resumeFileKey"',
  'id="resumeParsedContent"',
  "state.resumeListError",
  "state.resumeMessage"
]) {
  if (!resumeWorkspaceSource.includes(marker)) {
    throw new Error(`Resume workspace must use real records and preserve form state: ${marker}`);
  }
}
for (const marker of [
  "data-preview-file",
  "data-diagnose-resume",
  "data-delete-resume",
  'disabled title="请先上传并关联 PDF 文件"'
]) {
  if (!resumeWorkspaceSource.includes(marker)) {
    throw new Error(`Resume workspace must preserve record action contract: ${marker}`);
  }
}
for (const marker of [
  "body.resume-workspace-mode .page-header",
  ".resume-workspace-overview",
  ".resume-workspace-stats",
  ".resume-create-form",
  ".resume-form-step:focus-within",
  ".resume-record-card",
  ".resume-record-actions",
  "overflow-x: hidden",
  "overflow-wrap: anywhere",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned resume workspace style: ${marker}`);
  }
}
const diagnosisWorkspaceStart = app.indexOf("function renderResumeDiagnosisPage(");
const diagnosisWorkspaceEnd = app.indexOf("function bindResumeDiagnosisEvents(", diagnosisWorkspaceStart);
if (diagnosisWorkspaceStart < 0 || diagnosisWorkspaceEnd < 0) {
  throw new Error("Missing resume diagnosis workspace renderer");
}
const diagnosisWorkspaceSource = app.slice(diagnosisWorkspaceStart, diagnosisWorkspaceEnd);
for (const marker of [
  'classList.toggle("resume-diagnosis-workspace-mode", item.key === "resume-diagnosis")',
  "function diagnosisWorkspaceOverview(",
  "function diagnosisReadinessItem(",
  "function diagnosisWorkspaceNotice(",
  "diagnosis-workspace-hero",
  "diagnosis-input-card",
  "diagnosis-form-step",
  "diagnosis-result-workspace",
  "diagnosis-history-workspace"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned resume diagnosis workspace contract: ${marker}`);
  }
}
for (const marker of [
  "selectedDiagnosisResume(resumes)",
  "selected && selected.parsedContent",
  "state.diagnosisHistoryByResume",
  'id="resumeDiagnosisForm"',
  'id="diagnosisResumeId"',
  'id="diagnosisResumeTrigger"',
  'id="diagnosisJobDescription"',
  'id="diagnosisResumeText"',
  "result.overallScore",
  "result.scoreBreakdown",
  "result.revisionSuggestions"
]) {
  if (!diagnosisWorkspaceSource.includes(marker)) {
    throw new Error(`Resume diagnosis workspace must use real data and preserve form state: ${marker}`);
  }
}
for (const marker of [
  'id="rerunDiagnosisButton"',
  "data-preview-file",
  "data-view-diagnosis",
  "data-delete-diagnosis",
  'record.fallbackStatus === "AGENT_AI"'
]) {
  if (!diagnosisWorkspaceSource.includes(marker)) {
    throw new Error(`Resume diagnosis workspace must preserve action contract: ${marker}`);
  }
}
for (const marker of [
  "body.resume-diagnosis-workspace-mode .page-header",
  ".diagnosis-workspace-hero",
  ".diagnosis-readiness-grid",
  ".diagnosis-form-step",
  ".diagnosis-result-hero",
  ".diagnosis-insight-columns",
  ".diagnosis-revision-card",
  ".diagnosis-history-card",
  "overflow-x: hidden",
  "overflow-wrap: anywhere",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned resume diagnosis workspace style: ${marker}`);
  }
}
const aiInterviewWorkspaceStart = app.indexOf("function renderInterviewPage(");
const aiInterviewWorkspaceEnd = app.indexOf("function renderInterviewHistoryPage(", aiInterviewWorkspaceStart);
if (aiInterviewWorkspaceStart < 0 || aiInterviewWorkspaceEnd < 0) {
  throw new Error("Missing AI interview workspace renderer");
}
const aiInterviewWorkspaceSource = app.slice(aiInterviewWorkspaceStart, aiInterviewWorkspaceEnd);
for (const marker of [
  'classList.toggle("ai-interview-workspace-mode", item.key === "interview")',
  "function renderInterviewPreparationOverview(",
  "function interviewReadinessItem(",
  "function interviewDifficultyLabel(",
  "ai-interview-workspace",
  "interview-preparation-hero",
  "interview-preparation-card",
  "interview-setup-notice",
  "interview-live-workspace",
  "interview-result-workspace",
  "interview-analysis-state"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Missing redesigned AI interview workspace contract: ${marker}`);
  }
}
for (const marker of [
  "state.interviewSetupPosition",
  "state.interviewSetupResumeId",
  "state.interviewSetupDifficulty",
  "normalizeArray(state.resumes)",
  'id="interviewPosition"',
  'id="interviewResume"',
  'id="interviewDifficulty"',
  'data-interview-action="start"',
  'data-link="interview-history"'
]) {
  if (!aiInterviewWorkspaceSource.includes(marker)) {
    throw new Error(`AI interview setup must use real state and preserve controls: ${marker}`);
  }
}
for (const marker of [
  'data-interview-action="speech"',
  'id="interviewAnswer"',
  'data-interview-action="answer"',
  'data-interview-action="finish"',
  'data-interview-action="reset"',
  'data-interview-action="current-transcript"',
  "report.overallScore",
  "report.scoreReasons"
]) {
  if (!aiInterviewWorkspaceSource.includes(marker)) {
    throw new Error(`AI interview workspace must preserve live session and report actions: ${marker}`);
  }
}
for (const marker of [
  "body.ai-interview-workspace-mode .page-header",
  ".interview-preparation-hero",
  ".interview-readiness-list",
  ".interview-setup-step",
  ".interview-setup-notice",
  ".interview-live-topbar",
  ".interview-live-progress",
  "body.ai-interview-workspace-mode .interview-result-workspace",
  "body.ai-interview-workspace-mode .interview-analysis-state",
  "overflow-x: hidden",
  "overflow-wrap: anywhere",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    throw new Error(`Missing redesigned AI interview workspace style: ${marker}`);
  }
}
for (const marker of [
  "post(endpoints.identityCurrent, {})",
  "prepareUserScopedStorage(previousUserId, userId)",
  "productionIdentityRequired(\"cc001-identity-current-failed\")",
  "return baseKey + \".user.\" + encodeURIComponent(userId)",
  "parseUserStorageJson(\"cyancruise.homeIntent\")"
]) {
  if (!app.includes(marker)) {
    throw new Error(`Account switching must refresh server identity and isolate browser state: ${marker}`);
  }
}
if (app.includes('state.identity.mode !== "production" || state.identity.userId')) {
  throw new Error("Production identity refresh must not be skipped because a cached userId exists");
}
for (const legacyAccess of [
  'localStorage.getItem("cyancruise.homeIntent',
  'localStorage.setItem("cyancruise.homeIntent',
  'localStorage.removeItem("cyancruise.homeIntent',
  'localStorage.getItem("cyancruise.previewProfile',
  'localStorage.setItem("cyancruise.previewProfile',
  'localStorage.removeItem("cyancruise.previewProfile'
]) {
  if (app.includes(legacyAccess)) {
    throw new Error(`User browser state must not use an unscoped storage key: ${legacyAccess}`);
  }
}
for (const marker of ["speechSynthesis", "SpeechSynthesisUtterance", "speakPanoramaQuestion", "panoramaLastSpokenQuestion", "播放题目"]) {
  if (!app.includes(marker)) throw new Error(`Missing panoramic interviewer speech marker: ${marker}`);
}
for (const marker of ["panoramaSpeechToken", "panoramaRecognitionToken", "startPanoramaAnswer(true)", "题目已读完，正在自动开启语音输入", "系统只会把语音转成文字，不保存音频"]) {
  if (!app.includes(marker)) throw new Error(`Missing panorama automatic answer marker: ${marker}`);
}
for (const marker of ["panoramaMatureMaleVoice", "warmPanoramaVoices", "utterance.pitch = 0.82", "utterance.rate = 0.9", "yunxi|yunyang"]) {
  if (!app.includes(marker)) throw new Error(`Missing panorama mature male voice marker: ${marker}`);
}
if (app.includes("题目出现后已自动计时")) {
  throw new Error("Panorama answer timer must start after the AI finishes reading the question");
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
for (const marker of [
  "interviewOperationToken",
  "panoramaOperationToken",
  "interviewEnded",
  "panoramaEnded",
  "renderInterviewAnalysisPending",
  "isCurrentInterviewOperation(operationToken)",
  "isCurrentPanoramaOperation(operationToken)",
  "摄像头、麦克风、语音识别和题目朗读均已停止"
]) {
  if (!app.includes(marker)) throw new Error(`Missing immediate interview exit marker: ${marker}`);
}
for (const marker of [
  'interviewEnd: "/cc001/interview/finish"',
  "exitInterviewWithoutAnswers",
  "exitPanoramaInterviewWithoutAnswers",
  "settleTextInterviewOnExit",
  "settlePanoramaInterviewOnExit",
  "settleInterviewsOnPageExit",
  "keepalive: true",
  "state.interviewAnswerCount === 0",
  "state.panoramaAnswerCount === 0"
]) {
  if (!app.includes(marker)) throw new Error(`Missing zero-answer interview exit marker: ${marker}`);
}
const panoramaFinishSource = app.slice(
  app.indexOf("function finishPanoramaInterview()"),
  app.indexOf("function resetPanoramaInterview()")
);
if (panoramaFinishSource.indexOf("stopPanoramaMedia();") < 0
    || panoramaFinishSource.indexOf("stopPanoramaMedia();") > panoramaFinishSource.indexOf("post(endpoints.guidedInterviewFinish")) {
  throw new Error("Panoramic interview media must stop before requesting the final analysis");
}
for (const marker of [
  'id="panoramaLiveRoom"',
  'id="panoramaQuestionNumber"',
  'id="panoramaRoomError"',
  'id="panoramaRoomNotice"',
  "function syncPanoramaRoomUi(options)",
  "function syncPanoramaRoomMessage(id, message)"
]) {
  if (!app.includes(marker)) throw new Error(`Missing stable panoramic interview room marker: ${marker}`);
}
const panoramaAnswerInputSource = app.slice(
  app.indexOf("function startPanoramaAnswer(automatic)"),
  app.indexOf("function updatePanoramaAnsweringUi(answering, message)")
);
const panoramaSubmitSource = app.slice(
  app.indexOf("function submitPanoramaAnswer(timedOut)"),
  app.indexOf("function finishPanoramaInterview()")
);
for (const [name, source] of [
  ["speech input", panoramaAnswerInputSource],
  ["answer submission", panoramaSubmitSource]
]) {
  if (!source.includes("syncPanoramaRoomUi(")) {
    throw new Error(`Panoramic interview ${name} must update the existing room locally`);
  }
  if (source.includes("renderPage(")) {
    throw new Error(`Panoramic interview ${name} must not redraw the full page`);
  }
}
if (!app.includes('if (!isCurrentPanoramaOperation(operationToken) || state.panoramaEnded) return;')
    || !app.includes('if (!isCurrentInterviewOperation(operationToken) || state.interviewEnded) return;')) {
  throw new Error("Late interview answer callbacks must not restore an ended session");
}
for (const marker of ["interviewDelete", "删除面试记录", "删除记录"]) {
  if (!app.includes(marker)) throw new Error(`Missing interview deletion marker: ${marker}`);
}
for (const marker of ["interviewPage", "interview-history", "interviewHistoryPageNumber", "size: 10", "interview-history-pager full", "formatInterviewDateTime", "isCompletedInterviewHistory"]) {
  if (!app.includes(marker)) throw new Error(`Missing paged interview history marker: ${marker}`);
}
for (const removedMarker of [
  "查看结果",
  "继续面试",
  'data-interview-action="open"',
  'data-view="result"',
  "结束时间：尚未结束"
]) {
  if (app.includes(removedMarker)) {
    throw new Error(`Completed interview history must not restore an active interview: ${removedMarker}`);
  }
}
for (const marker of [
  'if (key === "interview-history") return "interview";',
  'if (key === "interview-panorama-history") return "interview-panorama";'
]) {
  if (!app.includes(marker)) throw new Error(`Missing fixed interview history back marker: ${marker}`);
}
for (const marker of [
  "function interviewHistoryHomeRoute(key)",
  'data-interview-history-home="',
  'data-link="\' + escapeHtml(interviewHome) + \'">返回'
]) {
  if (!app.includes(marker)) throw new Error(`Missing direct interview history home navigation: ${marker}`);
}
const historyHomeGuardIndex = app.indexOf("var interviewHome = interviewHistoryHomeRoute(item.key);");
const genericPageShellIndex = app.indexOf("if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pageShell", historyHomeGuardIndex);
if (historyHomeGuardIndex < 0 || genericPageShellIndex < 0 || historyHomeGuardIndex > genericPageShellIndex) {
  throw new Error("Interview history direct-home action must take priority over generic page-shell back navigation");
}
if (app.includes("interviewHistoryReturnRoute")) {
  throw new Error("Interview history must not return to its recorded source route");
}
for (const marker of ['"interview-history": "interview"', '"interview-panorama-history": "interview-panorama"']) {
  if (!navigationSource.includes(marker)) throw new Error(`Missing fixed interview history navigation: ${marker}`);
}
for (const marker of ["interviewViewMode", "查看问答", "interviewQuestionAnswerPairs", "面试官问题", "我的回答"]) {
  if (!app.includes(marker)) throw new Error(`Missing interview transcript marker: ${marker}`);
}
for (const marker of ['backRouteFor(item.key)', 'var label = labels[parent] || "返回"', 'data-back-route="']) {
  if (!app.includes(marker) && !pageShellSource.includes(marker)) {
    throw new Error(`Missing parent-level page header navigation marker: ${marker}`);
  }
}
for (const marker of ["function restoreRouteScroll(route)", 'root.style.scrollBehavior = "auto"', "window.scrollTo(0, typeof y === \"number\" ? y : 0)"]) {
  if (!app.includes(marker)) throw new Error(`Missing synchronous back-route scroll restoration marker: ${marker}`);
}
const restoreScrollStart = app.indexOf("function restoreRouteScroll(route)");
const restoreScrollEnd = app.indexOf("function currentScrollTop()", restoreScrollStart);
if (restoreScrollStart < 0 || restoreScrollEnd < 0 || app.slice(restoreScrollStart, restoreScrollEnd).includes("setTimeout")) {
  throw new Error("Back-route scroll restoration must not be delayed to a later paint frame");
}
for (const marker of ["diagnosisResumePicker", "diagnosisResumeTrigger", "data-diagnosis-resume-id", "chooseDiagnosisResume"]) {
  if (!app.includes(marker)) throw new Error(`Missing themed diagnosis selector marker: ${marker}`);
}
for (const marker of ["assessmentDetailPageItem", "data-assessment-back", "assessmentSelectedScaleId"]) {
  if (!app.includes(marker)) throw new Error(`Missing assessment detail navigation marker: ${marker}`);
}
for (const marker of ["查看上次结果", "data-assessment-retake", "startNewAssessmentScale", "assessmentAnswerReviewPanel", "latestAssessmentRecord"]) {
  if (!app.includes(marker)) throw new Error(`Missing reusable completed assessment marker: ${marker}`);
}
if (app.includes('(done ? "重新补全" : "开始")')) {
  throw new Error("Completed assessments must open their latest result instead of immediately starting over");
}
if (!pageShellSource.includes('item.key === "assessment"') || !pageShellSource.includes('data-assessment-back')) {
  throw new Error("Assessment detail header must return to the assessment list");
}
for (const marker of [".diagnosis-select-trigger", ".diagnosis-select-menu", ".diagnosis-select-option.active"]) {
  if (!styles.includes(marker)) throw new Error(`Missing themed diagnosis selector style: ${marker}`);
}
for (const marker of ["enhanceAppSelects", "data-app-select-trigger", "data-app-select-option", "MutationObserver"]) {
  if (!app.includes(marker)) throw new Error(`Missing global themed selector behavior: ${marker}`);
}
for (const marker of [".app-select-trigger", ".app-select-menu", ".app-select-option.active", ".app-select.drop-up"]) {
  if (!styles.includes(marker)) throw new Error(`Missing global themed selector style: ${marker}`);
}
for (const marker of [".app-select-trigger:focus", "chooseAppSelectOption(appSelectOption, event.detail === 0)", "optionButton.blur()"]) {
  if (!app.includes(marker) && !styles.includes(marker)) throw new Error(`Missing themed selector focus recovery marker: ${marker}`);
}
for (const marker of ["profile-derived-field", "来自升学自画像", "保存升学方向"]) {
  if (!app.includes(marker) && !styles.includes(marker)) throw new Error(`Missing study profile-derived school marker: ${marker}`);
}
if (app.includes('id="studyCenterTargetSchool"')) {
  throw new Error("Study center target school must be read from the study self-profile instead of edited locally");
}
if (app.includes('["explore",') || app.includes('goal === "explore"') || app.includes('normalized === "explore"')) {
  throw new Error("The unavailable explore route must not be exposed or selected by the home page");
}
for (const marker of [
  'field("homeGoal", "当前路线", "select", selectedGoal, [["employment", "就业"], ["study", "深造"]])',
  'if (normalized === "employment" || normalized === "study")'
]) {
  if (!app.includes(marker)) throw new Error(`Missing explicit home route restriction: ${marker}`);
}
for (const marker of ["assessmentPlainLanguageSummary", "mbtiPlainLanguageSummary", "assessmentDimensionDistribution", "本次选择更偏向"]) {
  if (!app.includes(marker)) throw new Error(`Missing plain-language assessment result marker: ${marker}`);
}
if (app.includes("维度计数：")) {
  throw new Error("Assessment dimension codes must not be shown directly to users");
}
for (const marker of ['data-link="workbench">首页', 'data-interview-action="leave"']) {
  if (app.includes(marker) || pageShellSource.includes(marker)) {
    throw new Error(`Unexpected unconditional page header navigation marker: ${marker}`);
  }
}
for (const marker of [".voice-answer-button:focus", ".voice-answer-button:focus-visible", ".voice-answer-button:active span", "background: #43bca7", "background: transparent"]) {
  if (!styles.includes(marker)) throw new Error(`Missing voice answer interaction style: ${marker}`);
}
for (const removedMarker of [
  'feature("AI 模拟面试", "面", "通过文字问答练习岗位表达，完成后查看评分和历史记录", "interview"',
  'feature("全景仿真面试", "仿", "开启摄像头进入沉浸式面试房间，完成后查看评分和历史记录", "interview-panorama"'
]) {
  if (app.includes(removedMarker) || routesSource.includes(removedMarker)) {
    throw new Error(`Interview entry must not remain under employment: ${removedMarker}`);
  }
}
for (const marker of ["interviewSetupDifficulty", "syncInterviewSetupDraftFromPage", "normalizeInterviewDifficulty", 'option value="Easy"']) {
  if (!app.includes(marker)) throw new Error(`Missing AI interview setup state marker: ${marker}`);
}
for (const marker of [
  'interview-entry-actions',
  'data-link="interview-history">查看面试记录',
  'data-link="interview-panorama-history">查看面试记录',
  'data-link="interview-history">查看 AI 模拟面试记录',
  'data-link="interview-panorama-history">查看全景仿真面试记录'
]) {
  if (!app.includes(marker)) throw new Error(`Missing interview page record entry marker: ${marker}`);
}
for (const removedMarker of ['data-link="employment-home">返回就业', '"interview": "employment-home"', '"interview-panorama": "employment-home"']) {
  if (app.includes(removedMarker) || navigationSource.includes(removedMarker)) {
    throw new Error(`Standalone interview page still has a parent navigation path: ${removedMarker}`);
  }
}
for (const removedMarker of ['function renderInterviewHub(', 'registerPage("interview-home"', '返回面试中心']) {
  if (appSources.includes(removedMarker)) throw new Error(`Obsolete interview center marker remains: ${removedMarker}`);
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
  "study-resources",
  "postgraduate-recommendation",
  "study-abroad",
  "onboarding",
  "today-action",
  "assessment",
  "resume",
  "resume-diagnosis",
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
  "/cc001/study-center/resources/list",
  "/cc001/notifications/list",
  "/cc001/notifications/unread-count",
  "/cc001/notifications/read",
  "/cc001/notifications/read-all",
  "/cc001/notifications/delete",
  "/cc001/notifications/subscription/grant",
  "/cc001/notifications/subscription/quota",
  "/cc001/notifications/weekly-report/run",
  "/cc001/admin/whoami",
  "/cc001/admin/organizations/dashboard",
  "/cc001/admin/users/list",
  "/cc001/admin/users/ban",
  "/cc001/admin/users/unban",
  "/cc001/admin/questions/list",
  "/cc001/admin/questions/save",
  "/cc001/admin/questions/update",
  "/cc001/admin/questions/approve",
  "/cc001/admin/questions/reject",
  "/cc001/admin/questions/delete",
  "/cc001/admin/assessment/questions/save",
  "/cc001/admin/assessment/questions/delete",
  "/cc001/admin/content/list",
  "/cc001/admin/content/save",
  "/cc001/admin/content/pin",
  "/cc001/admin/content/hide",
  "/cc001/admin/content/delete",
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
  "/cc001/career-plan/daily/get",
  "/cc001/career-plan/daily/task/update",
  "/cc001/interview/list",
  "/cc001/interview/page",
  "/cc001/interview/start",
  "/cc001/interview/guided/start",
  "/cc001/interview/guided/answer",
  "/cc001/interview/guided/finish",
  "/cc001/interview/finish",
  "/cc001/interview/messages",
  "/cc001/interview/delete",
  "/cc001/assistant-chat/send",
  "/cc001/assistant-chat/session/list"
];

const requiredPageShellRoutes = [
  "workbench",
  "employment-home",
  "further-study-home",
  "study-resources",
  "postgraduate-recommendation",
  "study-abroad",
  "onboarding",
  "today-action",
  "assessment",
  "resume",
  "file-upload-preview",
  "resume-diagnosis",
  "career-plan",
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
  "interview",
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
for (const marker of ['resume: ""', '"resume-diagnosis": ""', 'route === "resume" || route === "resume-diagnosis"']) {
  if (!navigationSource.includes(marker)) {
    throw new Error(`Resume workspaces must not define a page-header back route: ${marker}`);
  }
}
for (const marker of [
  'if (key === "resume" || key === "resume-diagnosis") return "";',
  '"resume": ""',
  '"resume-diagnosis": ""'
]) {
  if (!app.includes(marker)) {
    throw new Error(`Runtime fallback must suppress resume workspace back navigation: ${marker}`);
  }
}

function finalFunctionSource(name, nextName) {
  const start = app.lastIndexOf(`function ${name}(`);
  const end = start < 0 ? -1 : app.indexOf(`function ${nextName}(`, start);
  if (start < 0 || end < 0) fail(`Missing final function block: ${name}`);
  return app.slice(start, end);
}

const companionHubSources = [
  finalFunctionSource("renderPostgraduatePage", "renderPostgraduateSchoolPage"),
  finalFunctionSource("renderRecommendationPage", "renderRecommendationRankingPage"),
  finalFunctionSource("renderStudyAbroadPage", "renderStudyAbroadProfilePage")
];
for (const marker of [
  "function renderCompanionHub(config)",
  "function padCompanionStep(value)",
  "companion-hub-route",
  "companion-action-card",
  "companion-action-enter"
]) {
  if (!app.includes(marker)) fail(`Missing redesigned companion hub marker: ${marker}`);
}
for (const [index, source] of companionHubSources.entries()) {
  if (!source.includes("renderCompanionHub({")) {
    fail(`Further-study companion hub ${index + 1} must use the shared action-card renderer`);
  }
  if (source.includes("postgraduate-action-grid") || source.includes("选择一个功能")) {
    fail(`Further-study companion hub ${index + 1} must not retain the duplicated legacy action row`);
  }
}
for (const marker of [
  "companion-hub--exam",
  "companion-hub--recommendation",
  "companion-hub--abroad",
  ".companion-action-card:hover",
  ".companion-action-card:focus-visible",
  "@media (max-width: 680px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) fail(`Missing redesigned companion hub style: ${marker}`);
}
for (const marker of [
  "--hub-accent: #239b7b",
  "--hub-accent-strong: #116a54",
  "--hub-accent: #177d83",
  "--hub-accent-strong: #0e575d",
  "--hub-accent: #2c8996",
  "--hub-accent-strong: #1a626d"
]) {
  if (!styles.includes(marker)) fail(`Further-study companion themes must use the unified cyan palette: ${marker}`);
}
const companionThemeStyles = styles.slice(
  styles.indexOf(".companion-hub--exam"),
  styles.indexOf(".companion-hub-hero")
);
for (const legacyColor of ["#b77a27", "#76501e", "#526fbd", "#354d91"]) {
  if (companionThemeStyles.includes(legacyColor)) {
    fail(`Further-study companion themes still contain a legacy non-cyan color: ${legacyColor}`);
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
const studyRoute = routeMap.routes.find((route) => route.key === "further-study-home");
const studyMount = routeMap.platformMounts.find((mount) => mount.routeKey === "further-study-home");
const studyCompanionChildren = {
  postgraduate: ["postgraduate-school", "postgraduate-plan", "postgraduate-mistake", "postgraduate-reexam"],
  "postgraduate-recommendation": ["recommendation-ranking", "recommendation-background", "recommendation-material", "recommendation-tutor"],
  "study-abroad": ["study-abroad-profile", "study-abroad-language", "study-abroad-school", "study-abroad-statement", "study-abroad-visa"]
};

if (!studyRoute || studyRoute.title !== "升学中心") {
  fail("Further-study route metadata must use the study route map title");
}
if (!studyMount || studyMount.title !== "升学中心") {
  fail("Further-study platform mount must use the study route map title");
}
for (const [parent, children] of Object.entries(studyCompanionChildren)) {
  const parentRoute = routeMap.routes.find((route) => route.key === parent);
  if (!parentRoute || parentRoute.parentRoute) {
    fail(`Study companion home must remain an independent platform entry: ${parent}`);
  }
  for (const child of children) {
    const childRoute = routeMap.routes.find((route) => route.key === child);
    if (!childRoute || childRoute.parentRoute !== parent) {
      fail(`Study companion child route missing parent: ${child} -> ${parent}`);
    }
    const mapping = `"${child}": "${parent}"`;
    if (!navigationSource.includes(mapping) || !app.includes(mapping)) {
      fail(`Study companion runtime navigation missing parent: ${child} -> ${parent}`);
    }
  }
}
for (const label of ["返回考研陪伴", "返回保研陪伴", "返回留学陪伴"]) {
  if (!pageShellSource.includes(label) || !app.includes(label)) {
    fail(`Study companion child page missing named back action: ${label}`);
  }
}

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
const studyResourcesRoute = routeMap.routes.find((item) => item.key === "study-resources");
const studyResourcesMount = routeMap.platformMounts.find((item) => item.routeKey === "study-resources");
if (!studyResourcesRoute || studyResourcesRoute.parentRoute !== "further-study-home"
    || !studyResourcesRoute.visibility || studyResourcesRoute.visibility.defaultNav !== false
    || studyResourcesRoute.visibility.debugNav !== false || studyResourcesRoute.visibility.hashDirect !== true) {
  fail("Study resource library route must be hash-direct and return to further-study-home");
}
if (!studyResourcesMount || studyResourcesMount.publishability !== "entry-only") {
  fail("Study resource library mount metadata must keep the page as an internal entry");
}
for (const marker of ['data-link="study-resources">全部资源', "function renderStudyResourcesPage(", '"study-resources": "further-study-home"']) {
  if (!appSources.includes(marker)) {
    fail(`Missing study resource library marker: ${marker}`);
  }
}

for (const marker of ['direction === "RECOMMENDATION" ? "生成保研规划"',
    'direction === "STUDY_ABROAD" ? "生成留学规划"',
    '&& direction !== "STUDY_ABROAD"',
    '尚未生成真实" + currentStudyDirectionLabel + "规划',
    '尚未生成真实" + studyDirectionLabel + "规划',
    'direction: firstText(getValue(state.studyCenterSelection, "direction"), "")']) {
  if (!appSources.includes(marker)) {
    fail(`Missing recommendation planning isolation marker: ${marker}`);
  }
}
const studyMaterialDeleteApi = routeMap.routes.flatMap((route) => route.webApis || [])
  .find((api) => api.path === "/cc001/study-center/materials/delete");
if (!studyMaterialDeleteApi || !Array.isArray(studyMaterialDeleteApi.body)
    || !studyMaterialDeleteApi.body.includes("direction")) {
  fail("Study planning material deletion must carry the selected direction");
}

for (const key of ["interview-history", "interview-panorama-history"]) {
  if (routeMap.platformMounts.some((mount) => mount.routeKey === key)) {
    fail(`Interview history must only be entered from its interview page: ${key}`);
  }
}

for (const marker of ["function renderInterviewAnalysisBody(", "scoreReasons", "AI 面试分析暂时无法生成",
    "本报告只分析本次面试保存的文字问答", "系统没有生成或保存默认分数",
    'report.analysisSource === "BASIC_RULES"', "基础规则复盘", "不代表 AI 深度分析"]) {
  if (!app.includes(marker)) {
    fail(`Missing real interview analysis marker: ${marker}`);
  }
}
for (const forbidden of ["完成了有效练习", "补充具体证据", "AI 服务可用后可获得更细致的逐项分析"]) {
  if (app.includes(forbidden)) {
    fail(`Interview page still contains fabricated analysis copy: ${forbidden}`);
  }
}
const interviewFinishApis = routeMap.routes.filter((route) => ["interview", "interview-panorama"].includes(route.key))
  .flatMap((route) => route.webApis || []).filter((api) => api.path === "/cc001/interview/guided/finish");
if (!interviewFinishApis.length || interviewFinishApis.some((api) => !api.fallback
    || !api.fallback.includes("不生成") || !api.fallback.includes("分数"))) {
  fail("Interview finish contracts must preserve answers and explicitly forbid fallback scores");
}
const zeroAnswerExitApis = routeMap.routes.filter((route) => ["interview", "interview-panorama"].includes(route.key))
  .flatMap((route) => route.webApis || []).filter((api) => api.path === "/cc001/interview/finish");
if (zeroAnswerExitApis.length !== 2 || zeroAnswerExitApis.some((api) => api.dto !== "String"
    || !api.fallback || !api.fallback.includes("主界面") || !api.fallback.includes("删除"))) {
  fail("Both interview modes must discard zero-answer sessions and return to their preparation home");
}
for (const key of ["interview-history", "interview-panorama-history"]) {
  const historyRoute = routeMap.routes.find((route) => route.key === key);
  if (!historyRoute || !historyRoute.recordEligibility
      || !historyRoute.recordEligibility.includes("COMPLETED")
      || (historyRoute.webApis || []).some((api) => api.path === "/cc001/interview/guided/finish")) {
    fail(`Interview history must contain only completed answered records without a report action: ${key}`);
  }
}

const panoramaWorkspaceSource = app.slice(
  app.indexOf("function renderPanoramaInterviewPage("),
  app.indexOf("function renderInterviewSetup(")
);
for (const marker of [
  'classList.toggle("panorama-interview-workspace-mode", item.key === "interview-panorama")',
  "function panoramaDeviceStatus(",
  "function panoramaReadinessItem(",
  "panorama-workspace-topbar",
  "panorama-readiness-strip",
  "panorama-camera-zone",
  "panorama-settings-card",
  "panorama-room-topbar",
  'id="panoramaProgressBar"',
  'id="panoramaProgressText"',
  "panorama-result-workspace",
  "panorama-analysis-state"
]) {
  if (!app.includes(marker)) {
    fail(`Missing panorama workspace marker: ${marker}`);
  }
}
for (const marker of [
  'id="panoramaCamera"',
  'id="panoramaPosition"',
  'id="panoramaResume"',
  'id="panoramaDifficulty"',
  'data-panorama-action="camera"',
  'data-panorama-action="fallback"',
  'data-panorama-action="start"',
  'id="panoramaLiveRoom"',
  'id="panoramaQuestionNumber"',
  'id="panoramaTimer"',
  'id="panoramaAnswer"',
  "syncPanoramaRoomUi",
  "state.panoramaCameraState",
  "state.panoramaStream"
]) {
  if (!panoramaWorkspaceSource.includes(marker)) {
    fail(`Panorama workspace lost media or interaction contract: ${marker}`);
  }
}
for (const marker of [
  "body.panorama-interview-workspace-mode",
  ".panorama-readiness-strip",
  ".panorama-preparation-grid",
  ".panorama-room-progress",
  ".panorama-answer-heading",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    fail(`Missing panorama workspace style marker: ${marker}`);
  }
}
const panoramaOverlayStart = styles.indexOf("body.panorama-interview-workspace-mode .panorama-overlay");
const panoramaOverlayEnd = styles.indexOf(".panorama-workspace-topbar", panoramaOverlayStart);
if (panoramaOverlayStart < 0 || panoramaOverlayEnd < 0
    || !styles.slice(panoramaOverlayStart, panoramaOverlayEnd).includes("background: transparent;")) {
  fail("Panorama room must not cover its background image with a theme overlay");
}
for (const marker of ["20260720-cyancruise-v282"]) {
  if (!indexHtml.includes(marker) || !appSources.includes(marker)) {
    fail(`Panorama workspace resource version is not aligned: ${marker}`);
  }
}

for (const marker of [
  'classList.toggle("message-center-workspace-mode", item.key === "messages" || item.key === "message-detail")'
]) {
  if (!app.includes(marker)) {
    fail(`Missing message center workspace route marker: ${marker}`);
  }
}
for (const marker of [
  "message-overview-hero",
  "message-inbox-panel",
  "message-toolbar-actions",
  "message-empty-state",
  "messageTypeIcon",
  "messageTypeTone",
  "message-detail-nav",
  "message-detail-foot",
  'data-message-action="refresh"',
  'data-message-action="read-all"',
  'data-message-action="weekly-report"',
  'data-message-action="read"',
  'data-message-action="open"',
  'data-message-action="delete"',
  'data-message-action="page"',
  "quotaSummary(data.quotas)",
  "applyRead(context, notificationId)",
  "applyAllRead(context)",
  "applyArchive(context, notificationId)",
  "PAGE_SIZE = 10"
]) {
  if (!messagesPageSource.includes(marker)) {
    fail(`Message center workspace lost a real-data or interaction contract: ${marker}`);
  }
}
for (const marker of [
  "body.message-center-workspace-mode",
  ".message-overview-hero",
  ".message-empty-state",
  ".message-type-icon",
  ".message-detail-content",
  "@media (max-width: 760px)",
  "@media (prefers-reduced-motion: reduce)"
]) {
  if (!styles.includes(marker)) {
    fail(`Missing message center workspace style marker: ${marker}`);
  }
}
for (const marker of ["20260720-cyancruise-v282"]) {
  if (!indexHtml.includes(marker) || !appSources.includes(marker)) {
    fail(`Message center workspace resource version is not aligned: ${marker}`);
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

for (const marker of [
  "function previewEmploymentResources",
  "function previewPlanPhases",
  "function defaultDailySuggestions",
  "function previewResumeRecord",
  "function previewAssessmentScale",
  "function previewAssessmentResult",
  "function defaultAssessmentRoles"
]) {
  if (app.includes(marker)) {
    fail(`Fabricated frontend fallback returned to runtime: ${marker}`);
  }
}

for (const marker of [
  "defaultPlan(",
  "fallbackQuestion(",
  "applyRevisionFallback(",
  "https://example.test/",
  "void seed("
]) {
  if (realDataBackendSources.includes(marker)) {
    fail(`Fabricated backend fallback returned to runtime: ${marker}`);
  }
}

console.log("cyancruise route map ok");
