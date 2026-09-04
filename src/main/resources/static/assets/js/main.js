document.addEventListener("DOMContentLoaded", () => {
    initMobileMenu();
    initActiveNavLink();
    updateDocumentTitle();
    initNavbarScrollEffect();
    initGlobalAnimations();
    initTerminalTyping();
    initSystemNodeHUD();
});

function initMobileMenu() {
    const btn = document.getElementById("mobile-menu-btn");
    const menu = document.getElementById("mobile-menu");
    if (!btn || !menu) return;

    btn.addEventListener("click", () => {
        const isHidden = menu.classList.contains("hidden");
        if (isHidden) {
            menu.classList.remove("hidden");
            menu.classList.add("flex");
            if (window.anime) {
                anime({
                    targets: menu,
                    opacity: [0, 1],
                    translateY: [-8, 0],
                    duration: 200,
                    easing: "easeOutQuad"
                });
            }
        } else {
            if (window.anime) {
                anime({
                    targets: menu,
                    opacity: [1, 0],
                    translateY: [0, -8],
                    duration: 150,
                    easing: "easeInQuad",
                    complete: () => {
                        menu.classList.add("hidden");
                        menu.classList.remove("flex");
                    }
                });
            } else {
                menu.classList.add("hidden");
                menu.classList.remove("flex");
            }
        }
    });

    menu.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", () => {
            menu.classList.add("hidden");
            menu.classList.remove("flex");
        });
    });
}

function initActiveNavLink() {
    const path = window.location.pathname;
    const normalizedPath = (path === "" || path === "/index.html") ? "/" : path;

    document.querySelectorAll(".nav-link").forEach(link => {
        const href = link.getAttribute("href");
        if (!href) return;
        const isActive = (href === normalizedPath) || (normalizedPath !== "/" && href !== "/" && normalizedPath.startsWith(href));
        if (isActive) {
            link.classList.remove("text-zinc-400", "hover:text-white");
            link.classList.add("text-white", "bg-zinc-800", "font-semibold");
        } else {
            link.classList.remove("text-white", "bg-zinc-800", "font-semibold");
            link.classList.add("text-zinc-400", "hover:text-white");
        }
    });

    document.querySelectorAll(".mobile-nav-link").forEach(link => {
        const href = link.getAttribute("href");
        if (!href) return;
        const isActive = (href === normalizedPath) || (normalizedPath !== "/" && href !== "/" && normalizedPath.startsWith(href));
        if (isActive) {
            link.classList.remove("text-zinc-300");
            link.classList.add("text-white", "bg-zinc-800", "font-semibold");
        } else {
            link.classList.remove("text-white", "bg-zinc-800", "font-semibold");
            link.classList.add("text-zinc-300");
        }
    });
}

function initNavbarScrollEffect() {
    const navbar = document.getElementById("main-navbar");
    if (!navbar) return;

    window.addEventListener("scroll", () => {
        const navContainer = navbar.firstElementChild;
        if (window.scrollY > 30) {
            navContainer.classList.add("bg-[#09090B]/95", "border-zinc-700");
            navContainer.classList.remove("bg-[#121214]/80", "border-zinc-800");
        } else {
            navContainer.classList.add("bg-[#121214]/80", "border-zinc-800");
            navContainer.classList.remove("bg-[#09090B]/95", "border-zinc-700");
        }
    });
}

function initGlobalAnimations() {
    if (window.gsap && window.ScrollTrigger) {
        gsap.registerPlugin(ScrollTrigger);

        if (document.querySelector(".hero-container")) {
            gsap.from(".hero-element", {
                y: 24,
                opacity: 0,
                stagger: 0.08,
                duration: 0.6,
                ease: "power2.out"
            });
        }

        gsap.utils.toArray(".reveal-on-scroll").forEach((el) => {
            gsap.from(el, {
                scrollTrigger: {
                    trigger: el,
                    start: "top 88%",
                    toggleActions: "play none none reverse"
                },
                y: 30,
                opacity: 0,
                duration: 0.6,
                ease: "power2.out"
            });
        });
    }
}

function initTerminalTyping() {
    const target = document.getElementById("terminal-typing");
    if (!target) return;

    const command = "mvn spring-boot:run";
    let index = 0;
    target.textContent = "";

    function typeChar() {
        if (index < command.length) {
            target.textContent += command.charAt(index);
            index++;
            setTimeout(typeChar, 50);
        }
    }

    setTimeout(typeChar, 600);
}

function initSystemNodeHUD() {
    const hud = document.getElementById("system-node-hud");
    if (!hud) return;

    const wireframeBox = document.getElementById("hud-wireframe-cube");
    const nodePills = document.querySelectorAll(".hud-node-pill");
    const telemetryPercent = document.getElementById("hud-telemetry-percent");
    const telemetryNode = document.getElementById("hud-telemetry-node");
    const progressBar = document.getElementById("hud-progress-fill");

    const sections = [
        { id: "hero", name: "NODE 1: API GATEWAY", targetIndex: 0 },
        { id: "about", name: "NODE 2: CORE BACKEND", targetIndex: 1 },
        { id: "projects", name: "NODE 3: DATABASE CLUSTER", targetIndex: 2 },
        { id: "contact", name: "NODE 4: EVENT STREAM", targetIndex: 3 }
    ];

    if (window.gsap && window.ScrollTrigger) {
        gsap.to(wireframeBox, {
            scrollTrigger: {
                trigger: "body",
                start: "top top",
                end: "bottom bottom",
                scrub: 0.5,
                onUpdate: (self) => {
                    const progress = Math.round(self.progress * 100);
                    if (telemetryPercent) telemetryPercent.textContent = `${progress}%`;
                    if (progressBar) progressBar.style.width = `${progress}%`;

                    const rotX = 20 + self.progress * 360;
                    const rotY = 35 + self.progress * 720;
                    if (wireframeBox) {
                        wireframeBox.style.transform = `rotateX(${rotX}deg) rotateY(${rotY}deg)`;
                    }
                }
            }
        });

        sections.forEach((sec, idx) => {
            const sectionEl = document.getElementById(sec.id) || (sec.id === "hero" ? document.querySelector("section") : null);
            if (!sectionEl) return;

            ScrollTrigger.create({
                trigger: sectionEl,
                start: "top 50%",
                end: "bottom 50%",
                onEnter: () => activateNode(idx, sec.name),
                onEnterBack: () => activateNode(idx, sec.name)
            });
        });

        setTimeout(() => {
            if (window.ScrollTrigger) ScrollTrigger.refresh();
        }, 100);
    }

    function activateNode(index, name) {
        if (telemetryNode) telemetryNode.textContent = name;

        nodePills.forEach((pill, pIndex) => {
            const dot = pill.querySelector(".node-dot");
            const label = pill.querySelector(".node-label");

            if (pIndex === index) {
                pill.classList.add("border-white", "bg-zinc-800", "text-white");
                pill.classList.remove("border-zinc-800", "bg-transparent", "text-zinc-500");
                if (dot) {
                    dot.classList.add("bg-emerald-400", "shadow-[0_0_8px_#10B981]");
                    dot.classList.remove("bg-zinc-600");
                }
                if (label) label.classList.add("text-white");
            } else {
                pill.classList.remove("border-white", "bg-zinc-800", "text-white");
                pill.classList.add("border-zinc-800", "bg-transparent", "text-zinc-500");
                if (dot) {
                    dot.classList.remove("bg-emerald-400", "shadow-[0_0_8px_#10B981]");
                    dot.classList.add("bg-zinc-600");
                }
                if (label) label.classList.remove("text-white");
            }
        });
    }

    nodePills.forEach(pill => {
        pill.addEventListener("click", () => {
            const targetId = pill.getAttribute("data-target");
            const targetEl = document.getElementById(targetId);
            if (targetEl) {
                targetEl.scrollIntoView({ behavior: "smooth" });
            }
        });
    });
}

function updateDocumentTitle() {
    const titleHolder = document.querySelector("[data-page-title]");
    if (titleHolder) {
        const title = titleHolder.getAttribute("data-page-title");
        if (title) {
            document.title = title;
        }
    }
}

document.addEventListener("htmx:afterSettle", () => {
    initActiveNavLink();
    updateDocumentTitle();
    if (window.ScrollTrigger) {
        ScrollTrigger.getAll().forEach(t => t.kill());
    }
    initGlobalAnimations();
    initTerminalTyping();
    initSystemNodeHUD();
});

// --- BẢO MẬT & MÃ HÓA DỮ LIỆU LIÊN HỆ PHÍA CLIENT (HYBRID RSA-OAEP + AES-256-GCM) ---
let isContactSubmitting = false;

async function encryptContactPayload(publicKeyBase64, payloadObj) {
    const binaryDer = window.atob(publicKeyBase64.trim());
    const bytes = new Uint8Array(binaryDer.length);
    for (let i = 0; i < binaryDer.length; i++) {
        bytes[i] = binaryDer.charCodeAt(i);
    }

    const rsaPublicKey = await window.crypto.subtle.importKey(
        "spki",
        bytes.buffer,
        {
            name: "RSA-OAEP",
            hash: "SHA-256"
        },
        false,
        ["encrypt"]
    );

    const aesKey = await window.crypto.subtle.generateKey(
        {
            name: "AES-GCM",
            length: 256
        },
        true,
        ["encrypt"]
    );
    const iv = window.crypto.getRandomValues(new Uint8Array(12));

    const encodedPayload = new TextEncoder().encode(JSON.stringify(payloadObj));
    const encryptedData = await window.crypto.subtle.encrypt(
        {
            name: "AES-GCM",
            iv: iv
        },
        aesKey,
        encodedPayload
    );

    const rawAesKey = await window.crypto.subtle.exportKey("raw", aesKey);
    const encryptedKey = await window.crypto.subtle.encrypt(
        {
            name: "RSA-OAEP"
        },
        rsaPublicKey,
        rawAesKey
    );

    const toBase64 = (buffer) => {
        const u8 = new Uint8Array(buffer);
        let binary = '';
        for (let i = 0; i < u8.byteLength; i++) {
            binary += String.fromCharCode(u8[i]);
        }
        return window.btoa(binary);
    };

    return {
        encryptedData: toBase64(encryptedData),
        encryptedKey: toBase64(encryptedKey),
        iv: toBase64(iv)
    };
}

async function handleContactSubmit() {
    if (isContactSubmitting) {
        console.warn("Đang trong quá trình gửi tin nhắn, vui lòng chờ...");
        return;
    }

    const form = document.getElementById("contact-form");
    if (!form) return;

    // 1. Kiểm tra validation HTML5
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    isContactSubmitting = true;

    const submitBtn = document.getElementById("btn-submit-contact");
    const hideEls = submitBtn ? submitBtn.querySelectorAll(".htmx-indicator-hide") : [];
    const showEls = submitBtn ? submitBtn.querySelectorAll(".htmx-indicator") : [];

    const setLoading = (loading) => {
        if (!submitBtn) return;
        submitBtn.disabled = loading;
        if (loading) {
            hideEls.forEach(el => el.classList.add("hidden"));
            showEls.forEach(el => {
                el.classList.remove("hidden");
                el.classList.add("inline-flex");
            });
        } else {
            hideEls.forEach(el => el.classList.remove("hidden"));
            showEls.forEach(el => {
                el.classList.add("hidden");
                el.classList.remove("inline-flex");
            });
        }
    };

    setLoading(true);

    try {
        let pubKey = form.getAttribute("data-pubkey");
        if (!pubKey || pubKey.trim() === "") {
            try {
                const pkRes = await fetch("/contact/pubkey");
                if (pkRes.ok) {
                    const pkData = await pkRes.json();
                    pubKey = pkData.publicKey;
                    if (pubKey) form.setAttribute("data-pubkey", pubKey);
                }
            } catch (pkErr) {
                console.warn("Không lấy được public key từ server:", pkErr);
            }
        }

        const formData = {
            fullName: form.querySelector("#fullName")?.value || "",
            email: form.querySelector("#email")?.value || "",
            subject: form.querySelector("#subject")?.value || "",
            message: form.querySelector("#message")?.value || "",
            _hp_website: form.querySelector("#_hp_website")?.value || "",
            timestamp: Date.now()
        };

        let postParams = formData;

        // 2. Thực hiện mã hóa nếu Web Crypto API khả dụng và có Public Key
        if (window.crypto && window.crypto.subtle && pubKey && pubKey.trim().length > 0) {
            try {
                const encrypted = await encryptContactPayload(pubKey.trim(), formData);
                postParams = {
                    encryptedData: encrypted.encryptedData,
                    encryptedKey: encrypted.encryptedKey,
                    iv: encrypted.iv
                };
            } catch (cryptoErr) {
                console.warn("Mã hóa client thất bại, chuyển sang gửi bảo mật tiêu chuẩn:", cryptoErr);
                postParams = formData;
            }
        }

        // 3. Gửi dữ liệu bảo mật lên Server qua Fetch API (DUY NHẤT 1 REQUEST)
        const bodyParams = new URLSearchParams(postParams);
        const response = await fetch("/contact", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "X-Requested-With": "XMLHttpRequest"
            },
            body: bodyParams
        });

        const result = await response.json();

        if (response.ok && result.success) {
            if (typeof window.showAlert === "function") {
                window.showAlert("success", result.message);
                setTimeout(() => {
                    window.showAlert("info", "Vui lòng kiểm tra thêm hòm thư rác (Spam/Junk) nếu chưa thấy email xác nhận trong Hộp thư đến.", 6000);
                }, 750);
            }
            form.reset();
        } else {
            if (typeof window.showAlert === "function") {
                window.showAlert("error", result.message || "Đã có lỗi xảy ra. Vui lòng thử lại!");
            }
        }
    } catch (netErr) {
        console.error("Lỗi gửi liên hệ:", netErr);
        if (typeof window.showAlert === "function") {
            window.showAlert("error", "Đã có lỗi kết nối mạng khi gửi tin nhắn. Vui lòng thử lại!");
        }
    } finally {
        setLoading(false);
        isContactSubmitting = false;
    }
}

// Bắt sự kiện click nút gửi (button type="button")
document.addEventListener("click", function (e) {
    const btn = e.target.closest("#btn-submit-contact");
    if (btn) {
        e.preventDefault();
        e.stopPropagation();
        handleContactSubmit();
    }
});

// Bắt sự kiện nhấn phím Enter trên các trường input của contact form
document.addEventListener("keydown", function (e) {
    if (e.key === "Enter" && e.target && e.target.form && e.target.form.id === "contact-form" && e.target.tagName !== "TEXTAREA") {
        e.preventDefault();
        e.stopPropagation();
        handleContactSubmit();
    }
});

// Chặn triệt để sự kiện submit chuẩn ở capturing phase để HTMX hoặc trình duyệt không bao giờ kích hoạt submit thứ 2
document.addEventListener("submit", function (e) {
    if (e.target && e.target.id === "contact-form") {
        e.preventDefault();
        e.stopPropagation();
        e.stopImmediatePropagation();
        handleContactSubmit();
    }
}, true);

// Ngăn HTMX gửi AJAX request đối với contact form nếu vô tình bị HTMX nhắm tới
document.addEventListener("htmx:beforeRequest", function (e) {
    const elt = e.detail && e.detail.elt;
    if (elt && (elt.id === "contact-form" || (elt.closest && elt.closest("#contact-form")))) {
        e.preventDefault();
    }
});
