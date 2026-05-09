// نسخة محسنة تمنع أي تحويل تلقائي للملفات وتفرض التحقق من الامتداد الأصلي فقط

let bannedWords = [];

fetch('./banned-words.json')
  .then(res => res.json())
  .then(data => {
    bannedWords = data;
  })
  .catch(err => console.error("فشل تحميل الكلمات المحظورة:", err));

function containsBannedWord(text) {
  const normalizedText = text.toLowerCase();
  return bannedWords.some(word => normalizedText.includes(word.toLowerCase()));
}

const allowedExtensions = ['.png', '.jpg', '.jpeg', '.pdf', '.docx', '.txt', "."];

function isAllowedFile(filename) {
  const ext = filename.substring(filename.lastIndexOf('.')).toLowerCase();
  return allowedExtensions.includes(ext);
}

document.addEventListener('DOMContentLoaded', function () {
  const postInput = document.getElementById("postInput");
  const postButton = document.getElementById("postButton");
  const postsContainer = document.getElementById("postsContainer");
  const imageUpload = document.getElementById("imageUpload");
  const fileUpload = document.getElementById("fileUpload");
  const searchInput = document.getElementById("searchInput");
  const suggestionBox = document.getElementById('suggestionBox');

  let selectedImageFile = null;
  let selectedFileFile = null;
  let isPosting = false;
  const token = window.JWT.getToken();

  function debounce(fn, delay) {
    let timer;
    return function (...args) {
      clearTimeout(timer);
      timer = setTimeout(() => fn.apply(this, args), delay);
    };
  }

  // إخفاء صندوق الاقتراحات عند الضغط خارج الصندوق
  document.addEventListener('click', (event) => {
    if (!searchInput.contains(event.target) && !suggestionBox.contains(event.target)) {
      suggestionBox.innerHTML = '';
    }
  });

  searchInput.addEventListener('input', debounce(async (e) => {
    const query = e.target.value.trim();
    if (query.length < 2) return;

    try {
      const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        const suggestions = await response.json();
        displaySearchSuggestions(suggestions);
      }
    } catch (error) {
      alert('فشل في جلب الاقتراحات. تحقق من الاتصال.');
    }
  }, 400));

  searchInput.addEventListener('keydown', async (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const hashtag = searchInput.value.trim();

      try {
        const response = await fetch(`/api/posts/hashtag?hashtag=${encodeURIComponent(hashtag)}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("فشل في جلب النتائج.");

        const grouped = await response.json();

        const posts = grouped.posts || [];
        displayFilteredPosts(posts);

        const experts = grouped.experts || [];
        console.log("خبرات مطابقة:", experts);

        suggestionBox.innerHTML = '';
      } catch (error) {
        alert(error.message);
      }
    }
  });

  function displaySearchSuggestions(result) {
    suggestionBox.innerHTML = '';

    if ((!result.posts || result.posts.length === 0) && (!result.experts || result.experts.length === 0)) {
      const noResultsItem = document.createElement('div');
      noResultsItem.textContent = 'No results found';
      noResultsItem.className = 'suggestion-item no-results';
      suggestionBox.appendChild(noResultsItem);
      return;
    }

    let displayed = 0;

    if (result.posts && result.posts.length > 0) {
      const post = result.posts[0];
      const item = document.createElement('div');
      item.textContent = `Post : ${post.tag}`;
      item.className = 'suggestion-item';
      suggestionBox.appendChild(item);
      displayed++;
    }

    if (result.experts && result.experts.length > 0 && displayed === 0) {
      const expert = result.experts[0];
      const item = document.createElement('div');
      item.textContent = `User : ${expert.username}`;
      item.className = 'suggestion-item';
      suggestionBox.appendChild(item);
    }
  }

  imageUpload.addEventListener("change", (event) => {
    const file = event.target.files[0];
    if (file && isAllowedFile(file.name)) {
      selectedImageFile = file;
    } else {
      alert("امتداد الصورة غير مدعوم");
      imageUpload.value = '';
    }
  });

  fileUpload.addEventListener("change", (event) => {
    const file = event.target.files[0];
    if (file && isAllowedFile(file.name)) {
      selectedFileFile = file;
    } else {
      alert("امتداد الملف غير مدعوم");
      fileUpload.value = '';
    }
  });

  postButton.addEventListener('click', async () => {
    if (isPosting) return;

    let postContent = postInput.value.trim();

    if (containsBannedWord(postContent)) {
      alert('المنشور يحتوي على كلمات غير مسموح بها.');
      return;
    }

    const hashtagsArray = [];
    const hashtagRegex = /#([\w\u0600-\u06FF]+)/g;
    let match;
    while ((match = hashtagRegex.exec(postContent)) !== null) {
      hashtagsArray.push(match[1]);
    }
    postContent = postContent.replace(hashtagRegex, '').trim();
    const validHashtags = hashtagsArray.filter(tag => tag.length > 0);

    if (validHashtags.some(tag => containsBannedWord(tag))) {
      alert('الهاشتاق يحتوي على كلمات غير مسموح بها.');
      return;
    }

    if (!postContent) return alert('يرجى كتابة محتوى المنشور!');
    if (validHashtags.length === 0) return alert('يرجى إضافة هاشتاق واحد على الأقل يحتوي على كلمات أو أرقام!');

    const hashtags = validHashtags.join(' ');

    const postData = JSON.stringify({
      content: postContent,
      hashtag: hashtags
    });

    const formData = new FormData();
    formData.append('data', postData);
    if (selectedImageFile) formData.append('file', selectedImageFile);
    else if (selectedFileFile) formData.append('file', selectedFileFile);

    isPosting = true;
    postButton.textContent = 'جاري النشر...';
    postButton.disabled = true;

try {
  const response = await fetch(`/api/posts/create`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` },
    body: formData
  });

  if (!response.ok) {
    const contentType = response.headers.get("content-type");
    const result = contentType && contentType.includes("application/json")
      ? await response.json()
      : await response.text();
    throw new Error(result.message || result || 'حدث خطأ أثناء النشر.');
  }

  const result = await response.json();

  alert('تم نشر المنشور بنجاح!');
  postInput.value = '';
  imageUpload.value = '';
  fileUpload.value = '';
  selectedImageFile = null;
  selectedFileFile = null;
  fetchPosts();
} catch (error) {
  alert(`⚠️ ${error.message}`);
}

     finally {
      isPosting = false;
      postButton.textContent = 'نشر';
      postButton.disabled = false;
    }
  });

  async function fetchPosts() {
    try {
      const response = await fetch(`/api/posts/all`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        postsContainer.innerHTML = "<p>لا توجد منشورات لعرضها.</p>";
        return;
      }

      const posts = await response.json();
      displayFilteredPosts(posts);
    } catch (error) {
      postsContainer.innerHTML = "<p>حدث خطأ أثناء تحميل المنشورات.</p>";
    }
  }

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
        <button class="uil uil-heart ${post.isLiked ? 'active-like' : ''}" onclick="toggleLike(this, '${post.id}')"></button>
        <span class="likes-count">${post.numberOfLikes}</span>
        <button class="uil uil-bookmark-full ${post.isSaved ? 'active-save' : ''}" onclick="toggleSave(this, '${post.id}')"></button>
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


  window.toggleLike = async function (button, postId) {
    if (!postId) return alert('مفقود معرف المنشور');

    const isActive = button.classList.contains('active-like');

    try {
      const response = await fetch(`/api/likes/toggle?postId=${postId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        button.classList.toggle('active-like');
        const postElement = button.closest('.feed');
        const likesCountElement = postElement.querySelector('.likes-count');
        let currentLikes = parseInt(likesCountElement.textContent);
        likesCountElement.textContent = isActive ? currentLikes - 1 : currentLikes + 1;
      } else {
        const msg = await response.text();
        alert(`حدث خطأ أثناء تحديث الإعجاب: ${msg}`);
      }
    } catch (error) {
      alert(`حدث خطأ: ${error.message}`);
    }
  };

  window.toggleSave = async function (button, postId) {
    if (!postId) return alert('مفقود معرف المنشور');

    try {
      const response = await fetch(`/api/saves/toggle?postId=${postId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        button.classList.toggle('active-save');
      } else {
        const msg = await response.text();
        alert(`حدث خطأ أثناء تحديث الحفظ: ${msg}`);
      }
    } catch (error) {
      alert(`حدث خطأ: ${error.message}`);
    }
  };

  fetchPosts();
});
