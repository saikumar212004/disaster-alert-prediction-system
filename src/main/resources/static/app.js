let currentUser = null;
const $ = (id) => document.getElementById(id);
async function api(url, opt) {
    const r = await fetch(url, opt);
    if (!r.ok) throw new Error(await r.text());
    return r.json();
}
function showApp() {
    $("loginView").classList.add("hidden");
    $("app").classList.remove("hidden");
    $("userChip").textContent = currentUser.name;
    $("helloName").textContent = currentUser.name.split(" ")[0];
    $("roleBadge").textContent = currentUser.role;
    if (currentUser.role !== "ADMIN") document.querySelector(".admin-only").style.display = "none";
    loadAll();
}
$("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    $("loginMsg").textContent = "";
    try {
        const x = await api("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: $("email").value, password: $("password").value }),
        });
        if (x.success) {
            currentUser = x;
            showApp();
        } else $("loginMsg").innerHTML = '<p class="invalid">' + x.message + "</p>";
    } catch (err) {
        $("loginMsg").innerHTML =
            '<p class="invalid">Backend is not reachable. Start the Spring Boot application first.</p>';
    }
});
document.querySelectorAll(".nav").forEach(
    (b) =>
        (b.onclick = () => {
            document.querySelectorAll(".nav").forEach((x) => x.classList.remove("active"));
            b.classList.add("active");
            document.querySelectorAll(".page").forEach((x) => x.classList.remove("active"));
            $(b.dataset.page).classList.add("active");
            if (b.dataset.page === "alerts") renderAlerts();
            if (b.dataset.page === "history") renderHistory();
            if (b.dataset.page === "credentials") loadCredentials();
            if (b.dataset.page === "admin") showAdmin("users");
        })
);
$("logout").onclick = () => location.reload();
async function loadAll() {
    try {
        const [s, d, a, h] = await Promise.all([
            api("/api/stats"),
            api("/api/dashboard"),
            api("/api/alerts"),
            api("/api/history"),
        ]);
        $("sAlerts").textContent = s.activeAlerts;
        $("sUsers").textContent = s.users;
        $("sPred").textContent = s.predictions;
        $("alertCount").textContent = s.activeAlerts;
        renderMap(d);
        renderSignals(d);
        renderAlerts(a);
        renderHistory(h);
    } catch (e) {
        toast("Unable to load data. Check MySQL and Spring Boot.");
    }
}
function renderMap(rows) {
    const risk = rows.filter((x) => x.risk_level);
    $("riskMap").innerHTML =
        risk
            .slice(0, 3)
            .map((x, i) => `<div class="zone z${i + 1}">${x.location}<br>${x.risk_level}</div>`)
            .join("") || '<div style="padding:30px;color:#789">No readings available</div>';
}
function renderSignals(rows) {
    $("signals").innerHTML = rows
        .slice(0, 5)
        .map(
            (x) =>
                `<div class="signal"><b>${x.location}</b><span>🌡 ${x.temperature}°C &nbsp; 💧 ${x.humidity}% &nbsp; 🌧 ${x.rainfall}mm</span></div>`
        )
        .join("");
}
function renderAlerts(rows) {
    $("alertCards").innerHTML = rows
        .map(
            (a) =>
                `<div class="alertcard ${a.severity === "MEDIUM" ? "medium" : ""}"><div class="alerttop"><span class="alerttype">⚠ ${a.disaster_type}</span><span class="risk ${a.severity}">${a.severity}</span></div><h3>${a.title}</h3><p>${a.message}</p><div class="meta"><span>📍 ${a.region}</span><span>◷ ${new Date(a.issued_at).toLocaleString()}</span><span>↗ ${a.channels}</span></div></div>`
        )
        .join("");
}
function renderHistory(rows) {
    const f = $("historyFilter").value;
    const data = f === "All disasters" ? rows : rows.filter((x) => x.disaster_type === f);
    $("historyTable").innerHTML = data
        .map(
            (x) =>
                `<tr><td><b>${x.disaster_type}</b></td><td>${x.location}</td><td><span class="risk ${x.risk_level}">${x.risk_level}</span></td><td>${x.probability}%</td><td>${x.confidence}%</td><td>${new Date(x.predicted_at).toLocaleString()}</td></tr>`
        )
        .join("");
}
$("historyFilter").onchange = async () => renderHistory(await api("/api/history"));
async function runPrediction() {
    const body = {
        location: $("pLocation").value,
        temperature: $("pTemp").value,
        humidity: $("pHum").value,
        rainfall: $("pRain").value,
        windSpeed: $("pWind").value,
        waterLevel: $("pWater").value,
        seismic: $("pSeismic").value,
    };
    try {
        const x = await api("/api/predict", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
        });
        const p = x.prediction;
        $("predictionResult").innerHTML =
            `<div class="prediction"><span class="eyebrow">PREDICTION RESULT</span><h2>${p.disasterType}</h2><div class="big">${p.probability.toFixed(0)}%</div><div class="progress"><i style="width:${p.probability}%"></i></div><p><b>Risk:</b> <span class="risk ${p.riskLevel}">${p.riskLevel}</span> &nbsp; <b>Confidence:</b> ${p.confidence}%</p><p>${p.message}</p><div class="recommend"><b>Recommended action</b><br>${p.recommendedAction}</div></div>`;
        toast("Prediction saved to MySQL");
        loadAll();
    } catch (e) {
        toast("Prediction failed. Check database connection.");
    }
}
async function verifyCredential() {
    const code = $("credentialCode").value.trim();
    try {
        const x = await api("/api/credential/verify/" + encodeURIComponent(code));
        if (x.valid) {
            const c = x.credential;
            $("credentialResult").innerHTML =
                `<div class="verified"><b>✓ Credential Verified</b><br>${c.credential_type}<br>Holder: ${c.full_name}<br>Issuer: ${c.issuer}<br>Expires: ${c.expires_at}</div>`;
        } else $("credentialResult").innerHTML = '<div class="invalid">✕ Credential is invalid or not found.</div>';
    } catch (e) {
        toast("Verification service unavailable");
    }
}
async function loadCredentials() {
    const rows = await api("/api/credentials");
    $("credentialList").innerHTML = rows
        .map(
            (c) =>
                `<div class="credential"><b>${c.credential_code}</b><span>${c.full_name} • ${c.credential_type} • <strong>${c.status}</strong></span></div>`
        )
        .join("");
}
async function showAdmin(tab) {
    if (currentUser?.role !== "ADMIN") return toast("Admin role required");
    const box = $("adminContent");
    if (tab === "users") {
        const r = await api("/api/users");
        box.innerHTML = `<table><thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Location</th><th>Credential</th></tr></thead><tbody>${r.map((x) => `<tr><td>${x.full_name}</td><td>${x.email}</td><td>${x.role}</td><td>${x.location}</td><td>${x.credential_status}</td></tr>`).join("")}</tbody></table>`;
    } else if (tab === "credentials") {
        const r = await api("/api/credentials");
        box.innerHTML = `<table><thead><tr><th>Code</th><th>Holder</th><th>Type</th><th>Status</th><th>Expires</th></tr></thead><tbody>${r.map((x) => `<tr><td>${x.credential_code}</td><td>${x.full_name}</td><td>${x.credential_type}</td><td>${x.status}</td><td>${x.expires_at}</td></tr>`).join("")}</tbody></table>`;
    } else {
        const r = await api("/api/logs");
        box.innerHTML = `<table><thead><tr><th>Time</th><th>User</th><th>Module</th><th>Action</th><th>Details</th></tr></thead><tbody>${r.map((x) => `<tr><td>${new Date(x.created_at).toLocaleString()}</td><td>${x.full_name || "SYSTEM"}</td><td>${x.module}</td><td>${x.action}</td><td>${x.details || ""}</td></tr>`).join("")}</tbody></table>`;
    }
}
function toast(msg) {
    $("toast").textContent = msg;
    $("toast").classList.add("show");
    setTimeout(() => $("toast").classList.remove("show"), 2600);
}
function renderAlerts(rows) {
    $("alertCards").innerHTML = rows
        .map(
            (a) =>
                `<div class="alertcard ${a.severity === "MEDIUM" ? "medium" : ""}"><div class="alerttop"><span class="alerttype">⚠ ${a.disaster_type}</span><span class="risk ${a.severity}">${a.severity}</span></div><h3>${a.title}</h3><p>${a.message}</p><div class="meta"><span>📍 ${a.region}</span><span>◷ ${new Date(a.issued_at).toLocaleString()}</span><span>↗ ${a.channels}</span></div></div>`
        )
        .join("");
}
