import { fetchAndStoreExperiences, getStoredExperiences } from './experience-session.js';

document.addEventListener("DOMContentLoaded", function () {
    const userInfo = JWT.getUserInfo();
    const token = JWT.getToken();

    const experienceForm = document.getElementById("experience-form");
    const experienceInput = document.getElementById("experience-input");
    const experienceList = document.getElementById("experience-list");
    const experienceBtn = document.getElementById("experience-btn");
    const profileImg = document.getElementById("profile-picture");
    const uploadInput = document.getElementById("upload-image-input");
    const aboutMeText = document.getElementById("about-me-text");
    const aboutMeDisplay = document.getElementById("about-me-display");
    const saveAboutMeBtn = document.getElementById("save-about-me");
    const postBtn = document.getElementById("post-btn");
    const likeBtn = document.getElementById("like-btn");
    const savedBtn = document.getElementById("saved-btn");
    const postsContainer = document.getElementById("content-card");

    let experiences = getStoredExperiences();
    let isFirstExperience = (experiences.length === 0);

let posts = [];
let likedPosts = [];
let savedPosts = [];

fetch("/api/posts/user", {
    headers: { "Authorization": `Bearer ${token}` }
})
.then(res => res.json())
.then(data => {
    posts = data.myPosts || [];
    likedPosts = data.likedPosts || [];
    savedPosts = data.savedPosts || [];

    sessionStorage.setItem("userPosts", JSON.stringify(posts));
    sessionStorage.setItem("likedPosts", JSON.stringify(likedPosts));
    sessionStorage.setItem("savedPosts", JSON.stringify(savedPosts));

    displayFilteredPosts(posts); // عرض البوستات الخاصة بالمستخدم أولاً
})
.catch(err => {
    console.error("فشل في جلب البوستات:", err);
});


    postBtn.addEventListener("click", () => {
        const data = JSON.parse(sessionStorage.getItem("userPosts") || '[]');
        displayFilteredPosts(data);
    });

    likeBtn.addEventListener("click", () => {
        const data = JSON.parse(sessionStorage.getItem("likedPosts") || '[]');
        displayFilteredPosts(data);
    });

    savedBtn.addEventListener("click", () => {
        const data = JSON.parse(sessionStorage.getItem("savedPosts") || '[]');
        displayFilteredPosts(data);
    });

    if (userInfo) {
        const nameEl = document.getElementById("profile-name");
        const emailEl = document.getElementById("profile-email");
        const universityEl = document.getElementById("profile-university");

        if (!nameEl.innerText) nameEl.innerText = userInfo.sub || 'N/A';
        if (!emailEl.innerText) emailEl.innerText = userInfo.email || 'N/A';
        if (!universityEl.innerText) universityEl.innerText = userInfo.university || 'N/A';
    }

    if (!sessionStorage.getItem("userProfileImage")) {
        fetch("/api/profiles/picture", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        })
        .then(response => {
            if (!response.ok) throw new Error("Failed to load profile picture");
            return response.text();
        })
        .then(imageUrl => {
            if (imageUrl) {
                profileImg.src = imageUrl;
                sessionStorage.setItem("userProfileImage", imageUrl);
            }
        })
        .catch(error => {
            console.error("Error fetching profile picture:", error);
        });
    } else {
        profileImg.src = sessionStorage.getItem("userProfileImage");
    }

    uploadInput.addEventListener("change", function (event) {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        fetch("/api/profiles/upload", {
            method: "POST",
            body: formData,
            headers: new Headers({
                "Authorization": `Bearer ${token}`
            })
        })
        .then(response => {
            if (!response.ok) throw new Error("Failed to upload image");
            return response.text();
        })
        .then(imageUrl => {
            if (imageUrl && imageUrl.startsWith("/uploads/")) {
                profileImg.src = imageUrl;
                sessionStorage.setItem("userProfileImage", imageUrl);
                alert("تم رفع الصورة بنجاح.");
            } else {
                throw new Error("Invalid image URL returned");
            }
        })
        .catch(error => {
            console.error("Error uploading image:", error);
            alert("حدث خطأ أثناء رفع الصورة.");
        });
    });

    const updateButtonState = () => {
        experienceBtn.disabled = experienceInput.value.trim().length === 0;
    };

    const loadExperiences = () => {
        if (experiences.length === 0) {
            fetchAndStoreExperiences().then(() => {
                experiences = getStoredExperiences();
                updateExperienceList();
                isFirstExperience = experiences.length === 0;
                experienceBtn.innerText = isFirstExperience ? "Add" : "Update";
                updateButtonState();
            });
        } else {
            updateExperienceList();
            experienceBtn.innerText = "Update";
            updateButtonState();
        }
    };

    const updateExperienceList = () => {
        experienceList.innerHTML = "";
        experiences.forEach(exp => {
            const li = document.createElement("li");
            li.textContent = exp;
            experienceList.appendChild(li);
        });
    };

    experienceForm.addEventListener("submit", function (event) {
        event.preventDefault();
        const experienceText = experienceInput.value.trim();
        if (!experienceText) return;

        const currentExperiences = getStoredExperiences();
        if (currentExperiences.some(exp => exp.toLowerCase() === experienceText.toLowerCase())) {
            alert("This experience already exists.");
            return;
        }

        const url = isFirstExperience ? "/api/experiences/new" : "/api/experiences/update";
        const method = isFirstExperience ? "POST" : "PUT";

        experienceBtn.disabled = true;

        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ experience: experienceText })
        })
        .then(response => {
            if (!response.ok) throw new Error("Failed to save experience");
            return response.text();
        })
        .then(() => {
            const updatedExperiences = [experienceText, ...currentExperiences];
            sessionStorage.setItem("userExperiences", JSON.stringify(updatedExperiences));

            const li = document.createElement("li");
            li.textContent = experienceText;
            experienceList.insertBefore(li, experienceList.firstChild);

            experienceInput.value = "";
            isFirstExperience = false;
            experienceBtn.innerText = "Update";
            alert("Experience added successfully.");
        })
        .catch(error => {
            console.error("Error while saving experience:", error);
        })
        .finally(() => {
            updateButtonState();
        });
    });

    experienceBtn.addEventListener("click", () => {
        experienceInput.disabled = false;
        experienceBtn.innerText = "Update";
        updateButtonState();
    });

    experienceInput.addEventListener("input", updateButtonState);
    experienceInput.disabled = false;

    loadExperiences();

    const storedAboutMe = sessionStorage.getItem("aboutMe");
    if (storedAboutMe) {
        aboutMeText.value = storedAboutMe;
    } else {
        fetch("/api/email/get/info", {
            headers: { "Authorization": `Bearer ${token}` }
        })
        .then(res => {
            if (!res.ok) throw new Error("فشل في جلب المعلومات");
            return res.text();
        })
        .then(data => {
            aboutMeText.value = data;
            sessionStorage.setItem("aboutMe", data);
        })
        .catch(err => console.error("خطأ في تحميل About Me:", err));
    }

    saveAboutMeBtn.addEventListener("click", function () {
        const aboutMeContent = aboutMeText.value.trim();
        if (!aboutMeContent) {
            alert("Please write something in About Me.");
            return;
        }

        fetch("/api/email/update", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ aboutMe: aboutMeContent })
        })
        .then(response => {
            if (!response.ok) throw new Error("Failed to update About Me");
            return response.text();
        })
        .then(() => {
            sessionStorage.setItem("aboutMe", aboutMeContent);
            alert("About Me updated successfully.");
        })
        .catch(error => {
            console.error("Error updating About Me:", error);
            alert("Error updating About Me. Please try again.");
        });
    });
});

function displayFilteredPosts(posts) {
  postsContainer.innerHTML = '';

  if (posts.length === 0) {
    postsContainer.innerHTML = "<p>لا توجد منشورات لعرضها.</p>";
    return;
  }

  posts.reverse().forEach(post => {
    const postElement = document.createElement('div');
    postElement.classList.add('feed');
    postElement.innerHTML = `
      <div class="head">
        <div class="user">
          <div class="profile-photo" style="width: 50px; height: 50px; border-radius: 50%; background: #ccc;">
            <img src="${post.profileUrl}">
          </div>
          <div class="info">
            <h3>${post.username}</h3>
            <small>${post.university}</small>
          </div>
        </div>
        <span class="edit"><i class="uil uil-ellipsis-h"></i></span>
      </div>
      <div class="caption">
        <p>${post.content}</p>
        <div class="hashtags" style="color: #3498db;">${post.hashtag || ''}</div>
      </div>
      <div class="post-media">
        ${post.imageUrl ? `<img src="${post.imageUrl}" class="post-image">` : ""}
        ${post.fileUrl ? renderFilePreview(post.fileUrl) : ""}
      </div>
      <div class="action-buttons">
        <button class="uil uil-heart btn-action-icon ${post.isLiked ? 'btn-like-active' : ''}" onclick="toggleLike(this, '${post.id}')"></button>
        <span class="likes-count">${post.numberOfLikes}</span>
        <button class="uil uil-bookmark-full btn-action-icon ${post.isSaved ? 'btn-save-active' : ''}" onclick="toggleSave(this, '${post.id}')"></button>
      </div>`;
    postsContainer.appendChild(postElement);
  });
}

// دالة جديدة لعرض معاينة الملف حسب الامتداد
function renderFilePreview(fileUrl) {
  const ext = fileUrl.split('.').pop().toLowerCase();

  if (ext === 'pdf') {
    return `
      <iframe src="${fileUrl}#toolbar=0" type="application/pdf" width="100%" height="300px" style="border: none; border-radius: 8px; margin-top: 10px;"></iframe>
    `;
  }

  if (['txt', 'docx'].includes(ext)) {
    return `
      <div style="background: #f0f2f5; border: 1px solid #ccc; padding: 16px; border-radius: 10px; display: flex; align-items: center; gap: 15px; margin-top: 10px;">
        <div style="font-size: 2rem;">📄</div>
        <div style="flex-grow: 1;">
          <div style="font-weight: bold;">ملف ${ext.toUpperCase()}</div>
        </div>
        <a href="${fileUrl}" target="_blank" style="background: #3498db; color: white; padding: 8px 14px; border-radius: 6px; text-decoration: none;">
          تحميل
        </a>
      </div>
    `;
  }

  return `<a href="${fileUrl}" target="_blank" class="file-link">📁 تحميل الملف</a>`;
}
